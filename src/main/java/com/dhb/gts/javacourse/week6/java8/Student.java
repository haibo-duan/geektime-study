package com.dhb.gts.javacourse.week6.java8;

import java.io.Serializable;

public class Student implements Serializable {

	private int id;
	private String name;

	public Student(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public void init() {
		System.out.println("hello ... ...");
	}

	public Student create() {
		Student s = new Student(101, "KK101");
		return s;
	}

}
