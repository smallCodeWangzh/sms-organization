package com.briup.organization.web.controller;

import com.briup.organization.bean.Deparment;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.service.IDepartmentService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiDepartmentCreateRequest;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.request.OapiUserGetDeptMemberRequest;
import com.dingtalk.api.response.OapiDepartmentCreateResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.briup.organization.util.*;
import com.dingtalk.api.response.OapiUserGetDeptMemberResponse;
import com.taobao.api.ApiException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


/**
 * ClassName: DapartmentController
 * Description:部门管理的web层
 * date: 2020/4/19 0:01
 *
 * @author kangya
 * @since JDK 1.8
 */
@Api(value="部门controller",tags={"部门管理接口"})
@RequestMapping("/department")
@RestController
public class DapartmentController {
    //注入service层
    @Autowired
    private IDepartmentService deptservice;


    //定义全局变量：token值
    String token =null;
    //定义全局变量：部门id
    Long id = null;
    //定义全局变量：部门名称
    String name = null;
    //定义全局变量：上级部门id
    Long parent_id = null;
    //定义全局变量：部门对应的企业群名
    String crowd = null;
    //定义全局变量：部门人数
    Long count = null;

    /*同步钉钉上的部门数据*/
    @ApiOperation(value = "同步钉钉上的数据到本地")
    @GetMapping("/dingding")
    public ResponseEntity<Message> synchronizationDepartment() throws CustomerException {

      try {
          //调用DingDingUtil中的getToken方法获取token
           token = new DingDingUtil().getToken();

          //获取部门信息
          DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
          OapiDepartmentListRequest request = new OapiDepartmentListRequest();
          OapiDepartmentListResponse response = client1.execute(request, token);
          //获取部门人数
          DingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getDeptMember");
          OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();

          //遍历钉钉中的部门数据
          for (OapiDepartmentListResponse.Department department : response.getDepartment()) {
              Deparment deparment = new Deparment();
              //获取钉钉数据库中的部门编号
              id = department.getId();
              //获取钉钉数据库中的部门名称
              name = department.getName();
              //获取钉钉数据库中的上级部门id
              parent_id = department.getParentid();
              //获取钉钉数据库中的是否同步创建一个关联此部门的企业群
              crowd =department.getSourceIdentifier();
              deparment.setId(id);
              deparment.setName(name);
              deparment.setParentId(parent_id);
              deparment.setCrowd(crowd);

              //对部门id做非空判断
              if (id!=null){
                  req.setDeptId(id.toString());
              }
              OapiUserGetDeptMemberResponse rsp = client2.execute(req,token);
              //将获取到的用户id列表保存至List集合中,list数组的长度为部门人数
              List list = rsp.getUserIds();
              count = new Long((long) (list.size()));
              deparment.setCount(count);

              //调用service层：将从钉钉中获取到的部门数据保存到本地数据库，实现同步
              deptservice.insertDepartment(deparment);

          }
          //返回响应体
          return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
      }catch (ApiException e){
          throw new CustomerException(CodeStatus.ERROR);
      }

    }


    /*查询所有部门*/
    @ApiOperation(value = "查询上级部门查询对应部门信息")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "parentId",value = "上级部门id",required = true,dataType = "Long")
    )
    @GetMapping("/{parentId}")
    public ResponseEntity<Message<Deparment>> getDepartmentList(Long parentId) throws CustomerException {
        //通过本地数据库保存的数据进行查询
        //如果部门id为空，显示异常
        if (parentId==null) return ResponseEntity.badRequest().body(new Message(CodeStatus.NOT_DEPT));

        //调用service层
        List<Deparment>  deptList =deptservice.selectDepartmentByParentId(parentId);
        //返回响应体
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS,deptList));
    }


    /*新增子部门*/
    @ApiOperation(value = "新增子部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId",value = "上级部门id，根部门值为：1\"",required = true,dataType = "Long"),
            @ApiImplicitParam(name = "name",value = "部门名称",required = true,dataType = "String"),
            @ApiImplicitParam(name = "autoCreate",value = "选择是否创建部门群,true为创建",required = true,dataType = "Boolean")
            } )
    @PostMapping
    public ResponseEntity<Message<Deparment>> insertDepartment(Long parentId,String name,Boolean autoCreate) throws CustomerException {
        //新增子部门：因为部门编号需要与钉钉保持一致，因此从钉钉数据库新增部门，得到部门id，再保存到本地数据库
        //调用钉钉API完成新增功能
        try{
            String token = new DingDingUtil().getToken();
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/create");
            OapiDepartmentCreateRequest request = new OapiDepartmentCreateRequest();
            request.setParentid(parentId.toString());
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
            //调用service层
            deptservice.insertDepartment(department);
            //返回响应体
            return ResponseEntity.ok(new Message(CodeStatus.SUCCESS,department));
        }catch (ApiException e){
            throw new CustomerException(CodeStatus.ERROR);
        }

    }
}