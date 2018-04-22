package com.nova.paas.auth.entity;

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
@Table(name = "au_func_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncAccess implements Serializable {

    private static final long serialVersionUID = 5528890711926409777L;

    private String id;
    private String tenantId;
    private String roleId;
    private String funcId;
    private String modifiedBy;
    private Long modifiedAt;
    private Boolean delFlag;
}
