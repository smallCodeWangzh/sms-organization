package com.briup.organization.web.controller;

import com.briup.organization.bean.Member;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.service.impl.MemberImpl;
import com.briup.organization.util.CodeStatus;
import com.briup.organization.util.ExcelUtils;
import com.briup.organization.util.Message;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Message> synchronizationMember(){


        memberService.memberDataSynchronization();
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "查询某个部门下的所有成员")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "departmentId",value = "部门Id",paramType = "form")
    )
    @GetMapping("/{departmentId}")
    public ResponseEntity<Message> queryMembersByDepartmentId(@PathVariable String departmentId){
        List<Member> list = memberService.getMembersByDeptId(departmentId);
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS,list));
    }

    @ApiOperation(value = "根据成员id查询详情")
    @GetMapping("/single/{id}")
    public ResponseEntity<Message> queryMemberDetailById(@PathVariable String id){
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS,memberService.getMemberDetailById(id)));
    }

    @ApiOperation(value = "新增成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "成员编号",required = false),
            @ApiImplicitParam(name = "name", value = "成员名字",required = true,dataType = "String"),
            @ApiImplicitParam(name = "email", value = "邮箱",required = false),
            @ApiImplicitParam(name = "jobNumber", value = "工号",required = false),
            @ApiImplicitParam(name = "departmentId", value = "部门编号",required = false,allowMultiple = true,dataType = "Int"),
            @ApiImplicitParam(name = "workAddress", value = "工作地点",required = false),
            @ApiImplicitParam(name = "schoolClazz", value = "学校班级",required = false),
            @ApiImplicitParam(name = "graduateSchool", value = "毕业学校",required = false),
            @ApiImplicitParam(name = "roleName", value = "角色名字",required = false),
            @ApiImplicitParam(name = "phone", value = "手机号码",required = true,dataType = "String"),
            @ApiImplicitParam(name = "position", value = "职位",required = false),
    })
    @PostMapping("/")
    public ResponseEntity<Message> addMember(String id,
                                             String name,
                                             String email,
                                             String jobNumber,
                                             int[] departmentId,
                                             String workAddress,
                                             String schoolClazz,
                                             String graduateSchool,
                                             String roleName,
                                             String phone,
                                             String position){
        //id可自定义，如果不填则会自动生成
        //部门id要求是一个集合，中括号包裹，多个用英文逗号隔开

       Member member = new Member();
       member.setId(id);
       member.setName(name);
       member.setEmail(email);
       member.setJobNumber(jobNumber);
       member.setWorkAddress(workAddress);
       member.setSchoolClazz(schoolClazz);
       member.setGraduateSchool(graduateSchool);
       member.setRoleName(roleName);
       member.setPhone(phone);
       member.setPosition(position);
       try{
           memberService.addMember(member, departmentId);
       }catch (CustomerException e){
           return ResponseEntity.ok(new Message(CodeStatus.USER_CREATE_FAILED));
       }
       return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "修改单个成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "成员id",required = true),
            @ApiImplicitParam(name = "name", value = "成员名字"),
            @ApiImplicitParam(name = "email", value = "邮箱"),
            @ApiImplicitParam(name = "jobNumber", value = "工号"),
            @ApiImplicitParam(name = "departmentId", value = "部门编号",dataType = "String",allowMultiple = true),
            @ApiImplicitParam(name = "workAddress", value = "工作地点"),
            @ApiImplicitParam(name = "schoolClazz", value = "学校班级"),
            @ApiImplicitParam(name = "graduateSchool", value = "毕业学校"),
            @ApiImplicitParam(name = "roleName", value = "角色名字"),
            @ApiImplicitParam(name = "phone", value = "手机号码"),
            @ApiImplicitParam(name = "position", value = "职位")
    })
    @PutMapping("/")
    public ResponseEntity<Message> updateMember(Member member,int[] departmentId){
        try {
            memberService.updateMember(member,departmentId);

        }catch (CustomerException e){
            return ResponseEntity.ok(new Message(CodeStatus.ERROR));
        }
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "删除多个成员")
    @ApiImplicitParam(name = "ids", value = "删除的用户id列表",required = true,allowMultiple = true)
    @DeleteMapping("/")
    public ResponseEntity<Message> bulkDeleteMember(@RequestParam(value = "ids",required = true) List<String> ids){
        System.out.println(ids);
        try{
            memberService.deleteMemberByIds(ids);
        }catch (CustomerException e){
            return ResponseEntity.ok(new Message(CodeStatus.ERROR));
        }
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }


    @ApiOperation(value = "下载excel模版",notes="注意！测试的时候请将地址粘贴到浏览器地址栏测试",produces ="application/octet-stream")
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcelTemplate(HttpServletResponse response){
        //提供文件下载，文件路径未项目路径file包
        File file = new File("src/main/resources/briup-template.xls");//新建一个文件

        byte[] body = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            body = new byte[is.available()];
            is.read(body);
        } catch (IOException e) {
            return new ResponseEntity<byte[]>(body, HttpStatus.FOUND); //暂时用找不到文件来替代
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + file.getName());
        HttpStatus statusCode = HttpStatus.OK;
        return new ResponseEntity<byte[]>(body, headers, statusCode);
    }

    @ApiOperation(value = "根据部门id导出部门成员列表-Excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "select", value = "是否勾选 仅导出未激活员工，1代表勾选，其余参数代表未勾选", paramType = "path"),
            @ApiImplicitParam(name = "ids", value = "选择的员工的id列表，使用逗号隔开", paramType = "path"),
            @ApiImplicitParam(name = "title", value = "自定义导出字段，多个字段用逗号隔开，为空默认导出全部",paramType = "query",defaultValue = "",dataType = "String"),
    })
    @GetMapping("/export/{select}/{ids}")
    public void downloadExcelWithMember(@PathVariable int select,
                                        @PathVariable String ids,
                                        @RequestParam String title,
                                        HttpServletResponse response){
        // 查询出所有当前部门的成员信息
        if(StringUtils.isBlank(ids)){
           throw new CustomerException(CodeStatus.ERROR);
        }
        List<Object> dataObjs = memberService.createExcel(select,ids,title);

        //心态炸了呀，把Object转换为无错误的固定格式的list，后期再进行封装，主要场景会不同
        List<Map<String,Object>> list = new ArrayList<>();
        Object obj = dataObjs.get(3);
        if(obj instanceof ArrayList<?>){
            for(Object o : (List<?>)obj){
                Map<String,Object> map = new HashMap<String,Object>();
                if(o instanceof Map<?,?>){
                    for (Map.Entry<?, ?> oo : ((Map<?, ?>) o).entrySet()){
                        map.put(String.class.cast(oo.getKey()),Object.class.cast(oo.getValue()));
                    }
                    list.add(Map.class.cast(o));
                }
            }
        }

        //第三个数据，Object转String集合，然后String集合转String数组
        List<String> list2 = new ArrayList<>();
        Object obj2 = dataObjs.get(2);
        if(obj2 instanceof ArrayList<?>){
            for(Object o2 : (ArrayList<?>)obj2){
                String hah = String.class.cast(o2);
                list2.add(hah);
            }
        }
        String[] re2 = new String[list2.size()];
        for (int i=0;i<re2.length;i++){
            re2[i] = list2.get(i);
        }

        //调用工具类导出excel
        ExcelUtils.createExcel(response,(String) dataObjs.get(0),(String[]) dataObjs.get(1),re2, list);
    }
}
