package com.nova.paas.org.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPojo implements Serializable {
    private static final long serialVersionUID = -1509615237437775230L;
    private String id;
    private String tenantId;
    private String deptId;
    private String name;
    private String email;
    private String positionId;
}
