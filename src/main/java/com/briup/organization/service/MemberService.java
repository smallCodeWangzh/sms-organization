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
    public void memberDataSynchronization () throws CustomerException;

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
     * @param deptIds
     * @throws CustomerException
     */
    public void addMember(Member member , int[] deptIds) throws CustomerException;

    /**
     * 根据选定的字段和选定的成员生成Excel文档
     * @param isAlive 是否仅导出激活员工
     * @param ids  需要导出的用户id列表，以逗号隔开
     * @param title  需要导出的字段名称，以逗号隔开
     * @return 返回生成excel需要的数据集合
     * @throws CustomerException
     */
    public List<Object> createExcel(int isAlive,String ids,String title) throws CustomerException;

    /**
     * 批量删除成员
     * @param ids
     * @throws CustomerException
     */
    public void deleteMemberByIds(List<String> ids) throws CustomerException;


    /**
     * 更新成员信息
     * @param member
     * @param deptIds
     * @throws CustomerException
     */
    public void updateMember(Member member , int[] deptIds) throws CustomerException;
}
