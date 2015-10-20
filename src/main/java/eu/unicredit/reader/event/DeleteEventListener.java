package eu.unicredit.reader.event;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;

import eu.unicredit.reader.AbstractReplicatorListener;
import eu.unicredit.replicator.schema.ReplicaSchema;
import eu.unicredit.util.Utils;

public class DeleteEventListener extends  AbstractReplicatorListener {


	private static Logger LOG = LoggerFactory.getLogger(DeleteEventListener.class);
	
	 

	@Override
	public void onEvent(Event event) {

		if(!event.getHeader().getEventType().equals(EventType.DELETE_ROWS)) return;
		
		
		
		DeleteRowsEventData deleteRow=(DeleteRowsEventData)event.getData();
		EventHeaderV4 header=(EventHeaderV4)event.getHeader();
		
		List<Serializable[]> ll = deleteRow.getRows();
		for (Serializable[] serializables : ll) {
			ReplicaSchema schema = trackerLog.getCurrentSchema();
			schema.setEventType(event.getHeader().getEventType().name());
			for (Serializable serializable : serializables) {
				Object bb = Utils.deserialize(serializable);
				schema.addBefore((bb!=null?bb.toString():null));
				schema.getHeader().setTimestamp(header.getTimestamp());
				LOG.debug((bb!=null?bb.toString():null));
				
			}
			trackerLog.push();
		}
	}

}
