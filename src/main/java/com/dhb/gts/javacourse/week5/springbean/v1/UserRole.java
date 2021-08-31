package com.dhb.gts.javacourse.week5.springbean.v1;

import lombok.Data;

@Data
public class UserRole {
	//用户
	private User user;
	//角色
	private Role role;

	public UserRole() {
	}

	public UserRole(User user, Role role) {
		this.user = user;
		this.role = role;
	}
}
