package com.nova.paas.auth.entity;

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
    private String roleId;
    private String entityId;
    private String recordTypeId;
    private String viewId;
    private String modifiedBy;
    private Long modifiedAt;
    private Boolean delFlag;
}
