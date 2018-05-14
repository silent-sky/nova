package com.nova.paas.auth.entity.permission;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_user_share")
@Data
@Builder
public class EntityShare implements Serializable {
    private String id;
    private String tenantId;
    private String entityId;
    private Integer shareType;
    private String shareId;
    private Integer receiveType;
    private String receiveId;
    private Integer permission;
    private Integer status;
    private String createdBy;
    private Long createdAt;
    private String modifiedBy;
    private Long modifiedAt;
    private Boolean delFlag;

}
