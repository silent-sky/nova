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
  applicantAccount,//提交人账号
  state,//实例状态
  type,//流程类型
  triggerType,//触发动作
  triggerSource,//触发来源
  remind,//提醒
  remindLatency,//提醒时间
  modifyTime, execution, reminders,//task提醒设置
  bpmExtension,//bpm扩展属性

  /**
   * dataLockType == 0或字段不存在，表示锁定主对象和从对象
   * dataLockType == 1       表示只锁定主对象
   */
  dataLockType,

  /**
   *  防止工作流的死循环
   */
  eventId,

  /**
   *  记录流程实例的版本,如6.2版本
   */
  version,

  triggerData;
}
