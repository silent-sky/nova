package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * zhenghaibo
 * 2018/5/10 10:28
 */
@Data
public class RulePojo implements Serializable {
    private static final long serialVersionUID = -6859009559698607346L;

    private String id;
    private String tenantId;
    private String appId;
    private String entityId;
    private String ruleType;
    private List<String> triggerTypes;
    //e.g. (1 and 2) or (3 and 4) or 5.the number symbol is the criteria rowNo.if this is blank,default logic is "and".
    private String conditionPattern;
    private List<CriteriaPojo> conditions;
    private String sourceWorkflowId;


    public RulePojo condition(CriteriaPojo criteriaPojo) {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        conditions.add(criteriaPojo);
        return this;
    }
}
