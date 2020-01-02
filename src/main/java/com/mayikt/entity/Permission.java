package com.mayikt.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Permission {
	private Integer id;
	// 权限名称
	private String permName;
	// 权限标识
	private String permTag;
	// 请求url
	private String url;
}
