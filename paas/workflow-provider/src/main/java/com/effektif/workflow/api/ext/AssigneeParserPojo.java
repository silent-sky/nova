package com.effektif.workflow.api.ext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/4/8 20:04
 */
public class AssigneeParserPojo implements Serializable {
    private static final long serialVersionUID = -6768431061158331734L;
    private Map<String, List<String>> assigneeMap;
    private String tenantId;
    private String appId;
    private String userId;
    private String taskId;
    private String applicantId;
    private String workflowId;
    private String workflowInstanceId;
    private Map<String, Object> bpmExtension;
    private String activityId;
    private String entityId;
    private String objectId;
    private Map<String, Object> variables;

    public Map<String, List<String>> getAssigneeMap() {
        return assigneeMap;
    }

    public void setAssigneeMap(Map<String, List<String>> assigneeMap) {
        this.assigneeMap = assigneeMap;
    }

    public AssigneeParserPojo assigneeMap(Map<String, List<String>> assigneeMap) {
        this.assigneeMap = assigneeMap;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public AssigneeParserPojo tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public AssigneeParserPojo appId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AssigneeParserPojo userId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public AssigneeParserPojo taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    public void setWorkflowInstanceId(String workflowInstanceId) {
        this.workflowInstanceId = workflowInstanceId;
    }

    public AssigneeParserPojo workflowInstanceId(String workflowInstanceId) {
        this.workflowInstanceId = workflowInstanceId;
        return this;
    }

    public Map<String, Object> getBpmExtension() {
        return bpmExtension;
    }

    public void setBpmExtension(Map<String, Object> bpmExtension) {
        this.bpmExtension = bpmExtension;
    }

    public AssigneeParserPojo bpmExtension(Map<String, Object> bpmExtension) {
        this.bpmExtension = bpmExtension;
        return this;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public AssigneeParserPojo activityId(String activityId) {
        this.activityId = activityId;
        return this;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
