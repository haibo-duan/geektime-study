package com.dhb.gts.javacourse.week4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ConditionDemo {

	final Lock lock = new ReentrantLock();
	final Condition notFull = lock.newCondition();
	final Condition notEmpty = lock.newCondition();

	final Object[] items = new Object[20];
	int putptr, takepr, count;

	public static void main(String[] args) {
		ConditionDemo demo = new ConditionDemo();
		IntStream.range(0,10).forEach(i -> {
			new Thread(() -> {
				while (true) {
					try {
						demo.put(new Object());
						System.out.println("Threand [" + Thread.currentThread().getName() + "] produce Object !!!");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			},i+"").start();
		});

		IntStream.range(11,15).forEach(i -> {
			new Thread(() -> {
				while (true) {
					try {
						demo.take();
						System.out.println("Threand [" + Thread.currentThread().getName() + "] consumer Object !!!");
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			},i+"").start();
		});
	}

	public void put(Object x) throws InterruptedException {
		lock.lock();
		try {
			//当count等于数组大小的时候，当前线程等待，直到notFull通知，再进行生产
			while (count == items.length) {
				notFull.await();
			}
			items[putptr] = x;
			if (++putptr == items.length) {
				putptr = 0;
			}
			++count;
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	public Object take() throws InterruptedException {
		lock.lock();
		try {
			//当count为0的时候 进入等待 直到notEmpty通知
			while (count == 0) {
				notEmpty.await();
			}
			Object x = items[takepr];
			if (++takepr == items.length) {
				takepr = 0;
			}
			--count;
			notFull.signal();
			return x;
		} finally {
			lock.unlock();
		}
	}

}
