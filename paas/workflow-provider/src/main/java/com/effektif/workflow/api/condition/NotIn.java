package com.effektif.workflow.api.condition;

import com.effektif.workflow.api.bpmn.BpmnElement;
import com.effektif.workflow.api.json.TypeName;

/**
 * zhenghaibo
 * 2018/4/8 12:05
 */
@TypeName("notIn")
@BpmnElement("notIn")
public class NotIn extends In {
    private static final long serialVersionUID = -6894252463063478828L;

    @Override
    protected String getName() {
        return "notIn";
    }
}
