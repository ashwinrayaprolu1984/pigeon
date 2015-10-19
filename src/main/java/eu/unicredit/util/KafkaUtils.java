package eu.unicredit.util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.api.PartitionFetchInfo;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchRequest;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import kafka.network.BlockingChannel;
import kafka.network.Receive;

public class KafkaUtils {

	public static void main(String[] args) {
		
		KafkaUtils ku = new KafkaUtils();
		PartitionMetadata pm = ku.findLeader("10.124.56.154:2181", "redolog2", 0);
		long offset = ku.getLogSize(pm.leader().host(),pm.leader().port(),"redolog2",0);
		ku.readAtOffSet(pm.leader().host(),pm.leader().port(),"redolog2",offset-1,0);
		
	}


	private ZkClient createZKClient(){
		return   new ZkClient("10.124.56.152:2181", 3000,30000, new ZkSerializer() {

			@Override
			public byte[] serialize(Object arg0) throws ZkMarshallingError {
				if(arg0 instanceof String)
					try {
						return ((String)arg0).getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

				return null;
			}

			@Override
			public Object deserialize(byte[] bytes) throws ZkMarshallingError {
				if (bytes == null) return 
						null;
				else
					try {
						return new String(bytes, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				return null;
			}
		} );
	}
	public void getWithChannel(){
		ZkClient zkClient =   new ZkClient("10.124.56.152:2181", 3000,30000, new ZkSerializer() {

			@Override
			public byte[] serialize(Object arg0) throws ZkMarshallingError {
				if(arg0 instanceof String)
					try {
						return ((String)arg0).getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				return null;
			}

			@Override
			public Object deserialize(byte[] bytes) throws ZkMarshallingError {
				if (bytes == null) return 
						null;
				else
					try {
						return new String(bytes, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				return null;
			}
		} );
		BlockingChannel channel = kafka.client.ClientUtils.channelToOffsetManager("cc", zkClient, 3000, 3000);
		TopicAndPartition topicPartition=new TopicAndPartition("redolog2", 0);
		List<TopicAndPartition> ll = new ArrayList<>();
		ll.add(topicPartition);

		List<String> listaTopics=new ArrayList<>();
		listaTopics.add("redolog");
		TopicMetadataRequest tt = new TopicMetadataRequest(listaTopics);
		//		FetchRequest tt = new FetchRequest(0, "a", 500,2096, m);

		channel.send(tt.underlying());

		Receive response=channel.receive();


		ByteBuffer b = ByteBuffer.wrap(response.buffer().array());
		b.get();
		b.getInt();
		byte[] bb=new byte[b.remaining()];
		b.get(bb);
		System.out.println(new String(bb));

	}

	public List<String> getBrokerList(String zookeperConnect) throws IOException, KeeperException, InterruptedException{
		List<String> broker=new ArrayList<String>();

		ObjectMapper mapper = new ObjectMapper();
		ZooKeeper zk = new ZooKeeper(zookeperConnect, 10000, null);
		List<String> ids = zk.getChildren("/brokers/ids", false);
		ids.forEach((t) -> {
			String brokerInfo;
			try {
				brokerInfo = new String(zk.getData("/brokers/ids/" + t, false, null));
				Map<Object,Object> mappa= mapper.readValue(brokerInfo.getBytes(), Map.class);
				System.out.println(mappa.get("host")+":"+mappa.get("port"));
				broker.add(mappa.get("host")+":"+mappa.get("port"));
			} catch (KeeperException | InterruptedException | IOException e) {
				e.printStackTrace();
			}

		});

		return broker;
	}

	//	public void last(){
	//		Properties props = new Properties();
	//		props.put("metadata.broker.list", "10.124.56.152:9092");
	//		props.put("bootstrap.servers", "10.124.56.152:9092");
	//		props.put("group.id", "main");
	//		//		 props.put("session.timeout.ms", "8000");
	//		props.put("enable.auto.commit", "false");
	//		props.put("key.deserializer",				org.apache.kafka.common.serialization.StringDeserializer.class);
	//		props.put("value.deserializer",				org.apache.kafka.common.serialization.StringDeserializer.class);
	//		props.put("partition.assignment.strategy", "roundrobin");
	//		KafkaConsumer consumer = new KafkaConsumer(props);
	//
	//		consumer.subscribe("redolog");
	//		int commitInterval = 100;
	//		int numRecords = 0;
	//		boolean isRunning = true;
	//		Map<TopicPartition, Long> consumedOffsets = new HashMap<TopicPartition, Long>();
	//
	//		//		 while(isRunning) {
	//		Map<String, ConsumerRecords> records = consumer.poll(100);
	//		try {
	//			System.out.println("Record size " + records.size());
	//			Map<TopicPartition, Long> lastConsumedOffsets = process(records);
	//			consumedOffsets.putAll(lastConsumedOffsets);
	//			numRecords += records.size();
	//			// commit offsets for all partitions of topics foo, bar synchronously, owned by this consumer instance
	//			//		         if(numRecords % commitInterval == 0) 
	//			//		           consumer.commit(false);
	//		} catch(Exception e) {
	//			e.printStackTrace();
	//			//		         try {
	//			//		             // rewind consumer's offsets for failed partitions
	//			//		             // assume failedPartitions() returns the list of partitions for which the processing of the last batch of messages failed
	//			//		             List<TopicPartition> failedPartitions = failedPartitions();   
	//			//		             Map<TopicPartition, Long> offsetsToRewindTo = new HashMap<TopicPartition, Long>();
	//			//		             for(TopicPartition failedPartition : failedPartitions) {
	//			//		                 // rewind to the last consumed offset for the failed partition. Since process() failed for this partition, the consumed offset
	//			//		                 // should still be pointing to the last successfully processed offset and hence is the right offset to rewind consumption to.
	//			//		                 offsetsToRewindTo.put(failedPartition, consumedOffsets.get(failedPartition));
	//			//		             }
	//			//		             // seek to new offsets only for partitions that failed the last process()
	//			//		             consumer.seek(offsetsToRewindTo);
	//			//		         } catch(Exception ee) { ee.printStackTrace(); } // rewind failed
	//		}
	//		//		 }         
	//		consumer.close();
	//	}
	//
	//
	//	private Map<TopicPartition, Long> process(Map<String, ConsumerRecords> records) {
	//		Map<TopicPartition, Long> processedOffsets = new HashMap<TopicPartition, Long>();
	//		for(Entry<String, ConsumerRecords> recordMetadata : records.entrySet()) {
	//			List<ConsumerRecord> recordsPerTopic = recordMetadata.getValue().records();
	//			for(int i = 0;i < recordsPerTopic.size();i++) {
	//				ConsumerRecord record = recordsPerTopic.get(i);
	//				// process record
	//				try {
	//					System.out.println(record.offset());
	//					processedOffsets.put(record.topicAndPartition(), record.offset());
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}               
	//			}
	//		}
	//		return processedOffsets; 
	//	}


	public PartitionMetadata findLeader(String zookeperConnect, String topic, int partition) {
		List<String> brokerList;
		try {
			brokerList = this.getBrokerList(zookeperConnect);
		} catch (IOException | KeeperException | InterruptedException e1) {
			throw new RuntimeException(e1);
		}

		for (String broker : brokerList) {
			SimpleConsumer consumer = null;
			try {
				StringTokenizer token=new StringTokenizer(broker, ":");
				consumer = new SimpleConsumer(token.nextToken(), Integer.parseInt(token.nextToken()), 100000, 64 * 1024, "leaderLookup");

				List<String> topics = Arrays.asList(topic) ;

				TopicMetadataRequest req = new TopicMetadataRequest(topics);
				kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

				List<TopicMetadata> metaData = resp.topicsMetadata();

				for (TopicMetadata item : metaData) {
					for (PartitionMetadata part : item.partitionsMetadata()) {
						if (part.partitionId() == partition) {
							return part;
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Error communicating with Broker [" + broker + "] to find Leader for [" + topic
						+ ", " + partition + "] Reason: " + e);
			} finally {
				if (consumer != null) consumer.close();
			}
		}
		return null;
	}

	//	public void getTopicInfo(String topic,int partition){
	//
	//		SimpleConsumer sc = new SimpleConsumer("10.124.56.154", 9092, 500, 4096, "a");
	//		TopicAndPartition tp = new TopicAndPartition(topic, partition);
	//		//		PartitionFetchInfo pf = new PartitionFetchInfo(0, 1000);
	//
	//		List<String> lstTopic=new ArrayList<String>();
	//		lstTopic.add("redolog2");
	//		
	//		ZkClient zkClient = createZKClient();
	//		Map<String,Seq<Object>> map= ZkUtils.getPartitionsForTopics(zkClient,
	//				scala.collection.JavaConversions.asScalaBuffer(lstTopic).toSeq()
	//				
	//		);
	//		
	//		
	//		Map<TopicAndPartition, PartitionFetchInfo> m=new HashMap<>();
	//
	//		FetchRequest rf = new FetchRequest(0, "a", 500,2096, m);
	//		FetchResponse fetchResponse =sc.fetch(rf);
	//
	//		
	//
	//		Map<TopicAndPartition, PartitionOffsetRequestInfo> ii=new HashMap<>();
	//		ii.put(tp,new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1));
	//		OffsetResponse response =sc.getOffsetsBefore(new OffsetRequest(ii,kafka.api.OffsetRequest.CurrentVersion(),"a"));
	//
	//		long[] offsets = response.offsets("redolog2", partition);
	//
	//		if (response.hasError()) {
	//			System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode("redolog2", partition) );
	//			System.exit(1);
	//		}
	//
	//		System.out.println(offsets[0]);
	//		sc.close();
	//	}
	//
	//	
	public long getLogSize(String leaderHost,int leaderPort,String topic,int partition){

		//		PartitionMetadata pm= findLeader(zookeeperConnect, topic, partition);
		SimpleConsumer sc = new SimpleConsumer(leaderHost, leaderPort, 500, 4096, "checkLogSize");
		try{
			TopicAndPartition tp = new TopicAndPartition(topic, partition);

			Map<TopicAndPartition, PartitionFetchInfo> m=new HashMap<>();

			FetchRequest rf = new FetchRequest(0, "checkLogSize", 500,2096, m);
			FetchResponse fetchResponse =sc.fetch(rf);

			Map<TopicAndPartition, PartitionOffsetRequestInfo> ii=new HashMap<>();
			ii.put(tp,new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1));
			OffsetResponse response =sc.getOffsetsBefore(new OffsetRequest(ii,kafka.api.OffsetRequest.CurrentVersion(),"a"));

			long[] offsets = response.offsets(topic, partition);

			if (response.hasError()) {
				System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition) );
				System.exit(1);
			}

			return offsets[0];
		}finally{
			sc.close();
		}
	}

	//	
	//	
	//
	public byte[] readAtOffSet(String leaderHost,int leaderPort,String topic,long offset,int partition){


		SimpleConsumer sc = new SimpleConsumer(leaderHost, leaderPort, 500, 4096, "readAtOffSet");
		TopicAndPartition tp = new TopicAndPartition(topic, 0);
		//		PartitionFetchInfo pf = new PartitionFetchInfo(0, 1000);

		Map<TopicAndPartition, PartitionFetchInfo> m=new HashMap<>();
		PartitionFetchInfo pf = new PartitionFetchInfo(offset, 16777216);
		m.put(tp, pf);

		FetchRequest rf = new FetchRequest(0, "readAtOffSet", 100,1, m);
		FetchResponse fetchResponse =sc.fetch(rf);



		ByteBufferMessageSet bms= fetchResponse.messageSet(topic, 0);
		for (MessageAndOffset messageAndOffset : bms) {
			ByteBuffer bf = messageAndOffset.message().payload();
			bf.get();
			bf.getInt();
			
			int pos = bf.position();
			int len = bf.limit() - pos;
			byte[] b = new byte[len];
			bf.get(b, 0, len);


			return b;
		}
		return null;



	}

}
