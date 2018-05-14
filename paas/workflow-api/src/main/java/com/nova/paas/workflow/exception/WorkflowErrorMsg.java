package com.nova.paas.workflow.exception;

/**
 * zhenghaibo
 * 2018/5/8 19:53
 */
public enum WorkflowErrorMsg {
    //通用信息
    PAAS_WF_DEFAULT_EXCEPTION(301100001, "流程服务异常"),

    //对象映射相关
    PAAS_WF_OBJECT_MAPPING_RULE_IS_NULL_EXCEPTION(301136001, "对象映射规则为空"),
    PAAS_WF_TENANT_ID_IS_NULL_EXCEPTION(301136002, "企业id不能为空"),

    //配置相关
    PAAS_WF_CONFIG_PARAMETER_INVALID_EXCEPTION(201136001, "参数无效"),;

    private int code;
    private String message;

    WorkflowErrorMsg(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static WorkflowErrorMsg valueOf(int code) {
        for (WorkflowErrorMsg errorCode : WorkflowErrorMsg.values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Invalid code value: " + code);
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getStrCode() {
        return Integer.toString(code);
    }
}
