package com.dhb.gts.javacourse.week4;

import java.util.concurrent.Phaser;

/**
 * 通过Phaser 来实现
 */
public class AsyncRun12 {

	private static final Phaser phaser = new Phaser(2);

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
		phaser.arriveAndAwaitAdvance();

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
				phaser.arriveAndAwaitAdvance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
