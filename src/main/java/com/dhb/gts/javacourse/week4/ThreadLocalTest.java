package com.dhb.gts.javacourse.week4;

import java.util.concurrent.TimeUnit;

public class ThreadLocalTest {
	
	private static ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

	public static void main(String[] args) throws Exception{
		threadLocal.set("111");
		Thread t1 = new Thread(() -> {
//			threadLocal.set("本地变量1");
			System.out.println(Thread.currentThread().getName()+":"+threadLocal.get());
		},"thread1");

		Thread t2 = new Thread(() -> {
//			threadLocal.set("本地变量2");
			System.out.println(Thread.currentThread().getName()+":"+threadLocal.get());
		},"thread2");

		t1.start();
		t2.start();
		TimeUnit.SECONDS.sleep(1);
		System.out.println(threadLocal.get());
	}
}
