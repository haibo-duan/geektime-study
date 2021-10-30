package com.dhb.hazelcast.demo.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class HazelcastGetStartClient {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setClusterName("hazelcast-cluster");
		HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
		Map<Integer, String> clusterMap = instance.getMap("map");
//		Queue<String> clusterQueue = instance.getQueue("MyQueue");

		log.info(instance.getConfig().toString());
		log.info("Map Value:" + clusterMap.toString());
		log.info("Map Size :" + clusterMap.size());
		instance.shutdown();
	}
}
