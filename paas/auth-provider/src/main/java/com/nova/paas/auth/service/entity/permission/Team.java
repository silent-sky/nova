package com.nova.paas.auth.service.entity.permission;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_team")
@Data
@Builder
public class Team implements Serializable {

    private static final long serialVersionUID = 7132283704869187571L;
    private String id;
    private String tenantId;
    private String appId;
    //private String entityId;
    private String objectId;

    private Integer memberType;
    private String memberId;
    private String roleType;
    private Integer permission;
    private String name;
    private String objectDescribeId;
    private String objectDescribeApiName;
    private int version;
    private long createTime;
    private String extendObjDataId;
    private String recordType;

    private Integer isDeleted;//delFlag;
    private String lastModifiedBy;//modifier;
    private long lastModifiedTime; //modifyTime;

    private String createdBy;

}
