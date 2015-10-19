package eu.unicredit.replicator.schema;

import java.io.Serializable;
import java.security.Timestamp;

public class HeaderTransaction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8171725828755362254L;
	private long timestamp;
	private TransactionIdentifier transactionId= new TransactionIdentifier();
	

	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public TransactionIdentifier getTransactionIdentifier() {
		return transactionId;
	}
	public void setTransactionIdentifier(TransactionIdentifier transactionId) {
		this.transactionId = transactionId;
	}
	
	
	public class TransactionIdentifier implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2382932445505021095L;

		private TransactionIdentifier(){}
		private long transactionSequenceNumber;
		private Timestamp timestamp;
		private long lastPositionNumber;

		
		
		public long getTransactionSequenceNumber() {
			return transactionSequenceNumber;
		}
		public void setTransactionSequenceNumber(long transactionSequenceNumber) {
			this.transactionSequenceNumber = transactionSequenceNumber;
		}
		public Timestamp getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		
		public long getLastPositionNumber() {
			return lastPositionNumber;
		}
		public void setLastPositionNumber(long lastPositionNumber) {
			this.lastPositionNumber = lastPositionNumber;
		}
		
		
	}
	
	

}
