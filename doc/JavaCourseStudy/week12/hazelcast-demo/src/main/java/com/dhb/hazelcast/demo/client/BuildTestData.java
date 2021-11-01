package com.dhb.hazelcast.demo.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Map;

@Slf4j
public class BuildTestData {
	public static final int count = 1000000;

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setClusterName("hazelcast-cluster");
		HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
		Map<Integer, String> clusterMap = instance.getMap("map");
		StopWatch stopWatch = new StopWatch();
		log.info("begin put data ...");
		int k = 0;
		stopWatch.start(k + "");
		for (int i = 0; i < count; i++) {
			clusterMap.put(i, i + "");
			int j = i / 10000;
			if (j > 0 && j > k) {
				k = j;
				stopWatch.stop();
				stopWatch.start(k + "");
				log.info("已插入数据 {} 万条。", k);
			}
		}
		stopWatch.stop();
		log.info("end put data ... {}", stopWatch.prettyPrint());
		instance.shutdown();
	}
}
