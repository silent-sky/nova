package com.effektif.workflow.impl.conditions;

import com.effektif.workflow.api.condition.Condition;
import com.effektif.workflow.api.condition.HasAllOf;
import com.effektif.workflow.impl.data.TypedValueImpl;
import com.effektif.workflow.impl.workflowinstance.ScopeInstanceImpl;

import java.util.List;

/**
 * zhenghaibo
 * 2018/4/8 11:58
 */
public class HasAllOfImpl extends ComparatorImpl {
    @Override
    public Class<? extends Condition> getApiType() {
        return HasAllOf.class;
    }

    @Override
    public boolean compare(TypedValueImpl leftValue, TypedValueImpl rightValue, ScopeInstanceImpl scopeInstance) {
        if (isNull(leftValue) && isNull(rightValue))
            return true;
        if (isNotNull(leftValue) && isNull(rightValue))
            return false;
        if (isNull(leftValue) && isNotNull(rightValue))
            return false;
        List left = (List) leftValue.value;
        List right = (List) rightValue.value;

        return right.containsAll(left);
    }

    @Override
    public String getComparatorSymbol() {
        return "<hasAllOf>";
    }
}
