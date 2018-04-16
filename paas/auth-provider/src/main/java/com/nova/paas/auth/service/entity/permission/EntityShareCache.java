package com.nova.paas.auth.service.entity.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_entity_share_cache")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityShareCache implements Serializable {

    private static final long serialVersionUID = 7616664573885935156L;
    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private String entityShareId;
    private String shareUser;
    private String receiveUser;
    private Integer permission;
    private Integer receiveType;

}
