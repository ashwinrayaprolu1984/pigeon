package eu.unicredit.kafka;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unicredit.conf.Config;
import eu.unicredit.exception.CheckTopicFailureException;
import eu.unicredit.exception.NoMessageToParseException;
import eu.unicredit.util.AvroDecoder;
import eu.unicredit.util.KafkaUtils;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import transactionlog.Record;

public class KafkaDelegate {


	private Properties propsConsumer = Config.getInstance().getProperties(Config.CONTEXT.CONSUMER);
	private static Logger LOG = LoggerFactory.getLogger(KafkaDelegate.class);

	public boolean checkTopicIsEmpty(String zookeeperConnect,String topic,int partition) throws CheckTopicFailureException{

		try{
			KafkaUtils ku = new KafkaUtils();
			PartitionMetadata pm= ku.findLeader(zookeeperConnect, topic, partition);
			long logSize=ku.getLogSize(pm.leader().host(),pm.leader().port(), topic, 0);

			LOG.debug("Topic Size " + logSize);
			if(logSize==0)return true;

			
			//to be sure there are not previous messages
			byte[] bb = ku.readAtOffSet(pm.leader().host(),pm.leader().port(),topic, logSize-1, 0);
			if(bb==null)return true;
		}catch(Exception e){
			throw new CheckTopicFailureException(e);
		}

		return false;

	}


	public void send(String topic,String message) throws Exception{
		
		Producer<Integer , String> producer = new kafka.javaapi.producer.Producer<Integer, String>(new ProducerConfig(Config.getInstance().getProperties(Config.CONTEXT.PRODUCER)));
		
		Config.getInstance().getProperties(Config.CONTEXT.PRODUCER).forEach((x,y)-> LOG.debug(x+"="+y));
		producer.send(new KeyedMessage<Integer, String>(topic, message));
		producer.close();
		
	}
	
	public KafkaStream<byte[],byte[]> createMessageStream(String topic) throws Exception{
		
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, 1);

		final ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(propsConsumer));

		Map<String, List<KafkaStream<byte[],byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public synchronized void start() {
				super.start();
				LOG.info("Pigeon Shutdown - Closing kafka consumer on topic ["+ topic + "]");
				consumer.shutdown();
			}

		});
		
		
		return consumerMap.get(topic).get(0);
	}

	public byte[] readLastMessage(String zookeeperConnect,String topic,int partition){

		KafkaUtils ku = new KafkaUtils();

		PartitionMetadata pm =  ku.findLeader(zookeeperConnect, topic, partition);
		long logSize=ku.getLogSize(pm.leader().host(), pm.leader().port(), topic, partition);

		LOG.debug("Topic size [" + logSize+ "]");
		if(logSize==0)return null;
		long start = System.currentTimeMillis();
		LOG.debug("Read last message start time(ms) "+ start);
		byte[] bb = ku.readAtOffSet(pm.leader().host(), pm.leader().port(), topic, logSize-1, 0);
		LOG.debug("End read last message  time(ms) "+ (System.currentTimeMillis()-start));

		return bb;

	}



	public Record readAndParseLastMessage(String zookeeperConnect,String topic,int partition) throws NoMessageToParseException{

		AvroDecoder avroDecoder = new AvroDecoder();
		byte[] bb= this.readLastMessage(zookeeperConnect, topic, partition);
		try{
			if(bb==null)throw new NoMessageToParseException("Last message not found on topic " + topic);
			return avroDecoder.deserialize(bb);
		}catch(IOException | NoMessageToParseException e){
			throw new NoMessageToParseException(e);
		}



	}
}
