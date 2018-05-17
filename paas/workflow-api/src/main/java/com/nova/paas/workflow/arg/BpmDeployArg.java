package com.nova.paas.workflow.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/5/16 15:40
 */
@Data
public class BpmDeployArg implements Serializable {

    private static final long serialVersionUID = -4445635595610414574L;

    private CommonContext context;
    private boolean newFlag;
    private Map workflowJson;
    private Map ruleJson;
}
