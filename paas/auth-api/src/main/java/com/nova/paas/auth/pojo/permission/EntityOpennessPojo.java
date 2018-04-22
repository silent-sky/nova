package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class EntityOpennessPojo implements Serializable {

    private static final long serialVersionUID = -2750015193580135222L;

    private String id;
    private String tenantId;
    private String entityId;
    /**
     * @see com.nova.paas.common.constant.PermissionConstant.DataRightsSceneType
     */
    private Integer permission;
    private Integer scope;
    private String creator;
    private long createTime;
    private String modifier;
    private long modifyTime;

}
