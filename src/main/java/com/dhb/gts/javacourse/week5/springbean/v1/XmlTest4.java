package com.dhb.gts.javacourse.week5.springbean.v1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlTest4 {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("app-v1-4.xml");
		UserRole userRole = (UserRole) context.getBean("userRole");
		System.out.println(userRole.toString());
	}

}

