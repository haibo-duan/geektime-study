package com.dhb.gts.javacourse.week5.proxy;

public class ProxyTest {

	public static void main(String[] args) {
		testStaticProxy();
		
	}
	
	private static void testStaticProxy() {
		Target target = new TargetImpl();
		StaticProxy proxy = new StaticProxy(target);
		String result = proxy.execute();
		System.out.println(result);
	}
}
