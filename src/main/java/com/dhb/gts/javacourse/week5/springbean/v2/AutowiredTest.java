package com.dhb.gts.javacourse.week5.springbean.v2;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutowiredTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("app-v2.xml");
		UserRole userRole = (UserRole) context.getBean("userRole");
		System.out.println(userRole.toString());
		UserRole1 userRole1 = (UserRole1) context.getBean("userRole1");
		System.out.println(userRole1.toString());
		UserRole2 userRole2 = (UserRole2) context.getBean("userRole2");
		System.out.println(userRole2.toString());
	}
}
