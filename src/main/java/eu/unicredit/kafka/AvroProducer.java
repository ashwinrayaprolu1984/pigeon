package eu.unicredit.kafka;

import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;

import eu.unicredit.conf.Config;
import eu.unicredit.conf.ConfigKafkaClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;





public class AvroProducer  
{ 
	private final KafkaProducer<String, Object> producer=null;
	private final Properties props ;
	private final String topic;
	private Producer<String, Object> producerJava;
	private Schema schema;

	
	public void setSchema(String schemaMessage){
		schema=new Schema.Parser().parse(schemaMessage);
	}

	public void setSchema(Schema schemaMessage){
		schema=schemaMessage;
	}

	
	public AvroProducer(String topic)
	{
		
		props=Config.getInstance().getProperties(Config.CONTEXT.PRODUCER);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,				KafkaAvroSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,				KafkaAvroSerializer.class);
		props.put("serializer.class", "io.confluent.kafka.serializers.KafkaAvroEncoder");
		
		producerJava = new kafka.javaapi.producer.Producer<String, Object>(new kafka.producer.ProducerConfig(props));
		
		
		System.out.println("Producer - Topic [" + topic + "]") ;
		this.topic=topic;
	}

	
	public void send(Object avroRecord){
		String key="k2";
		
		try {
			KeyedMessage<String, Object> km = new KeyedMessage<String, Object>(topic, avroRecord);
			producerJava.send(km);
			
		} catch(SerializationException e) {
			e.printStackTrace();
		}

	}

	public void send(String m) {
		
		GenericRecord avroRecord = new GenericData.Record(schema);
		avroRecord.put("col1", "stringa");
		avroRecord.put("col2", "con stringa");

		this.send(avroRecord);
		

	}

	public void close(){
		if(producer!=null)
			producer.close();
	}




}