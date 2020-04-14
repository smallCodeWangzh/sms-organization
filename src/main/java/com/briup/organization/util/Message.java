package com.briup.organization.util;

import lombok.Data;

import java.util.Date;

/**
 *
 * 自定义响应结构
 * @author wangzh@briup.com
 */
@Data
public class Message<T> {
    private Integer code;
    private String message;
    private Long time;
    private T data;
    public Message(CodeStatus codeStatus) {
        this.code = codeStatus.getCode();
        this.message = codeStatus.getMessage();
        this.time = new Date().getTime();
    }

    public Message(CodeStatus codeStatus,T data) {
        this(codeStatus);
        this.data = data;
    }

}
