package com.briup.organization.bean;

import java.io.Serializable;
import lombok.Data;

/**
 * 部门的实体类
 */
@Data
public class Deparment implements Serializable {
    /**
     * 部门编号
     */
    private Long id;
    /**
     * 部门名字
     */
    private String name;
    /**
     * 上级部门id，根部门值为：1
     */
    private Long parentId;
    /**
     * 部门的成员数量，默认值为:0
     */
    private Long count;
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

    /**
     * 无参构造器
     */
    public Deparment() {

    }

    /**
     * 有参构造器：5参
     * @param id  部门编号
     * @param name  部门名称
     * @param parentId 上级部门
     * @param count  部门人数
     * @param crowd  部门对应的群名
     */
    public Deparment(Long id, String name, Long parentId, Long count, String crowd) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.count = count;
        this.crowd = crowd;
    }

    /**
     * 有参构造器：4参，不包括部门人数
     * @param id     部门编号
     * @param name   部门名称
     * @param parentId   上级部门
     * @param crowd      部门对应的群名
     */
    public Deparment(Long id, String name, Long parentId, String crowd) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.crowd = crowd;
    }

}