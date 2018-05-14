package com.nova.paas.workflow.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/5/9 20:03
 */
@Data
public class CompleteTaskParam implements Serializable {

    private static final long serialVersionUID = 4042738117439999456L;

    private String taskId;
    private String actionType;
    private Map<String, Object> conditionMap;
    private String opinion;
}
