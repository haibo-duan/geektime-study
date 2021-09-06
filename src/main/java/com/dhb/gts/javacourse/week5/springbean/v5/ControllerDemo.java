package com.dhb.gts.javacourse.week5.springbean.v5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ControllerDemo {

	@Autowired
	ISchool school;

	@RequestMapping("/startler")
	@ResponseBody
	public String startler(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(school.toString());
		return school.toString();
	}
}
