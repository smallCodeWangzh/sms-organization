package com.briup.organization.bean;

import java.io.Serializable;
import lombok.Data;

/**
 * tb_deparment_member
 * @author 
 */
@Data
public class DeparmentMemberKey implements Serializable {
    /**
     * 部门id
     */
    private Long deparmentId;

    /**
     * 成员id
     */
    private String memberId;

    private static final long serialVersionUID = 1L;
}