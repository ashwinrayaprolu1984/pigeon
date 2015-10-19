package eu.unicredit.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;

public class ConfigKafkaClient {

	private static ConfigKafkaClient cfg =new ConfigKafkaClient();

	private final  Properties propProducer=new Properties();
	private final Properties propConsumer=new Properties();

	public Properties getPropertiesProducer(){
		return propProducer;
	}
	
	

	public Properties getPropertiesConsumer(){
		return propConsumer;
	}

	public static ConfigKafkaClient getInstance(){
		return cfg;
	}

	private ConfigKafkaClient() {

		try (InputStream isp = ConfigKafkaClient.class.getClassLoader().getResourceAsStream("kafka-producer.properties");
				InputStream isc = ConfigKafkaClient.class.getClassLoader().getResourceAsStream("kafka-consumer.properties")
				)
				{
			propProducer.load(isp);
			propConsumer.load(isc);
				} catch (IOException e) {
					e.printStackTrace();
				} 
	}


	public   ConsumerConfig getConsumerConfig() {
		Properties props=ConfigKafkaClient.getInstance().getPropertiesConsumer();
		return new ConsumerConfig(props);
	}
}
