package com.effektif.workflow.mongo;

public interface WorkflowFields {

  String _ID = "_id";
  String NAME = "name";
  String DESC = "description";
  String SOURCE_WORKFLOW_ID = "sourceWorkflowId";
  String ENABLE = "enable";//是否启用 true,false
  String TYPE = "type";//类型 审批流/工作流
  String TENANT_ID = "tenantId";
  String APP_ID = "appId";
  String ENTITY_ID = "entityId";
  String OBJECT_ID = "objectId";
  String PRIORITY = "priority";
  String CREATED_AT = "createdAt";
  String CREATED_BY = "createdBy";
  String UPDATED_AT = "updatedAt";
  String UPDATED_BY = "updatedBy";
  String DELETED = "deleted";
  String REMIND = "remind";
  String REMIND_LATENCY = "remindLatency";
  String ACTIVITIES = "activities";
  String VARIABLES = "variables";
  String TRANSITIONS = "transitions";
  String EXECUTION = "execution";
  String TRIGGER_TYPES = "triggerTypes";

  interface Versions {
    String WORKFLOW_NAME = "workflowName";
    String VERSION_IDS = "versionIds";
    String LOCK = "lock";
  }


  interface VersionsLock {
    String OWNER = "owner";
    String TIME = "time";
  }
}
