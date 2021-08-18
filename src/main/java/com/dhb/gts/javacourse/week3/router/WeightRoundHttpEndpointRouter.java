package com.dhb.gts.javacourse.week3.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class WeightRoundHttpEndpointRouter implements HttpEndpointRouter{

	private static final Pattern pattern = Pattern.compile("^- server[\\d]{1,2},[\\d]{1,4} ?$");
	private static final Map<Integer,Integer> weightMap = new HashMap<>();
	private static Map<Integer, AtomicInteger> current = new HashMap<>();
	private static final Random random = new Random();
	
	public WeightRoundHttpEndpointRouter(List<String> weights) {
		
		for(String str:weights) {
			Matcher m = pattern.matcher(str);
			if(m.matches()) {
				String[] servers = str.replaceAll("- server","").trim().split(",");
				int index = Integer.parseInt(servers[0]) - 1;
				int weight = Integer.parseInt(servers[1]);
				weightMap.put(index,weight);
			}
		}
	}
	
	private void resetCurrent() {
		current.clear();
		for(Map.Entry<Integer,Integer> entry : weightMap.entrySet()){
			current.put(entry.getKey(),new AtomicInteger(entry.getValue()));
		}
	}

	@Override
	public String route(List<String> endpoints) {
		String result = null;
		if(current.size()<= 0 ){
			resetCurrent();
		}
		while (result == null) {
			int index = random.nextInt(current.size());
			if (current.get(index).getAndDecrement() > 0) {
				result = endpoints.get(index);
			}
			for (Map.Entry<Integer, AtomicInteger> entry : current.entrySet()) {
				if (entry.getKey() > endpoints.size() || entry.getValue().get() <= 0) {
					current.remove(entry);
				}
			}
		}
		return result;
	}

	public static void main(String[] args) {
		List<String> weights = new ArrayList<>();
		weights.add("- server01,50");
		weights.add("- server02,20");
		weights.add("- server03,30");
		WeightRoundHttpEndpointRouter router = new WeightRoundHttpEndpointRouter(weights);
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		IntStream.range(1, 100).forEach((i) -> {
			System.out.println(router.route(list));
		});
	}
}
