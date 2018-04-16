package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDeptRelationCachePojo implements Serializable {
    private static final long serialVersionUID = -1571007082476856281L;
    private String id;
    private String tenantId;
    private String deptId;
    private String userId;
    private Integer relationType;
}
