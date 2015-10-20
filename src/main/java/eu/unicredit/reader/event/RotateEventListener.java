package eu.unicredit.reader.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.RotateEventData;

import eu.unicredit.reader.AbstractReplicatorListener;

public class RotateEventListener extends AbstractReplicatorListener {

	private static Logger LOG = LoggerFactory.getLogger(RotateEventListener.class);
	
	@Override
	public void onEvent(Event event) {
		
		if(!event.getHeader().getEventType().equals(EventType.ROTATE))return;
		
		RotateEventData data=(RotateEventData )event.getData();
		LOG.debug("ROTATE " + data.getBinlogFilename());
		synchronized (trackerLog) {
			trackerLog.setLogFileName(data.getBinlogFilename());
		}
		
	}

}
