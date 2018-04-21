package com.nova.paas.auth.entity;

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
    private String targetId;
    private String roleId;
    /**
     * @see com.nova.paas.common.constant.AuthConstant.TargetType
     */
    private Integer targetType;
    private String modifiedBy;
    private Long modifiedAt;
    //    private Boolean defaultRole;
    private String deptId;
    private Boolean delFlag;

    public UserRole(
            String id,
            String tenantId,
            String appId,
            String targetId,
            String roleId,
            Integer targetType,
            String modifiedBy,
            Long modifiedAt,
            Boolean delFlag) {
        this.id = id;
        this.tenantId = tenantId;
        this.appId = appId;
        this.targetId = targetId;
        this.roleId = roleId;
        this.targetType = targetType;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
        this.delFlag = delFlag;
    }
}
