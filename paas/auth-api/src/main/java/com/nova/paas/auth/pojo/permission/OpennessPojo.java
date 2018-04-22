package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class OpennessPojo implements Serializable {
    private static final long serialVersionUID = -8625057508414742220L;

    private String id;
    private String tenantId;
    private String entityId;
    private Integer scope;
    private Integer permission;
}
