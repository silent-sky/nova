package com.effektif.workflow.impl.ext;

import com.effektif.workflow.api.ext.ExecutionPojo;
import com.effektif.workflow.api.model.TaskId;
import com.effektif.workflow.api.workflow.Extensible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 18/4/8 14:36
 */
public class Task extends Extensible implements Serializable {
  private static final long serialVersionUID = -237118055514253051L;
  protected TaskId id;
  protected String name;
  protected String description;
  protected String applicantId;
  protected String applicantAccount;
  protected Long createTime;
  protected Long modifyTime;
  protected String assigneeId;
  protected List<String> assigneeIds;
  protected List<String> candidateIds;
  //是否在实例中更换过审批人
  protected Boolean assigneeChanged;
  //已废弃
  protected List<List<String>> assigneeChangeLog;
  //是否为审批例外人
  protected Boolean candidateEditable;
  protected Long dueDate;
  protected Boolean completed;
  protected Boolean canceled;

  protected String activityId;
  protected String activityInstanceId;
  protected Boolean activityNotify;
  protected String workflowInstanceId;
  protected String sourceWorkflowId;
  protected String workflowId;

  protected String tenantId;
  protected String appId;
  protected String entityId;
  protected String objectId;
  protected String deptId;
  protected Map<String, List<String>> assignee;
  protected List<ApprovalOpinion> opinions;
  protected String actionType;


  /**
   * 1:表示可以指定下一个审批人
   * 0或空：不可以
   */

  private Integer assignNextTask;

  /**
   * 1:是外部节点
   * 0或空：不是外部节点
   */

  private Integer externalApplyTask;

  /**
   * 1:表示该节点的审批人由上一节点指定
   * 0或空：不需要
   */

  private Integer candidateByPreTask;

  /**
   * 审批人员类别
   *
   * @see com.effektif.workflow.api.ext.WorkflowConstants.AssigneeType
   */
  protected String assignType;
  /**
   * task审批类型
   *
   * @see com.effektif.workflow.api.ext.WorkflowConstants.UserTaskType
   */
  protected String taskType;
  protected String state;
  protected Boolean remind;
  protected Integer remindLatency;

  protected String workflowName;
  protected String workflowDescription;
  protected String errMsg;
  protected Map<String, Object> bpmExtension;
  //task提醒
  protected Object reminders;
  //是否需要上一级审批
  protected Boolean demandSuperior;

  //0：上级审批；1：流程终止；2：指定审批人
  protected Integer demandBeyondAssignee;

  public Integer getAllPassType() {
    return allPassType;
  }

  public void setAllPassType(Integer allPassType) {
    this.allPassType = allPassType;
  }

  /**
   *  会签时：
   *  allPassType = 1 表示所有人会签人员都要进行一次操作，即使第一个人执行了reject，后面的人也要执行
   *  allPassType = 0 如果第一个人执行了reject，则流程就终止，会签的其他人不需要执行。
   */

  private Integer allPassType;

  /**
   *  针对demandBeyondAssignee==2的情况
   */

  protected Map<String, List<String>> beyondAssignee;

  public Map<String, List<String>> getBeyondAssignee() {
    return beyondAssignee;
  }

  public void setBeyondAssignee(Map<String, List<String>> beyondAssignee) {
    this.beyondAssignee = beyondAssignee;
  }



  public Integer getDemandBeyondAssignee() {
    return demandBeyondAssignee;
  }

  public void setDemandBeyondAssignee(Integer demandBeyondAssignee) {
    this.demandBeyondAssignee = demandBeyondAssignee;
  }



  protected Map<String, List<ExecutionPojo>> execution;
  protected Object rule;
  protected Map sourceTransition;

  public Task name(String name) {
    this.name = name;
    return this;
  }

  public Task assigneeId(String assigneeId) {
    this.assigneeId = assigneeId;
    return this;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public Task candidateId(String candidateId) {
    if (this.candidateIds == null) {
      this.candidateIds = new ArrayList<>();
    }
    this.candidateIds.add(candidateId);
    return this;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public TaskId getId() {
    return id;
  }

  public void setId(TaskId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(String applicantId) {
    this.applicantId = applicantId;
  }

  public String getApplicantAccount() {
    return applicantAccount;
  }

  public void setApplicantAccount(String applicantAccount) {
    this.applicantAccount = applicantAccount;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public String getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(String assigneeId) {
    this.assigneeId = assigneeId;
  }

  public List<String> getCandidateIds() {
    return candidateIds;
  }

  public void setCandidateIds(List<String> candidateIds) {
    this.candidateIds = candidateIds;
  }

  public Long getDueDate() {
    return dueDate;
  }

  public void setDueDate(Long dueDate) {
    this.dueDate = dueDate;
  }

  public Long getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Long modifyTime) {
    this.modifyTime = modifyTime;
  }

  public Boolean getCanceled() {
    return canceled;
  }

  public void setCanceled(Boolean canceled) {
    this.canceled = canceled;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getActivityInstanceId() {
    return activityInstanceId;
  }

  public void setActivityInstanceId(String activityInstanceId) {
    this.activityInstanceId = activityInstanceId;
  }

  public Boolean getActivityNotify() {
    return activityNotify;
  }

  public void setActivityNotify(Boolean activityNotify) {
    this.activityNotify = activityNotify;
  }

  public String getWorkflowInstanceId() {
    return workflowInstanceId;
  }

  public void setWorkflowInstanceId(String workflowInstanceId) {
    this.workflowInstanceId = workflowInstanceId;
  }

  public String getSourceWorkflowId() {
    return sourceWorkflowId;
  }

  public void setSourceWorkflowId(String sourceWorkflowId) {
    this.sourceWorkflowId = sourceWorkflowId;
  }

  public String getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(String workflowId) {
    this.workflowId = workflowId;
  }

  public String getAssignType() {
    return assignType;
  }

  public void setAssignType(String assignType) {
    this.assignType = assignType;
  }

  public String getDeptId() {
    return deptId;
  }

  public void setDeptId(String deptId) {
    this.deptId = deptId;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getTaskType() {
    return taskType;
  }

  public void setTaskType(String taskType) {
    this.taskType = taskType;
  }

  public Map<String, List<String>> getAssignee() {
    return assignee;
  }

  public void setAssignee(Map<String, List<String>> assignee) {
    this.assignee = assignee;
  }

  public List<ApprovalOpinion> getOpinions() {
    return opinions;
  }

  public void setOpinions(List<ApprovalOpinion> opinions) {
    this.opinions = opinions;
  }

  public Boolean getRemind() {
    return remind;
  }

  public void setRemind(Boolean remind) {
    this.remind = remind;
  }

  public Integer getRemindLatency() {
    return remindLatency;
  }

  public void setRemindLatency(Integer remindLatency) {
    this.remindLatency = remindLatency;
  }

  public Map<String, Object> getBpmExtension() {
    return bpmExtension;
  }

  public void setBpmExtension(Map<String, Object> bpmExtension) {
    this.bpmExtension = bpmExtension;
  }

  public String getWorkflowName() {
    return workflowName;
  }

  public void setWorkflowName(String workflowName) {
    this.workflowName = workflowName;
  }

  public String getWorkflowDescription() {
    return workflowDescription;
  }

  public void setWorkflowDescription(String workflowDescription) {
    this.workflowDescription = workflowDescription;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }

  public List<String> getAssigneeIds() {
    return assigneeIds;
  }

  public void setAssigneeIds(List<String> assigneeIds) {
    this.assigneeIds = assigneeIds;
  }

  public Boolean getAssigneeChanged() {
    return assigneeChanged;
  }

  public void setAssigneeChanged(Boolean assigneeChanged) {
    this.assigneeChanged = assigneeChanged;
  }

  public Object getReminders() {
    return reminders;
  }

  public void setReminders(Object reminders) {
    this.reminders = reminders;
  }

  public List<List<String>> getAssigneeChangeLog() {
    return assigneeChangeLog;
  }

  public void setAssigneeChangeLog(List<List<String>> assigneeChangeLog) {
    this.assigneeChangeLog = assigneeChangeLog;
  }

  public Map<String, List<ExecutionPojo>> getExecution() {
    return execution;
  }

  public void setExecution(Map<String, List<ExecutionPojo>> execution) {
    this.execution = execution;
  }

  public Object getRule() {
    return rule;
  }

  public void setRule(Object rule) {
    this.rule = rule;
  }

  public Boolean getCandidateEditable() {
    return candidateEditable;
  }

  public void setCandidateEditable(Boolean candidateEditable) {
    this.candidateEditable = candidateEditable;
  }

  public Map getSourceTransition() {
    return sourceTransition;
  }

  public void setSourceTransition(Map sourceTransition) {
    this.sourceTransition = sourceTransition;
  }

  public Boolean getDemandSuperior() {
    return demandSuperior;
  }

  public void setDemandSuperior(Boolean demandSuperior) {
    this.demandSuperior = demandSuperior;
  }

  public Integer getAssignNextTask() {
    return assignNextTask;
  }

  public void setAssignNextTask(Integer assignNextTask) {
    this.assignNextTask = assignNextTask;
  }

  public Integer getExternalApplyTask() {
    return externalApplyTask;
  }

  public Integer getCandidateByPreTask() {
    return candidateByPreTask;
  }

  public void setCandidateByPreTask(Integer candidateByPreTask) {
    this.candidateByPreTask = candidateByPreTask;
  }

  public void setExternalApplyTask(Integer externalApplyTask) {
    this.externalApplyTask = externalApplyTask;
  }
}
