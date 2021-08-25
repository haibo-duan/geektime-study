package com.dhb.gts.javacourse.week4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 采用 FutureTask实现
 */
public class AsyncRun17 {


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
		ExecutorService service = Executors.newCachedThreadPool();

		SumThread sumThread = new SumThread();
		FutureTask<Integer> futureTask = new FutureTask<>(sumThread);
		service.submit(futureTask);
		service.shutdown();
		System.out.println("异步计算结果：" + futureTask.get());
		System.out.println("计算耗时：" + (System.currentTimeMillis() - start) + "  ms");
	}

	static class SumThread implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			return sum();
		}
	}
}
