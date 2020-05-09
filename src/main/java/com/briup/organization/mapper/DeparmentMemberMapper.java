package com.briup.organization.mapper;

import com.briup.organization.bean.DeparmentMemberExample;
import com.briup.organization.bean.DeparmentMemberKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DeparmentMemberMapper {
    long countByExample(DeparmentMemberExample example);

    int deleteByExample(DeparmentMemberExample example);

    int deleteByPrimaryKey(DeparmentMemberKey key);

    int insert(DeparmentMemberKey record);

    int insertSelective(DeparmentMemberKey record);

    List<DeparmentMemberKey> selectByExample(DeparmentMemberExample example);

    int updateByExampleSelective(@Param("record") DeparmentMemberKey record, @Param("example") DeparmentMemberExample example);

    int updateByExample(@Param("record") DeparmentMemberKey record, @Param("example") DeparmentMemberExample example);
}