package com.briup.organization.bean;

import java.io.Serializable;
import lombok.Data;

/**
 * 部门类
 * @author wangzh@briup.com
 */
@Data
public class Deparment implements Serializable {
    /**
     * 部门编号,与钉钉上的部门编号保持一致
部门编号,与钉钉上的部门编号保持一致
部门编号,与钉钉上的部门编号保持一致
     */
    private Long id;

    /**
     * 部门名字
     */
    private String name;

    /**
     * 上级部门id，一级部门值为：0
     */
    private Long parentId;

    /**
     * 部门的成员数量，默认值为:0
     */
    private Long count;

    /**
     * 是否为父级部门，0为否，1为是
     */
    private Boolean isParent;

    /**
     * 群名
     */
    private String crowd;

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