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
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: kangya
 * @CreateTime: 2020-4-19 1:21
 * @Description: 部门管理的部门管理的业务逻辑接口的实现
 */
@Service
public class DepartmentImpl implements IDepartmentService {

    @Autowired
    SqlSessionFactory factory;

    /**
     * 获取要保存到本地数据库中的部门信息
     * @return  封装的要保存到本地数据库的部门集合
     * @throws CustomerException 自定义异常
     */
    @Override
    public List<Deparment> getDepartList() throws CustomerException {
        //定义变量：部门的集合
        List<Deparment> departmentList =new ArrayList<Deparment>();

        try {
            //调用DingDingUtil中的getToken方法获取token
            String token = new DingDingUtil().getToken();

            //使用钉钉API获取部门信息
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
            OapiDepartmentListRequest req = new OapiDepartmentListRequest();
            req.setFetchChild(true);  //是否递归部门的全部子部门
            OapiDepartmentListResponse resp = client.execute(req, token);

            //获取钉钉部门的信息，并封装为List集合
            List<OapiDepartmentListResponse.Department> dingDepartmentList = resp.getDepartment();

            //遍历钉钉中的部门数据
             for (OapiDepartmentListResponse.Department dingDepartment :dingDepartmentList) {
                 //创建对象：deparment，将从钉钉中拿到的数据封装为本地的deparment对象
                 Deparment deparment = new Deparment();
                 //获取钉钉数据库中的部门编号
                 Long id = dingDepartment.getId();
                 //获取钉钉数据库中的部门名称
                 String name = dingDepartment.getName();
                 //获取钉钉数据库中的上级部门id
                 Long parent_id = dingDepartment.getParentid();
                 //获取钉钉数据库中的是否同步创建一个关联此部门的企业群
                 Boolean flag_CreateDeptGroup =dingDepartment.getCreateDeptGroup();

                 //封装钉钉数据到deparment对象中
                 deparment.setId(id);
                 deparment.setName(name);
                 deparment.setParentId(parent_id);
                 //若选择同步创建一个关联此部门的企业群，则群名默认等于部门名称
                 if (flag_CreateDeptGroup==true){
                     deparment.setCrowd(name);
                 }
                 //调用totalDeptPeoples方法统计部门人数
                 deparment.setCount(totalDeptPeoples(id));
                 //将封装好的deparment添加到departmentList中
                 departmentList.add(deparment);
             }
        }catch (ApiException e){
             throw new CustomerException(CodeStatus.ERROR);
        }
          return  departmentList;
    }


    /**
     * 统计每个部门的人数
     * @param id  部门的id
     * @return 部门的人数
     * @throws CustomerException 自定义异常
     */
      @Override
      public Long totalDeptPeoples(Long id) throws CustomerException{
          //定义变量：部门人数
          Long count = null;
          try {
          //调用DingDingUtil中的getToken方法获取token
          String  token = new DingDingUtil().getToken();

          //使用钉钉API获取部门用户Id列表
          DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getDeptMember");
          OapiUserGetDeptMemberRequest req1 = new OapiUserGetDeptMemberRequest();

          //使用钉钉API获取杰普软件企业员工人数
          DingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_org_user_count");
          OapiUserGetOrgUserCountRequest req2 = new OapiUserGetOrgUserCountRequest();


          //使用钉钉API获取子部门的列表ID
          DingTalkClient client3 = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list_ids");
          OapiDepartmentListIdsRequest req3 = new OapiDepartmentListIdsRequest();

          //使用钉钉API查询部门的所有上级父部门
          DingTalkClient client4 = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list_parent_depts_by_dept");
          OapiDepartmentListParentDeptsByDeptRequest req4 = new OapiDepartmentListParentDeptsByDeptRequest();

          //获取杰普软件企业员工人数
          //0：包含未激活钉钉的人员数  1：不包含未激活钉钉的人员数量
          req2.setOnlyActive(0L);
          OapiUserGetOrgUserCountResponse resp2 = client2.execute(req2, token);
          Long c = resp2.getCount();

          //获取子部门id列表
          req3.setId(String.valueOf(id));
          OapiDepartmentListIdsResponse resp3 = client3.execute(req3, token);
          List<Long> subDeptIdList = resp3.getSubDeptIdList();

          //获取父部门id列表
          req4.setId(String.valueOf(id));
          OapiDepartmentListParentDeptsByDeptResponse resp4 = client4.execute(req4, token);
          List<Long> parentDeptIdList = resp4.getParentIds();

          //钉钉的规则（不能修改）：部门内的人数是计算本部门及其子部门里的人数相加的，重复的人员只计算一次。
          //因此按照钉钉规则进行统计部门的相应的人数
          if (id==1L){
              //如果id=1L，说明是根部门杰普软件，通过调用钉钉API直接得到人数
              count = c;
          }else if(subDeptIdList.size()==0&&parentDeptIdList.size()>0){
              //如果该部门对应无子部门，有父部门，说明为最小级部门，人数为该部门的人数（调用钉钉API获取部门用户Id列表）
              req1.setDeptId(String.valueOf(id));
              OapiUserGetDeptMemberResponse resp1 = client1.execute(req1, token);
              List userIdlist = resp1.getUserIds();
              count = new Long((long)(userIdlist.size()));
          }else {
             /* 如果该部门对应有子部门以及父部门，说明为一级、二级、三级部门
              钉钉的规则：部门内的人数是计算本部门及其子部门里的人数相加的，重复的人员只计算一次。
              因此将本部门+子部门的成员id放到一个hashset集合里面，进行去重，得到size，即可算出人数*/

              //1.得到本部门的成员id并存于set集合中（调用钉钉API获取部门用户Id列表）
              req1.setDeptId(String.valueOf(id));
              OapiUserGetDeptMemberResponse resp1 = client1.execute(req1, token);
              List selfUserIdlist= resp1.getUserIds();
              Set<String> selfUserIdSet = new HashSet<String>(selfUserIdlist);
              selfUserIdSet.addAll(selfUserIdlist);

              //2.得到子部门的成员id并存于set集合
              //定义一个集合：当前部门的所有子部门的用户id的集合
              List subDeptUserIdlists = new ArrayList();
              //遍历得到每个子部门的成员id，需要考虑：子部门下是否还存在部门
              for (Long subDeptId:subDeptIdList) {
                  //得到子部门的id后，应该再次判断，是否还存在子部门
                  //2.1 获取子部门id列表（调用钉钉API获取子部门id列表）
                  req3.setId(String.valueOf(subDeptId));
                  OapiDepartmentListIdsResponse resp3_1 = client3.execute(req3, token);
                  List<Long> subsDeptIdList = resp3_1.getSubDeptIdList();

                  //如果还存在子部门
                  if (subsDeptIdList.size()>0){
                      for (Long subsDeptId:subsDeptIdList) {
                          //得到子部门的子部门id后，应该再次判断，是否还存在子部门
                          //2.2 获取子部门id列表（调用钉钉API获取子部门id列表）
                          req3.setId(String.valueOf(subsDeptId));
                          OapiDepartmentListIdsResponse resp3_2 = client3.execute(req3, token);
                          List<Long> subssDeptIdList = resp3_2.getSubDeptIdList();

                          ////如果还存在子部门
                          if (subssDeptIdList.size()>0){
                              for (Long subssDeptId:subssDeptIdList) {
                                  //得到子部门的子部门的子部门id后，应该再次判断，是否还存在子部门
                                  //2.3 获取子部门id列表（调用钉钉API获取子部门id列表）
                                  req1.setDeptId(String.valueOf(subssDeptId));
                                  OapiUserGetDeptMemberResponse resp1_3 = client1.execute(req1, token);
                                  List subssDeptUserIdlist = resp1_3.getUserIds();
                                  //把得到的子部门的成员id遍历保存到subDeptUserIdlists中
                                  for (Object subsDeptUserId: subssDeptUserIdlist) {
                                      subDeptUserIdlists.add(subsDeptUserId);
                                  }
                              }
                          }

                          //需要获取该子部门下的成员id
                          req1.setDeptId(String.valueOf(subsDeptId));
                          OapiUserGetDeptMemberResponse resp1_2 = client1.execute(req1, token);
                          List subsDeptUserIdlist = resp1_2.getUserIds();
                          //把得到的子部门的成员id遍历保存到subDeptUserIdlists中
                          for (Object subsDeptUserId: subsDeptUserIdlist) {
                              subDeptUserIdlists.add(subsDeptUserId);
                          }
                      }
                  }

                  //如果不存在子部门，直接获取子部门的成员id
                  req1.setDeptId(String.valueOf(subDeptId));
                  OapiUserGetDeptMemberResponse resp1_1 = client1.execute(req1, token);
                  List subDeptUserIdlist = resp1_1.getUserIds();

                  //把得到的子部门的成员id遍历保存到subDeptUserIdlists中
                  for (Object subDeptUserId: subDeptUserIdlist) {
                      subDeptUserIdlists.add(subDeptUserId);
                  }
              }

              //把该部门的所有子部门的成员id都保存到subDeptUserIdlists后进行去除重复人员
              Set<String> subDeptUserIdSet = new HashSet<String>(subDeptUserIdlists);
              subDeptUserIdSet.addAll(subDeptUserIdlists);

              //3. 本部门的id和子部门的成员id都得到后，保存到一个set集合，再次去重
              Set userIdset = new HashSet();
              userIdset.addAll(selfUserIdSet);
              userIdset.addAll(subDeptUserIdSet);
              count = new Long((long)(userIdset.size()));
          }
          }catch (ApiException e){
              throw new CustomerException(CodeStatus.ERROR);
          }
          return  count;
      }

    /**
     * 将从钉钉中获取到的部门数据保存到本地数据库，实现同步
     * @param deparmentList 保存到本地数据库的部门集合
     * @throws CustomerException
     */
    @Override
    public void insertDepartment(List<Deparment> deparmentList) throws CustomerException {
        if (deparmentList==null){
            throw new CustomerException(CodeStatus.SYN_DINGDING);
        }else {
            SqlSession sqlSession = factory.openSession();
            DeparmentExMapper departmentExMapper = sqlSession.getMapper(DeparmentExMapper.class);
            //调用dao层：进行插入数据之前先清空表数据
            departmentExMapper.truncateTableDeparment();
            departmentExMapper.insertDeparmentList(deparmentList);
            sqlSession.commit();
        }
    }

    /**
     * 查询上级部门查询对应部门信息
     * @param parentId 上级部门id
     * @return
     * @throws CustomerException
     */
    @Override
    public List<Deparment> selectDepartmentByParentId(Long parentId) throws CustomerException {
        //通过本地数据库保存的数据进行查询
        //如果部门id为空，显示异常
        if (parentId==null) {
            throw new CustomerException(CodeStatus.NOT_DEPT);
        }else{
            SqlSession sqlSession = factory.openSession();
            DeparmentMapper departmentMapper = sqlSession.getMapper(DeparmentMapper.class);
            DeparmentExample deparmentExample = new DeparmentExample();
            //调用dao层:查询对应的部门信息
            deparmentExample.createCriteria().andParentIdEqualTo(parentId);
            List<Deparment> deptList = departmentMapper.selectByExample(deparmentExample);
            sqlSession.commit();
            return deptList;
        }
    }

    @Override
    public Deparment newInsertDepartment(Long parentId, String name, Boolean autoCreate) throws CustomerException {
        //新增子部门：因为部门编号需要与钉钉保持一致，因此从钉钉数据库新增部门，得到部门id，再保存到本地数据库
        try{
            //调用钉钉API完成新增功能
            String token = new DingDingUtil().getToken();
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/create");
            OapiDepartmentCreateRequest request = new OapiDepartmentCreateRequest();
           //如果parentId为空，则认为添加到根部门
            if (parentId==null){
                request.setParentid("1");
            }else{
                request.setParentid(String.valueOf(parentId));
            }
            request.setCreateDeptGroup(autoCreate);
            request.setName(name);
            OapiDepartmentCreateResponse response = client.execute(request,token);

            //获取创建的部门id
            Long deptId = response.getId();
            String crowd = null;
            //如果选择创建部门群，群名默认为部门名
            if (autoCreate==true){
                crowd=name;
            }
            //调用4参构造器，默认新创建的部门人数为0
            Deparment department = new Deparment(deptId,name,parentId,crowd);
            return  department;
        }catch (ApiException e){
            throw new CustomerException(CodeStatus.ERROR);
        }
    }

    /**
     * 保存新增的部门信息到本地数据库，实现同步
     * @param deparment 从钉钉新增部门后得到的部门信息
     * @throws CustomerException 自定义异常
     */
    @Override
    public void savenewInsertDepartment(Deparment deparment) throws CustomerException {
        if (deparment == null) {
            throw new CustomerException(CodeStatus.NOT_DEPT);
        } else {
            //调用dao层保存部门信息
            SqlSession sqlSession = factory.openSession();
            DeparmentMapper departmentMapper = sqlSession.getMapper(DeparmentMapper.class);
            //调用dao层:查询对应的部门信息
            departmentMapper.insert(deparment);
            sqlSession.commit();
        }
    }
}
