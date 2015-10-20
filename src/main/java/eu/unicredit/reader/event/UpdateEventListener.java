package eu.unicredit.reader.event;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;

import eu.unicredit.reader.AbstractReplicatorListener;
import eu.unicredit.replicator.schema.ReplicaSchema;
import eu.unicredit.util.Utils;

public class UpdateEventListener extends AbstractReplicatorListener {

	
	private static Logger LOG = LoggerFactory.getLogger(UpdateEventListener.class);

	@Override
	public void onEvent(Event event) {
		if(!event.getHeader().getEventType().equals(EventType.UPDATE_ROWS))return;
		
		UpdateRowsEventData u = (UpdateRowsEventData)event.getData();
		EventHeaderV4 header=(EventHeaderV4)event.getHeader();
		
		List<Map.Entry<Serializable[], Serializable[]>> ll=u.getRows();

		
		
		for (Map.Entry<Serializable[], Serializable[]>  entry : ll) {
			ReplicaSchema schema = trackerLog.getCurrentSchema();
			schema.setEventType(event.getHeader().getEventType().name());
			Object bb=null;
			for (Serializable  s : entry.getKey()) {
				bb=Utils.deserialize(s);
				schema.addBefore((bb!=null?bb.toString():null));
				schema.getHeader().setTimestamp(header.getTimestamp());
				LOG.debug((bb!=null?bb.toString():null));
			}
			for (Serializable  s : entry.getValue()) {
				bb=Utils.deserialize(s);
				schema.addAfter((bb!=null?bb.toString():null));
				schema.getHeader().setTimestamp(header.getTimestamp());
				LOG.debug((bb!=null?bb.toString():null));
			}
			trackerLog.push();
		}
	}

}
