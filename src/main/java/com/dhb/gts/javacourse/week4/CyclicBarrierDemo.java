package com.dhb.gts.javacourse.week4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CyclicBarrierDemo {

	public static void main(String[] args) throws Exception{
		CyclicBarrier cyclicBarrier = new CyclicBarrier(5, new Runnable() {
			@Override
			public void run() {
				System.out.println("回调>>"+Thread.currentThread().getName());
				System.out.println("回调>>线程组执行结束");
				System.out.println("==>各个子线程执行结束。");
			}
		});
		IntStream.range(0,5).forEach(i -> {
			new Thread(new ReadNum(i,cyclicBarrier)).start();
		});
		System.out.println("主线程执行完毕！");
		cyclicBarrier.reset();
		TimeUnit.MILLISECONDS.sleep(500);
		//CyclicBarrier可以重复利用 这是CountDownlatch无法做到的
		IntStream.range(10,15).forEach(i -> {
			new Thread(new ReadNum(i,cyclicBarrier)).start();
		});
		System.out.println("再次运行执行完毕！");
		
	}
	
	static class ReadNum implements Runnable{
		private int id;
		private CyclicBarrier cyc;

		public ReadNum(int id, CyclicBarrier cyc) {
			this.id = id;
			this.cyc = cyc;
		}

		@Override
		public void run() {
			synchronized (this){
				try {
					System.out.println("id ["+id+"], thread name is ["+Thread.currentThread().getName()+"]");
					cyc.await();
					System.out.println("线程组任务["+id+"] 结束，其他任务继续！");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
