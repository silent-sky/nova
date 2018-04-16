package com.nova.paas.auth.arg;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/13 16:39
 */
@Data
public class RoleArg implements Serializable {
    private static final long serialVersionUID = -583420985564135415L;

    private String id;
    private String roleName;
}
