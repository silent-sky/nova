package com.nova.paas.workflow.service;

import com.nova.paas.workflow.exception.WorkflowServiceException;
import com.nova.paas.workflow.pojo.RulePojo;

import java.util.Set;

/**
 * zhenghaibo
 * 2018/5/16 18:02
 */
public interface RuleService {
    String createRule(RulePojo rulePojo) throws WorkflowServiceException;

    void updateRule(RulePojo rulePojo) throws WorkflowServiceException;

    void deleteRule(String tenantId, String userId, Set<String> sourceWorkflowIds) throws WorkflowServiceException;
}
