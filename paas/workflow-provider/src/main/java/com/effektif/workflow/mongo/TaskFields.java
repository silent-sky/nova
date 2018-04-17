package com.effektif.workflow.mongo;

/**
 * zhenghaibo
 * 2018/4/8 17:53
 */
public interface TaskFields {
  String _ID = "_id";
  String NAME = "name";
  String DESCRIPTION = "description";
  String SOURCE_WORKFLOW_ID = "sourceWorkflowId";
  String WORKFLOW_ID = "workflowId";
  String WORKFLOW_INSTANCE_ID = "workflowInstanceId";
  String TENANT_ID = "tenantId";
  String APP_ID = "appId";
  String ENTITY_ID = "entityId";
  String OBJECT_ID = "objectId";
  String APPLICANT_ID = "applicantId";
  String ASSIGNEE_IDS = "assigneeIds";
  String CANDIDATE_IDS = "candidateIds";
  String ASSIGNEE = "assignee";
  //是否在实例中更换过审批人
  String ASSIGNEE_CHANGED = "assigneeChanged";
  //已废弃
  String ASSIGNEE_CHANGE_LOG = "assigneeChangeLog";
  //更换审批人历史
  String APPROVER_MODIFY_LOG = "approverModifyLog";
  //是否为审批例外人
  String CANDIDATE_EDITABLE = "candidateEditable";
  String TASK_TYPE = "taskType";
  String ACTION_TYPE = "actionType";
  String CREATE_TIME = "createTime";
  String MODIFY_TIME = "modifyTime";
  String MODIFIER = "modifier";
  String COMPLETED = "completed";
  String CANCELED = "canceled";
  String OPINIONS = "opinions";
  String STATE = "state";
  String REMIND = "remind";
  String REMIND_LATENCY = "remindLatency";
  String REMIND_ID = "remindId";
  String ERR_MSG = "errMsg";
  String ACTIVITY_ID = "activityId";
  String ACTIVITY_INSTANCE_ID = "activityInstanceId";
  String WORKFLOW_NAME = "workflowName";
  String WORKFLOW_DESCRIPTION = "workflowDescription";
  String BPM_EXTENSION = "bpmExtension";
  String RULE = "rule";
  String EXECUTION = "execution";
  String REMINDERS = "reminders";
  String DELETED = "deleted";
  String SOURCE_TRANSITION = "sourceTransition";
  String TASK_EXECUTE_STATE = "taskExecuteState";
  String TASK_EXECUTION_EXECUTED_INDEX = "taskExecutionExecutedIndex";

  String DEMAND_BEYOND_ASSIGNEE = "demandBeyondAssignee";
  String BEYOND_ASSIGNEE = "beyondAssignee";
  String ALL_PASS_TYPE = "allPassType";
  String EXTERNAL_APPLY_TASK = "externalApplyTask";
  String ASSIGN_NEXT_TASK = "assignNextTask";
  String CANDIDATE_BY_PRE_TASK = "candidateByPreTask";

}
