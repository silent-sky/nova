package com.effektif.workflow.impl.ext;

import com.effektif.workflow.api.query.OrderDirection;
import com.effektif.workflow.api.query.Query;

import java.io.Serializable;
import java.util.List;

/**
 * zhenghaibo
 * 18/4/8 14:19
 */
public class UserTaskQuery extends Query implements Serializable {
    private static final long serialVersionUID = 5564377519097045990L;
    protected String taskId;
    protected String taskName;
    protected String applicantId;
    protected String assigneeId;
    protected List<String> assigneeIds;
    protected Boolean completed;
    protected String workflowInstanceId;
    protected String tenantId;
    protected String appId;
    protected String entityId;
    protected String objectId;

    public Boolean getCompleted() {
        return this.completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public UserTaskQuery completed() {
        this.completed = true;
        return this;
    }

    public String getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    public void setWorkflowInstanceId(String workflowInstanceId) {
        this.workflowInstanceId = workflowInstanceId;
    }

    public UserTaskQuery open() {
        this.completed = false;
        return this;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public UserTaskQuery applicantId(String applicantId) {
        this.applicantId = applicantId;
        return this;
    }

    public String getAssigneeId() {
        return this.assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public UserTaskQuery assigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public UserTaskQuery taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public UserTaskQuery taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public UserTaskQuery tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public UserTaskQuery appId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public UserTaskQuery entityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public UserTaskQuery objectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

    public List<String> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<String> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }

    @Override
    public UserTaskQuery skip(Integer skip) {
        super.skip(skip);
        return this;
    }

    @Override
    public UserTaskQuery limit(Integer limit) {
        super.limit(limit);
        return this;
    }

    @Override
    public UserTaskQuery orderBy(String field, OrderDirection direction) {
        super.orderBy(field, direction);
        return this;
    }
}
