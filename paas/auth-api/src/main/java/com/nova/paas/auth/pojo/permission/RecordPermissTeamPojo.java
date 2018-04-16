package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RecordPermissTeamPojo implements Serializable {
    private static final long serialVersionUID = 6936827916654204855L;
    private String entityId;
    private String objectId;
    private List<TeamPojo> team;
}
