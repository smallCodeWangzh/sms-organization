package com.briup.organization.handler;

import com.briup.organization.exception.CustomerException;
import com.briup.organization.util.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * 自定义异常处理器，只要在web层抛出的自定义异常都被自己所处理
 *
 * @author wangzh
 * */
@ControllerAdvice
public class CustomerExceptionHandler{

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<Message> handler(CustomerException exception) {
        return ResponseEntity.status(exception.getCodeStatus().getCode())
                .body(new Message(exception.getCodeStatus()));
    }
}
