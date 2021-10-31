package com.dhb.hazelcast.demo.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
public class BuildTestData {
	public static final int count = 1000000;

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setClusterName("hazelcast-cluster");
		HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
		Map<Integer, String> clusterMap = instance.getMap("map");

		log.info("begin put data ...");
		for(int i=0;i<count;i++){
			clusterMap.put(i,i+"");
		}
		log.info("end put data ...");
		instance.shutdown();
	}
	
	
}
