package com.effektif.workflow.api.condition;

import com.effektif.workflow.api.bpmn.BpmnElement;
import com.effektif.workflow.api.json.TypeName;

/**
 * zhenghaibo
 * 2018/4/8 11:24
 */
@TypeName("hasAllOf")
@BpmnElement("hasAllOf")
public class HasAllOf extends Comparator {
    private static final long serialVersionUID = -4859894856186422221L;

    @Override protected String getName() {
        return "hasAllOf";
    }
}
