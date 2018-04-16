package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class EntityFieldShareReceivePojo implements Serializable {
    private static final long serialVersionUID = 2776010603208020814L;
    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private String ruleCode;
    private Integer receiveType;
    private String receiveId;
    private Integer permission;
    private Integer status;
}
