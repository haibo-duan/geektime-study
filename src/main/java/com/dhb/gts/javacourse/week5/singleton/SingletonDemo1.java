package com.dhb.gts.javacourse.week5.singleton;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式  采用静态常量的方式实现。
 *   简单实用，线程安全。
 *   唯一缺点是不管用到与否，在类加载的时候都会进行实例化。
*@date  2021/8/30 13:44
*/
public class SingletonDemo1 {
	
	private final static SingletonDemo1 INSTANCE = new SingletonDemo1();
	
	private SingletonDemo1() {}
	
	public static SingletonDemo1 getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		SingletonDemo1 singleton1 = SingletonDemo1.getInstance();
		SingletonDemo1 singleton2 = SingletonDemo1.getInstance();
		System.out.println(singleton1 == singleton2);
	}
}
