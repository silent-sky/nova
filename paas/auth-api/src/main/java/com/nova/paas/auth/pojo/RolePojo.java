package com.nova.paas.auth.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/1/11 19:38
 */
@Data
public class RolePojo implements Serializable {
    private static final long serialVersionUID = 3073867806457952623L;
    private String id;
    private String tenantId;
    private String roleCode;
    private String roleName;
    /**
     * @see com.nova.paas.common.constant.AuthConstant.RoleType
     */
    private Integer roleType;
    private String description;
}
