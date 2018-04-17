package com.effektif.workflow.impl.activity.types;

import com.effektif.workflow.api.activities.UserTask;
import com.effektif.workflow.api.ext.AssigneeParserPojo;
import com.effektif.workflow.api.ext.WorkflowBindingEnum;
import com.effektif.workflow.api.ext.WorkflowConstants;
import com.effektif.workflow.impl.WorkflowParser;
import com.effektif.workflow.impl.activity.AbstractActivityType;
import com.effektif.workflow.impl.ext.Task;
import com.effektif.workflow.impl.ext.TaskStore;
import com.effektif.workflow.impl.workflow.ActivityImpl;
import com.effektif.workflow.impl.workflow.WorkflowImpl;
import com.effektif.workflow.impl.workflowinstance.ActivityInstanceImpl;
import com.effektif.workflow.impl.workflowinstance.VariableInstanceImpl;
import com.effektif.workflow.impl.workflowinstance.WorkflowInstanceImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/8 15:13
 */
public class UserTaskImpl extends AbstractActivityType<UserTask> {
  protected TaskStore taskStore;

  public UserTaskImpl() {
    super(UserTask.class);
  }

  @Override
  public void parse(ActivityImpl activityImpl, UserTask userTask, WorkflowParser parser) {
    super.parse(activityImpl, userTask, parser);
    this.taskStore = parser.getConfiguration(TaskStore.class);
  }

  @Override
  public void execute(ActivityInstanceImpl activityInstance) {
    WorkflowImpl workflow = activityInstance.workflow;
    WorkflowInstanceImpl workflowInstance = activityInstance.workflowInstance;

    Task task = this.generateTask(activityInstance);

    task.setName(activity.getName());
    task.setDescription(activity.getDescription());
    task.setDueDate(activity.getDueDate());
    task.setActivityNotify(true);
    task.setActivityId(activity.getId());
    task.setActivityInstanceId(activityInstance.id);
    task.setWorkflowInstanceId(workflowInstance.id.getInternal());
    task.setWorkflowId(workflow.id.getInternal());
    task.setSourceWorkflowId(workflow.sourceWorkflowId);

    task.setAssignType(activity.getAssignType());
    task.setAssignee(activity.getAssignee());
    task.setTaskType(activity.getTaskType());
    task.setTenantId(this.getWorkflowProperty(workflow, WorkflowBindingEnum.tenantId.toString()));
    task.setAppId(this.getWorkflowProperty(workflow, WorkflowBindingEnum.appId.toString()));
    task.setEntityId(this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.entityId.toString()));
    task.setObjectId(this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.objectId.toString()));
    task.setApplicantId(this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.applicantId.toString()));
    task.setApplicantAccount(this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.applicantAccount.toString()));
    task.setRemind((Boolean) workflow.getProperty(WorkflowBindingEnum.remind.toString()));
    task.setRemindLatency((Integer) workflow.getProperty(WorkflowBindingEnum.remindLatency.toString()));
    task.setReminders(activityInstance.activity.getProperty(WorkflowBindingEnum.reminders.toString()));
    task.setState(WorkflowConstants.UserTaskStatus.IN_PROGRESS);
    Long currentTime = System.currentTimeMillis();
    task.setCreateTime(currentTime);
    task.setModifyTime(currentTime);
    task.setWorkflowName(workflow.getName());
    task.setWorkflowDescription(workflow.getDescription());


//    Map<String, List<ExecutionPojo>> taskExecution = Maps.newHashMap();
//
//    // 初始化执行状态为未执行
//    if (activity.getExecution() != null && !activity.getExecution().isEmpty()) {
//      for (Map.Entry<String, List<ExecutionPojo>> stringListPojo : activity.getExecution().entrySet()) {
//        if (stringListPojo.getValue() != null && !stringListPojo.getValue().isEmpty()) {
//          List<ExecutionPojo> executionPojos = Lists.newArrayList();
//
//          List<ExecutionPojo> workflowexecutionPojos = JSONArray.parseArray(JSON.toJSONString(stringListPojo.getValue()), ExecutionPojo.class);
//
//
//          //          for (int i = 0; i < workflowexecutionPojos.size(); i++) {
//          //            ExecutionPojo executionPojo = workflowexecutionPojos.get(i);
//          //            executionPojo.setExecutionState("await");
//          //            executionPojos.add(executionPojo);
//          //          }
//
//
//          for (ExecutionPojo executionPojo : workflowexecutionPojos) {
//            executionPojo.setExecutionState("await");
//            executionPojos.add(executionPojo);
//          }
//          taskExecution.put(stringListPojo.getKey(), executionPojos);
//        }
//
//      }
//    }


    task.setExecution(activity.getExecution());
    task.setCandidateEditable(activity.getCandidateEditable());
    task.setSourceTransition((Map<String, String>) activityInstance.activity.getProperty("sourceTransition"));
    task.setDemandSuperior(activity.getDemandSuperior());
    task.setDemandBeyondAssignee(activity.getDemandBeyondAssignee());
    task.setBeyondAssignee(activity.getBeyondAssignee());
    task.setAllPassType(activity.getAllPassType());
    task.setExternalApplyTask(activity.getExternalApplyTask());
    task.setAssignNextTask(activity.getAssignNextTask());
    task.setCandidateByPreTask(activity.getCandidateByPreTask());

    try {

      //暂时只有bpm有此特殊需求，而且此操作比较耗费性能
      if (WorkflowConstants.AppId.APP_ID_BPM.equals(task.getAppId())) {
        if ((task.getExternalApplyTask() == null || task.getExternalApplyTask() == 0) &&
          (null == task.getCandidateIds() || task.getCandidateIds().size() < 1)) {
          task.setErrMsg("当前节点无审批人");
          task.setState(WorkflowConstants.UserTaskStatus.ERROR);
        }
        Map<String, Object> variableMap = new HashMap<>();
        Map<String, VariableInstanceImpl> variableInstancesMap = workflowInstance.variableInstancesMap;
        if (null != variableInstancesMap) {
          variableInstancesMap.keySet().forEach(key -> {
            if (null != variableInstancesMap.get(key) && null != variableInstancesMap.get(key).value) {
              variableMap.put(key, variableInstancesMap.get(key).value.toString());
            }
          });
        }
        Map<String, Object> extInfoMap = new HashMap<>();

        extInfoMap.put("properties", activityInstance.activity.getProperties());
        extInfoMap.put("variables", variableMap);
        extInfoMap.put(WorkflowBindingEnum.appId.toString(), task.getAppId());

        Class<?> bpmServiceExtension = Class.forName("com.facishare.paas.workflow.kernel.support.BPMServiceSupport");
        Method beforeMethod = bpmServiceExtension.getMethod("beforeTaskInsert", Map.class);
        Map<String, Object> beforeResultObject = (Map<String, Object>) beforeMethod.invoke(bpmServiceExtension.newInstance(), extInfoMap);

        this.appendTaskInfo(task, beforeResultObject);
        task.setRule(activity.getRule());

      }

      taskStore.insertTask(task);

      Class<?> approvalFlowServiceSupport = Class.forName("com.facishare.paas.workflow.kernel.support.ApprovalFlowServiceSupport");
      Method method = approvalFlowServiceSupport.getMethod("afterTaskInsert", String.class);
      method.invoke(approvalFlowServiceSupport.newInstance(), task.getId().getInternal());
    } catch (Exception e) {
      throw new RuntimeException("user task error", e);
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

  private void appendTaskInfo(Task task, Map<String, Object> map) {
    if (null != task && null != map && map.size() > 0) {
      if (map.containsKey(WorkflowBindingEnum.bpmExtension.toString())) {
        task.setBpmExtension((Map<String, Object>) map.get(WorkflowBindingEnum.bpmExtension.toString()));
      }
      if (map.containsKey(WorkflowBindingEnum.objectId.toString())) {
        task.setObjectId((String) map.get(WorkflowBindingEnum.objectId.toString()));
      }
      if (map.containsKey(WorkflowBindingEnum.entityId.toString())) {
        task.setEntityId((String) map.get(WorkflowBindingEnum.entityId.toString()));
      }
      if (map.containsKey(WorkflowBindingEnum.state.toString())) {
        task.setState((String) map.get(WorkflowBindingEnum.state.toString()));
      }
      if (map.containsKey("errMsg")) {
        task.setErrMsg((String) map.get("errMsg"));
      }
      if (map.containsKey(WorkflowBindingEnum.remind.toString())) {
        task.setRemind((Boolean) map.get(WorkflowBindingEnum.remind.toString()));
      }
      if (map.containsKey(WorkflowBindingEnum.remindLatency.toString())) {
        task.setRemindLatency((Integer) map.get(WorkflowBindingEnum.remindLatency.toString()));
      }
    }
  }

  private Task generateTask(ActivityInstanceImpl activityInstance) {
    WorkflowImpl workflow = activityInstance.workflow;
    WorkflowInstanceImpl workflowInstance = activityInstance.workflowInstance;

    Task task = new Task();
    task.setId(taskStore.generateTaskId());

    Map<String, List<String>> assigneeMap = activity.getAssignee();
    Map<String, List<String>> filteredMap = new HashMap();
    String taskType = activity.getTaskType();
    String workflowInstanceId = workflowInstance.getId().toString();
    List<String> candidateIds = new ArrayList();
    try {
      Class<?> userTaskSupport = Class.forName("com.facishare.paas.workflow.kernel.support.UserTaskSupport");
      Method parseMethod = userTaskSupport.getMethod("parseAssignee", AssigneeParserPojo.class);

      String tenantId = this.getWorkflowProperty(workflow, WorkflowBindingEnum.tenantId.toString());
      String appId = this.getWorkflowProperty(workflow, WorkflowBindingEnum.appId.toString());
      String applicantId = this.getWorkflowInstanceProperty(workflowInstance, WorkflowBindingEnum.applicantId.toString());
      String taskId = task.getId().toString();
      String activityId = activity.getId();
      if (null != taskType) {
        switch (taskType) {
          case WorkflowConstants.UserTaskType.ALL:
            filteredMap.clear();
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.PERSON)) {
              filteredMap.put(WorkflowConstants.AssigneeType.PERSON, assigneeMap.get(WorkflowConstants.AssigneeType.PERSON));
              //              candidateIds.addAll(assigneeMap.get(WorkflowConstants.AssigneeType.PERSON));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.GROUP)) {
              filteredMap.put(WorkflowConstants.AssigneeType.GROUP, assigneeMap.get(WorkflowConstants.AssigneeType.GROUP));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.EXT_BPM)) {
              filteredMap.put(WorkflowConstants.AssigneeType.EXT_BPM, assigneeMap.get(WorkflowConstants.AssigneeType.EXT_BPM));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.extUserType)) {
              filteredMap.put(WorkflowConstants.AssigneeType.extUserType, assigneeMap.get(WorkflowConstants.AssigneeType.extUserType));
            }
            break;
          case WorkflowConstants.UserTaskType.ANYONE:
            filteredMap.clear();
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.DEPT)) {
              filteredMap.put(WorkflowConstants.AssigneeType.DEPT, assigneeMap.get(WorkflowConstants.AssigneeType.DEPT));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.GROUP)) {
              filteredMap.put(WorkflowConstants.AssigneeType.GROUP, assigneeMap.get(WorkflowConstants.AssigneeType.GROUP));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.ROLE)) {
              filteredMap.put(WorkflowConstants.AssigneeType.ROLE, assigneeMap.get(WorkflowConstants.AssigneeType.ROLE));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.EXT_BPM)) {
              filteredMap.put(WorkflowConstants.AssigneeType.EXT_BPM, assigneeMap.get(WorkflowConstants.AssigneeType.EXT_BPM));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.PERSON)) {
              //              candidateIds.addAll(assigneeMap.get(WorkflowConstants.AssigneeType.PERSON));
              filteredMap.put(WorkflowConstants.AssigneeType.PERSON, assigneeMap.get(WorkflowConstants.AssigneeType.PERSON));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.extUserType)) {
              filteredMap.put(WorkflowConstants.AssigneeType.extUserType, assigneeMap.get(WorkflowConstants.AssigneeType.extUserType));
            }
            break;
          case WorkflowConstants.UserTaskType.ONE:
            filteredMap.clear();
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.DEPT_LEADER)) {
              filteredMap.put(WorkflowConstants.AssigneeType.DEPT_LEADER, assigneeMap.get(WorkflowConstants.AssigneeType.DEPT_LEADER));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.EXT_BPM)) {
              filteredMap.put(WorkflowConstants.AssigneeType.EXT_BPM, assigneeMap.get(WorkflowConstants.AssigneeType.EXT_BPM));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.extUserType)) {
              filteredMap.put(WorkflowConstants.AssigneeType.extUserType, assigneeMap.get(WorkflowConstants.AssigneeType.extUserType));
            }
            //                        candidateIds =
            //                            new ArrayList((Set<String>) parseMethod.invoke(userTaskSupport.newInstance()
            //                                    , new AssigneeParserPojo()
            //                                            .assigneeMap(filteredMap)
            //                                            .tenantId(tenantId)
            //                                            .appId(appId)
            //                                            .userId(applicantId)
            //                                            .taskId(taskId)
            //                                            .workflowInstanceId(workflowInstanceId)));
            if (assigneeMap.containsKey(WorkflowConstants.AssigneeType.APPLICANT)) {
              //              candidateIds.add(applicantId);
              filteredMap.put(WorkflowConstants.AssigneeType.APPLICANT, assigneeMap.get(WorkflowConstants.AssigneeType.APPLICANT));
            }
            if (null != assigneeMap.get(WorkflowConstants.AssigneeType.PERSON)) {
              //              candidateIds.addAll(assigneeMap.get(WorkflowConstants.AssigneeType.PERSON));
              filteredMap.put(WorkflowConstants.AssigneeType.PERSON, assigneeMap.get(WorkflowConstants.AssigneeType.PERSON));
            }
            if (1 < candidateIds.size()) {
              String candidateId = candidateIds.get(0);
              candidateIds = new ArrayList<>();
              candidateIds.add(candidateId);
            }
            break;
          case WorkflowConstants.UserTaskType.ONE_BY_ONE:
            //Todo: 准备处理重构后的level和grade
            break;
          default:
            break;
        }
        AssigneeParserPojo assigneeParserPojo = new AssigneeParserPojo().assigneeMap(filteredMap)
                                                                        .tenantId(tenantId)
                                                                        .appId(appId)
                                                                        .userId(applicantId)
                                                                        .taskId(taskId)
                                                                        .activityId(activityId)
                                                                        .workflowInstanceId(workflowInstanceId);


        assigneeParserPojo.setApplicantId(applicantId);
        assigneeParserPojo.setWorkflowId(workflow.id.getInternal());


        Map<String, Object> variableMap = new HashMap<>();
        Map<String, VariableInstanceImpl> variableInstancesMap = workflowInstance.variableInstancesMap;
        if (null != variableInstancesMap) {
          variableInstancesMap.keySet().forEach(key -> {
            if (null != variableInstancesMap.get(key) && null != variableInstancesMap.get(key).value) {
              variableMap.put(key, variableInstancesMap.get(key).value.toString());
            }
          });
        }

        //        List<VariableInstanceImpl> variableInstances = workflowInstance.variableInstances;
        //        if (variableInstances != null) {
        //          for (VariableInstanceImpl variableInstance : variableInstances) {
        //            variableMap.put(variableInstance.getVariable().id, variableInstance.getValue());
        //          }
        //        }


        assigneeParserPojo.setVariables(variableMap);

        Map<String, Object> properties = activityInstance.activity.getProperties();
        Map<String, Object> bpmVariables = variableMap;

        if (properties != null && bpmVariables != null) {

          Map<String, Object> bpmExtension = (Map<String, Object>) properties.get(WorkflowBindingEnum.bpmExtension.toString());

          if (bpmExtension != null) {

            Map<String, Object> objectIdMap = (Map<String, Object>) bpmExtension.get(WorkflowBindingEnum.objectId.toString());

            if (objectIdMap != null) {

              String objectIdVar = (String) objectIdMap.get("expression");
              assigneeParserPojo.setObjectId((String) bpmVariables.get(objectIdVar));
              assigneeParserPojo.setEntityId((String) bpmExtension.get(WorkflowBindingEnum.entityId.toString()));
            }
          }


        }


        if (null != filteredMap.get(WorkflowConstants.AssigneeType.EXT_BPM)) {
          Map<String, Object> bpmExtension = (Map<String, Object>) activityInstance.activity.getProperty(WorkflowBindingEnum.bpmExtension.toString());
          if (null == bpmExtension) {
            bpmExtension = new HashMap<>();
          }
          Map<String, VariableInstanceImpl> variables = activityInstance.workflowInstance.variableInstancesMap;
          Map<String, Object> bpmExtMap = new HashMap();

          if (null != variables) {
            variables.forEach((key, value) -> {
              if (null != value && null != value.value) {
                bpmExtMap.put(key, value.value);
              }
            });
          }

          //                    if (variables != null && variables.size() > 0) {
          //                        for (VariableInstanceImpl variable : variables) {
          //                            if (variable != null && null != variable.value && variable.variable.id) {
          //                                bpmExtMap.put(variable.getId(), variable.getValue());
          //                            }
          //                        }
          //                    }
          bpmExtension.put("variables", bpmExtMap);
          assigneeParserPojo.bpmExtension(bpmExtension);
        }


        candidateIds.addAll((Set<String>) parseMethod.invoke(userTaskSupport.newInstance(), assigneeParserPojo));
      }
    } catch (Exception e) {
      throw new RuntimeException("user task error", e);
    }
    task.setCandidateIds(new ArrayList(new HashSet(candidateIds)));
    return task;
  }

}

