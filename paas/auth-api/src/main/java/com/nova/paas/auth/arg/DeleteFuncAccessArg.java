package com.nova.paas.auth.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 16:39
 */
@Data
public class DeleteFuncAccessArg implements Serializable {
    private static final long serialVersionUID = -583420985564135415L;

    private CommonContext context;
    private String roleId;
    private Set<String> funcIds;
}
