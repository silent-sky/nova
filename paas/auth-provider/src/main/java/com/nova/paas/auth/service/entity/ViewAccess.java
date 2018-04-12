package com.nova.paas.auth.service.entity;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * zhenghaibo
 * 18/4/10 17:33
 */
@Table(name = "au_view_access")
@Data
public class ViewAccess implements Serializable {

    private String id;
    private String tenantId;
    private String appId;
    private String roleCode;
    private String entityId;
    private String recordTypeId;
    private String viewId;
    private String modifier;
    private long modifyTime;
    private Boolean delFlag;
}
