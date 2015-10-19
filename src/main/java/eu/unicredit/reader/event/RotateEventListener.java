package eu.unicredit.reader.event;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.RotateEventData;

import eu.unicredit.reader.AbstractReplicatorListener;

public class RotateEventListener extends AbstractReplicatorListener {

	@Override
	public void onEvent(Event event) {
		
		if(!event.getHeader().getEventType().equals(EventType.ROTATE))return;
		
		RotateEventData data=(RotateEventData )event.getData();
		System.out.println("ROTATE " + data.getBinlogFilename());
		synchronized (trackerLog) {
			trackerLog.setLogFileName(data.getBinlogFilename());
		}
		
	}

}
