package eu.unicredit.bridge;

import eu.unicredit.conf.Config;
import eu.unicredit.kafka.AvroProducer;
import eu.unicredit.replicator.TrackerLog;
import eu.unicredit.replicator.schema.ReplicaSchema;
import transactionlog.EventTypeTransactionLog;
import transactionlog.Record;

public class KafkaSender {

	private AvroProducer producer;
	private TrackerLog trackerLog=TrackerLog.getInstance();
	
	public void init(){
		 producer=new AvroProducer(Config.getInstance().getProperties(Config.CONTEXT.PIGEON, Config.KEY.TOPIC));
		 
	}
	
	public void send(ReplicaSchema schema,int cuurentItem) throws Exception{
		try{
			TrackerLog trackerLog=TrackerLog.getInstance();
		Record e = new Record();
		e.setRdbmsType(trackerLog.getRdbmsType());
		e.setDatabase(schema.getDatabase());
		e.setTimestampOperation(schema.getHeader().getTimestamp());
		e.setTable(schema.getTable());
		e.setTransactionId(schema.getHeader().getTransactionIdentifier().getTransactionSequenceNumber()+"");
		e.setTransactionSequenceNumber(schema.getHeader().getTransactionIdentifier().getTransactionSequenceNumber());
		e.setTotalCountTransactionEvent(trackerLog.getGlobalItemCount());
		e.setCurrentEventIndex(cuurentItem);
		e.setLastPositionNumber(schema.getHeader().getTransactionIdentifier().getLastPositionNumber());
		e.setLogFileName(trackerLog.getLogFileName() );
		e.setEventType(EventTypeTransactionLog.valueOf(schema.getEventType()) );
		e.setAfterValue(schema.getAfter());
		e.setBeforeValue(schema.getBefore());
		e.setColumnName(schema.getColumn());
		
		System.out.println("SenderEvent "  +schema.getEventType());
		System.out.println("Afeter " + schema.getAfter().size());
		System.out.println("Before " + schema.getBefore().size());
		
		producer.setSchema(e.getSchema());
		producer.send(e);
		}catch(Exception e){
			System.out.println("Count.........." + trackerLog.item.get());
			System.exit(0);
			throw e;
		}
	}
	
	public void close(){
		producer.close();
	}
	
}
