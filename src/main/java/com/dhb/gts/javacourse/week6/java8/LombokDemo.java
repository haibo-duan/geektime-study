package com.dhb.gts.javacourse.week6.java8;

public class LombokDemo {

	public static void main(String[] args) {
		
		User u = new User("a",22);
		System.out.println(u.toString());
		
		User u1 = User.builder().name("ss").age(18).build();
		
		
		
	}
}
