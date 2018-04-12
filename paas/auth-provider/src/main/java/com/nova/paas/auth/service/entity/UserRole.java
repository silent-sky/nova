package com.nova.paas.auth.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * zhenghaibo
 * 18/4/10 17:33
 */
@Table(name = "au_user_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    private String id;
    private String tenantId;
    private String appId;
    private String orgId;
    private String roleCode;
    /**
     * @see com.nova.paas.common.constant.AuthConstant.RoleType
     */
    private Integer orgType;
    private String modifier;
    private long modifyTime;
    private Boolean defaultRole;
    private String deptId;
    private Boolean delFlag;

    public UserRole(
            String id,
            String tenantId,
            String appId,
            String orgId,
            String roleCode,
            Integer orgType,
            String modifier,
            long modifyTime,
            Boolean delFlag) {
        this.id = id;
        this.tenantId = tenantId;
        this.appId = appId;
        this.orgId = orgId;
        this.roleCode = roleCode;
        this.orgType = orgType;
        this.modifier = modifier;
        this.modifyTime = modifyTime;
        this.delFlag = delFlag;
    }
}
