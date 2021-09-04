package com.dhb.gts.javacourse.week5.springbean.v5;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AnnotationTest {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("app-v5.xml");
		ISchool school = (ISchool) context.getBean("school");
		school.ding();
		System.out.println(school.toString());
	}
}
