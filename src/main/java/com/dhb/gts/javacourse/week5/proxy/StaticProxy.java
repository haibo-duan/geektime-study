package com.dhb.gts.javacourse.week5.proxy;

public class StaticProxy implements Target{

	private Target target;

	public StaticProxy(Target target) {
		this.target = target;
	}

	public void before() {
		System.out.println("StaticProxy method execute before ...");
	}

	public void after() {
		System.out.println("StaticProxy method execute after ...");
	}
	
	@Override
	public String execute() {
		before();
		String result = this.target.execute();
		after();
		return result;
	}
}
