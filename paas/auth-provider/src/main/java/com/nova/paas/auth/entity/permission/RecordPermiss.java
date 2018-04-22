package com.nova.paas.auth.entity.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_record_permission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordPermiss implements Serializable {

    private static final long serialVersionUID = -2210941707678234571L;
    private String id;
    private String tenantId;
    private String entityId;
    private String objectId;
    private String owner;
    private String dept;
    private Integer delFlag;
    private String creator;
    private long createTime;
    private String modifier;
    private long modifyTime;
}
