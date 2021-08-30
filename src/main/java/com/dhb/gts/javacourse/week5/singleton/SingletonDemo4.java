package com.dhb.gts.javacourse.week5.singleton;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式--方法上加锁
 * 考虑到基本的懒汉式单利模式的线程安全的问题，最简单粗暴的方式就是在getInstance方法上加锁。
 * 这种方式能解决线程安全的问题，但是，在方法上粗暴的使用synchronized，将并行的方式直接变成了串行化。极大的降低了效率。
 * 用在生产系统中该方法将成为系统的瓶颈所在，因此这种方式虽然可用，但是并不推荐使用。
 * 
*@date  2021/8/30 14:33
*/
public class SingletonDemo4 {

	private static SingletonDemo4 INSTANCE;

	public SingletonDemo4() {
	}

	public static synchronized SingletonDemo4 getInstance() {
		if(INSTANCE == null) {
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			INSTANCE = new SingletonDemo4();
		}
		return INSTANCE;
	}


	public static void main(String[] args) {
		IntStream.range(0,100).forEach(i -> {
			new Thread(() -> {
				System.out.println(SingletonDemo4.getInstance().hashCode());
			}).start();
		});
	}
}
