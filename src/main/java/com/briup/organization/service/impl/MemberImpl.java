package com.briup.organization.service.impl;

import com.briup.organization.exception.CustomerException;
import com.briup.organization.service.MemberService;
import com.briup.organization.util.CodeStatus;
import com.briup.organization.util.DingDingUtil;
import com.briup.organization.util.GetAccessTokenUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetDeptMemberRequest;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.response.OapiUserGetDeptMemberResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.tools.javac.jvm.Code;
import com.taobao.api.ApiException;

/**
 * @Description: 成员接口实现
 * @Author: GX Cui
 * @Date 3:52 下午 2020/4/20
 */
public class MemberImpl implements MemberService {
    @Override
    public void memberDataSynchronization() throws CustomerException {
        //获取部门列表
        //遍历部门列表
        //获取钉钉数据
        

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
            rsp = client.execute(req, GetAccessTokenUtil.getAT());

        } catch (ApiException | JsonProcessingException e) {
            throw new CustomerException(CodeStatus.ERROR);
        }
        return rsp.getBody();
    }
    /**
     * @Description: TODO
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
            rsp = client.execute(req, "09875b817e553eea80248227bdc480af");
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.ERROR);
        }
        return rsp.getBody();
    }

}
