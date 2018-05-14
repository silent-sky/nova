package com.nova.paas.workflow.constant;

public interface WorkflowRuleConstant {
    interface Operator {
        String COMMON_EMPTY = "empty";
        String COMMON_NOT_EMPTY = "notEmpty";

        String STRING_EQUALS = "equals";
        String STRING_NOT_EQUALS = "notEquals";
        String STRING_STARTS_WITH = "startsWith";
        String STRING_NOT_STARTS_WITH = "notStartsWith";
        String STRING_ENDS_WITH = "endsWith";
        String STRING_NOT_ENDS_WITH = "notEndsWith";
        String STRING_CONTAINS = "contains";
        String STRING_NOT_CONTAINS = "notContains";
        String STRING_IN = "in";
        String STRING_NOT_IN = "notIn";
        String STRING_HAS_ANY_OF = "hasAnyOf";
        String STRING_HAS_NONE_OF = "hasNoneOf";

        String NUMBER_LESS_THAN = "<";
        String NUMBER_GREATER_THAN = ">";
        String NUMBER_LESS_THAN_OR_EQUAL = "<=";
        String NUMBER_GREATER_THAN_OR_EQUAL = ">=";
        String NUMBER_EQUALS = "==";
        String NUMBER_NOT_EQUALS = "!=";

        String BOOLEAN_TRUE = "isTrue";
        String BOOLEAN_FALSE = "isFalse";
    }


    interface FieldType {
        String STRING = "string";
        String NUMBER = "number";
        String BOOLEAN = "boolean";
    }


    interface VARIABLE {
        //inner variable
        String TENANT_ID = "@TENANT_ID";
        String APP_ID = "@APP_ID";
        String USER_ID = "@USER_ID";
        //outer variable
        String OWNER_MAIN_DEPT = "@OWNER_MAIN_DEPT";
    }


    interface TriggerType {
        String CREATE = "1"; //创建
        String UPDATE = "2";  //更新
        String INVALID = "3";//作废
        String DELETE = "4"; // 删除

        String SCHEDULE = "99"; // 周期性执行
    }

}
