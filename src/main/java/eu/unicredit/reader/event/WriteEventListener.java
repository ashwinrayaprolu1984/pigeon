package eu.unicredit.reader.event;

import java.io.Serializable;
import java.util.List;

import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

import eu.unicredit.reader.AbstractReplicatorListener;
import eu.unicredit.replicator.TrackerLog;
import eu.unicredit.replicator.schema.ReplicaSchema;
import eu.unicredit.util.Utils;

public class WriteEventListener extends AbstractReplicatorListener {

 
	@Override
	public void onEvent(Event event) {

		if(!event.getHeader().getEventType().equals(EventType.WRITE_ROWS))return;


		WriteRowsEventData w = (WriteRowsEventData)event.getData();
		EventHeaderV4 header= (EventHeaderV4)event.getHeader();

		
		
		List<Serializable[]> lst= w.getRows();
		for (Serializable[]  ss : lst) {
			ReplicaSchema schema = trackerLog.getCurrentSchema();
			schema.setEventType(event.getHeader().getEventType().name());
			for (Serializable s : ss) {
				Object bb= Utils.deserialize(s);
				
				schema.addAfter((bb!=null?bb.toString():null));
				schema.getHeader().setTimestamp(header.getTimestamp());
				
				//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//				ObjectOutputStream oos;
				//				try {
				//					oos = new ObjectOutputStream(baos);
				//					oos.writeObject(s);
				//					oos.flush();
				//					oos.close();
				//					InputStream is = new ByteArrayInputStream(baos.toByteArray());
				//					ObjectInputStream ois=new ObjectInputStream(is);
				//					byte[] bb = (byte[])ois.readObject();
				//					
				System.out.println(bb);

				
			}
			trackerLog.push();
		}
	}

}
