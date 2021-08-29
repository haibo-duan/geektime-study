package com.dhb.gts.javacourse.week5.proxy;

import java.lang.reflect.Proxy;

public class ProxyTest {

	public static void main(String[] args) {
		testStaticProxy();
		testDynamicProxy();
	}
	
	private static void testStaticProxy() {
		Target target = new TargetImpl();
		StaticProxy proxy = new StaticProxy(target);
		String result = proxy.execute();
		System.out.println(result);
	}
	
	private static void testDynamicProxy() {
		Target target = new TargetImpl();
		DynamicProxy proxy = new DynamicProxy(target);
		Target proxySubject = (Target)Proxy.newProxyInstance(TargetImpl.class.getClassLoader(),TargetImpl.class.getInterfaces(),proxy);
		String result = proxySubject.execute();
		System.out.println(result);
	}
}
