package com.briup.organization.service.impl;


import com.briup.organization.bean.Deparment;
import com.briup.organization.bean.DeparmentExample;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.mapper.DeparmentMapper;
import com.briup.organization.mapper.ex.DeparmentExMapper;
import com.briup.organization.service.IDepartmentService;
import com.briup.organization.util.CodeStatus;
import com.briup.organization.util.DingDingUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetDeptMemberRequest;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.dingtalk.api.response.OapiUserGetDeptMemberResponse;
import com.taobao.api.ApiException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    /**
     * 注入SqlSessionFactory，生产sqlSession
     */
    @Autowired
    SqlSessionFactory factory;

    /**
     * 获取要保存到本地数据库中的部门信息
     * @param dingDepartmentList 钉钉中的部门集合
     * @return  封装的要保存到本地数据库的部门集合
     * @throws CustomerException 自定义异常
     */
    @Override
    public List<Deparment> getDepartList(List<OapiDepartmentListResponse.Department> dingDepartmentList) throws CustomerException {

        //定义变量：token值
        String token =null;
        //定义变量：部门id
        Long id = null;
        //定义变量：部门名称
        String name = null;
        //定义变量：上级部门id
        Long parent_id = null;
        //定义变量：部门对应的企业群名
        String crowd = null;
        //定义变量：部门人数
        Long count = null;
        //定义变量：部门的集合
        List<Deparment> departmentList =new ArrayList<Deparment>();

        try {
            //获取tonken值
            token = new DingDingUtil().getToken();

            //获取部门人数
            DingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getDeptMember");
            OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();

             //遍历钉钉中的部门数据
             for (OapiDepartmentListResponse.Department dingDepartment :dingDepartmentList) {
                 //创建对象：deparment，将从钉钉中拿到的数据封装为本地的deparment对象
                 Deparment deparment = new Deparment();
                 //获取钉钉数据库中的部门编号
                 id = dingDepartment.getId();
                 //获取钉钉数据库中的部门名称
                 name = dingDepartment.getName();
                 //获取钉钉数据库中的上级部门id
                 parent_id = dingDepartment.getParentid();
                 //获取钉钉数据库中的是否同步创建一个关联此部门的企业群
                 crowd =dingDepartment.getSourceIdentifier();

                 //封装钉钉数据到deparment对象中
                 deparment.setId(id);
                 deparment.setName(name);
                 deparment.setParentId(parent_id);
                 deparment.setCrowd(crowd);

                 //获取部门人数
                 req.setDeptId(id.toString());
                 OapiUserGetDeptMemberResponse rsp = client2.execute(req, token);
                 //将获取到的用户id列表保存至List集合中,list数组的长度为子部门人数
                 List list = rsp.getUserIds();
                 count = new Long((long) (list.size()));
                 deparment.setCount(count);
                 //将封装好的deparment添加到departmentList中
                 departmentList.add(deparment);
             }
        }catch (ApiException e){
             throw new CustomerException(CodeStatus.ERROR);
        }
          return  departmentList;
    }


    /**
     * 将从钉钉中获取到的部门数据保存到本地数据库，实现同步
     * @param deparmentList 保存到本地数据库的部门集合
     * @throws CustomerException
     */
    @Override
    public void insertDepartment(List<Deparment> deparmentList) throws CustomerException {
        //调用dao层
       SqlSession sqlSession = factory.openSession();
       DeparmentExMapper departmentExMapper = sqlSession.getMapper(DeparmentExMapper.class);
       departmentExMapper.insertDeparmentList(deparmentList);
       sqlSession.commit();

    }

    /**
     * 查询上级部门查询对应部门信息
     * @param parentId 上级部门id
     * @return
     * @throws CustomerException
     */
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
