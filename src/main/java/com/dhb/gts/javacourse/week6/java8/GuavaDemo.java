package com.dhb.gts.javacourse.week6.java8;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

public class GuavaDemo {

	static EventBus bus = new EventBus();

	static {
		bus.register(new GuavaDemo());
	}

	@SneakyThrows
	public static void main(String[] args) {
		List<String> lists = testStirng();
		List<Integer> list = testList();
		testMap(list);
		testBiMap(lists);
		testEventBus();
	}

	private static void testEventBus() {
		Student student2 = new Student(2, "KK02");
		System.out.println(Thread.currentThread().getName() + " I want " + student2 + " run now.");
		bus.post(new AEvent(student2));
	}

	private static void testBiMap(List<String> lists) {
		BiMap<String, Integer> words = HashBiMap.create();
		words.put("First", 1);
		words.put("Second", 2);
		words.put("Third", 3);

		System.out.println(words.get("Second").intValue());
		System.out.println(words.inverse().get(3));

		Map<String, String> map1 = Maps.toMap(lists.listIterator(), a -> a + "-value");
		print(map1);
	}

	public static void testMap(List<Integer> list) {
		Multimap<Integer, Integer> buMultimap = ArrayListMultimap.create();
		list.forEach(a -> buMultimap.put(a, a + 1));
		print(buMultimap);
	}

	private static List<Integer> testList() {
		List<Integer> list = Lists.newArrayList(4, 2, 4, 3, 5, 1, 2, 6, 7);
		List<List<Integer>> list1 = Lists.partition(list, 3);
		print(list1);
		return list;
	}

	private static List<String> testStirng() {
		List<String> lists = Lists.newArrayList("a", "v", "g", "d", "d", "9");
		String result = Joiner.on(",").join(lists);
		System.out.println(result);

		String test = "343535,,,343535,fdgr,eg,sds";
		lists = Splitter.on(",").splitToList(test);
		System.out.println(lists);
		return lists;
	}

	private static void print(Object obj) {
		System.out.println(JSON.toJSONString(obj));
	}

	public void handle(AEvent aEvent) {
		System.out.println(Thread.currentThread().getName() + " " + aEvent.student + "is running..");
	}

	@Data
	@AllArgsConstructor
	public static class AEvent {
		private Student student;
	}
}
