package com.nova.paas.auth.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * zhenghaibo
 * 18/4/9 18:00
 * 角色功能权限实体
 */
@Table(name = "auth_func_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncAccess implements Serializable {

    private static final long serialVersionUID = 5528890711926409777L;

    private String id;
    private String tenantId;
    private String appId;
    private String roleCode;
    private String funcCode;
    private String modifier;
    private Long modifyTime;
    private Boolean delFlag;
}
