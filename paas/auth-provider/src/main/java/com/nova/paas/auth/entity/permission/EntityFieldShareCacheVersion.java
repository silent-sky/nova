package com.nova.paas.auth.entity.permission;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_entity_field_share_cache_version")
@Data
public class EntityFieldShareCacheVersion implements Serializable {
    private static final long serialVersionUID = 236939567559839311L;
    private String id;
    private String tenantId;
    private String entityId;
    private String ruleCode;
    private String currentVersion;
    private String newVersion;
}
