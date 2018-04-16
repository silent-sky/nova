package com.nova.paas.auth.service.entity.permission;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "auth_dept_user_cache")
@Data
public class UserDeptRelationCache implements Serializable {

    private static final long serialVersionUID = 7514172010580812555L;
    private String id;
    private String tenantId;
    //private String appId;
    private String deptId;
    private String userId;
    private Integer relationType;

}
