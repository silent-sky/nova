package com.nova.paas.auth.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/21 16:52
 */
@Data
public class UpdateUserRoleArg implements Serializable {
    private static final long serialVersionUID = 4663881858069923860L;

    private CommonContext context;
    private String userId;
    private Set<String> roleIds;
}
