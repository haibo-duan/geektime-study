package com.dhb.gts.javacourse.week5.proxy;

public class StaticProxy implements Target{

	private Target target;

	public StaticProxy(Target target) {
		this.target = target;
	}

	@Override
	public String execute() {
		System.out.println("perProcess");
		String result = this.target.execute();
		System.out.println("postProcess");
		return result;
	}
}
