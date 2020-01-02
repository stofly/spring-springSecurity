package com.mayikt.entity;

import lombok.Data;
import lombok.ToString;

// 角色信息表
@Data
@ToString
public class Role {
	private Integer id;
	private String roleName;
	private String roleDesc;
}
