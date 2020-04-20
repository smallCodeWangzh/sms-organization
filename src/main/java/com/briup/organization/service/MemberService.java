package com.briup.organization.service;

import com.briup.organization.exception.CustomerException;

/**
 * @Description: 成员管理业务逻辑接口
 * @Author: GX Cui
 * @Date 3:38 下午 2020/4/20
 */
public interface MemberService {


    public void memberDataSynchronization () throws CustomerException;
}
