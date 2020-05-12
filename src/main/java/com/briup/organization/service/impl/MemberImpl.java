package com.briup.organization.service.impl;

import com.briup.organization.bean.Deparment;
import com.briup.organization.bean.DeparmentExample;
import com.briup.organization.bean.DeparmentMemberKey;
import com.briup.organization.bean.Member;
import com.briup.organization.bean.custom.DingMemberDetail;
import com.briup.organization.bean.custom.DingMemberIds;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.mapper.DeparmentMapper;
import com.briup.organization.mapper.DeparmentMemberMapper;
import com.briup.organization.mapper.MemberMapper;
import com.briup.organization.mapper.ex.MemberExMapper;
import com.briup.organization.service.MemberService;
import com.briup.organization.util.CodeStatus;
import com.briup.organization.util.DingDingUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetDeptMemberRequest;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.response.OapiUserGetDeptMemberResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description: 成员接口实现
 * @Author: GX Cui
 * @Date 3:52 下午 2020/4/20
 */
@Service
public class MemberImpl implements MemberService {

    @Autowired
    private DepartmentImpl deptservice;

    @Resource
    private DeparmentMapper deptMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MemberExMapper memberExMapper;

    @Autowired
    private DeparmentMemberMapper dmmapper;

    @Override
    public void memberDataSynchronization() throws CustomerException {
        //获取部门列表
        List<Deparment>  deptlist =  deptMapper.selectByExample(new DeparmentExample());
        //准备二维数组接收所有用户id
        List<List<String>> allIdValue = new ArrayList<>();
        //准备一个map来接收部门id和对应的用户id的集合
        Map<Long,List<String>> dmmap = new HashMap<>();


        //遍历部门列表拿到部门id
        for (Deparment deparment : deptlist){

            String result = getUserIdListByDeptId(String.valueOf(deparment.getId()));
            //解析
            ObjectMapper mapper = new ObjectMapper();
            DingMemberIds ids = null;
            try {
                ids = mapper.readValue(result, DingMemberIds.class);
                //拿到所有的id
                List<String> idArr = Arrays.asList(ids.getUserIds());
                allIdValue.add(idArr);
                dmmap.put(deparment.getId(),idArr);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        //遍历id，拿到用用户详情
        for (List<String> a: allIdValue) {
            for (String id : a){
                //先判断再调用，不然会有调用次数限制，先和数据库进行比对，再进行接口调用
                Member m =  memberMapper.selectByPrimaryKey(id);
                if(m!=null){
                    continue;
                }else {
                    String temp = getMemberDetailByUserId(id);
                    ObjectMapper mapper = new ObjectMapper();
                    DingMemberDetail detail = null;
                    try {
                        detail = mapper.readValue(temp, DingMemberDetail.class);

                        //设置加入数据库中的数据，5月1日cgx修改表，去掉id自动增长
                        Member member = new Member();
                        member.setId(detail.getUserid()); //成员id
                        member.setName(detail.getName()); //成员名字
                        //判断是否有邮箱
                        if (detail.getEmail() == null) {
                            member.setEmail("未开通邮箱"); //企业邮箱
                        } else {
                            member.setEmail(detail.getEmail()); //企业邮箱
                        }
                        member.setJobNumber(detail.getJobnumber()); //工号
                        //判断是否显示手机号
                        if (detail.isHide()) {
                            member.setPhone(detail.getTel()); //手机号码
                        } else {
                            member.setPhone("号码已隐藏"); //手机号码
                        }
                        //判断是否有角色，多个角色只展示第一个
                        if (detail.getRoles() == null || detail.getRoles().length == 0) {
                            member.setRoleName("无角色");
                        } else {
                            member.setRoleName(detail.getRoles()[0].getName()); //角色名，角色名不止一个，如果有多个显示第一个
                        }
                        member.setPosition(detail.getPosition());//职位
                        member.setStatus(detail.isActive());//成员状态 1是已激活，0是未激活，默认是1
                        member.setWorkAddress("");//工作地点
                        member.setSchoolClazz("");//学校班级
                        member.setGraduateSchool(""); //毕业学校

                        //memberMapper.insert(member);
                        //memberMapper.insertSelective(member);
                        memberExMapper.insertWithDingId(member);

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //遍历map插入到桥表中
        for (Map.Entry<Long,List<String>> entry:dmmap.entrySet()){
            for (String id:entry.getValue()) {
                DeparmentMemberKey dm = new DeparmentMemberKey();
                dm.setMemberId(id);
                dm.setDeparmentId(entry.getKey());
                System.out.println(dm.getMemberId()+"------"+dm.getDeparmentId());
                dmmapper.insert(dm);
            }
        }

    }

    @Override
    public List<Member> getMembersByDeptId(String deptId) throws CustomerException {
        return memberExMapper.getAllMembersByDeptId(deptId);
    }

    @Override
    public Member getMemberDetailById(String id) throws CustomerException {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addMember(Member member) throws CustomerException {

        //钉钉官方添加，获得钉钉官方ID

        //本地数据库添加

    }

    /* 私有方法封装  */
    /**
     * @Description: 根据用户id获取用户详情
     * @Author: GX Cui
     * @Date 5:21 下午 2020/4/20
     * @Param String userId  用户id
     * @Return 返回值为json字符串
     */
    private String getMemberDetailByUserId(String userId) throws  CustomerException{
        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.USER_GET);
        OapiUserGetRequest req = new OapiUserGetRequest();
        req.setUserid(userId);
        req.setHttpMethod("GET");
        OapiUserGetResponse rsp = null;
        try {
            rsp = client.execute(req, DingDingUtil.getToken());
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.ERROR);
        }
        return rsp.getBody();
    }
    /**
     * @Description: 根据部门id获取用户id列表
     * @Author: GX Cui
     * @Date 5:24 下午 2020/4/20
     * @Param String deptId  部门id
     * @Return 返回值为json字符串
     */
    private String getUserIdListByDeptId(String deptId) throws CustomerException{
        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.DEPARTMENT_MEMBER_GET);
        OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
        req.setHttpMethod("GET");
        req.setDeptId(deptId);
        OapiUserGetDeptMemberResponse rsp = null;
        try {
            rsp = client.execute(req, DingDingUtil.getToken());
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.ERROR);
        }
        return rsp.getBody();
    }

    private String createUser(){
        return null;
    }

}
