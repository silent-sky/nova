package com.effektif.workflow.impl.ext;

import com.effektif.workflow.api.ext.WorkflowConstants;
import com.effektif.workflow.api.model.Message;
import com.effektif.workflow.api.model.TaskId;
import com.effektif.workflow.api.model.TypedValue;
import com.effektif.workflow.api.model.WorkflowInstanceId;
import com.effektif.workflow.impl.WorkflowEngineImpl;
import com.effektif.workflow.impl.configuration.Brewable;
import com.effektif.workflow.impl.configuration.Brewery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 18/4/8 14:46
 */
public class TaskFactory implements Brewable {

  public static final Logger log = LoggerFactory.getLogger(TaskFactory.class);

  protected TaskStore taskStore;
  protected WorkflowEngineImpl workflowEngine;

  @Override
  public void brew(Brewery brewery) {
    this.taskStore = brewery.get(TaskStore.class);
    this.workflowEngine = brewery.get(WorkflowEngineImpl.class);
  }

  public Task createTask(Task task) {
    if (task == null) {
      task = new Task();
    }
    TaskId taskId = task.getId();
    if (taskId == null) {
      taskId = taskStore.generateTaskId();
    }
    task.setId(taskId);
    taskStore.insertTask(task);
    return task;
  }

  public Task assignTask(String taskId, List<String> assigneeIds) {
    Task task = taskStore.assignTask(taskId, assigneeIds);
    return task;
  }

  public Task completeTask(String taskId) {
    Task task = taskStore.completeTask(taskId, null);
    task.setCompleted(true);
    if (Boolean.TRUE.equals(task.getActivityNotify())) {
      task.setActivityNotify(null);
      Message message = new Message().workflowInstanceId(new WorkflowInstanceId(task.getWorkflowInstanceId())).activityInstanceId(task.getActivityInstanceId());
      workflowEngine.send(message);
    }
    return task;
  }

  public Task completeTaskWithCondition(String taskId,
                                        Map<String, Object> conditionMap,
                                        Map<String, Object> bindingMap,
                                        String actionType,
                                        ApprovalOpinion opinion) {
    Task task = this.findTaskById(taskId);

    List<ApprovalOpinion> taskOpinions = task.getOpinions();
    if (taskOpinions == null) {
      taskOpinions = new ArrayList();
    }
    taskOpinions.add(opinion);
    List<String> assigneeIds = task.getAssigneeIds();
    if (assigneeIds == null) {
      assigneeIds = new ArrayList<>();
      assigneeIds.add(opinion.getUserId());
      task.setAssigneeIds(assigneeIds);
    } else if (!assigneeIds.contains(opinion.getUserId())) {
      assigneeIds.add(opinion.getUserId());
    }
    if (this.mayComplete(task, opinion.getUserId(), actionType)) {
      if (null == conditionMap) {
        conditionMap = new HashMap<>();
      }
      if (WorkflowConstants.UserTaskType.ALL.equals(task.getTaskType())) {
        actionType = this.getCountersignatureResult(taskOpinions);
      }
      //bpm坚持的key样式 -_-||
      conditionMap.put("activity_" + task.getActivityId() + "##result", actionType);
      task = taskStore.completeTask(taskId, actionType, taskOpinions, assigneeIds);

      task.setCompleted(true);
      if (Boolean.TRUE.equals(task.getActivityNotify())) {
        task.setActivityNotify(null);
        Message message = new Message().workflowInstanceId(new WorkflowInstanceId(task.getWorkflowInstanceId()))
                                       .activityInstanceId(task.getActivityInstanceId());
        if (conditionMap != null && conditionMap.size() > 0) {
          for (Map.Entry entry : conditionMap.entrySet()) {
            message.data(entry.getKey().toString(), entry.getValue());
          }
        }

        // TODO: hanmz 2017/11/17 生成下一个节点前执行后动作
        conditionMap = actionAfterAction();
        if (conditionMap != null && conditionMap.size() > 0) {
          for (Map.Entry entry : conditionMap.entrySet()) {
            message.data(entry.getKey().toString(), entry.getValue());
          }
        }
        message.setTransientData(bindingMap);

        workflowEngine.send(message);
      }
    } else {
      task = taskStore.updateApprovalOpinion(taskId, taskOpinions, assigneeIds);
    }

    return task;
  }


  public void afterAction2NextTask(String instanceId, Map<String, Object> conditionMap,Map<String, Object> bindingMap, String activityInstanceId) {
    Message message = new Message().workflowInstanceId(new WorkflowInstanceId(instanceId)).activityInstanceId(activityInstanceId);
    if (conditionMap != null && conditionMap.size() > 0) {
      for (Map.Entry entry : conditionMap.entrySet()) {
        message.data(entry.getKey().toString(), entry.getValue());
      }
    }

    message.setTransientData(bindingMap);

    workflowEngine.send(message);
  }


  public Task completeTaskWithConditionObject(String taskId,
                                              Map<String, Object> conditionMap,
                                              Map<String, Object> bindingMap,
                                              String actionType,
                                              String state,
                                              List<ApprovalOpinion> opinionList) {
    Task task = taskStore.completeTask(taskId, actionType, state, opinionList);
    task.setCompleted(true);
    if (Boolean.TRUE.equals(task.getActivityNotify())) {
      task.setActivityNotify(null);
      Message message = new Message().workflowInstanceId(new WorkflowInstanceId(task.getWorkflowInstanceId())).activityInstanceId(task.getActivityInstanceId());
      if (conditionMap != null && conditionMap.size() > 0) {
        for (Map.Entry entry : conditionMap.entrySet()) {
          message.data(entry.getKey().toString(), entry.getValue());
        }
      }

      // TODO: hanmz 2017/11/17 生成下一个节点前执行后动作
      conditionMap = actionAfterAction();
      if (conditionMap != null && conditionMap.size() > 0) {
        for (Map.Entry entry : conditionMap.entrySet()) {
          message.data(entry.getKey().toString(), entry.getValue());
        }
      }
      message.setTransientData(bindingMap);

      workflowEngine.send(message);
    }
    return task;
  }

  @Deprecated
  public Task completeTaskWithCondition(String taskId,
                                        Map<String, TypedValue> conditionMap,
                                        Map<String, Object> bindingMap,
                                        String actionType,
                                        String state,
                                        List<ApprovalOpinion> opinionList) {
    Task task = taskStore.completeTask(taskId, actionType, state, opinionList);
    task.setCompleted(true);
    if (Boolean.TRUE.equals(task.getActivityNotify())) {
      task.setActivityNotify(null);
      Message message = new Message().workflowInstanceId(new WorkflowInstanceId(task.getWorkflowInstanceId())).activityInstanceId(task.getActivityInstanceId());
      if (conditionMap != null && conditionMap.size() > 0) {
        for (Map.Entry entry : conditionMap.entrySet()) {
          message.data(entry.getKey().toString(), ((TypedValue) entry.getValue()).getValue(), ((TypedValue) entry.getValue()).getDataType());
        }
      }
      message.setTransientData(bindingMap);
      workflowEngine.send(message);
    }
    return task;
  }

  public Task findTaskById(String taskId) {
    List<Task> tasks = this.findTasks(new UserTaskQuery().taskId(taskId));
    if (tasks.isEmpty()) {
      return null;
    }
    Task task = tasks.get(0);
    return task;
  }

  public List<Task> findTasks(UserTaskQuery taskQuery) {
    return taskStore.findTasks(taskQuery);
  }

  public void deleteTasks(UserTaskQuery taskQuery) {
    taskStore.deleteTasks(taskQuery);
  }

  private boolean mayComplete(Task task, String userId, String actionType) {
    boolean canComplete = true;
    //会签
    if (WorkflowConstants.UserTaskType.ALL.equals(task.getTaskType())) {
      // allPassType = 0 如果第一个人执行了reject，则流程就终止，会签的其他人不需要执行。
      if (WorkflowConstants.Action.REJECT.equals(actionType) && (task.getAllPassType() == null || task.getAllPassType() == 0)) {
        return canComplete;
      }
      List<String> assigneeIds = task.getAssigneeIds() == null ? new ArrayList() : task.getAssigneeIds();
      if (assigneeIds.size() < task.getCandidateIds().size()) {
        canComplete = false;
      }
    }


    return canComplete;
  }

  //会签结果: all agree -> agree; one reject -> reject
  private String getCountersignatureResult(List<ApprovalOpinion> opinionList) {
    String result = WorkflowConstants.Action.AGREE;
    for (ApprovalOpinion opinion : opinionList) {
      if (WorkflowConstants.Action.REJECT.equals(opinion.getActionType())) {
        result = WorkflowConstants.Action.REJECT;
        break;
      }
    }
    return result;
  }

  /**
   * todo 执行task后动作
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> actionAfterAction() {
    try {
      Class<?> afterActionStandard = Class.forName("com.facishare.paas.workflow.kernel.support.AfterActionSupport");
      Method beforeMethod = afterActionStandard.getMethod("actionTaskAfterAction");
      Map<String, Object> map = (Map<String, Object>) beforeMethod.invoke(afterActionStandard.newInstance());
      return (Map<String, Object>) map.get("conditionMap");
    } catch (Exception e) {
      throw new RuntimeException("user task after action error", e);
    }
  }
}
