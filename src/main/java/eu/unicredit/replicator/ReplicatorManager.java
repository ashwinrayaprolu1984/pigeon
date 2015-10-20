package eu.unicredit.replicator;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.NullEventDataDeserializer;

import eu.unicredit.conf.Config;
import eu.unicredit.exception.NoMessageToParseException;
import eu.unicredit.kafka.KafkaDelegate;
import eu.unicredit.reader.event.DeleteEventListener;
import eu.unicredit.reader.event.RotateEventListener;
import eu.unicredit.reader.event.TableMapEventListener;
import eu.unicredit.reader.event.UpdateEventListener;
import eu.unicredit.reader.event.WriteEventListener;
import eu.unicredit.reader.event.XIDEventListener;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import transactionlog.Record;

public class ReplicatorManager {

	private static Logger LOG = LoggerFactory.getLogger(ReplicatorManager.class);


	public void start() throws Throwable {

		BinaryLogClient client = new BinaryLogClient(
				Config.getInstance().getProperties(Config.CONTEXT.DB, Config.KEY.MARIADB_HOST),
				Integer.parseInt(Config.getInstance().getProperties(Config.CONTEXT.DB,  Config.KEY.MARIADB_PORT)),
				Config.getInstance().getProperties(Config.CONTEXT.DB,Config.KEY.MARIADB_USER), 
				Config.getInstance().getProperties(Config.CONTEXT.DB,Config.KEY.MARIADB_PASSSWORD));

		this.initClientMariaDB(client);
		this.initConcurrencyManagement(client);
		this.connect(client);


	}

	private void connect(BinaryLogClient client) throws Exception {
		TrackerLog.getInstance().setLogFileName(client.getBinlogFilename());
		TrackerLog.getInstance().setRdbmsType("MariaDB");
		client.connect();
		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public synchronized void start() {
				try {
					LOG.info("Pigeon Shutdown - Disconnect TransactionLog Reader");
					client.disconnect();
				} catch (IOException e) {
					LOG.error("Error closin channel to db",e);
				}
			}
			
		});
		LOG.info("Connected!");		
	}

	private void initClientMariaDB(BinaryLogClient client){

		EventDeserializer eventDeserializer = new EventDeserializer();
		eventDeserializer.setEventDataDeserializer(EventType.QUERY, new NullEventDataDeserializer() );
		client.setEventDeserializer(eventDeserializer);

		client.registerEventListener(new WriteEventListener());
		client.registerEventListener(new UpdateEventListener());
		client.registerEventListener(new TableMapEventListener());
		client.registerEventListener(new XIDEventListener());
		client.registerEventListener(new DeleteEventListener());
		client.registerEventListener(new RotateEventListener());
	}



	@SuppressWarnings({ "rawtypes" })
	private void initConcurrencyManagement(BinaryLogClient client) throws Exception{

		String topic=Config.getInstance().getProperties(Config.CONTEXT.PIGEON,Config.KEY.TOPIC);
		String electionQueue=Config.getInstance().getProperties(Config.CONTEXT.PIGEON,Config.KEY.ELECTION_QUEUE);


		KafkaDelegate kafkaDelegate= new KafkaDelegate();
		kafkaDelegate.send(electionQueue, Thread.currentThread().getName());


		KafkaStream stream = kafkaDelegate.createMessageStream(electionQueue);
		ConsumerIterator it = stream.iterator();

		try {
			LOG.info("Waiting to lead............................");
			it.isEmpty();
			LOG.info("Check topic is empty");
			boolean isEmpty =kafkaDelegate.checkTopicIsEmpty(Config.getInstance().getProperties(Config.CONTEXT.CONSUMER,Config.KEY.ZOOKEPER_URL), topic, 0);
			if(!isEmpty){
				LOG.info("Tring to restore from latest transaction sequence number........");
				Record r = kafkaDelegate.readAndParseLastMessage(Config.getInstance().getProperties(Config.CONTEXT.CONSUMER,Config.KEY.ZOOKEPER_URL), topic, 0);
				client.setBinlogPosition(r.getLastPositionNumber());
				client.setBinlogFilename(r.getLogFileName().toString());
			}else{
				LOG.info("Topic empty!!!");
			}
			LOG.info("Lead election!!!!");
		} catch ( NoMessageToParseException e) {
			throw new RuntimeException(e);
		}

	}

}
