package com.briup.organization.web.controller;

import com.briup.organization.bean.Deparment;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.service.IDepartmentService;
import com.briup.organization.util.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: kangya
 * @CreateTime: 2020-4-19 0:01
 * @Description: 部门管理的访问接口
 */
@Api(value="部门controller",tags={"部门管理接口"})
@RequestMapping("/department")
@RestController
public class DapartmentController {

    @Autowired
    private IDepartmentService deptservice;

    @Scheduled(cron="0 0 2 * * ? ")
    @ApiOperation(value = "同步钉钉上部门的数据到本地")
    @GetMapping("/dingding")
    public ResponseEntity<Message> synchronizationDepartment() throws CustomerException {
          //调用service层getDingDepartList方法，获取到要保存到本地的Deparment的集合
          List<Deparment> deparmentList = deptservice.getDepartList();

          //调用service层insertDepartment方法：将从钉钉中获取到的部门数据保存到本地数据库，实现同步
          deptservice.insertDepartment(deparmentList);

          //返回响应体
         return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));

    }

    @ApiOperation(value = "查询上级部门查询对应部门信息")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "parentId",value = "上级部门id",required = true,dataType = "Long")
    )
    @GetMapping("/{parentId}")
    public ResponseEntity<Message<Deparment>> getDepartmentList(Long parentId) throws CustomerException {
        //调用service层：查询对应的部门信息
        List<Deparment>  deptList =deptservice.selectDepartmentByParentId(parentId);
        //返回响应体
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS,deptList));
    }



    @ApiOperation(value = "新增子部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId",value = "上级部门id，根部门值为：1\"",required = true,dataType = "Long"),
            @ApiImplicitParam(name = "name",value = "部门名称",required = true,dataType = "String"),
            @ApiImplicitParam(name = "autoCreate",value = "选择是否创建部门群,true为创建",required = true,dataType = "Boolean")
            } )
    @PostMapping
    public ResponseEntity<Message> newInsertDepartment(Long parentId,String name,Boolean autoCreate) throws CustomerException {
        //调用service层:获取从钉钉新增的部门信息
        Deparment department =  deptservice.newInsertDepartment(parentId,name,autoCreate);
        //调用service层：将刚刚从钉钉新增的部门信息保存到本地数据库
        deptservice.savenewInsertDepartment(department);
        //返回响应体
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));

    }
}
