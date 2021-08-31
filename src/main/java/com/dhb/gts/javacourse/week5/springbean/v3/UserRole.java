package com.dhb.gts.javacourse.week5.springbean.v3;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Data
public class UserRole {
	//用户
	@Resource(type = User.class)
	private User user;
	//角色
	@Resource(name = "role")
	private Role role;

	public UserRole() {
	}

	public UserRole(User user, Role role) {
		this.user = user;
		this.role = role;
	}
}
