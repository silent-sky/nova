package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/9 20:30
 */
@Data
public class WorkflowDefinitionPojo implements Serializable {

    private static final long serialVersionUID = -6269220016295181077L;

    private String workflowJson;
    private String ruleJson;
}
