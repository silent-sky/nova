package com.nova.paas.workflow.service;

import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.workflow.exception.WorkflowServiceException;
import com.nova.paas.workflow.pojo.RulePojo;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/5/16 18:02
 */
public interface RuleService {
    String createRule(CommonContext context, RulePojo rulePojo) throws WorkflowServiceException;

    void updateRule(CommonContext context, RulePojo rulePojo) throws WorkflowServiceException;

    void deleteRule(CommonContext context, Set<String> sourceWorkflowIds) throws WorkflowServiceException;

    List<RulePojo> findRules(CommonContext context, String workflowType, String entityId, String triggerType) throws WorkflowServiceException;
}
