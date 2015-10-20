package eu.unicredit.reader.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.XidEventData;

import eu.unicredit.reader.AbstractReplicatorListener;
import eu.unicredit.replicator.schema.ReplicaSchema;

public class XIDEventListener extends AbstractReplicatorListener {

	private static Logger LOG = LoggerFactory.getLogger(XIDEventListener.class);

	@Override
	public void onEvent(Event event) {

		if(!event.getHeader().getEventType().equals(EventType.XID))return;
		ReplicaSchema schema=trackerLog.getCurrentSchema();
		XidEventData xidEvent= (XidEventData) event.getData();
		schema.getHeader().getTransactionIdentifier().setTransactionSequenceNumber(xidEvent.getXid());

		EventHeaderV4 header =  (EventHeaderV4)event.getHeader();


		schema.getHeader().getTransactionIdentifier().setLastPositionNumber(header.getNextPosition());
		LOG.debug("XID="+xidEvent.getXid());
		trackerLog.commit();
	}

}
