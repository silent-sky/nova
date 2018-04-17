package com.effektif.workflow.api.condition;

import com.effektif.workflow.api.bpmn.BpmnElement;
import com.effektif.workflow.api.json.TypeName;

/**
 * zhenghaibo
 * 2018/4/8 11:34
 */
@TypeName("hasNoneOf")
@BpmnElement("hasNoneOf")
public class HasNoneOf extends Comparator {
    private static final long serialVersionUID = -2380397726352458423L;

    @Override protected String getName() {
        return "hasNoneOf";
    }
}
