package com.effektif.workflow.impl.conditions;

import com.effektif.workflow.api.condition.Condition;
import com.effektif.workflow.api.condition.In;
import com.effektif.workflow.impl.data.TypedValueImpl;
import com.effektif.workflow.impl.workflowinstance.ScopeInstanceImpl;

import java.util.List;

/**
 * zhenghaibo
 * 2018/4/8 13:24
 */
public class InImpl extends ComparatorImpl {

    @Override
    public Class<? extends Condition> getApiType() {
        return In.class;
    }

    @Override
    public boolean compare(TypedValueImpl leftValue, TypedValueImpl rightValue, ScopeInstanceImpl scopeInstance) {
        if (isNull(leftValue) && isNull(rightValue))
            return true;
        if (isNotNull(leftValue) && isNull(rightValue))
            return false;
        if (isNull(leftValue) && isNotNull(rightValue))
            return false;
        String left = (String) leftValue.value;
        List right = (List) rightValue.value;
        return right.contains(left);
    }

    @Override
    public String getComparatorSymbol() {
        return "<in>";
    }
}
