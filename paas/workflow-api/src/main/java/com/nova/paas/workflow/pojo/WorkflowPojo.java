package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2017/4/25 18:00
 */
@Data
public class WorkflowPojo implements Serializable {
  private static final long serialVersionUID = -8539761602016784251L;

  private String sourceWorkflowId;
  private String workflowId;
  private String ruleId;
}
