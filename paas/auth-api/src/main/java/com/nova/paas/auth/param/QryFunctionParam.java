package com.nova.paas.auth.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/22 10:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QryFunctionParam implements Serializable {
    private static final long serialVersionUID = 1088273952788120373L;

    private Set<String> ids;
    private String funcName;
    private String parentId;
    private Integer funcType;
}
