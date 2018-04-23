package com.nova.paas.auth.arg;

import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/13 16:39
 */
@Data
public class CreateRoleArg implements Serializable {
    private static final long serialVersionUID = -583420985564135415L;

    private CommonContext context;
    private RolePojo pojo;
}
