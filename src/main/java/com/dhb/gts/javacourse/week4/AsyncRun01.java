package com.dhb.gts.javacourse.week4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncRun01 {

	public static void main(String[] args) throws Exception{
		long start = System.currentTimeMillis();

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		Callable<Integer> task = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return sum();
			}
		};
		Future<Integer> future = executorService.submit(task);
		System.out.println("异步计算结果："+future.get());
		System.out.println("计算耗时："+(System.currentTimeMillis() - start) +"  ms");
		executorService.shutdown();
		
	}
	
	

	private static int sum() {
		return fibo(36);
	}

	private static int fibo(int a) {
		if(a<2){
			return 1;
		}
		return fibo(a-1) + fibo(a-2);
	}

}
