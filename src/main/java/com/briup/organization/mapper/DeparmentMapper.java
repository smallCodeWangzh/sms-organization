package com.briup.organization.mapper;

import com.briup.organization.bean.Deparment;
import com.briup.organization.bean.DeparmentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DeparmentMapper {
    long countByExample(DeparmentExample example);

    int deleteByExample(DeparmentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Deparment record);

    int insertSelective(Deparment record);

    List<Deparment> selectByExample(DeparmentExample example);

    Deparment selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Deparment record, @Param("example") DeparmentExample example);

    int updateByExample(@Param("record") Deparment record, @Param("example") DeparmentExample example);

    int updateByPrimaryKeySelective(Deparment record);

    int updateByPrimaryKey(Deparment record);
}