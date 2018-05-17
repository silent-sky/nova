package com.nova.paas.workflow.entity;

import com.effektif.workflow.mongo.WorkflowFields;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * zhenghaibo
 * 2018/5/16 10:53
 */
@Data
@Document(collection = "rules")
public class RuleEntity implements Serializable {
    private static final long serialVersionUID = -7186801739479441062L;

    @Id
    private ObjectId id;

    /**
     * 标记该WorkflowRule是否有效,true表示删除，false表示该有效
     */
    private Boolean deleted;
    private String tenantId;
    private String appId;
    private String entityId;
    /**
     * 关联的流程类型(工作流，审批流)
     */
    private String ruleType;
    /**
     * 1:新增、2:编辑、3:作废,4:删除,-1:周期性任务
     */
    private List<String> triggerTypes;

    /**
     * 规则描述信息
     */
     private String description;

    /**
     * 1:定时执行的工作流
     * 0或者空，普通的工作流
     */
    private Integer scheduleType;

    /**
     * e.g. (1 and 2) or (3 and 4) or 5.
     * the number symbol is the criteria rowNo.
     * if this is blank,default logic is "and".
     * conditions中各个条件的连接关系,根据rowNo( ( ( 1 and 2 ) or 3 ) or 4 )
     */
    private String conditionPattern;
    /**
     * 规则表达式数组
     */
    private List<CriteriaEntity> conditions;

    /**
     * 0或空表示满足条件触发，1表示总是触发
     */
    private Integer conditionsDisable;

    /**
     * 规则对应的工作流srcId
     */
    private String sourceWorkflowId;

    private Long createdAt;
    private String createdBy;
    private Long modifiedAt;
    private String modifiedBy;

    public RuleEntity addCriteriaEntity(CriteriaEntity criteriaEntity) {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        conditions.add(criteriaEntity);
        return this;
    }
}
