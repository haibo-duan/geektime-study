package com.dhb.gts.javacourse.week4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CountDownLatchDemo2 {
	
	public static final int threadCount = 200;

	public static void main(String[] args) throws Exception{
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		IntStream.range(0,200).forEach((i)-> {
			final int threadnum = i;
			executorService.execute(() -> {
				try {
					test(threadnum);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			});
		});
		
		countDownLatch.await();
		System.out.println("所有程序员都完成任务，项目顺利上线！");

		executorService.shutdown();
		
	}
	
	private static void test(int threadNum) throws Exception {
		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println(String.format("程序员[%d]完成任务。。",threadNum));
		TimeUnit.MILLISECONDS.sleep(100);
	}
}
