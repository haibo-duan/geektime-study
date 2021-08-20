package com.dhb.gts.javacourse.week3.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NewScheduledThreadExecutorDemo {

	public static void main(String[] args) {
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(16);

		IntStream.range(0,100).forEach((i)-> {
			final int no = i;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("start:"+no);
						TimeUnit.SECONDS.sleep(1);
						System.out.println("end:"+no);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			executorService.schedule(runnable,10,TimeUnit.SECONDS);
		});
		executorService.shutdown();
		System.out.println("Main Thread end!");
	}
}
