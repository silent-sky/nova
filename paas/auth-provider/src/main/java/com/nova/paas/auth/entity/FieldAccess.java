package com.nova.paas.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * zhenghaibo
 * 18/4/9 18:00
 * 字段权限实体
 */
@Table(name = "au_field_access")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldAccess implements Serializable {
    private static final long serialVersionUID = -1179316286787259367L;

    private String id;
    private String tenantId;
    private String roleId;
    private String entityId;
    private String fieldId;
    /**
     * @see com.nova.paas.common.constant.AuthConstant.FieldPermissionType
     */
    private Integer permission;
    private String modifiedBy;
    private Long modifiedAt;
    private Boolean delFlag;
}
