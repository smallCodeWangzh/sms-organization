package com.briup.organization.web.controller;

import com.briup.organization.bean.Member;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.service.impl.MemberImpl;
import com.briup.organization.util.CodeStatus;
import com.briup.organization.util.ExcelUtils;
import com.briup.organization.util.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    @ApiOperation(value = "根据成员id查询详情")
    @GetMapping("/single/{id}")
    public ResponseEntity<Message> queryMemberDetailById(@PathVariable String id) throws CustomerException{
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS,memberService.getMemberDetailById(id)));
    }

    @ApiOperation(value = "新增成员")
    @GetMapping("/")
    public ResponseEntity<Message> addMember(Member member) throws CustomerException{
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "修改单个成员")
    @PutMapping("/")
    public ResponseEntity<Message> updateMember(Member member) throws CustomerException{
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "删除多个成员")
    @DeleteMapping("/")
    public ResponseEntity<Message> bulkDeleteMember(@RequestParam List<String> ids) throws CustomerException{
        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }


    @ApiOperation(value = "下载excel模版",notes="注意！测试的时候请将地址粘贴到浏览器地址栏测试",produces ="application/octet-stream")
    @GetMapping("/download")
    public ResponseEntity<Message> downloadExcelTemplate(HttpServletResponse response) throws CustomerException{
        //提供文件下载，文件路径未项目路径file包

        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }

    @ApiOperation(value = "根据部门id导出部门成员列表-Excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "select", value = "是否勾选 仅导出未激活员工，1代表勾选，其余参数代表未勾选", paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "选择的员工的id列表，使用逗号隔开", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "自定义导出字段，多个字段用逗号隔开，为空默认导出全部", paramType = "query"),
    }
    )
    @GetMapping("/export/{select}/{ids}")
    public ResponseEntity<Message> downloadExcelWithMember(@PathVariable int select,
                                                           @PathVariable String ids,
                                                           @RequestParam String title) throws CustomerException{
        // 查询出所有当前部门的成员信息
//        String excelName = "member_list";
//        String[] headList = new String[]{"编号","标题","内容"};
//        String[] fieldList = new String[]{"id","title","content"};
//        List<Map<String, Object>> dataList = new ArrayList<>();
//        List<Article> list =articleService.findAll();
//        for(Article a : list){
//            Map<String, Object> map = new HashMap<>();
//            map.put("id",a.getId());
//            map.put("title",a.getTitle());
//            map.put("content",a.getContent());
//            dataList.add(map);
//        }
//
//        //调用工具类导出excel
//        try {
//            ExcelUtils.createExcel(response,excelName,headList,fieldList,dataList);
//        } catch (Exception e) {
//            return ResponseEntity.ok(new Message(CodeStatus.ERROR));
//        }

        return ResponseEntity.ok(new Message(CodeStatus.SUCCESS));
    }



}
