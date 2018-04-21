package com.nova.paas.auth.entity;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * zhenghaibo
 * 18/4/9 17:15
 */
@Table(name = "au_recordType_access")
@Data
public class RecordTypeAccess implements Serializable {
    private static final long serialVersionUID = 9149791696371079639L;

    private String id;
    private String tenantId;
    private String appId;
    private String roleId;
    private String entityId;
    private String recordTypeId;
    private Boolean defaultType;
    private String modifiedBy;
    private Long modifiedAt;
    private Boolean delFlag;
}
