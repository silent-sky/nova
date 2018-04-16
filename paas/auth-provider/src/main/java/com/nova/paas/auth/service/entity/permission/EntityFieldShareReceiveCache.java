package com.nova.paas.auth.service.entity.permission;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_entity_field_share_receive_cache")
@Data
public class EntityFieldShareReceiveCache implements Serializable {
    private static final long serialVersionUID = 6879825050295096177L;
    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private String ruleCode;
    private String userId;
    private Integer permission;
}
