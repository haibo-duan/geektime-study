package com.dhb.gts.javacourse.week4;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompleteFutureDemo {

	public static void main(String[] args) throws Exception{
		CompletableFuture<String> future1 = new CompletableFuture<>();
		ExecutorService cachePool = Executors.newCachedThreadPool();
		cachePool.execute(() -> {
			try {
				TimeUnit.SECONDS.sleep(3);
				future1.complete("异步执行结果");
				System.out.println(Thread.currentThread().getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		//whenComplete方法 返回的仍然是原来的future
		CompletableFuture<String> future2 = future1.whenComplete((s,throwable) -> {
			System.out.println("当异步任务执行完毕之后，打印异步任务的执行结果："+s);
		});
		//thenApply 返回的是一个全新的future
		CompletableFuture<Integer> future3 = future2.thenApply(s -> {
			try {
				System.out.println("当前异步任务结束时，从上一次异步任务的结果，继续开始一个新的异步任务！");
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return s.length();
		});

		System.out.println("阻塞方式执行结果："+future3.get());
		cachePool.shutdown();
		
	}
}
