package com.briup.organization.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.omg.PortableInterceptor.SUCCESSFUL;

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
    PARAM_IS_NULL(500,"参数为空");
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String message;
}
