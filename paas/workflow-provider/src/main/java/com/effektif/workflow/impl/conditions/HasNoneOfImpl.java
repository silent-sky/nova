
package com.effektif.workflow.impl.conditions;

import com.effektif.workflow.api.condition.Condition;
import com.effektif.workflow.api.condition.HasNoneOf;
import com.effektif.workflow.impl.data.TypedValueImpl;
import com.effektif.workflow.impl.workflowinstance.ScopeInstanceImpl;

import java.util.Collections;
import java.util.List;

/**
 * zhenghaibo
 * 2018/4/8 11:59
 */
public class HasNoneOfImpl extends ComparatorImpl {
    @Override
    public Class<? extends Condition> getApiType() {
        return HasNoneOf.class;
    }

    @Override
    public boolean compare(TypedValueImpl leftValue, TypedValueImpl rightValue, ScopeInstanceImpl scopeInstance) {
        if (isNull(leftValue) && isNull(rightValue))
            return false;
        if (isNotNull(leftValue) && isNull(rightValue))
            return true;
        if (isNull(leftValue) && isNotNull(rightValue))
            return true;
        List left = (List) leftValue.value;
        List right = (List) rightValue.value;

        return Collections.disjoint(left, right);
    }

    @Override
    public String getComparatorSymbol() {
        return "<hasNoneOf>";
    }
}
