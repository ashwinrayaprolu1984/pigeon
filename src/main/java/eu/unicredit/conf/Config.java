package eu.unicredit.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {


	private static Logger LOG = LoggerFactory.getLogger(Config.class);
	
	private static Config conf = new Config();
	private Properties propsCommon= new Properties();
	private Properties propsConsumer= new Properties();
	private Properties propsProducer= new Properties();
	private Properties propsPigeon= new Properties();
	private Properties propsDb= new Properties();
	private HashMap<CONTEXT, Properties> map=new HashMap<>();

	private Config(){
		init();
	}

	public String getProperties(CONTEXT context,  Config.KEY key){
		
		return map.getOrDefault(context, propsCommon).getProperty(key.value);
	}

	
	public Properties getProperties(CONTEXT context){
		Properties p = new Properties();
		p.putAll(map.get(context));
		return p;
	}

	
	public static Config getInstance(){
		return conf;
	}

	private  void init(){
		try {
			loadProperties();
			loadPropertiesDB();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}



	private void loadPropertiesDB() throws IOException{
		map.put(CONTEXT.DB, propsDb);
		InputStream is = Config.class.getClassLoader().getResourceAsStream("db.properties");
		propsDb.load(is);
	}

	private void loadProperties() throws IOException{
		map.put(CONTEXT.COMMON, propsCommon);
		map.put(CONTEXT.CONSUMER, propsConsumer);
		map.put(CONTEXT.PRODUCER, propsProducer);
		map.put(CONTEXT.PIGEON, propsPigeon);
		

		InputStream is = Config.class.getClassLoader().getResourceAsStream("pigeon.properties");
		Properties tmp = new Properties();

		tmp.load(is);

		Set<Map.Entry<Object,Object>> mapPropertiesEntry = tmp.entrySet();

		Map<String, String> tmpMap = mapPropertiesEntry.stream().filter(x->x.getKey().toString().startsWith(CONTEXT.COMMON.value)).
				collect(Collectors.toMap(
						x->x.getKey().toString().substring(x.getKey().toString().indexOf(".")+1), 
						x-> x.getValue().toString())
						);
		propsCommon.putAll(tmpMap);

		tmpMap = mapPropertiesEntry.stream().filter(x->x.getKey().toString().startsWith(CONTEXT.CONSUMER.value)).
				collect(Collectors.toMap(
						x->x.getKey().toString().substring(x.getKey().toString().indexOf(".")+1), 
						x-> x.getValue().toString())
						);
		propsConsumer.putAll(tmpMap);

		tmpMap = mapPropertiesEntry.stream().filter(x->x.getKey().toString().startsWith(CONTEXT.PRODUCER.value)).
				collect(Collectors.toMap(
						x->x.getKey().toString().substring(x.getKey().toString().indexOf(".")+1), 
						x-> x.getValue().toString())
						);
		propsProducer.putAll(tmpMap);

		tmpMap = mapPropertiesEntry.stream().filter(x->x.getKey().toString().startsWith(CONTEXT.PIGEON.value)).
				collect(Collectors.toMap(
						x->x.getKey().toString().substring(x.getKey().toString().indexOf(".")+1), 
						x-> x.getValue().toString())
						);
		propsPigeon.putAll(tmpMap);

		tmp.forEach((x,y)->LOG.debug(x + "=" + y));
		tmp=null;

	}
	
	
	public enum CONTEXT{
		COMMON("common"),
		PIGEON("pigeon"),
		PRODUCER("producer"),
		DB("db"),
		CONSUMER("consumer");

		private final String value;

		public String getValue(){return this.value;}

		CONTEXT(String value){this.value=value;}
	}

	public enum KEY{
		TOPIC("kafka.topic"),
		ZOOKEPER_URL("zookeeper.connect"),
		MARIADB_HOST("mariadb.host"),
		MARIADB_PORT("mariadb.port"),
		MARIADB_USER("mariadb.user"),
		MARIADB_PASSSWORD("mariadb.password"),
		ELECTION_QUEUE("election.queue"),
		GROUPID("group.id");

		private final String value;

		public String getValue(){return this.value;}

		KEY(String value){this.value=value;}
	}



}
