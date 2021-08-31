package com.dhb.gts.javacourse.week5.springbean.v2;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserRole {
	//用户
	@Autowired
	private User user;
	//角色
	@Autowired
	private Role role;

	public UserRole() {
	}

	public UserRole(User user, Role role) {
		this.user = user;
		this.role = role;
	}
}   
