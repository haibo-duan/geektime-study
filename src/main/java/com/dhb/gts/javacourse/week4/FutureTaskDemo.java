package com.dhb.gts.javacourse.week4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class FutureTaskDemo {

	public static void main(String[] args) throws Exception{
		ExecutorService service = Executors.newCachedThreadPool();
		Task task = new Task();
		FutureTask<Integer> futureTask = new FutureTask<>(task);
		service.submit(futureTask);
		service.shutdown();
		System.out.println("主线程正在执行任务。。");
		TimeUnit.SECONDS.sleep(1);
		System.out.println("-------------------");
		System.out.println("执行结果："+futureTask.get());
		System.out.println("任务执行完成！");
	}
	
	static class  Task implements Callable<Integer> {
		@Override
		public Integer call() throws Exception {
			try {
				System.out.println("子线程正在执行。。。");
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int num = 0;
			for(int i=0;i<=100;i++) {
				num += i;
			}
			return num;
		}
	}
}
