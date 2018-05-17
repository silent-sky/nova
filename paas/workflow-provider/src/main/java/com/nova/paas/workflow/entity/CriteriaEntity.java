package com.nova.paas.workflow.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/16 11:26
 */
@Data
public class CriteriaEntity implements Serializable {
    private static final long serialVersionUID = 3693190060628171447L;

    /**
     * 行号
     */
    private Integer rowNo;
    /**
     * 每行规则operator的左边的数据
     */
    private LeftSide leftSide;
    /**
     * 操作符
     *
     * @see com.nova.paas.workflow.constant.WorkflowRuleConstant.Operator
     */
    private String operator;
    /**
     * 每行规则operator的右边的数据
     */
    private RightSide rightSide;

    public String getFieldName() {
        if (leftSide != null) {
            return leftSide.getFieldName();
        }
        return "";
    }

    public void setFieldName(String fieldName) {
        if (leftSide == null) {
            leftSide = new LeftSide();
        }
        leftSide.setFieldName(fieldName);
    }

    public String getFieldType() {
        if (leftSide != null) {
            return leftSide.getFieldType();
        }
        return "";
    }

    public void setFieldType(String fieldName) {
        if (leftSide == null) {
            leftSide = new LeftSide();
        }
        leftSide.setFieldType(fieldName);
    }

    public String getValue() {
        if (rightSide != null) {
            return rightSide.getValue();
        }
        return "";
    }

    public void setValue(String value) {
        if (rightSide == null) {
            rightSide = new RightSide();
        }
        rightSide.setValue(value);
    }

    //每行规则operator的左边的数据
    private static class LeftSide implements Serializable {
        private static final long serialVersionUID = -2652903735886555728L;
        //属性名称
        private String fieldName;
        /**
         * 属性类型
         *
         * @see com.nova.paas.workflow.constant.WorkflowRuleConstant.FieldType
         */
        private String fieldType;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

    }


    //每行规则operator的右边的数据
    private static class RightSide implements Serializable {
        private static final long serialVersionUID = 5198338967461926678L;
        //逻辑比较的值
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
