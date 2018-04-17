package com.effektif.workflow.test.api;

import com.effektif.workflow.api.activities.EndEvent;
import com.effektif.workflow.api.activities.ExecutionItem;
import com.effektif.workflow.api.activities.ExecutionTask;
import com.effektif.workflow.api.activities.StartEvent;
import com.effektif.workflow.api.ext.WorkflowConstants;
import com.effektif.workflow.api.workflow.ExecutableWorkflow;
import com.effektif.workflow.impl.json.DefaultJsonStreamMapper;
import com.effektif.workflow.test.WorkflowTest;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/8 09:55
 */
public class ExecutionTaskTest extends WorkflowTest {

    @Test
    public void testExecution() throws Exception {
        ExecutionTask task = new ExecutionTask();
        ExecutionItem item1 = new ExecutionItem();
        ExecutionItem item2 = new ExecutionItem();
        LinkedHashMap<String, Set<String>> recipients = new LinkedHashMap<>();
        recipients.put("PERSON", Sets.newHashSet("user1", "user2", "user3"));

        item1.setTaskType(WorkflowConstants.ExecuteType.SEND_QIXIN);
        item1.setTitle("qixin1_title");
        item1.setContent("qixin1_content");
        item1.setSender("user1");
        item1.setRecipients(recipients);

        item2.setTaskType(WorkflowConstants.ExecuteType.SEND_QIXIN);
        item2.setTitle("qixin2_title");
        item2.setContent("qixin2_content");
        item2.setSender("user2");
        item2.setRecipients(recipients);

        ExecutableWorkflow workflow = new ExecutableWorkflow()
            .sourceWorkflowId("src1").name("workflow_src1")
            .activity("start", new StartEvent())
            .activity("ex1", task.item(item1).item(item2))
            .activity("end", new EndEvent());

        deploy(workflow);
        String json = new DefaultJsonStreamMapper().write(workflow).toString();
        System.out.println("============================= json: " + json);
        ExecutableWorkflow workflow1 = new DefaultJsonStreamMapper().readString(json, ExecutableWorkflow.class);
        System.out.println("============================= workflow: " + workflow1.getSourceWorkflowId() + workflow1.getName());
        //        start(workflow);


    }
}
