package com.dhb.gts.javacourse.week3.thread;

import java.util.stream.IntStream;

public class Counter {
	
	private int sum = 0;
	private final Object lock = new Object();
	
	public void incr() {
		synchronized (lock) {
			sum = sum + 1;
		}
	}
	
	public int getSum() {
		return sum;
	}

	public static void main(String[] args) throws Exception{
		int loop = 100_000;
		
		Counter counter = new Counter();
		IntStream.range(0,loop).forEach((i) -> {
			counter.incr();
		});

		System.out.println("single thread："+counter.getSum());
		
		final Counter counter2 = new Counter();
		Thread t1 = new Thread(() -> {
			IntStream.range(0,loop/2).forEach((i) ->{
				counter2.incr();
			});
		});
		Thread t2 = new Thread(() -> {
			IntStream.range(0,loop/2).forEach(i -> {
				counter2.incr();
			});
		});
		
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println("multiple threads:"+counter2.getSum());
	}
}
