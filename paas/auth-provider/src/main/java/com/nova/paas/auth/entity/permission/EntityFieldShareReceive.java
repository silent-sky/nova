package com.nova.paas.auth.entity.permission;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_entity_field_share_receive")
@Data
@Builder
public class EntityFieldShareReceive implements Serializable {
    private static final long serialVersionUID = 1258173777440955738L;
    private String getTenantId;
    private String tenantId;
    private String entityId;
    private String ruleCode;
    private Integer receiveType;
    private String receiveId;
    private Integer permission;
    private Integer status;
}
