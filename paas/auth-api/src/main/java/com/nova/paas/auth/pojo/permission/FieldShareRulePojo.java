package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FieldShareRulePojo implements Serializable {
    private static final long serialVersionUID = 3036944074368622334L;
    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private String ruleCode;
    private Integer ruleOrder;
    private String fieldName;
    private String fieldType;  //枚举
    private String operate;   //枚举
    private List<String> fieldValue;
}
