package com.dhb.gts.javacourse.week3.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class ThreadMain {

	public static void main(String[] args) {
		ThreadA threadA = new ThreadA();
		threadA.start();
		System.out.println("这是主线程：");

		ThreadB threadB = new ThreadB();
		new Thread(threadB).start();
		System.out.println("这是主线程：");

		ThreadC threadC = new ThreadC();
		FutureTask<String> futureTask = new FutureTask<String>(threadC);
		new Thread(futureTask).start();
		System.out.println("这是主线程:begin!");
		try {
			System.out.println("得到的返回结果是:" + futureTask.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}	
	}
	
	
}

class ThreadA extends Thread{
	@Override
	public void run() {
		super.run();
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("这是线程A");
	}
}

class ThreadB extends Thread{
	@Override
	public void run() {
		super.run();
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("这是线程B");
		
		Thread currentThread = Thread.currentThread();
		String currentThreadName = currentThread.getName();

		System.out.println("这是线程的名称：" + currentThreadName);
		System.out.println("返回当前线程" + currentThreadName + "的线程组中活动线程的数量:" + Thread.currentThread().getThreadGroup().activeCount());
		System.out.println("返回该线程" + currentThreadName + "的标识符:" + currentThread.getId());
		System.out.println("返回该线程" + currentThreadName + "的优先级:" + currentThread.getPriority());
		System.out.println("返回该线程" + currentThreadName + "的状态:" + currentThread.getState());
		System.out.println("返回该线程" + currentThreadName + "所属的线程组:" + currentThread.getThreadGroup());
		System.out.println("测试该线程" + currentThreadName + "是否处于活跃状态:" + currentThread.isAlive());
		System.out.println("测试该线程" + currentThreadName + "是否为守护线程:" + currentThread.isDaemon());
	}
		
}

class ThreadC implements Callable<String> {
	@Override
	public String call() throws Exception {
		Thread.sleep(500);
		System.out.println("这是线程C");
		return "线程C";
	}

}