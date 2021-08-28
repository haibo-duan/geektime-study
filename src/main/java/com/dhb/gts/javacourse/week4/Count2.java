package com.dhb.gts.javacourse.week4;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Count2 {

	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

	public void get() {
		rwLock.readLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + " get begin");
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName() + " get end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public void put() {
		rwLock.writeLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + " put begin");
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName() + " put end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rwLock.writeLock().unlock();
		}
	}

}
