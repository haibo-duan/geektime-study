package com.dhb.gts.javacourse.week5.springbean.v3;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Role {
	//角色ID
	@Value("2")
	private Integer roleId;
	//角色名称
	@Value("用户")
	private String roleName;
}
