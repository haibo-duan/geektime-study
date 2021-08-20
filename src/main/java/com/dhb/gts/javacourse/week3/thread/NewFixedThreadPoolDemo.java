package com.dhb.gts.javacourse.week3.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NewFixedThreadPoolDemo {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(16);
		IntStream.range(0, 100).forEach((i) -> {
			final int no = i;
			executorService.execute(() -> {
				try {
					System.out.println("start:...");
					TimeUnit.SECONDS.sleep(1);
					System.out.println("end:...");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		});

		executorService.shutdown();
		System.out.println("Main Thread End!");
		
	}
}
