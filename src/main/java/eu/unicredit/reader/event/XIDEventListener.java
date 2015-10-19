package eu.unicredit.reader.event;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.XidEventData;

import eu.unicredit.reader.AbstractReplicatorListener;
import eu.unicredit.replicator.schema.ReplicaSchema;

public class XIDEventListener extends AbstractReplicatorListener {

 
	@Override
	public void onEvent(Event event) {
		
		if(!event.getHeader().getEventType().equals(EventType.XID))return;
		ReplicaSchema schema=trackerLog.getCurrentSchema();
		XidEventData xidEvent= (XidEventData) event.getData();
		schema.getHeader().getTransactionIdentifier().setTransactionSequenceNumber(xidEvent.getXid());
		
		EventHeaderV4 header =  (EventHeaderV4)event.getHeader();
		
		
		schema.getHeader().getTransactionIdentifier().setLastPositionNumber(header.getNextPosition());
		System.out.println("XID="+xidEvent.getXid());
		trackerLog.commit();
	}

}
