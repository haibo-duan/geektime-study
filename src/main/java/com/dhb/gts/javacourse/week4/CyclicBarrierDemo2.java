package com.dhb.gts.javacourse.week4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CyclicBarrierDemo2 {

	public static void main(String[] args) throws Exception{
		int N = 4;
		CyclicBarrier cyc = new CyclicBarrier(N);

		IntStream.range(0,N).forEach(i -> new Writer(cyc).start());
		TimeUnit.SECONDS.sleep(3);
		System.out.println("CyclicBarrier重用！");
//		cyc.reset();
		IntStream.range(0,N).forEach(i -> new Writer(cyc).start());
	}
	
	static class Writer extends Thread {
		private CyclicBarrier cyclicBarrier;
		public Writer(CyclicBarrier cyclicBarrier) {
			this.cyclicBarrier = cyclicBarrier;
		}

		@Override
		public void run() {
			try {
				System.out.println("线程"+Thread.currentThread().getName()+" 正在写入数据。。。");
				TimeUnit.SECONDS.sleep(1);
				System.out.println("线程"+Thread.currentThread().getName()+"写入数据完毕，等待其他线程写入完毕！");
				cyclicBarrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"所有线程写入完毕，继续处理其他任务。。");
		}
	}
}
