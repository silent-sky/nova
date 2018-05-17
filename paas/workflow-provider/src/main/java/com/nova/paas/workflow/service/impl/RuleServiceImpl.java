package com.nova.paas.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.workflow.dao.RuleDao;
import com.nova.paas.workflow.entity.RuleEntity;
import com.nova.paas.workflow.exception.WorkflowServiceException;
import com.nova.paas.workflow.pojo.RulePojo;
import com.nova.paas.workflow.service.RuleService;
import com.nova.paas.workflow.util.EntityConverter;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/5/16 18:04
 */
@Service
@Slf4j
public class RuleServiceImpl implements RuleService {
    @Inject
    private RuleDao ruleDao;

    @Override
    public String createRule(RulePojo rulePojo) throws WorkflowServiceException {

        return ruleDao.insert(EntityConverter.convertRule(rulePojo));
    }

    @Override
    public void updateRule(RulePojo rulePojo) throws WorkflowServiceException {
        ruleDao.update(EntityConverter.convertRule(rulePojo));
    }

    @Override
    public void deleteRule(String tenantId, String userId, Set<String> sourceWorkflowIds) throws WorkflowServiceException {
        ruleDao.delete(tenantId, userId, sourceWorkflowIds);
    }
}
