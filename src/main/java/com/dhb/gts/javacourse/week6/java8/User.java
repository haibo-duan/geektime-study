package com.dhb.gts.javacourse.week6.java8;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
@Getter
@Setter
public class User {
	
	private String name;
	
	private int age;
}
