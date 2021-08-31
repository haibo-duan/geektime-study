package com.dhb.gts.javacourse.week5.springbean.v3;

import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ToString
public class UserRole1 {
	//用户
	private User user;
	//角色
	private Role role;

	public UserRole1() {
	}

	public User getUser() {
		return user;
	}

	@Resource
	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	@Resource
	public void setRole(Role role) {
		this.role = role;
	}
	
	public UserRole1(User user, Role role) {
		this.user = user;
		this.role = role;
	}
}
