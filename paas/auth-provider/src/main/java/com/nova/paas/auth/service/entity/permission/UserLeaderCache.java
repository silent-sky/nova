package com.nova.paas.auth.service.entity.permission;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_user_leader_cache")
@Data
public class UserLeaderCache implements Serializable {

    private static final long serialVersionUID = 3190927598965959966L;
    private String id;
    private String tenantId;
    private String userId;
    private String leaderId;
    private Integer relationType;
}
