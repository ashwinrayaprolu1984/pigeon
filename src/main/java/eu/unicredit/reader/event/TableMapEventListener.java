package eu.unicredit.reader.event;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;

import eu.unicredit.reader.AbstractReplicatorListener;
import eu.unicredit.replicator.schema.ReplicaSchema;

public class TableMapEventListener extends AbstractReplicatorListener {
	
 

	@Override
	public void onEvent(Event event) {
		if(!event.getHeader().getEventType().equals(EventType.TABLE_MAP))return;
		TableMapEventData tableMapEvent =(TableMapEventData)event.getData();
		
		
		System.out.println("database="+tableMapEvent.getDatabase());
		System.out.println("table="+tableMapEvent.getTable());
		
		ReplicaSchema schema=trackerLog.getCurrentSchema();
		schema.setEventType(event.getHeader().getEventType().name());
		schema.setDatabase(tableMapEvent.getDatabase());
		schema.setTable(tableMapEvent.getTable());
		
		
	}

}
