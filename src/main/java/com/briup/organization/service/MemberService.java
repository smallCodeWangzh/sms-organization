package com.briup.organization.service;

import com.briup.organization.bean.Member;
import com.briup.organization.exception.CustomerException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * @Description: 成员管理业务逻辑接口
 * @Author: GX Cui
 * @Date 3:38 下午 2020/4/20
 */
public interface MemberService {

    /**
     * 钉钉成员信息同步到本地数据库
     * @throws CustomerException
     * @throws JsonProcessingException
     */
    public void memberDataSynchronization () throws CustomerException, JsonProcessingException;

    /**
     * 根据部门id查询部门下的所有成员详情
     * @param deptId
     * @return
     * @throws CustomerException
     */
    public List<Member> getMembersByDeptId(String deptId) throws CustomerException;

    /**
     * 根据成员id在本地数据库中查询
     * @param id
     * @return
     * @throws CustomerException
     */
    public Member getMemberDetailById(String id) throws CustomerException;

    /**
     * 新增成员
     * @param member
     * @throws CustomerException
     */
    public void addMember(Member member) throws CustomerException;
}
