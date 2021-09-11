package com.dhb.gts.javacourse.week6.java8;

import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StreamDemo {

	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(4,2,3,6,7,8,4,5,6);
		print(list);

		Optional<Integer> first = list.stream().findFirst();
		System.out.println(first.map(i -> i*100).orElse(100));
		
		int sum = list.stream().filter(i -> i<4).distinct().reduce(1,(a,b)->a+b);
		System.out.println("sum="+sum);
		
		int muti = list.stream().filter(i -> i<4).distinct().reduce(1,(a,b)->a*b);
		System.out.println("mutil="+muti);

		Map<Integer,Integer> map = list.stream().parallel().collect(Collectors.toMap(a->a,a->a+1,(a,b)-> a, LinkedHashMap::new));
		print(map);
		
		map.forEach((k,v)-> System.out.println("key:value="+k+":"+v));
		List<Integer> list1 = map.entrySet().parallelStream().map(e->e.getKey()+e.getValue()).collect(Collectors.toList());
		print(list1);
	}

	private static void print(Object obj) {
		System.out.println(JSON.toJSONString(obj));
	}
}
