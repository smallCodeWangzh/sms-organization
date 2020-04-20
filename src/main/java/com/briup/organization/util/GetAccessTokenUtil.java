package com.briup.organization.util;

import com.briup.organization.bean.custom.Token;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.ApiException;

/**
 * @Description: 手动获取钉钉访问接口令牌
 * @Author: GX Cui
 * @Date 3:30 下午 2020/4/20
 */
public class GetAccessTokenUtil {
    public static String getAT() throws ApiException, JsonProcessingException {
        //实例化token实体类
        Token token = null;
        //钉钉官方文档获取token
        //地址：https://ding-doc.dingtalk.com/doc#/serverapi2/eev437
        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.TOKEN_URL);
        OapiGettokenRequest req = new OapiGettokenRequest();
        req.setAppkey(DingDingUtil.APP_KEY);
        req.setAppsecret(DingDingUtil.APP_SECRET);
        req.setHttpMethod("GET");
        OapiGettokenResponse rsp = client.execute(req);
        //jackson解析json并且封装成对象
        ObjectMapper mapper = new ObjectMapper();
        token = mapper.readValue(rsp.getBody(), Token.class);

        return token.getAccess_token();

    }

    public static void main(String[] args) throws JsonProcessingException, ApiException {
        System.out.println(getAT());
    }
}
