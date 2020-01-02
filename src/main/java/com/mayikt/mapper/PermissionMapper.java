package com.mayikt.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.mayikt.entity.Permission;

@Mapper
public interface PermissionMapper {

	@Select(" select * from sys_permission ")
	List<Permission> findAllPermission();

}
