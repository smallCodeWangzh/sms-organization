package com.briup.organization.bean;

import java.io.Serializable;

/**
 * 部门的实体类
 */
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
     * 有参构造器
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
     * @param id
     * @param name
     * @param parentId
     * @param crowd
     */
    public Deparment(Long id, String name, Long parentId, String crowd) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.crowd = crowd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getCrowd() {
        return crowd;
    }

    public void setCrowd(String crowd) {
        this.crowd = crowd == null ? null : crowd.trim();
    }

    public String getBackup1() {
        return backup1;
    }

    public void setBackup1(String backup1) {
        this.backup1 = backup1 == null ? null : backup1.trim();
    }

    public String getBackup2() {
        return backup2;
    }

    public void setBackup2(String backup2) {
        this.backup2 = backup2 == null ? null : backup2.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", parentId=").append(parentId);
        sb.append(", count=").append(count);
        sb.append(", crowd=").append(crowd);
        sb.append(", backup1=").append(backup1);
        sb.append(", backup2=").append(backup2);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}