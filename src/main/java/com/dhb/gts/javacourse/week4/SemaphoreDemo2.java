package com.dhb.gts.javacourse.week4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SemaphoreDemo2 {
	
	private final static int threadCount = 20;

	public static void main(String[] args) {
		ExecutorService exec = Executors.newCachedThreadPool();
		
		final Semaphore semaphore = new Semaphore(5);

		IntStream.range(0,threadCount).forEach((i) -> {
			final int threadNum = i;
			exec.execute(() -> {
				try {
					semaphore.acquire(3);
					test(threadNum);
					semaphore.release(3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		
		exec.shutdown();
	}
	
	private static void test(int threadNum) throws Exception {
		System.out.println("id "+threadNum+","+Thread.currentThread().getName());
		TimeUnit.SECONDS.sleep(1);
	}
}
