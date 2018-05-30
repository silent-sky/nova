package com.nova.paas.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.effektif.workflow.api.ext.WorkflowBindingEnum;
import com.effektif.workflow.api.workflow.ExecutableWorkflow;
import com.effektif.workflow.impl.json.DefaultJsonStreamMapper;
import com.google.common.collect.Sets;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.workflow.constant.WorkflowConstant;
import com.nova.paas.workflow.dao.EffektifAdapter;
import com.nova.paas.workflow.dao.WorkflowDao;
import com.nova.paas.workflow.entity.WorkflowEntity;
import com.nova.paas.workflow.exception.WorkflowErrorMsg;
import com.nova.paas.workflow.exception.WorkflowServiceException;
import com.nova.paas.workflow.param.CompleteTaskParam;
import com.nova.paas.workflow.param.InstanceQueryParam;
import com.nova.paas.workflow.param.UserTaskQueryParam;
import com.nova.paas.workflow.pojo.*;
import com.nova.paas.workflow.service.BpmService;
import com.nova.paas.workflow.service.RuleService;
import com.nova.paas.workflow.util.WorkflowJsonCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * zhenghaibo
 * 2018/5/10 14:30
 */
@Service
@Slf4j
public class BpmServiceImpl implements BpmService {
    @Inject
    private EffektifAdapter effektifAdapter;
    @Inject
    private WorkflowDao workflowDao;
    @Inject
    private RuleService ruleService;

    @Override
    public WorkflowPojo deploy(CommonContext context, boolean isNew, String workflowJson, String ruleJson) throws WorkflowServiceException {
        WorkflowJsonCheckUtil.checkProperties(workflowJson);
        ExecutableWorkflow workflow = new DefaultJsonStreamMapper().readString(workflowJson, ExecutableWorkflow.class);
        String sourceWorkflowId = workflow.getSourceWorkflowId();

        //如果是新增流程，需要判断sourceWorkflowId是否唯一
        if (isNew) {
            workflow.setSourceWorkflowId(IdUtil.generateId());
            workflow.setCreatedBy(context.getUserId());
            if (!workflowDao.checkSourceWorkflowIdUnique(context.getTenantId(), workflow.getSourceWorkflowId())) {
                throw new WorkflowServiceException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
            }
        } else {
            if (StringUtils.isBlank(sourceWorkflowId)) {
                throw new WorkflowServiceException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
            }
            WorkflowEntity latestWorkflow = workflowDao.getLatestWorkflowBySourceId(context.getTenantId(), sourceWorkflowId);
            if (latestWorkflow != null) {
                workflow.setCreatedBy(context.getUserId());
            }

        }

        workflow.property(WorkflowBindingEnum.tenantId.toString(), context.getTenantId())
                .property(WorkflowBindingEnum.workflowType.toString(), WorkflowConstant.WorkflowType.BPM)
                .property(WorkflowBindingEnum.deleted.toString(), false);

        if (Objects.nonNull(workflow.getId())) {
            workflow.id(null);
        }
        WorkflowPojo workflowPojo = effektifAdapter.deployWorkflow(workflow);
        sourceWorkflowId = workflowPojo.getSourceWorkflowId();

        //流程启动规则
        if (isNew) {
            if (StringUtils.isNotBlank(ruleJson)) {
                RulePojo rulePojo = JSON.parseObject(ruleJson, RulePojo.class);
                rulePojo.setTenantId(context.getTenantId());
                rulePojo.setCreatedBy(context.getUserId());
                rulePojo.setRuleType(WorkflowConstant.WorkflowType.BPM);
                rulePojo.setSourceWorkflowId(sourceWorkflowId);
                String ruleId = ruleService.createRule(context, rulePojo);

                workflowPojo.setRuleId(ruleId);
            }
        } else {
            if (StringUtils.isNotBlank(ruleJson)) {
                RulePojo rulePojo = JSON.parseObject(ruleJson, RulePojo.class);
                if (!StringUtils.equals(rulePojo.getSourceWorkflowId(), sourceWorkflowId)) {
                    log.warn("ruleJson illegal", workflowJson);
                    throw new WorkflowServiceException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
                }
                rulePojo.setTenantId(context.getTenantId());
                rulePojo.setModifiedBy(context.getUserId());

                if (StringUtils.isBlank(rulePojo.getId())) {
                    String ruleId = ruleService.createRule(context, rulePojo);
                    workflowPojo.setRuleId(ruleId);
                } else {
                    ruleService.updateRule(context, rulePojo);
                    workflowPojo.setRuleId(rulePojo.getId());
                }

            } else {
                ruleService.deleteRule(context, Sets.newHashSet(sourceWorkflowId));
            }
        }
        return workflowPojo;
    }

    @Override
    public String startByRule(CommonContext context, String entityId, String objectId, String triggerType) throws WorkflowServiceException {
        List<RulePojo> ruleList = ruleService.findRules(context, WorkflowConstant.WorkflowType.BPM, entityId, triggerType);
        if (CollectionUtils.isEmpty(ruleList)) {
            log.warn("not match rules,context:{},entityId:{},triggerType:{}", context, entityId, triggerType);
            throw new WorkflowServiceException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
        }
        //默认取第一个匹配到的规则
        RulePojo rulePojo = ruleList.get(0);
        String sourceWorkflowId = rulePojo.getSourceWorkflowId();
        context.property(WorkflowBindingEnum.triggerType.toString(), triggerType);
        String instanceId = this.start(context, sourceWorkflowId, entityId, objectId, null);
        return instanceId;

    }

    @Override
    public String start(CommonContext context, String sourceWorkflowId, String entityId, String objectId, Map<String, Object> conditionMap)
            throws WorkflowServiceException {
        Map<WorkflowBindingEnum, Object> bindingMap = this.conventBindingMap(context, entityId, objectId);

        if (StringUtils.isBlank(sourceWorkflowId)) {
            throw new WorkflowServiceException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
        }
        WorkflowEntity workflow = workflowDao.getLatestWorkflowBySourceId(context.getTenantId(), sourceWorkflowId);
        if (workflow == null) {
            return "";
        }
        bindingMap.put(WorkflowBindingEnum.triggerType, context.getProperty(WorkflowBindingEnum.triggerType.toString()));

        String instanceId = effektifAdapter.startWorkflowByWorkflowId(workflow.getId().toString(), conditionMap, bindingMap);

        return instanceId;
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

    private Map<WorkflowBindingEnum, Object> conventBindingMap(CommonContext context, String entityId, String objectId) {
        EnumMap<WorkflowBindingEnum, Object> bindingMap = new EnumMap<WorkflowBindingEnum, Object>(WorkflowBindingEnum.class);
        bindingMap.put(WorkflowBindingEnum.tenantId, context.getTenantId());
        bindingMap.put(WorkflowBindingEnum.appId, context.getAppId());
        bindingMap.put(WorkflowBindingEnum.entityId, entityId);
        bindingMap.put(WorkflowBindingEnum.objectId, objectId);
        bindingMap.put(WorkflowBindingEnum.applicantId, context.getUserId());
        bindingMap.put(WorkflowBindingEnum.status, WorkflowConstant.InstanceStatus.IN_PROGRESS);
        bindingMap.put(WorkflowBindingEnum.workflowType, WorkflowConstant.WorkflowType.BPM);
        return bindingMap;
    }
}
