package com.dhb.gts.javacourse.week5.singleton;

import java.util.stream.IntStream;

/**
 * @author dhaibo1986@live.cn
 * @description 懒汉式单例模式--枚举
 * 在《effective java》中还有一种更简单的写法，那就是枚举。也是《effective java》作者最为推崇的方法。
 * 这种方法不仅可以解决线程同步问题，还可以防止反序列化。
 * 枚举类由于没有构造方法（枚举是java中约定的特殊格式，因此不需要构造函数。），因此不能够根据class反序列化之后实例化。因此这种写法是最完美的单例模式。
 * 
 * @date 2021/8/30 15:02
 */
public class SingletonDemo9 {

	public SingletonDemo9() {
	}

	public static SingletonDemo9 getInstance() {
		return Sigleton.INSTANCE.getInstance();
	}

	public static void main(String[] args) {
		IntStream.range(0, 100).forEach(i -> {
			new Thread(() -> {
				System.out.println(SingletonDemo9.getInstance().hashCode());
			}).start();
		});
	}


	private enum Sigleton {
		INSTANCE;

		private final SingletonDemo9 instance;

		Sigleton() {
			instance = new SingletonDemo9();
		}

		public SingletonDemo9 getInstance() {
			return instance;
		}
	}
}
