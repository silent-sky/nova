package com.nova.paas.auth.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/8 19:32
 */
@Data
public class FunctionAccessPojo implements Serializable {
    private static final long serialVersionUID = 5967155509926216032L;

    private String id;
    private String tenantId;
    private String appId;
    private String roleId;
    private String funcId;
}
