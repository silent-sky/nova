package com.effektif.workflow.api.condition;

import com.effektif.workflow.api.bpmn.BpmnElement;
import com.effektif.workflow.api.json.TypeName;

/**
 * zhenghaibo
 * 2018/4/8 12:02
 */
@TypeName("in")
@BpmnElement("in")
public class In extends Comparator {
    private static final long serialVersionUID = -4357987819924924575L;

    @Override
    protected String getName() {
        return "in";
    }
}
