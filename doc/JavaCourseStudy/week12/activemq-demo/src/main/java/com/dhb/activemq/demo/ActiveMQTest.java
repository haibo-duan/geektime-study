package com.dhb.activemq.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class ActiveMQTest {

	public static void main(String[] args) {
		Destination destination = new ActiveMQTopic("test.topic");
		testDestination(destination);
	}
	
	public static void testDestination(Destination destination) {
		try {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
			ActiveMQConnection conn = (ActiveMQConnection) factory.createConnection();
			conn.start();
			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// 创建消费者
			MessageConsumer consumer = session.createConsumer( destination );
			final AtomicInteger count = new AtomicInteger(0);
			MessageListener listener = new MessageListener() {
				@Override
				public void onMessage(Message message) {
					try {
						// 打印所有的消息内容
						// Thread.sleep();
						log.info(count.incrementAndGet() + " => receive from " + destination.toString() + ": " + message);
						// message.acknowledge(); // 前面所有未被确认的消息全部都确认。

					} catch (Exception e) {
						e.printStackTrace(); // 不要吞任何这里的异常，
					}
				}
			};
			// 绑定消息监听器
			consumer.setMessageListener(listener);
			
			// 创建生产者，生产100个消息
			MessageProducer producer = session.createProducer(destination);
			int index = 0;
			while (index++ < 100) {
				TextMessage message = session.createTextMessage(index + " message.");
				producer.send(message);
			}

			Thread.sleep(20000);
			session.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
