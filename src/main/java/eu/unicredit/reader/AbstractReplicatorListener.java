package eu.unicredit.reader;

import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;

import eu.unicredit.replicator.TrackerLog;

public abstract class AbstractReplicatorListener implements EventListener {

	protected TrackerLog trackerLog=TrackerLog.getInstance();
	
}
