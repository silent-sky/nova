package com.nova.paas.auth.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/4/21 16:52
 */
@Data
public class UpdateFieldAccessArg implements Serializable {
    private static final long serialVersionUID = 4663881858069923860L;

    private CommonContext context;
    private String roleId;
    private String entityId;
    private Map<String, Integer> fieldPermission;
}
