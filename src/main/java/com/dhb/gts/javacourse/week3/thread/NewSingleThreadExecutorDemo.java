package com.dhb.gts.javacourse.week3.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NewSingleThreadExecutorDemo {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		IntStream.range(0,10).forEach((i) -> {
			final int no = i;
			executorService.execute(() -> {
				try {
					System.out.println("start:"+no);
					TimeUnit.SECONDS.sleep(1);
					System.out.println("end:"+no);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		});
		executorService.shutdown();
		System.out.println("main Thread End!");
	}
}
