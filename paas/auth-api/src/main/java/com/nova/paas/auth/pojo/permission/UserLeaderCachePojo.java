package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLeaderCachePojo implements Serializable {
    private static final long serialVersionUID = -2974977700188707755L;
    private String id;
    private String tenantId;
    private String userId;
    private String leaderId;
    private Integer relationType;
}
