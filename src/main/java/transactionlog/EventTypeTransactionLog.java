/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package transactionlog;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public enum EventTypeTransactionLog { 
  WRITE_ROWS, DELETE_ROWS, UPDATE_ROWS  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"EventTypeTransactionLog\",\"namespace\":\"transactionlog\",\"symbols\":[\"WRITE_ROWS\",\"DELETE_ROWS\",\"UPDATE_ROWS\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
}