package com.effektif.workflow.api.ext;

/**
 * zhenghaibo
 * 2018/4/8 20:04
 */
public enum WorkflowBindingEnum {
    tenantId,//租户
    appId,//产品线
    entityId,//实体
    objectId,//对象id
    applicantId,//提交人id
    status,//状态
    deleted,//删除标志
    workflowType,//流程类型
    triggerType,//触发动作
    remind,//提醒
    remindLatency,//提醒时间
    reminders,//task提醒设置
    modifiedAt, /*更新时间*/
    execution, /*流程后动作*/
    eventId/*防止流程循环调用*/
}
