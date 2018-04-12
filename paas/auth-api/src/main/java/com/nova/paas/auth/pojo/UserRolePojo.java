package com.nova.paas.auth.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/8 19:48
 */
@Data
public class UserRolePojo implements Serializable {
    private static final long serialVersionUID = -8475563057884553587L;

    private String tenantId;
    private String appId;
    private String roleCode;
    private Integer orgType;
    private String orgId;
    private Boolean defaultRole;
    private String deptId;
}
