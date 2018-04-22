package com.nova.paas.auth.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/21 21:38
 */
@Data
public class DeleteFuncArg implements Serializable {
    private static final long serialVersionUID = -3913799477192618297L;

    private CommonContext context;
    private Set<String> ids;
}
