package com.dhb.gts.javacourse.week4;

import java.util.concurrent.CountDownLatch;

/**
 * 使用CountDownLatch来实现
 */
public class AsyncRun06 {

	private static final CountDownLatch latch = new CountDownLatch(1);

	private static int sum() {
		return fibo(36);
	}

	private static int fibo(int a) {
		if (a < 2) {
			return 1;
		}
		return fibo(a - 1) + fibo(a - 2);
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		SumThread sumThread = new SumThread();

		sumThread.start();
		latch.await();

		int result = sumThread.getResult();
		System.out.println("异步计算结果：" + result);
		System.out.println("计算耗时：" + (System.currentTimeMillis() - start) + "  ms");
	}

	static class SumThread extends Thread {

		private Integer result;

		public Integer getResult() {
			return result;
		}

		@Override
		public void run() {
			result = sum();
			latch.countDown();
		}
	}

}
