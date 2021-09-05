package com.dhb.gts.javacourse.week6.java8;

import java.util.Arrays;
import java.util.List;

public class ForeachDemo {
	
	private int x = 1;

	public static void main(String[] args) {
		ForeachDemo foreachDemo = new ForeachDemo();
		foreachDemo.test();
		System.out.println(foreachDemo.x);
	}
	
	private void test() {
		List list = Arrays.asList(1,2);
		int y = 1;
		list.forEach(e -> {
			x = 2;
		});
	}
}
 