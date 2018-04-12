package com.nova.paas.auth.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/8 19:43
 */
@Data
public class RoleRecordTypePojo implements Serializable {
    private static final long serialVersionUID = 7261036664421919431L;

    private String id;
    private String tenantId;
    private String appId;
    private String roleCode;
    private String entityId;
    private String recordTypeId;
    private boolean defaultType;
}
