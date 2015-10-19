package eu.unicredit;
import eu.unicredit.replicator.ReplicatorManager;

public class StreamingLog {

	public static void main(String[] args) {
		ReplicatorManager rm = new ReplicatorManager();
		try {
			rm.start();
		} catch (Throwable e) {
			e.printStackTrace();		}
	}


}
