package com.effektif.workflow.impl.activity.types;

import com.effektif.workflow.api.activities.ExecutionItem;
import com.effektif.workflow.api.activities.ExecutionTask;
import com.effektif.workflow.api.ext.ExecutionPojo;
import com.effektif.workflow.api.ext.WorkflowBindingEnum;
import com.effektif.workflow.impl.activity.AbstractActivityType;
import com.effektif.workflow.impl.workflow.WorkflowImpl;
import com.effektif.workflow.impl.workflowinstance.ActivityInstanceImpl;
import com.effektif.workflow.impl.workflowinstance.WorkflowInstanceImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 18/4/8 19:50
 */
public class ExecutionTaskImpl extends AbstractActivityType<ExecutionTask> {

  public ExecutionTaskImpl() {
    super(ExecutionTask.class);
  }

  @Override
  public void execute(ActivityInstanceImpl activityInstance) {
    try {
      WorkflowImpl workflow = activityInstance.workflow;
      WorkflowInstanceImpl workflowInstance = activityInstance.workflowInstance;

      Map<String, String> extMap = new HashMap<>();
      extMap.put(WorkflowBindingEnum.tenantId.toString(), this.getWorkflowProperty(workflow, WorkflowBindingEnum.tenantId.toString()));
      extMap.put(WorkflowBindingEnum.appId.toString(), this.getWorkflowProperty(workflow, WorkflowBindingEnum.appId.toString()));
      extMap.put(WorkflowBindingEnum.entityId.toString(), this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.entityId.toString()));
      extMap.put(WorkflowBindingEnum.objectId.toString(), this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.objectId.toString()));
      extMap.put(WorkflowBindingEnum.eventId.toString(), this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.eventId.toString()));
      extMap.put("workflowId", workflow.getId().toString());
      extMap.put("activityId", activity.getId());
      extMap.put(WorkflowBindingEnum.applicantId.toString(), this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.applicantId.toString()));
      extMap.put("workflowInstanceId", activityInstance.workflowInstance.getId().toString());
      extMap.put("workflowName", workflow.getName());
      List<ExecutionPojo> pojoList = this.convert(activity, extMap);
      Class<?> bean = Class.forName("com.facishare.paas.workflow.kernel.support.ExecutionTaskSupport");
      Method method = bean.getMethod("execute", List.class);
      method.invoke(bean.newInstance(), pojoList);
      activityInstance.onwards();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getObjectHelper(Object obj) {
    String result = null;
    if (obj != null) {
      result = obj.toString();
    }
    return result;
  }

  private String getWorkflowProperty(WorkflowImpl workflow, String key) {
    if (workflow == null || key == null) {
      return null;
    }
    return this.getObjectHelper(workflow.getProperty(key));
  }

  private String getWorkflowInstanceProperty(WorkflowInstanceImpl workflowInstance, String key) {
    if (workflowInstance == null || key == null) {
      return null;
    }
    if (workflowInstance.getProperty(key) != null) {
      return this.getObjectHelper(workflowInstance.getProperty(key));
    }
    return this.getObjectHelper(workflowInstance.getTransientProperty(key));
  }

  private List<ExecutionPojo> convert(ExecutionTask task, Map<String, String> extMap) {
    List<ExecutionPojo> pojoList = new ArrayList<>();
    if (task.getItemList() != null && task.getItemList().size() > 0) {
      for (ExecutionItem item : task.getItemList()) {
        ExecutionPojo pojo = new ExecutionPojo();
        pojo.setTaskType(item.getTaskType());
        pojo.setSender(item.getSender());
        pojo.setRecipients(item.getRecipients());
        pojo.setEmailAddress(item.getEmailAddress());
        pojo.setTitle(item.getTitle());
        pojo.setContent(item.getContent());
        pojo.setTemplate(item.getTemplate());
        pojo.setFieldMapping(item.getFieldMapping());
        pojo.setUpdateFieldJson(item.getUpdateFieldJson());
        pojo.setUpdateFieldObject(item.getUpdateFieldObject());
        pojo.setTriggerParam(item.getTriggerParam());
        pojo.setTenantId(extMap.get(WorkflowBindingEnum.tenantId.toString()));
        pojo.setAppId(extMap.get(WorkflowBindingEnum.appId.toString()));
        pojo.setWorkflowMap(extMap);
        pojo.setAfterActionDefinitionId(item.getAfterActionDefinitionId());
        pojo.setAfterActionMappingId(item.getAfterActionMappingId());
        pojo.setActionMapping(item.getActionMapping());
        pojo.setActionParams(item.getActionParams());
        pojoList.add(pojo);
      }
    }
    return pojoList;
  }
}
