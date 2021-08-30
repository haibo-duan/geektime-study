package com.dhb.gts.javacourse.week5.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {
	
	public void before() {
		System.out.println("CglibProxy method execute before ...");
	}

	public void after() {
		System.out.println("CglibProxy method execute after ...");
	}
	
	public Object createProxy(Class target) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target);
		enhancer.setCallback(new CglibProxy());
		return enhancer.create();
	}


	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		before();
		Object result = methodProxy.invokeSuper(o,objects);
		after();
		return result;
	}
}
