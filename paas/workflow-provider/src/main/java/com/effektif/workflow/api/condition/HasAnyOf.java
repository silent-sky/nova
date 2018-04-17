package com.effektif.workflow.api.condition;

import com.effektif.workflow.api.bpmn.BpmnElement;
import com.effektif.workflow.api.json.TypeName;

/**
 * zhenghaibo
 * 2018/4/8 11:22
 */
@TypeName("hasAnyOf")
@BpmnElement("hasAnyOf")
public class HasAnyOf extends Comparator {
    private static final long serialVersionUID = -5737672885372582153L;

    @Override protected String getName() {
        return "hasAnyOf";
    }
}
