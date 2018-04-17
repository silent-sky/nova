package com.effektif.workflow.api.ext;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2017/4/8 13:24
 */
public class ExecutionPojo implements Serializable {
  private static final long serialVersionUID = -8539066498976541093L;

  private String tenantId;
  private String appId;
  private String taskType;
  private int rowNo;

  /**
   * success,error,ignore
   */
  private String executionState;

  /**
   * 后动作错误信息
   */
  private String actionErrorMsg;


  /**
   * 后动作错误信息
   */
  private String activityId;

  /**
   * 后动作执行忽略操作人
   */
  private String userId;
  private long modifyTime;

  private String sender;
  /**
   * map<type,Set<id>>
   * type:PERSON,DEPT,ROLE,DEPT_LEADER
   */
  private LinkedHashMap<String, Set<String>> recipients;
  private Set<String> emailAddress;
  private String title;
  private String content;
  private String template;

  //zz 2017.11.13 start
  private String afterActionDefinitionId;
  private String afterActionMappingId;

  // zz  2017.11.13  end

  // wangbf 2018.01.19 start
  private Map<String, Object> actionMapping;
  private List<ActionParam> actionParams;
  // wangbf 2018.01.19 end

  /**
   * map<entityId,<field,value>>
   * value:constant
   * $$__value:variable
   * %%__value:expression
   */
  private LinkedHashMap<String, LinkedHashMap<String, String>> fieldMapping;

  private String updateFieldJson;
  private Object updateFieldObject;
  private Map<String, Object> triggerParam;

  private Map<String, String> workflowMap;

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getTaskType() {
    return taskType;
  }

  public void setTaskType(String taskType) {
    this.taskType = taskType;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public LinkedHashMap<String, Set<String>> getRecipients() {
    return recipients;
  }

  public void setRecipients(LinkedHashMap<String, Set<String>> recipients) {
    this.recipients = recipients;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public LinkedHashMap<String, LinkedHashMap<String, String>> getFieldMapping() {
    return fieldMapping;
  }

  public void setFieldMapping(LinkedHashMap<String, LinkedHashMap<String, String>> fieldMapping) {
    this.fieldMapping = fieldMapping;
  }

  public String getUpdateFieldJson() {
    return updateFieldJson;
  }

  public void setUpdateFieldJson(String updateFieldJson) {
    this.updateFieldJson = updateFieldJson;
  }

  public Set<String> getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(Set<String> emailAddress) {
    this.emailAddress = emailAddress;
  }

  public Map<String, Object> getTriggerParam() {
    return triggerParam;
  }

  public void setTriggerParam(Map<String, Object> triggerParam) {
    this.triggerParam = triggerParam;
  }

  public Object getUpdateFieldObject() {
    return updateFieldObject;
  }

  public void setUpdateFieldObject(Object updateFieldObject) {
    this.updateFieldObject = updateFieldObject;
  }

  public Map<String, String> getWorkflowMap() {
    return workflowMap;
  }

  public void setWorkflowMap(Map<String, String> workflowMap) {
    this.workflowMap = workflowMap;
  }

  public String getAfterActionDefinitionId() {
    return afterActionDefinitionId;
  }

  public void setAfterActionDefinitionId(String afterActionDefinitionId) {
    this.afterActionDefinitionId = afterActionDefinitionId;
  }

  public String getAfterActionMappingId() {
    return afterActionMappingId;
  }

  public void setAfterActionMappingId(String afterActionMappingId) {
    this.afterActionMappingId = afterActionMappingId;
  }

  public Map<String, Object> getActionMapping() {
    return actionMapping;
  }

  public void setActionMapping(Map<String, Object> actionMapping) {
    this.actionMapping = actionMapping;
  }

  public List<ActionParam> getActionParams() {
    return actionParams;
  }

  public void setActionParams(List<ActionParam> actionParams) {
    this.actionParams = actionParams;
  }

  public int getRowNo() {
    return rowNo;
  }

  public void setRowNo(int rowNo) {
    this.rowNo = rowNo;
  }

  public String getExecutionState() {
    return executionState;
  }

  public void setExecutionState(String executionState) {
    this.executionState = executionState;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public long getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(long modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getActionErrorMsg() {
    return actionErrorMsg;
  }

  public void setActionErrorMsg(String actionErrorMsg) {
    this.actionErrorMsg = actionErrorMsg;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }
}
