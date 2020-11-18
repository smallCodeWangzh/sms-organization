package com.briup.organization.mapper.ex;

import com.briup.organization.bean.Deparment;
import java.util.List;

/**
 * @Author: kangya
 * @CreateTime: 2020-4-24 1:19
 * @Description: 部门管理的映射接口扩展，对应映射文件：DeparmentExMapper.xml
 */
public interface DeparmentExMapper {
     /**
      * 保存部门信息到本地数据库
      * @param deparmentList
      */
     void insertDeparmentList(List<Deparment> deparmentList);

     /**
      * 清空表数据
      */
     void  truncateTableDeparment();
}