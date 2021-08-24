package com.dhb.gts.javacourse.week4;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SemaphoreDemo {

	public static void main(String[] args) {
		int N = 8;
		Semaphore semaphore = new Semaphore(2);
		IntStream.range(0,N).forEach(i -> new Worker(i,semaphore).start());
	}
	
	static class Worker extends Thread {
		
		private int num;
		private Semaphore semaphore;
		
		public Worker(int num,Semaphore semaphore) {
			this.num = num;
			this.semaphore = semaphore;
		}
		
		@Override
		public void run() {
			try {
				semaphore.acquire();
				System.out.println("工人["+this.num + "]占用一个机器开始生产！");
				TimeUnit.SECONDS.sleep(2);
				System.out.println("工人["+this.num+"]释放机器。");
				semaphore.release();
			}catch (Exception e){
				e.printStackTrace();
			}
		
		}
	}
}
