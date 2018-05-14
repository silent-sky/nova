package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/10 10:30
 */
@Data
public class CriteriaPojo implements Serializable {
    private static final long serialVersionUID = 5059364178702511100L;

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
     * @see WorkflowRuleConstant.Operator
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

    public String getFieldSrc() {
        if (leftSide != null) {
            return leftSide.getFieldSrc();
        }
        return "";
    }

    public void setFieldSrc(String fieldName) {
        if (leftSide == null) {
            leftSide = new LeftSide();
        }
        leftSide.setFieldSrc(fieldName);
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
         * @see WorkflowRuleConstant.FieldType
         */
        private String fieldType;
        //系统字段 ,对象字段
        private String fieldSrc;

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

        public String getFieldSrc() {
            return fieldSrc;
        }

        public void setFieldSrc(String fieldSrc) {
            this.fieldSrc = fieldSrc;
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
