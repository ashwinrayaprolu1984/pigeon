package eu.unicredit.load;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class InsertMovimenti {

	public InsertMovimenti() {
		// TODO Auto-generated constructor stub
	}


	public void insert() throws Exception{


		Connection conn = getConnection();
		String sql="INSERT INTO retail.testlog(col1,col2,col3) VALUES (?,?,?)";
		long start=System.currentTimeMillis();

		try(PreparedStatement st=conn.prepareStatement(sql);
				){

			conn.setAutoCommit(false);

			for(int i=0;i<=100000;i++){
				st.setString(1, "RIGA " + i);
				st.setString(2, i+"");
				st.setString(3,new BigDecimal(i).toString());
				
				st.execute();
			}

			conn.commit();
			System.out.println("Elapsed " + (System.currentTimeMillis()-start));

		}

	}


	public static void main(String[] args) {
		try {
			new InsertMovimenti().insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection(){

		try{
			String databaseURL="jdbc:mariadb://storm01.rnd.unicredit.eu,storm02.rnd.unicredit.eu";
			Class.forName("org.mariadb.jdbc.Driver");
			Properties properties = new Properties();
			properties.put("user", "mariadb");
			properties.put("password", "Mercurio1");
			// Get a connection
			Connection conn = DriverManager.getConnection(databaseURL, properties);
			return conn;
		}catch(Exception e ){
			e.printStackTrace();
		}
		return null;
	}
}
