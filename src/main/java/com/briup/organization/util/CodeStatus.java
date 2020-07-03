package com.briup.organization.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.USER_EXCEPTION;

/**
 * 自定义状态码信息
 *
 * @author wangzh@briup.com
 */
@AllArgsConstructor
@Getter
public enum  CodeStatus {
    SUCCESS(200,"请求成功"),
    ERROR(500,"服务器内部错误"),
    PARAM_IS_NULL(500,"参数为空"),
    /**
     * kangya:新增4个状态码
     */
    APIEXCEPTION(502,"获取Token失败"),
    PARAM_MIS_MATCH(400,"参数不匹配"),
    SYN_DINGDING(501,"添加成功，同步钉钉失败"),
    NOT_DEPT(404,"部门不存在"),

    /**
     * Cuigx：新增状态码
     */
    JSON_PARSING_ERROR(500,"json解析错误"),
    API_EXCEPTION(500,"钉钉官方API调用异常"),
    CLASS_NOT_FOUNT(500,"服务器类未找到"),
    NO_SUCH_METHOD(500,"服务器无此方法"),
    INVOKE_EXCEPTION(500,"服务器反射功能异常"),
    USER_CREATE_FAILED(500,"新增单个用户失败"),
    USER_ID_ALREADY_EXISTS(60102,"UserID在公司中已存在"),
    USER_UPDATE_FAILED(500,"钉钉端修改单个用户信息失败，本地数据库不做任何操作"),
    USER_DELETE_ID_NOT_EXISTS(500,"删除操作前验证id输入有误"),
    USER_MOBILE_ALREADY_EXISTS(60104,"UserID在公司中已存在");
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String message;
}
