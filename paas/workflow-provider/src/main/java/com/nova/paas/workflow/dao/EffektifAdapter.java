package com.nova.paas.workflow.dao;

import com.effektif.workflow.api.WorkflowEngine;
import com.effektif.workflow.api.ext.WorkflowBindingEnum;
import com.effektif.workflow.api.model.Deployment;
import com.effektif.workflow.api.model.TriggerInstance;
import com.effektif.workflow.api.model.WorkflowId;
import com.effektif.workflow.api.workflow.ExecutableWorkflow;
import com.effektif.workflow.api.workflowinstance.WorkflowInstance;
import com.nova.paas.workflow.pojo.WorkflowPojo;
import com.nova.paas.workflow.support.MongoConfigFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
    public String startWorkflowByWorkflowId(String workflowId, Map<String, Object> conditionMap, Map<WorkflowBindingEnum, Object> bindingMap) {
        WorkflowEngine engine = MongoConfigFactory.getInstance().getEngine();
        TriggerInstance trigger = new TriggerInstance().workflowId(new WorkflowId(workflowId));
        if (bindingMap != null && bindingMap.size() > 0) {
            Map<String, Object> safeBindingMap = new HashMap<>();
            for (Map.Entry entry : bindingMap.entrySet()) {
                safeBindingMap.put(entry.getKey().toString(), entry.getValue());
            }
            trigger.setTransientData(safeBindingMap);
        }
        WorkflowInstance instance = engine.start(trigger.data(conditionMap));
        return instance.getId().getInternal();
    }
}
