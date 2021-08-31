package com.dhb.gts.javacourse.week5.springbean.v2;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ToString
public class UserRole2 {
	//用户
	private User user;
	//角色
	private Role role;


	public User getUser() {
		return user;
	}

	@Autowired
	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	@Autowired
	public void setRole(Role role) {
		this.role = role;
	}

	public UserRole2() {
	}
	
	public UserRole2(User user, Role role) {
		this.user = user;
		this.role = role;
	}
}
