package com.pluto.stock.mapper;

import com.pluto.stock.pojo.SysRole;

/**
* @author plutodriven
* @description 针对表【sys_role(角色表)】的数据库操作Mapper
* @createDate 2023-07-26 21:45:32
* @Entity com.pluto.stock.pojo.SysRole
*/
public interface SysRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

}
