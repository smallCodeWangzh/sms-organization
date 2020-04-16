package com.briup.organization.bean;

import java.io.Serializable;
import lombok.Data;

/**
 * tb_member
 * @author 
 */
@Data
public class Member implements Serializable {
    /**
     * 成员编号，与钉钉保持一致

     */
    private Long id;

    /**
     * 成员名字
     */
    private String name;

    /**
     * 企业邮箱

     */
    private String email;

    /**
     * 工作地点
     */
    private String workAddress;

    /**
     * 学校班级
     */
    private String schoolClazz;

    /**
     * 毕业学校
     */
    private String graduateSchool;

    /**
     * 角色名字
     */
    private String roleName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 职位
     */
    private String position;

    /**
     * 成员状态 1是已激活，0是未激活，默认是1
     */
    private Boolean status;

    /**
     * 备用字段1
     */
    private String backup1;

    /**
     * 备用字段2
     */
    private String backup2;

    private static final long serialVersionUID = 1L;
}