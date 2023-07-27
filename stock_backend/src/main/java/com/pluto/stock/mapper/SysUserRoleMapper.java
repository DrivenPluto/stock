package com.pluto.stock.mapper;

import com.pluto.stock.pojo.SysUserRole;

/**
* @author plutodriven
* @description 针对表【sys_user_role(用户角色表)】的数据库操作Mapper
* @createDate 2023-07-26 21:45:32
* @Entity com.pluto.stock.pojo.SysUserRole
*/
public interface SysUserRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

}
