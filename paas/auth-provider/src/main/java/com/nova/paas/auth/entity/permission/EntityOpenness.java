package com.nova.paas.auth.entity.permission;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "au_entity_openness")
@Data
@Builder
public class EntityOpenness implements Serializable {
    private String id;
    private String tenantId;
    private String entityId;
    /**
     * @see com.nova.paas.common.constant.PermissionConstant.EntityOpennessType
     */
    private Integer permission;
    private Integer delFlag;
    private String creator;
    private long createTime;
    private String modifier;
    private long modifyTime;
}
