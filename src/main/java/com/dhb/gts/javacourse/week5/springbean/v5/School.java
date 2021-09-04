package com.dhb.gts.javacourse.week5.springbean.v5;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Data
@Component("school")
public class School implements ISchool {

	@Autowired
	@Qualifier("klass")
	Klass class1;

	@Autowired
	@Qualifier("student2")
	Student student100;

	@Override
	public void ding() {

		System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student100);

	}

}
