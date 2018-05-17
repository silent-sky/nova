package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/5/9 20:59
 */
@Data
public class TaskPojo implements Serializable {
    private static final long serialVersionUID = -3695426552083784530L;

    private String id;
    private String name;
    private String applicantId;
    private Long createdAt;
    private Long modifiedAt;
    private List<String> assigneeIds;
    private Boolean completed;
    private Boolean canceled;
    private String activityId;
    private String activityInstanceId;
    private String workflowInstanceId;
    private String sourceWorkflowId;
    private String workflowId;
    private String tenantId;
    private String entityId;
    private String objectId;
    private String actionType;
    private String taskType;
    private Integer remindLatency;
    private Map<String, List<String>> assignee;
    private List<OpinionPojo> opinions;
    private List<String> candidateIds;
    private String workflowName;
    private String workflowDescription;
    private String status;
    private Map<String, List<ExecutionPojo>> execution;
    private RulePojo rule;
}
