package com.dhb.gts.javacourse.week5.singleton;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式--基本实现
 * 懒汉式单例模式虽然能起到懒加载的效果，达到节约内存空间的目的。
 * 但是在多线程的条件下，如果一个线程进入了if判断，还没有执行，而另外一个线程也进入if判断。
 * 此时并会导致返回多个实例。因此这种方式在生产环境是不可取的。
 * 在getInstance方法中，添加了sleep时间，通过main方法中多线程执行效果就会非常明显，可以发现这样会导致每次输出的hashcode都不相同。
 * 
*@date  2021/8/30 14:04
*/
public class SingletonDemo3 {
	
	private static SingletonDemo3 INSTANCE;

	public SingletonDemo3() {
	}

	public static SingletonDemo3 getInstance() {
		if(INSTANCE == null) {
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			INSTANCE = new SingletonDemo3();
		}
		return INSTANCE;
	}


	public static void main(String[] args) {
		IntStream.range(0,100).forEach(i -> {
			new Thread(() -> {
				System.out.println(SingletonDemo3.getInstance().hashCode());
			}).start();
		});
	}
}
