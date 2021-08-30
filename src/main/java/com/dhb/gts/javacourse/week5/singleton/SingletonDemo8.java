package com.dhb.gts.javacourse.week5.singleton;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式--静态内部类
 * 结合饿汉模式的优点，既然饿汉模式可以完美而又简单的实现单例模式，而且还能保证线程安全。那么可以参考饿汉模式，结合懒汉模式懒加载的优点。
 * 在其内部建立一个静态的内部类，这个类只有调用getInstance的时候才会被加载，而利用classLoader，从而保证只有一个实例会被实例化。
 * 这种实现方式同样是不能防止反序列化的。如果要解决这个问题，可以通过Serializable、transient、readResolve()实现序列化来解决。
*@date  2021/8/30 15:02
*/
public class SingletonDemo8 {

	public SingletonDemo8() {
	}

	private static class SingletonDemo8Holder{
		private static final SingletonDemo8 INSTANCE = new SingletonDemo8();
	}
	
	public static SingletonDemo8 getInstance() {
		return SingletonDemo8Holder.INSTANCE;
	}


	public static void main(String[] args) {
		IntStream.range(0, 100).forEach(i -> {
			new Thread(() -> {
				System.out.println(SingletonDemo8.getInstance().hashCode());
			}).start();
		});
	}
}
