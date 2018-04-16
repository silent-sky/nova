package com.nova.paas.org.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeptPojo implements Serializable {
    private static final long serialVersionUID = -6388160847436882995L;
    private String id;
    private String tenantId;
    private String deptName;
    private String parentId;
    private String leaderUserId;
    private String leaderName;
}
