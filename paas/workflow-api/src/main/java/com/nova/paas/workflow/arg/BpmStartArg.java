package com.nova.paas.workflow.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/31 14:49
 */
@Data
public class BpmStartArg implements Serializable {
    private CommonContext context;
    private String sourceWorkflowId;
    private String entityId;
    private String objectId;
}
