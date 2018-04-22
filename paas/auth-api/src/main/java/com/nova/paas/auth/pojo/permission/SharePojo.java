package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class SharePojo implements Serializable {
    private static final long serialVersionUID = -8518967431809007268L;

    private String id;
    private String tenantId;
    private String entityId;
    private Integer shareType;
    private String shareId;
    private Integer receiveType;
    private String receiveId;
    private Integer permission;
    private Integer status;

}
