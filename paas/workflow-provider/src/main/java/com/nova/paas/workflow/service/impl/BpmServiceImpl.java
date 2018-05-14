package com.nova.paas.workflow.service.impl;

import com.effektif.workflow.api.ext.WorkflowBindingEnum;
import com.nova.paas.workflow.constant.WorkflowConstants;
import com.effektif.workflow.api.workflow.ExecutableWorkflow;
import com.effektif.workflow.impl.json.DefaultJsonStreamMapper;
import com.effektif.workflow.mongo.WorkflowFields;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.workflow.exception.WorkflowErrorMsg;
import com.nova.paas.workflow.exception.WorkflowServiceException;
import com.nova.paas.workflow.param.CompleteTaskParam;
import com.nova.paas.workflow.param.InstanceQueryParam;
import com.nova.paas.workflow.param.UserTaskQueryParam;
import com.nova.paas.workflow.pojo.InstancePojo;
import com.nova.paas.workflow.pojo.TaskPojo;
import com.nova.paas.workflow.pojo.WorkflowDefinitionPojo;
import com.nova.paas.workflow.pojo.WorkflowPojo;
import com.nova.paas.workflow.service.BpmService;
import com.nova.paas.workflow.util.WorkflowJsonCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * zhenghaibo
 * 2018/5/10 14:30
 */
@Service
@Slf4j
public class BpmServiceImpl implements BpmService {
    @Override
    public WorkflowPojo deploy(
            CommonContext context, boolean update, String workflowJson, String ruleJson) throws WorkflowServiceException {
        WorkflowJsonCheckUtil.checkProperties(workflowJson);
        ExecutableWorkflow workflow = new DefaultJsonStreamMapper().readString(workflowJson, ExecutableWorkflow.class);
        String sourceWorkflowId = workflow.getSourceWorkflowId();

        if (StringUtils.isBlank(sourceWorkflowId)) {
            throw new WorkflowServiceException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
        }

        //如果是新增流程，需要判断sourceWorkflowId是否唯一
        //        if (!update) {
        //            workflow.setCreatedAt(System.currentTimeMillis());
        //            workflow.property(WorkflowFields.CREATED_BY, context.getUserId());
        //            if (!workflowKernelService.isUniqueSourceWorkflowId(context.getTenantId(), workflow.getSourceWorkflowId())) {
        //                throw new WorkflowServiceException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
        //            }
        //        } else {
        //            WorkflowEntity oldestWorkflow = workflowKernelService.getOldestWorkflowBySourceId(context.getTenantId(), context.getAppId(), sourceWorkflowId);
        //            if (oldestWorkflow != null) {
        //                workflow.setCreatedAt(oldestWorkflow.getCreatedAt());
        //                workflow.property(WorkflowFields.CREATOR, oldestWorkflow.getCreator());
        //            }
        //
        //        }
        //
        //        workflow.property(WorkflowBindingEnum.tenantId.toString(), context.getTenantId())
        //                .property(WorkflowBindingEnum.appId.toString(), context.getAppId())
        //                .property(WorkflowBindingEnum.workflowType.toString(), WorkflowConstants.WorkflowType.BPM);
        //
        //        WfArgCheckUtil.checkWorkflowActivityList(context, workflow.getActivities());
        //
        //        if (Objects.nonNull(workflow.getId())) {
        //            workflow.id(null);
        //        }
        //        WorkflowPojo workflowPojo = workflowKernelService.deployWorkflow(workflow);
        //
        //        //处理过滤规则
        //        if (!update) {
        //            if (StringUtils.isNotBlank(ruleJson)) {
        //                String ruleId = startupRuleService.createWorkflowRule(context, ruleJson, sourceWorkflowId, WorkflowConstants.WorkflowType.BPM);
        //                workflowPojo.setRuleId(ruleId);
        //            }
        //        } else {
        //            if (StringUtils.isNotBlank(ruleJson)) {
        //                WorkflowRuleEntity rule = JSON.parseObject(ruleJson, WorkflowRuleEntity.class, ObjectIdTypeProvider.getInstance());
        //                if (!StringUtils.equals(rule.getWorkflowSrcId(), sourceWorkflowId)) {
        //                    log.warn("ruleJson illegal", workflowJson);
        //                    throw new WorkflowException(3, "流程规则与流程定义中的workflowSourceId不一致, {" + rule.getWorkflowSrcId() + ":" + sourceWorkflowId + " }");
        //                }
        //                rule.setTenantId(context.getTenantId());
        //                rule.setAppId(context.getAppId());
        //                rule.setModifier(context.getUserId());
        //
        //                if (rule.getId() == null) {
        //                    String ruleId = startupRuleKernelService.insertWorkflowRule(rule);
        //                    workflowPojo.setRuleId(ruleId);
        //                } else {
        //                    //                Objects.requireNonNull(rule.getId(), "ruleId is not allow null");
        //                    startupRuleKernelService.updateById(rule);
        //                    workflowPojo.setRuleId(rule.getId().toString());
        //                }
        //
        //            } else {
        //                startupRuleKernelService.removeWorkflowRules(context.getTenantId(), context.getAppId(), Lists.newArrayList(sourceWorkflowId), context.getUserId());
        //            }
        //        }
        //        return workflowPojo;
        return null;
    }

    @Override
    public String startByRule(CommonContext context, String entityId, String objectId, String triggerType) throws WorkflowServiceException {
        return null;
    }

    @Override
    public String start(CommonContext context, String entityId, String objectId) throws WorkflowServiceException {
        return null;
    }

    @Override
    public void completeTask(CommonContext context, CompleteTaskParam param) throws WorkflowServiceException {

    }

    @Override
    public void cancel(CommonContext context, String workflowInstanceId) throws WorkflowServiceException {

    }

    @Override
    public WorkflowDefinitionPojo findDefinition(CommonContext context, String workflowId) throws WorkflowServiceException {
        return null;
    }

    @Override
    public void deleteDefinition(CommonContext context, String sourceWorkflowId) throws WorkflowServiceException {

    }

    @Override
    public List<InstancePojo> findInstancesByCondition(
            CommonContext context, InstanceQueryParam param) throws WorkflowServiceException {
        return null;
    }

    @Override
    public List<TaskPojo> findTasksByCondition(CommonContext context, UserTaskQueryParam param) throws WorkflowServiceException {
        return null;
    }

    @Override
    public TaskPojo findTaskById(CommonContext context, String taskId) throws WorkflowServiceException {
        return null;
    }

    @Override
    public InstancePojo findWorkflowInstanceById(CommonContext context, String instanceId) throws WorkflowServiceException {
        return null;
    }

    @Override
    public void changeCandidates(CommonContext context, String taskId, List<String> candidateIds) throws WorkflowServiceException {

    }
}
