package com.dhb.gts.javacourse.week3.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NewCachedThreadPoolDemo {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		IntStream.range(0,10000).forEach((i)-> {
			final int no = i;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("start..");
						TimeUnit.SECONDS.sleep(1);
						System.out.println("end...");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			};
			executorService.execute(runnable);
		});
		executorService.shutdown();
		System.out.println("Main Thread End!");
	}
	
}
