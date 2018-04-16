package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EntityFieldSharePojo implements Serializable {
    private static final long serialVersionUID = 3125765669904090628L;
    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private String ruleName;
    private String ruleCode;
    private String ruleParse;
    private List<FieldShareRulePojo> rules;
    private List<EntityFieldShareReceivePojo> receives;
    private Integer status;
    private String creator;
    private long createTime;
    private String modifier;
    private long modifyTime;
}
