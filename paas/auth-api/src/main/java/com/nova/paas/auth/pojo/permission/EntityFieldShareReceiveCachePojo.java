package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class EntityFieldShareReceiveCachePojo implements Serializable {
    private static final long serialVersionUID = 2210886120071095499L;

    private String id;
    private String tenantId;
    private String entityId;
    private String ruleCode;
    private String userId;
    private Integer permission;

}
