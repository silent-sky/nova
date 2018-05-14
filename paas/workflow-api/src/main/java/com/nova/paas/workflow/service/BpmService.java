package com.nova.paas.workflow.service;

import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.workflow.exception.WorkflowServiceException;
import com.nova.paas.workflow.param.CompleteTaskParam;
import com.nova.paas.workflow.param.InstanceQueryParam;
import com.nova.paas.workflow.param.UserTaskQueryParam;
import com.nova.paas.workflow.pojo.InstancePojo;
import com.nova.paas.workflow.pojo.TaskPojo;
import com.nova.paas.workflow.pojo.WorkflowDefinitionPojo;
import com.nova.paas.workflow.pojo.WorkflowPojo;

import java.util.List;

/**
 * zhenghaibo
 * 2018/5/9 19:48
 */
public interface BpmService {

    /**
     * 部署BPM
     */
    WorkflowPojo deploy(CommonContext context, boolean update, String workflowJson, String ruleJson) throws WorkflowServiceException;

    /**
     * 根据rule启动匹配的流程
     */
    String startByRule(CommonContext context, String entityId, String objectId, String triggerType) throws WorkflowServiceException;

    /**
     * 启动一个流程实例
     */
    String start(CommonContext context, String entityId, String objectId) throws WorkflowServiceException;

    /**
     * 完成一个任务
     */
    void completeTask(CommonContext context, CompleteTaskParam param) throws WorkflowServiceException;

    /**
     * 取消流程
     */
    void cancel(CommonContext context, String workflowInstanceId) throws WorkflowServiceException;

    /**
     * 根据流程ID查询流程定义
     */
    WorkflowDefinitionPojo findDefinition(CommonContext context, String workflowId) throws WorkflowServiceException;

    /**
     * 删除流程定义
     */
    void deleteDefinition(CommonContext context, String sourceWorkflowId) throws WorkflowServiceException;

    /**
     * 查询流程实例
     */
    List<InstancePojo> findInstancesByCondition(CommonContext context, InstanceQueryParam param) throws WorkflowServiceException;

    /**
     * 查询任务
     */
    List<TaskPojo> findTasksByCondition(CommonContext context, UserTaskQueryParam param) throws WorkflowServiceException;

    /**
     * 根据id查询task
     */
    TaskPojo findTaskById(CommonContext context, String taskId) throws WorkflowServiceException;

    /**
     * 根据id查询流程实例
     */
    InstancePojo findWorkflowInstanceById(CommonContext context, String instanceId) throws WorkflowServiceException;

    /**
     * 修改待审批人
     */
    void changeCandidates(CommonContext context, String taskId, List<String> candidateIds) throws WorkflowServiceException;

}
