package com.effektif.workflow.impl.conditions;

import com.effektif.workflow.api.condition.Condition;
import com.effektif.workflow.api.condition.NotIn;
import com.effektif.workflow.impl.data.TypedValueImpl;
import com.effektif.workflow.impl.workflowinstance.ScopeInstanceImpl;

/**
 * zhenghaibo
 * 2018/4/8 13:30
 */
public class NotInImpl extends InImpl {

    @Override
    public Class<? extends Condition> getApiType() {
        return NotIn.class;
    }

    @Override
    public boolean compare(TypedValueImpl leftValue, TypedValueImpl rightValue, ScopeInstanceImpl scopeInstance) {
        return !super.compare(leftValue, rightValue, scopeInstance);
    }
}
