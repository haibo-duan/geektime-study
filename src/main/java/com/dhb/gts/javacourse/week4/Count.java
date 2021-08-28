package com.dhb.gts.javacourse.week4;

import java.util.concurrent.locks.ReentrantLock;

public class Count {

	final ReentrantLock lock = new ReentrantLock();


	public void get() {
		lock.lock();
		try {
			System.out.println(Thread.currentThread().getName() + " get begin");
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName() + " get end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();	
		}
	}

	public void put() {
		lock.lock();
		try {
			System.out.println(Thread.currentThread().getName() + " put begin");
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName() + " put end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}

}
