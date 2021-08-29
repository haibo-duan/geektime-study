package com.dhb.gts.javacourse.week5.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicProxy implements InvocationHandler {
	
	private Target target;

	public DynamicProxy(Target target) {
		this.target = target;
	}

	public void before() {
		System.out.println("DynamicProxy method execute before ...");	
	}

	public void after() {
		System.out.println("DynamicProxy method execute after ...");
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		before();
		Object o = method.invoke(target,args);
		after();
		return o;
	}
}
