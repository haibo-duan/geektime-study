package com.dhb.gts.javacourse.week5.singleton;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式--同步块double check + volatile
 * 考虑到Double check实现的单例模式存在可见性问题，我们可以通过在INSTANCE上加上volatile来实现。
 * 这样就能避免在前面DoubleCheck实现的单例模式里的问题，由于INSTANCE具备了可见性，此时再通过DoubleCheck的方式来实现，就不会出现目标对象实例化多次的问题。
 *  但是这种方式还是存在一个问题就是，如果我们无法避免反序列化的问题。通过反序列化，仍然可以将这个类实例化多次。
*@date  2021/8/30 15:02
*/
public class SingletonDemo7 {

	private static volatile SingletonDemo7 INSTANCE;

	public SingletonDemo7() {
	}

	public static SingletonDemo7 getInstance() {
		if (INSTANCE == null) {
			synchronized(SingletonDemo7.class) {
				if(INSTANCE == null) {
					try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					INSTANCE = new SingletonDemo7();
				}
			}
		}
		return INSTANCE;
	}


	public static void main(String[] args) {
		IntStream.range(0, 100).forEach(i -> {
			new Thread(() -> {
				System.out.println(SingletonDemo7.getInstance().hashCode());
			}).start();
		});
	}
}
