package com.nova.paas.auth.entity;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * zhenghaibo
 * 18/4/9 17:15
 */
@Table(name = "au_function")
@Data
public class Function implements Serializable {
    private static final long serialVersionUID = 5528890711926409746L;

    private String id;
    private String tenantId;
    private String funcName;
    private String funcCode;
    private Integer funcOrder;
    private Integer funcType;
    private String parentId;
    private String createdBy;
    private Long createdAt;
    private String modifiedBy;
    private Long modifiedAt;
    private Boolean isEnabled;
    private Boolean delFlag;
}
