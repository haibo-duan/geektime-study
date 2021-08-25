package com.dhb.gts.javacourse.week4;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 通过ArrayList实现
 */
public class AsyncRun16 {

	private static final ArrayList<Integer> list = new ArrayList<>();

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
		while (list.isEmpty()) {
			TimeUnit.MILLISECONDS.sleep(1);
		}
		Integer result = list.get(0);
		System.out.println("异步计算结果：" + result);
		System.out.println("计算耗时：" + (System.currentTimeMillis() - start) + "  ms");
	}

	static class SumThread extends Thread {
		
		@Override
		public void run() {
			try {
				int result = sum();
				list.add(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
