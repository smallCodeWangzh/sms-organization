package com.briup.organization.mapper.ex;

import com.briup.organization.bean.Deparment;
import com.briup.organization.bean.Member;

import java.util.List;

/**
 * @Description: 自动生成的inset方法没有id添加，没有再次生成，以拓展接口编写
 * @Author: GX Cui
 * @Date 9:45 上午 2020/5/7
 */
public interface MemberExMapper {
     /**
      * 保存成员信息到本地数据库
      * @param member
      */
     void insertWithDingId(Member member);

     /**
      * 根据桥表查询所有信息
      * @param deptId
      * @return
      */
     List<Member> getAllMembersByDeptId(String deptId);
}