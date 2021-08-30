package com.dhb.gts.javacourse.week5.singleton;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式--在方法中加同步代码块
 * 既然将synchronized加锁到getInstance方法中，这样会导致效率的下降，那么我们可以尝试将锁细化，将synchronized加锁在if判断之后。
 * 但是经过实验，我们发现，这种方式会带来线程安全的问题。
 * 当一个线程进入了if判断，还没执行同步块中的代码，此时另外一个线程也进入了if判断区域，那么只要if判断通过，虽然后面有synchronized保护，
 * 这也只能将这两个线程在new对象的过程中变成了顺序操作，从根本上来说，还是创建了两个实例。我们通过main函数执行可以很好的验证这一点。
 *
*@date  2021/8/30 14:47
*/
public class SingletonDemo5 {
	
	private static SingletonDemo5 INSTANCE;

	public SingletonDemo5() {
	}

	public static SingletonDemo5 getInstance() {
		if (INSTANCE == null) {
			synchronized(SingletonDemo5.class) {
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				INSTANCE = new SingletonDemo5();
			}
		}
		return INSTANCE;
	}


	public static void main(String[] args) {
		IntStream.range(0, 100).forEach(i -> {
			new Thread(() -> {
				System.out.println(SingletonDemo5.getInstance().hashCode());
			}).start();
		});
	}
}
