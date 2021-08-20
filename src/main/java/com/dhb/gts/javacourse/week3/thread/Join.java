package com.dhb.gts.javacourse.week3.thread;

import java.util.stream.IntStream;

public class Join {

	public static void main(String[] args) {
		Object obj = new Object();
		MyThread thread1 = new MyThread("thread1----");
		
		thread1.setObj(obj);
		thread1.start();
		
		synchronized (obj) {
			IntStream.range(0,100).forEach((i) -> {
				if(i == 20) {
					try {
//						obj.wait();
						thread1.join();
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(Thread.currentThread().getName()+"---"+i);
			});
		}
	}
}

class MyThread extends Thread {
	private  String name;
	private Object obj;
	

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	public MyThread(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		synchronized (obj) {
			IntStream.range(0,100).forEach((i) ->{
				System.out.println(name + i);
			});
		}
	}
}
