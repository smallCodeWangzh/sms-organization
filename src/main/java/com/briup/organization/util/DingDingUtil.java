package com.briup.organization.util;

import com.briup.organization.exception.CustomerException;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.taobao.api.ApiException;

/**
 * 钉钉相关，所有跟钉钉相关的常量必须写在这里面
 * @author wangzh@briup.com
 */
public class DingDingUtil {
    /**
     * AppKey
     */
    public static final String APP_KEY = "dingtr8oxqmbqmywxgot";
    /**
     * AppSecret
     */
    public static final String APP_SECRET = "qhz2jl1QK6Xu0WO_kCd-KZ8-pOS6xlPg43pinvJ1tkFMZDpOTaf307MH-nBM07wR";
    /**
     * 获取AccessToken的URL
     */
    public static final String TOKEN_URL = "https://oapi.dingtalk.com/gettoken";

    /**
     * 根据用户id获取用户详情的URL
     * cuigx 修改
     */
    public static final String USER_GET = "https://oapi.dingtalk.com/user/get";

    /**
     * 根据部门id获取用户id列表的URL
     * cuigx 修改
     */
    public static final String DEPARTMENT_MEMBER_GET = "https://oapi.dingtalk.com/user/getDeptMember";

    /**
     * 成员表全部属性定义
     * cuigx修改
     */
    public static final String MEMBER_ALL_FILED = "编号,姓名,企业邮箱,工作地点,学校班级,毕业学校,角色,手机,职位,工号,是否激活,备用字段1,备用字段2";

    /**
     * 添加单个用户
     * cuigx修改
     */
    public static final String USER_CREATE = "https://oapi.dingtalk.com/user/create";

    /**
     * 删除单个用户
     * cuigx修改
     */
    public static final String USER_DELETE = "https://oapi.dingtalk.com/user/delete";

    /**
     * 修改单个用户
     * cuigx修改
     */
    public static final String USER_UPDATE = "https://oapi.dingtalk.com/user/update";



    /**
     *  获取access_token值
     */
    public static  String getToken() throws CustomerException {
        try {
            //钉钉官方文档获取token
            //地址：https://ding-doc.dingtalk.com/doc#/serverapi2/eev437
            DingTalkClient client = new DefaultDingTalkClient(TOKEN_URL);
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey(APP_KEY);
            req.setAppsecret(APP_SECRET);
            req.setHttpMethod("GET");
            OapiGettokenResponse rsp = null;
            rsp = client.execute(req);

            String token = rsp.getAccessToken();
            return token;
        }catch (ApiException e){
            throw new CustomerException(CodeStatus.ERROR);
        }

    }
}
