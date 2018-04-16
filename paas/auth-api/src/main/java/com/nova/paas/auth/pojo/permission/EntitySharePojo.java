package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class EntitySharePojo implements Serializable {
    private static final long serialVersionUID = -2394486894766178481L;
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
    private String creator;
    private long createTime;
    private String modifier;
    private long modifyTime;
}
