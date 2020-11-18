package com.briup.organization.service;

import com.briup.organization.bean.Deparment;
import com.briup.organization.exception.CustomerException;


import java.util.List;

/**
 * @Author: kangya
 * @CreateTime: 2020-4-19 1:19
 * @Description: 部门管理的业务逻辑接口
 */
public interface IDepartmentService {
    /**
     * 获取要保存到本地数据库中的部门信息
     * @return 封装的要保存到本地数据库的部门集合
     * @throws CustomerException 自定义异常
     */
    public List<Deparment> getDepartList() throws CustomerException;

    /**
     * 统计每个部门的人数
     * @param id  部门的id
     * @return 部门的人数
     * @throws CustomerException 自定义异常
     */
    public Long totalDeptPeoples(Long id) throws CustomerException;

    /**
     * 保存部门信息到本地数据库，实现同步
     * @param deparmentList 保存到本地数据库的部门集合
     * @throws CustomerException 自定义异常
     */
    public void insertDepartment(List<Deparment> deparmentList) throws CustomerException;

    /**
     * 查询上级部门查询对应部门信息
     * @param parentId 上级部门id
     * @return 对应的部门信息
     * @throws CustomerException 自定义异常
     */

    public List<Deparment> selectDepartmentByParentId(Long parentId)throws CustomerException;

    /**
     * 新增部门
     * @param parentId 上级部门id
     * @param name  部门名称
     * @param autoCreate 选择是否创建部门群,true为创建
     * @return 新增的部门信息
     * @throws CustomerException
     */
    public Deparment newInsertDepartment(Long parentId,String name,Boolean autoCreate)throws CustomerException;

    /**
     * 保存新增的部门信息到本地数据库，实现同步
     * @param deparment 从钉钉新增部门后得到的部门信息
     * @throws CustomerException 自定义异常
     */
    public void savenewInsertDepartment(Deparment deparment) throws CustomerException;
}
