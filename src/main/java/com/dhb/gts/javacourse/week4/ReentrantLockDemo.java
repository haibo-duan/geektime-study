package com.dhb.gts.javacourse.week4;

import java.util.stream.IntStream;

public class ReentrantLockDemo {

	public static void main(String[] args) {
		final Count count = new Count();

		IntStream.range(0,2).forEach(i -> {
			new Thread(() -> {
				count.get();
			}).start();
		});

		IntStream.range(0,2).forEach(i -> {
			new Thread(() -> {
				count.put();
			}).start();
		});
	}
}
