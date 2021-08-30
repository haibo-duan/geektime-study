package com.dhb.gts.javacourse.week5.singleton;

/**
*@author dhaibo1986@live.cn
*@description 懒汉式单例模式  采用静态代码块的方式实现。
 *   实际上等价于静态常量的方式实现，都是在类加载过程中就实现了目标对象的实例化。两者是等价的 ，优缺点也一致。
 *   简单实用，线程安全。
 *   唯一缺点是不管用到与否，在类加载的时候都会进行实例化。
*@date  2021/8/30 13:55
*/
public class SingletonDemo2 {

	private final static SingletonDemo2 INSTANCE;
	
	static {
		INSTANCE = new SingletonDemo2();
	}

	private SingletonDemo2() {}

	public static SingletonDemo2 getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		SingletonDemo2 singleton1 = SingletonDemo2.getInstance();
		SingletonDemo2 singleton2 = SingletonDemo2.getInstance();
		System.out.println(singleton1 == singleton2);
	}
}
