package com.dhb.gts.javacourse.week5.singleton;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式--同步块double check
 * 考虑到同步代码块会存在线程安全问题，这个问题都是if判断引起的，那么一种解决方法就是在同步代码块中增加double check ,既实现双重判定检查。
 * 经过验证，这种方式能在大多数情况下都能很好的实现单例模式，执行main函数，基本上hashcode都相同。
 * 但是还是会在少数情况下，出现多个实例的问题。我们可以思考一下这个问题，这个问题正是jvm的可见性造成的。
 * 前面我们的判断都是线程还在执行中，没有对INSTANCE进行赋值，后续的线程就要进入if判断了，因此会造成目标对象被初始化多次，
 * 那么我们假设，如果第一个线程已经执行完了对INSTANCE的赋值，加锁结束，此时恰好有一个线程已经进入了第一个if判断，正在等待锁。
 * 拿到锁之后，进入第二个if判断，但是由于可见性问题，此时第二个线程还不能看到线程一的值已经被写入完毕。误以为还是空，因此再次实现一次实例化。
 * 
*@date  2021/8/30 15:02
*/
public class SingletonDemo6 {

	private static SingletonDemo6 INSTANCE;

	public SingletonDemo6() {
	}

	public static SingletonDemo6 getInstance() {
		if (INSTANCE == null) {
			synchronized(SingletonDemo6.class) {
				if(INSTANCE == null) {
					try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					INSTANCE = new SingletonDemo6();
				}
			}
		}
		return INSTANCE;
	}


	public static void main(String[] args) {
		IntStream.range(0, 100).forEach(i -> {
			new Thread(() -> {
				System.out.println(SingletonDemo6.getInstance().hashCode());
			}).start();
		});
	}
}
