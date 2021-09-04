package com.dhb.gts.javacourse.week5.springbean.v4;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlTest {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("app-v4.xml");
		ISchool school = (ISchool) context.getBean("school");
		school.ding();
		System.out.println(school.toString());
	}
}
