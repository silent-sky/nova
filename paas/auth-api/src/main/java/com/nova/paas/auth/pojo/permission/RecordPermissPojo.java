package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecordPermissPojo implements Serializable {
    private static final long serialVersionUID = -2750015193580135223L;
    private String entityId;
    private String objectId;
    private String owner;
    private String dept;
    private String creator;
    private long createTime;
    private String modifier;
    private long modifyTime;
}
