package com.nova.paas.metadata.exception;

/**
 * zhenghaibo
 * 2018/4/8 19:53
 */
public enum MetadataErrorCode {

    //对象映射相关
    PAAS_MD_OBJECT_MAPPING_RULE_IS_NULL_EXCEPTION(301136001, "对象映射规则为空"),
    PAAS_MD_TENANT_ID_IS_NULL_EXCEPTION(301136002, "企业id不能为空"),

    //配置相关
    PAAS_MD_CONFIG_PARAMETER_INVALID_EXCEPTION(201136001, "参数无效"),;

    private int code;
    private String message;

    MetadataErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static MetadataErrorCode valueOf(int code) {
        for (MetadataErrorCode errorCode : MetadataErrorCode.values()) {
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
