package com.briup.organization.util;

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

}
