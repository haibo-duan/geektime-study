package com.dhb.gts.javacourse.week4;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class CountDownLatchDemo {

	public static void main(String[] args) throws Exception{
		CountDownLatch countDownLatch = new CountDownLatch(5);
		IntStream.range(0,5).forEach(i ->
				new Thread(new ReadNum(i,countDownLatch)).start());
		countDownLatch.await();
		System.out.println("各个子线程任务结束。。");
		System.out.println("主线程任务结束。。。");
	}
	
	static class ReadNum implements Runnable {
		
		private int id;
		private CountDownLatch latch;

		public ReadNum(int id, CountDownLatch latch) {
			this.id = id;
			this.latch = latch;
		}

		@Override
		public void run() {
			synchronized (this) {
				System.out.println("id["+id+"], Thread name is ["+Thread.currentThread().getName()+"]");
				latch.countDown();
				System.out.println("线程组任务["+id+"]结束，其他任务继续。");
				
			}
		}
	}
}
