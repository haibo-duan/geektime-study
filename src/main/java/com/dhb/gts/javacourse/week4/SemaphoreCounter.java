package com.dhb.gts.javacourse.week4;

import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class SemaphoreCounter {
	
	private int sum = 0;
	
	private Semaphore readSemaphore = new Semaphore(100,true);
	private Semaphore writeSemaphore = new Semaphore(1);
	
	public int incrAndGet() {
		try {
			writeSemaphore.acquireUninterruptibly();
			return ++sum;
		}finally {
			writeSemaphore.release();
		}
	}
	
	public int getSum() {
		try {
			readSemaphore.acquireUninterruptibly();
			return sum;
		}finally {
			readSemaphore.release();
		}
	}



	public static void testLockCounter() {
		int loopNum = 100_0000;
		SemaphoreCounter counter = new SemaphoreCounter();
		IntStream.range(0,loopNum).parallel().forEach(i -> counter.incrAndGet());
		System.out.println(counter.getSum());
	}

	public static void main(String[] args) {
		testLockCounter();
	}
}
