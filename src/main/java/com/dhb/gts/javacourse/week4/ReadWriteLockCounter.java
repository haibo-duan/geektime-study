package com.dhb.gts.javacourse.week4;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class ReadWriteLockCounter {
	
	private int sum;
	
	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	
	public int incrAndGet() {
		try {
			lock.writeLock().lock();
			return ++sum;
		}finally {
			lock.writeLock().unlock();
		}
	}
	
	public int getSum() {
		try {
			lock.readLock().lock();
			return sum;
		}finally {
			lock.readLock().unlock();
		}
	}

	public static void testLockCounter() {
		int loopNum = 100_0000;
		ReadWriteLockCounter counter = new ReadWriteLockCounter();
		IntStream.range(0,loopNum).parallel().forEach(i -> counter.incrAndGet());
		System.out.println(counter.getSum());
	}

	public static void main(String[] args) {
		testLockCounter();
	}
}
