package com.dhb.gts.javacourse.week3.thread;

public class RunnerMain {

	public static void main(String[] args) throws Exception{
		Runner1 runner1 = new Runner1();
		Thread thread1 = new Thread(runner1);
		
		Runner2 runner2 = new Runner2();
		Thread thread2 = new Thread(runner2);
		
		thread1.start();
		thread2.start();
		
		thread2.interrupt();
		System.out.println(Thread.activeCount());
		Thread.currentThread().getThreadGroup().list();
		System.out.println(Thread.currentThread().getThreadGroup().getParent().activeGroupCount());
		Thread.currentThread().getThreadGroup().getParent().list();
		
	}
	
	
}

class Runner1 implements Runnable{

	@Override
	public void run() {
		System.out.println("进入Runner1运行状态");
	}
}

class Runner2 implements Runnable{

	@Override
	public void run() {
		System.out.println("进入Runner2运行状态");

		boolean result = Thread.currentThread().isInterrupted();
		boolean result1 = Thread.interrupted();
		boolean result2 = Thread.currentThread().isInterrupted();

		System.out.println("runner2.run result is ["+result+"]");
		System.out.println("runner2.run result1 is ["+result1+"]");
		System.out.println("runner2.run result2 is ["+result2+"]");
	}
}