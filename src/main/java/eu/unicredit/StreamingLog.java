package eu.unicredit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unicredit.replicator.ReplicatorManager;

public class StreamingLog {

	private static Logger LOG = LoggerFactory.getLogger(StreamingLog.class);

	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public synchronized void start() {
				LOG.info("Pigeon shutdown..........................................................");
			}

		});
		LOG.info("*******************************Starting new pigeon instance**********************************");
		ReplicatorManager rm = new ReplicatorManager();
		try {
			rm.start();


		} catch (Throwable e) {
			LOG.error("UnknownError",e);

			System.exit(1);
		}
	}


}
