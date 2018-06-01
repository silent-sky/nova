package com.nova.paas.workflow.arg;

import com.nova.paas.common.pojo.CommonContext;
import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/31 19:34
 */
@Data
public class BpmCompleteTaskArg implements Serializable {
    private static final long serialVersionUID = 3154969688313070223L;

    private CommonContext context;
    private String taskId;
    private String actionType;
    private String opinion;
}
