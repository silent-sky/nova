package com.nova.paas.auth.service.entity.permission;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_entity_share")
@Data
@Builder
public class EntityShare implements Serializable {
    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private Integer shareType;
    private String shareId;
    private Integer receiveType;
    private String receiveId;
    private Integer permission;
    private Integer status;
    private Integer delFlag;
    private String creator;
    private long createTime;
    private String modifier;
    private long modifyTime;

}
