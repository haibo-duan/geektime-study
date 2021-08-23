package com.dhb.gts.javacourse.week4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

public class AsyncRun07 {
	
	
	private static int sum() {
		return fibo(36);
	}

	private static int fibo(int a) {
		if(a<2){
			return 1;
		}
		return fibo(a-1) + fibo(a-2);
	}

	static class SumThread extends Thread{
		
		private Thread mainThread;

		private Integer result;

		public Integer getResult() {
			return result;
		}

		public Thread getMainThread() {
			return mainThread;
		}

		public void setMainThread(Thread mainThread) {
			this.mainThread = mainThread;
		}

		@Override
		public void run() {
			result = sum();
			LockSupport.unpark(getMainThread());
		}
	}

	public static void main(String[] args) throws Exception{
		long start = System.currentTimeMillis();
		SumThread sumThread = new SumThread();
		sumThread.setMainThread(Thread.currentThread());
		sumThread.start();
		LockSupport.park();
		
		int result = sumThread.getResult();
		System.out.println("异步计算结果："+result);
		System.out.println("计算耗时："+(System.currentTimeMillis() - start) +"  ms");
	}

}
