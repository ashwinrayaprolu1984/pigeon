package eu.unicredit.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unicredit.conf.Config;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;





public class AvroProducer  
{ 
	private final KafkaProducer<String, Object> producer=null;
	private final Properties props ;
	private final String topic;
	private Producer<String, Object> producerJava;
	
	private static Logger LOG = LoggerFactory.getLogger(AvroProducer.class);

	
	public AvroProducer(String topic)
	{
		props=Config.getInstance().getProperties(Config.CONTEXT.PRODUCER);
		
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,				KafkaAvroSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,				KafkaAvroSerializer.class);
		props.put("serializer.class", "io.confluent.kafka.serializers.KafkaAvroEncoder");
		
		LOG.info("Added the following keys to avro producer: " + "\n	" + 
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG + "="+KafkaAvroSerializer.class.getCanonicalName() + "\n	"+
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG +"="+KafkaAvroSerializer.class.getCanonicalName() + "\n	" + 
				"serializer.class"+"="+ "io.confluent.kafka.serializers.KafkaAvroEncoder");
		
		producerJava = new kafka.javaapi.producer.Producer<String, Object>(new kafka.producer.ProducerConfig(props));
		
		LOG.info("Producer - Topic [" + topic + "]") ;
		this.topic=topic;
		
		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public synchronized void start() {
				LOG.info("Pigeon - Shutdown - Closing AvroProducer connection on topic ["+ topic+"]");
				producerJava.close();
			}
			
			
		});
	}

	
	public void send(Object avroRecord) throws Exception {
			KeyedMessage<String, Object> km = new KeyedMessage<String, Object>(topic, avroRecord);
			producerJava.send(km);
	}

	public void close(){
		if(producer!=null)
			producer.close();
	}




}