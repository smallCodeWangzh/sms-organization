package com.briup.organization.exception;

import com.briup.organization.util.CodeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * 自定义异常，整个系统抛异常只能抛自定义异常
 *
 * @author wangzh@briup.com
 */
@Data
@AllArgsConstructor
public class CustomerException extends RuntimeException {
    private CodeStatus codeStatus;
}
