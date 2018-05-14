package com.nova.paas.workflow.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/5/10 10:46
 */
@Data
public class UserTaskQueryParam implements Serializable {
    private static final long serialVersionUID = 4006765365992880105L;

    private String id;
    private String taskName;
    private String applicantId;
    private String assigneeId;
    /*查询待办还是已办*/
    private Boolean completed;
    private String workflowInstanceId;
    private String tenantId;
    private String appId;
    private String entityId;
    private String objectId;
    /*只要任务的候选审批人中包含assigneeIds中的任意一个就行，用于批量查询待办、已办任务*/
    private Set<String> assigneeIds;
}
