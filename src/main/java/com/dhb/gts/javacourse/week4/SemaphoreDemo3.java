package com.dhb.gts.javacourse.week4;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SemaphoreDemo3 {

	public static void main(String[] args) {
		IntStream.range(0,10).forEach(i -> {
			//生产者
			new Thread(new Producer()).start();
			//消费者
			new Thread(new Consumer()).start();
		});
	}
	
	static WareHouse buffer = new WareHouse();
	
	static class Producer implements Runnable {

		static int num = 1;
		
		@Override
		public void run() {
			int n = num ++;
			while (true) {
				try {
					buffer.put(n);
					System.out.println(">"+n);
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static class Consumer implements Runnable {

		
		@Override
		public void run() {
			while (true) {
				try {
					System.out.println("<"+buffer.take());
					//速度较慢
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	static class WareHouse{
		//非满锁
		final Semaphore notFull = new Semaphore(10);
		//非空锁
		final Semaphore notEmpty = new Semaphore(0);
		//核心锁
		final Semaphore mutex = new Semaphore(1);
		//库存容量
		final Object[] items = new Object[10];
		
		int putptr,takeptr,count;

		/**
		 * 放库存
		 * @param obj
		 * @throws InterruptedException
		 */
		public void put(Object obj ) throws InterruptedException {
			notFull.acquire();
			mutex.acquire();
			items[putptr] = obj;
			try{
				if(++putptr == items.length) {
					putptr = 0;
					++ count;
				}
			}finally {
				mutex.release();
				notEmpty.release();
			}
		}


		public Object take() throws InterruptedException{
			notEmpty.acquire();
			mutex.acquire();
			Object obj = items[takeptr];
			
			try {
				if(++takeptr == items.length) {
					takeptr = 0;
					-- count;
				}
				return obj;
			}finally {
				mutex.release();
				notFull.release();
			}
		}
	}

}
