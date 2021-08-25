package com.dhb.gts.javacourse.week4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 通过CyclicBarrier 实现
 */
public class AsyncRun10 {

	private static final CyclicBarrier barrier = new CyclicBarrier(2);

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
		barrier.await();

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
			try {
				result = sum();
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
}
