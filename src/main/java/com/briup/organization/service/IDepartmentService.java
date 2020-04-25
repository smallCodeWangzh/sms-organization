package com.briup.organization.service;

import com.briup.organization.bean.Deparment;
import com.briup.organization.exception.CustomerException;
import com.dingtalk.api.response.OapiDepartmentListResponse;

import java.util.List;

/**
 * ClassName: IDepartmentService
 * Description:部门管理的接口
 * date: 2020/4/19 1:19
 *
 * @author kangya
 * @since JDK 1.8
 */
public interface IDepartmentService {
    /**
     * 获取要保存到本地数据库中的部门信息
     * @param dingDepartmentList 钉钉中的部门集合
     * @return 封装的要保存到本地数据库的部门集合
     * @throws CustomerException 自定义异常
     */
    List<Deparment> getDepartList(List<OapiDepartmentListResponse.Department> dingDepartmentList) throws CustomerException;

    /**
     * 保存部门信息到本地数据库，实现同步
     * @param deparmentList 保存到本地数据库的部门集合
     * @throws CustomerException 自定义异常
     */
    void insertDepartment(List<Deparment> deparmentList) throws CustomerException;

    /**
     * 查询上级部门查询对应部门信息
     * @param parentId 上级部门id
     * @return
     * @throws CustomerException 自定义异常
     */

    List<Deparment> selectDepartmentByParentId(Long parentId)throws CustomerException;
}
