package com.dhb.gts.javacourse.week5.proxy;

public class TargetImpl implements Target{

	@Override
	public String execute() {
		System.out.println("TargetImpl execute！");
		return "execute";
	}
}
