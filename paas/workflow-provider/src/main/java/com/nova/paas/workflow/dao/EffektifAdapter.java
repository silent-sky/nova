package com.nova.paas.workflow.dao;

import com.effektif.workflow.api.WorkflowEngine;
import com.effektif.workflow.api.model.Deployment;
import com.effektif.workflow.api.workflow.ExecutableWorkflow;
import com.nova.paas.workflow.pojo.WorkflowPojo;
import com.nova.paas.workflow.support.MongoConfigFactory;
import org.springframework.stereotype.Component;

/**
 * zhenghaibo
 * 2018/5/16 13:51
 */
@Component
public class EffektifAdapter {
    public WorkflowPojo deployWorkflow(ExecutableWorkflow workflow) {
        WorkflowEngine engine = MongoConfigFactory.getInstance().getEngine();
        Deployment deployment = engine.deployWorkflow(workflow).checkNoErrorsAndNoWarnings();
        WorkflowPojo workflowPojo = new WorkflowPojo();
        workflowPojo.setSourceWorkflowId(workflow.getSourceWorkflowId());
        workflowPojo.setWorkflowId(deployment.getWorkflowId().getInternal());
        return workflowPojo;
    }
}
