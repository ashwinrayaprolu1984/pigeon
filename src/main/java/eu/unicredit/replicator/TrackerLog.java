package eu.unicredit.replicator;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import eu.unicredit.bridge.KafkaSender;
import eu.unicredit.replicator.schema.ReplicaSchema;



public class TrackerLog {

	
	private static TrackerLog trackerLog=new TrackerLog();
	private ReplicaSchema currentSchema=new ReplicaSchema();
	private String logFileName;
	private String rdbmsType;
	public AtomicInteger item=new AtomicInteger(0);
	private static Map<String, List<CharSequence>> columnNameMap;
	
	private ConcurrentLinkedQueue<ReplicaSchema> messageQueue=new ConcurrentLinkedQueue<ReplicaSchema>();
	private KafkaSender sender=new KafkaSender();
	
	private void initDB() throws Exception{
		columnNameMap=new HashMap<>();
		InputStream isc = TrackerLog.class.getClassLoader().getResourceAsStream("db.properties");
		Properties p = new Properties();
		p.load(isc);
		
		String sql ="select * from information_schema.columns where table_schema not in ('information_schema','mysql','performance_schema') order by table_schema,table_name,ordinal_position";
		String databaseURL=p.getProperty("url");
		Class.forName(p.getProperty("driverClass"));
		Properties properties = new Properties();
		properties.put("user", p.getProperty("user"));
		properties.put("password", p.getProperty("password"));
		
		
		try(Connection conn = DriverManager.getConnection(databaseURL, properties);
				Statement st = conn.createStatement();
				ResultSet rs=st.executeQuery(sql);){
			
			String db =null;
			String table=null;
			
			
			List<CharSequence> listaColonne=null;
			
			while (rs.next()) {
				
				String currentDb = rs.getString("table_schema");
				String currentTable = rs.getString("table_name");
				if(!currentDb.equals(db) || !currentTable.equals(table)){
					listaColonne=new ArrayList<>();
					columnNameMap.put(currentDb+"."+currentTable, listaColonne);
					db=currentDb;
					table=currentTable;
				}
				
				String columnName=rs.getString("column_name");
				listaColonne.add(columnName);
				
			}
			
			// Get a connection
//			Connection conn = DriverManager.getConnection(databaseURL, properties);
//			Statement st = conn.createStatement();
			
		}catch(Exception e ){
			e.printStackTrace();
		}
		
	}
	
	private TrackerLog(){
		sender.init();
		try {
			initDB();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static TrackerLog getInstance(){
		return trackerLog;
	} 
	
	
	public ReplicaSchema getCurrentSchema(){
		return this.currentSchema;
		
	} 
	
	public void  setCurrentSchema(ReplicaSchema schema){
		synchronized (schema) {
			this.currentSchema=schema;
			
		}
	}
	
	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public void commit(){
		ReplicaSchema s;
		try {
			int i=0;
			while ((s = messageQueue.poll()) != null) {
				sender.send(s,++i);
	         }
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setCurrentSchema(new ReplicaSchema());
//		this.setLogFileName(null);
		item.set(0);
		
		
	}
	
	public Integer getGlobalItemCount(){
		return item.get();
	}
	
	

	public String getRdbmsType() {
		return rdbmsType;
	}

	public void setRdbmsType(String rdbmsType) {
		this.rdbmsType = rdbmsType;
	}

	public void push(){
		currentSchema.setColumn(columnNameMap.get(currentSchema.getDatabase()+"."+currentSchema.getTable()));
		messageQueue.add(currentSchema);
		this.setCurrentSchema(currentSchema.flush());
		item.incrementAndGet();
	}
	

	
	
	
	
}
