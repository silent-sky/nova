package com.nova.paas.auth.entity.permission;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Table(name = "auth_entity_field_share_cache")
@Data
@Builder
public class EntityFieldShareCache implements Serializable {
    private static final long serialVersionUID = -7173631860922803040L;
    private String id;
    private String tenantId;
    private String entityId;
    private String dataId;
    private List<String> rules;

}
