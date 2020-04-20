package com.briup.organization.service.impl;


import com.briup.organization.bean.Deparment;
import com.briup.organization.bean.DeparmentExample;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.mapper.DeparmentMapper;
import com.briup.organization.service.IDepartmentService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * ClassName: DepartmentImpl
 * Description:部门管理的sevice层实现类
 * date: 2020/4/19 1:21
 *
 * @author kangya
 * @since JDK 1.8
 */
@Service
public class DepartmentImpl implements IDepartmentService {
    @Autowired
    SqlSessionFactory factory;


    @Override
    public void insertDepartment(Deparment deparment) throws CustomerException {
        /**将从钉钉中获取到的部门数据保存到本地数据库，实现同步*/
        //调用dao层
       SqlSession sqlSession = factory.openSession();
       DeparmentMapper departmentMapper = sqlSession.getMapper(DeparmentMapper.class);
       //对部门id进行判空处理，若部门id不为空，则进行插入操作（使用insertSelective可对空字段进行判空）
       if (deparment.getId()!=null) {
           departmentMapper.insertSelective(deparment);
       }
       sqlSession.commit();

    }

    @Override
    public List<Deparment> selectDepartmentByParentId(Long parentId) throws CustomerException {
        //调用dao层
        SqlSession sqlSession = factory.openSession();
        DeparmentMapper departmentMapper = sqlSession.getMapper(DeparmentMapper.class);
        DeparmentExample deparmentExample = new DeparmentExample();
        deparmentExample.createCriteria().andParentIdEqualTo(parentId);
        List<Deparment> deptList = departmentMapper.selectByExample(deparmentExample);
        sqlSession.commit();
        return deptList;

    }
}
