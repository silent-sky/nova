package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeamMembersPojo implements Serializable {
    private String objectId;
    private List<TeamPojo> team;
}
