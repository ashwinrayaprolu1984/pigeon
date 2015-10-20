package eu.unicredit.reader.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;

import eu.unicredit.reader.AbstractReplicatorListener;
import eu.unicredit.replicator.schema.ReplicaSchema;

public class TableMapEventListener extends AbstractReplicatorListener {
	

	private static Logger LOG = LoggerFactory.getLogger(TableMapEventListener.class);

	@Override
	public void onEvent(Event event) {
		if(!event.getHeader().getEventType().equals(EventType.TABLE_MAP))return;
		TableMapEventData tableMapEvent =(TableMapEventData)event.getData();
		
		
		LOG.debug("database="+tableMapEvent.getDatabase());
		LOG.debug("table="+tableMapEvent.getTable());
		
		ReplicaSchema schema=trackerLog.getCurrentSchema();
		schema.setEventType(event.getHeader().getEventType().name());
		schema.setDatabase(tableMapEvent.getDatabase());
		schema.setTable(tableMapEvent.getTable());
		
		
	}

}
