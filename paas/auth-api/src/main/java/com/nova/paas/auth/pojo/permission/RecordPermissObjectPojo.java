package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RecordPermissObjectPojo implements Serializable {
    private static final long serialVersionUID = -7292127205195476302L;
    private String entityId;
    private String objectId;
    private String owner;
    private String dept;
    private List<TeamPojo> team;

}
