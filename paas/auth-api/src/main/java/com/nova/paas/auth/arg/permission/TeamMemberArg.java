package com.nova.paas.auth.arg.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamMemberArg implements Serializable {
    private static final long serialVersionUID = 387728979803211437L;

    private Integer memberType;
    private String memberId;
    private String roleType;
    private Integer permission;

}
