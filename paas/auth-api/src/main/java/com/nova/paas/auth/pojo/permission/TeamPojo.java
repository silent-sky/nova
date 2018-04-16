package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamPojo implements Serializable {
    private static final long serialVersionUID = -2750015193580135224L;
    private String entityId;
    private String objectId;
    private Integer memberType;
    private String memberId;
    private String roleType;
    private Integer permission;
    private String modifier;
    private long modifyTime;
}
