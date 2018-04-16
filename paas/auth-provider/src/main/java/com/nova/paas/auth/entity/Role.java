package com.nova.paas.auth.entity;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * zhenghaibo
 * 18/4/10 17:33
 */
@Table(name = "au_role")
@Data
public class Role implements Serializable {
    private String id;
    private String roleCode;
    private String roleName;
    /**
     * @see com.nova.paas.common.constant.AuthConstant.RoleType
     */
    private Integer roleType;
    private String description;
    private String appId;
    private String tenantId;
    private Boolean delFlag;
    private String creator;
    private Long createTime;
    private String modifier;
    private Long modifyTime;
}
