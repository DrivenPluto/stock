package com.pluto.stock.mapper;

import com.pluto.stock.pojo.SysLog;

/**
* @author plutodriven
* @description 针对表【sys_log(系统日志)】的数据库操作Mapper
* @createDate 2023-07-26 21:45:32
* @Entity com.pluto.stock.pojo.SysLog
*/
public interface SysLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

}
