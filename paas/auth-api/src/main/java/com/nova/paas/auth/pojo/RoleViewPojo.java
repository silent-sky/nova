package com.nova.paas.auth.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/8 19:44
 */
@Data
public class RoleViewPojo implements Serializable {
    private static final long serialVersionUID = -9193993307714251377L;

    private String id;
    private String tenantId;
    private String appId;
    private String roleCode;
    private String entityId;
    private String viewId;
    private String recordTypeId;
}
