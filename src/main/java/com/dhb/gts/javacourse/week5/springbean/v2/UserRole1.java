package com.dhb.gts.javacourse.week5.springbean.v2;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserRole1 {
	//用户
	private User user;
	//角色
	private Role role;

	public UserRole1() {
	}

	@Autowired
	public UserRole1(User user, Role role) {
		this.user = user;
		this.role = role;
	}
}
