package com.nova.paas.auth.pojo.permission;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class EntityShareCachePojo implements Serializable {
    private static final long serialVersionUID = 6191408585811403178L;
    private String id;
    private String tenantId;
    private String entityId;
    private String entityShareId;
    private String shareUser;
    private String receiveUser;
    private Integer receiveType;
    private Integer permission;
}
