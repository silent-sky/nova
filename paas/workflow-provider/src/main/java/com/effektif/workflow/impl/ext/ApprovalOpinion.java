package com.effektif.workflow.impl.ext;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/8 13:30
 */
@Data
public class ApprovalOpinion implements Serializable {

  private String tenantId;
  private String userId;
  private String actionType;
  private String opinion;
  private long replyTime;


}
