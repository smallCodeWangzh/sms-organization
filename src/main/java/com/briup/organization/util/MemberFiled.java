package com.briup.organization.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 使用枚举对member对象的属性进行迭代并标号
 * @Author: GX Cui
 * @Date 7:37 下午 2020/5/12
 * @Param
 * @Return
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum MemberFiled {
    ID(1,"id","编号"),

    /**
     * 成员名字
     */
    NAME(2,"name","姓名"),

    /**
     * 企业邮箱
     */
    EMAIL(3,"email","企业邮箱"),

    /**
     * 工作地点
     */
    WORKADDRESS(4,"workAddress","工作地点"),

    /**
     * 学校班级
     */
    SCHOOLCLAZZ(5,"schoolClazz","学校班级"),

    /**
     * 毕业学校
     */
    GRADUATESCHOOL(6,"graduateSchool","毕业学校"),

    /**
     * 角色名字
     */
    ROLENAME(7,"roleName","角色"),

    /**
     * 手机号码
     */
    PHONE(8,"phone","手机"),

    /**
     * 职位
     */
    POSITION(9,"position","职位"),

    /**
     * 工号
     */
    JOBNUMBER(10,"jobNumber","工号"),

    /**
     * 成员状态
     */
    STATUS(11,"status","是否激活"),

    /**
     * 备用字段1
     */
    BACKUP1(12,"backup1","备用字段1"),

    /**
     * 备用字段2
     */
    BACKUP2(13,"backup2","备用字段2");

    /**
     * 状态码
     */
    private Integer number;

    /**
     * 信息
     */
    private String message;
    /**
     *
     */
    private String description;
}

