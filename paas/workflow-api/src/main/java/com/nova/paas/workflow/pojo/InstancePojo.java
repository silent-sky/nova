package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * zhenghaibo
 * 2018/5/9 20:32
 */
@Data
public class InstancePojo implements Serializable {
    private static final long serialVersionUID = -4922370562804890956L;

    private String id;
    private String workflowId;
    private String workflowName;
    private String workflowDescription;
    private String sourceWorkflowId;
    private String tenantId;
    private Long start;
    private Long end;
    private Long duration;
    private String entityId;
    private String objectId;
    private String status;
    private String applicantId;
    private String actionType;
    private List<ActivityInstancePojo> activityInstances;
}
