package com.briup.organization.web.controller;

import com.briup.organization.bean.Member;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.service.impl.MemberImpl;
import com.briup.organization.util.CodeStatus;
import com.briup.organization.util.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 成员管理访问接口
 * @Author: GX Cui
 * @Date 4:20 下午 2020/4/20
 */
@Api(value="部门controller",tags={"成员管理接口"})
@RequestMapping("/member")
@RestController
public class MemberController {

    @Autowired
    MemberImpl memberService;

    @ApiOperation(value = "同步钉钉上成员的数据到本地")
    @GetMapping("/sync")
    public ResponseEntity<Message> synchronizationMember() throws CustomerException {

        memberService.memberDataSynchronization();

        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "查询某个部门下的所有成员")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "departmentId",value = "部门Id",paramType = "query")
    )
    @GetMapping("/{departmentId}")
    public ResponseEntity<Message> queryMembersByDepartmentId(@PathVariable String departmentId) throws CustomerException{
        List<Member> list = memberService.getMembersByDeptId(departmentId);
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS,list));
    }

    @ApiOperation(value = "新增成员")
    @GetMapping("/")
    public ResponseEntity<Message> addMember() throws CustomerException{

        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "根据成员id查询详情")
    @GetMapping("/single/{id}")
    public ResponseEntity<Message> queryMemberDetailById() throws CustomerException{


        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }



}
