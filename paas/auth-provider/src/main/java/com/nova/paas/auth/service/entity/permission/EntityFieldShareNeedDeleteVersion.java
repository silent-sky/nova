package com.nova.paas.auth.service.entity.permission;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_entity_field_share_need_delete_version")
@Data
public class EntityFieldShareNeedDeleteVersion implements Serializable {
    private static final long serialVersionUID = 5221363910678229725L;
    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private String version;
    private Integer delFlag;
    private long modifyTime;
}
