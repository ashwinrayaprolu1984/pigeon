package eu.unicredit.replicator.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReplicaSchema  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2007579011598657064L;
	private String database;
	private String table;
	private List<CharSequence> before=new ArrayList<CharSequence>();
	private List<CharSequence> after=new ArrayList<CharSequence>();
	private List<CharSequence> column=new ArrayList<CharSequence>();
	private String eventType;
	private HeaderTransaction header=new HeaderTransaction();
	
	
	public ReplicaSchema(){
		
	}
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public List<CharSequence> getBefore() {
		return before;
	}
	public void setBefore(List<CharSequence> before) {
		this.before = before;
	}
	public List<CharSequence> getAfter() {
		return after;
	}
	public void setAfter(List<CharSequence> after) {
		this.after = after;
	}
	public void addBefore(String string){
		before.add(string);
	}
	
	public void addAfter(String string){
		this.after.add(string);
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public HeaderTransaction getHeader() {
		return header;
	}

	public void setHeader(HeaderTransaction header) {
		this.header = header;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public ReplicaSchema flush(){
		ReplicaSchema schema=new ReplicaSchema();
		
		schema.setHeader(this.getHeader());
		schema.setDatabase(this.database);
		schema.setTable(this.table);
		return schema;
	}

	public List<CharSequence> getColumn() {
		return column;
	}

	public void setColumn(List<CharSequence> column) {
		this.column = column;
	}

	public void addColumn(String string){
		this.column.add(string);
	}
	
	
}
