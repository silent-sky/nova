package com.effektif.workflow.api.activities;

import com.effektif.workflow.api.ext.ActionParam;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/8 16:52
 */
public class ExecutionItem implements Serializable {
  private static final long serialVersionUID = -9201307438128145017L;

  /**
   * @see com.effektif.workflow.api.ext.WorkflowConstants.ExecuteType
   */
  private String taskType;

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


  //zz 2017.11.13 start
  private String afterActionDefinitionId;
  private String afterActionMappingId;
  // zz  2017.11.13  end

  // wangbf 2018.01.19 start
  private Map<String, Object> actionMapping;
  private List<ActionParam> actionParams;
  // wangbf 2018.01.19 end

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
}
