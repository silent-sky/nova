package com.nova.paas.auth.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/22 10:07
 */
@Data
public class QryFunctionArg implements Serializable {
    private static final long serialVersionUID = 1088273952788120373L;

    private CommonContext context;
    private Set<String> ids;
    private String funcName;
    private String parentId;
    private Integer funcType;
}
