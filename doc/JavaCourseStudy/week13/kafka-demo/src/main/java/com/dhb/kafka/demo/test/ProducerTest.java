package com.dhb.kafka.demo.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

@Slf4j
public class ProducerTest {

	private static Properties kafkaProps = new Properties();
	
	private static final Producer producer;
	static {
		kafkaProps.put("bootstrap.servers", "192.168.162.201:9092,192.168.162.202:9092,192.168.162.203:9092");
		//使用字符串做为key和value的序列化方式
		kafkaProps.put("key.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		kafkaProps.put("value.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String, String>(kafkaProps);
		//设置适当的key和value的序列化类型到Properties，创建一个生产者实例
	}
	
	public static void sendMsg(){
		
		ProducerRecord<String, String> record =
				new ProducerRecord<>("CustomerCountry", "Precision Products","France");
		try {
			//使用生产者的send方法发送消息。send方法将消息发送到特定的缓冲区，并通过特定的线程发送给broker。send方法返回要给RecordMetadata对象。由于我们没有对这个返回值做处理，因此无法确认是否发送成功。在可以容忍消息丢失的情况下，可以采用此方法发送，但是在生产环节中通常不这么处理。
			producer.send(record);
		} catch (Exception e) {
			//虽然我们互联了在发送消息给broker的过程中broker本身可能产生的错误，
			//但是如果生产者在发送消息给kafka之前遇到错误，我们仍然会得到一个异常。
			//SerializationException在序列化消息失败的时候抛出。
			//如果缓冲区已满，则抛出BufferExhaustedException或者TimeoutException，
			//如果发送线程被中断，则返回InterruptException异常。
			e.printStackTrace();
		}
	}

	public static void sendMsgSync() {
		ProducerRecord<String, String> record =
				new ProducerRecord<>("CustomerCountry", "Precision Products", "France syncc");
		try {
			//我们用Future.get方法来等待kafka的响应。如果消息没有成功发送给kafka,这个方法将抛出一个异常。如果没有异常，我们将获得一个RecordMetadata对象，我们可以用它来获得写入消息的offset。
			producer.send(record).get();
		} catch (Exception e) {
			//在向kafka发送数据之前有任何错误，kafka的broker就会返回一个不可重试的异常，如果我们尝试了最大的重试次数任然没有成功，那么将会遇到一个异常。在本例中，我们捕获了所有的异常并打印。
			e.printStackTrace();
		}
	}

	public static void sendMsgASync() {
		//ProducerRecord 和之前的发送方式一样
		ProducerRecord<String, String> record =
				new ProducerRecord<>("CustomerCountry", "Biomedical Materials", "USA");
		//在发送消息记录的时候，我们将回调的callback对象传递
		producer.send(record, new Callback() {
			@Override
			public void onCompletion(RecordMetadata m, Exception e) {
				System.out.println("********** async callback : "+ m.toString());
				if (e != null) {
					//如果kafka返回了一个非空异常，我们在此捕获并打印
					//在生产环境中会采用更健壮的异常处理方法进行处理
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		sendMsgASync();
	}

}
