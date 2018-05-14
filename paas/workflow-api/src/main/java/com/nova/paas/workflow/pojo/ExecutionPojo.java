package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2017/4/8 13:24
 */
@Data
public class ExecutionPojo implements Serializable {
    private static final long serialVersionUID = -8539066498976541093L;

    private String tenantId;
    private String appId;
    private String taskType;
    private int rowNo;

    private String sender;
    /**
     * map<type,Set<id>>
     * type:PERSON,DEPT,ROLE,DEPT_LEADER
     */
    private LinkedHashMap<String, Set<String>> recipients;
    private Set<String> emailAddress;
    private String title;
    private String content;
    private String template;

    /**
     * map<entityId,<field,value>>
     * value:constant
     * $$__value:variable
     * %%__value:expression
     */
    private LinkedHashMap<String, LinkedHashMap<String, String>> fieldMapping;

}
