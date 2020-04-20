package com.briup.organization.service;

import com.briup.organization.bean.Deparment;
import com.briup.organization.exception.CustomerException;

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
    void insertDepartment(Deparment deparment) throws CustomerException;

    List<Deparment> selectDepartmentByParentId(Long parentId)throws CustomerException;
}
