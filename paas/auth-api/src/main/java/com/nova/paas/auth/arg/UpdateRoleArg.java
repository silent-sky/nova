package com.nova.paas.auth.arg;

import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/21 16:52
 */
@Data
public class UpdateRoleArg implements Serializable {
    private static final long serialVersionUID = 4663881858069923860L;

    private CommonContext context;
    private RolePojo pojo;
}
