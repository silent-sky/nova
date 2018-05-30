package com.nova.paas.workflow.util;

import com.google.common.collect.Lists;
import com.nova.paas.workflow.entity.CriteriaEntity;
import com.nova.paas.workflow.entity.RuleEntity;
import com.nova.paas.workflow.pojo.CriteriaPojo;
import com.nova.paas.workflow.pojo.RulePojo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * zhenghaibo
 * 2018/5/16 18:46
 */
public class EntityConverter {
    /**
     * **************   RuleEntity <--> RulePojo   ***************
     */
    public static RulePojo convertRule(RuleEntity entity) {
        if (entity == null) {
            return null;
        }
        RulePojo pojo = new RulePojo();
        if (entity.getId() != null) {
            pojo.setId(entity.getId().toString());
        }
        pojo.setAppId(entity.getAppId());
        pojo.setEntityId(entity.getEntityId());
        pojo.setRuleType(entity.getRuleType());
        pojo.setTriggerTypes(entity.getTriggerTypes());
        pojo.setConditionPattern(entity.getConditionPattern());
        pojo.setConditions(convertRuleCriteriaEntity(entity.getConditions()));
        pojo.setSourceWorkflowId(entity.getSourceWorkflowId());
        return pojo;
    }

    public static RuleEntity convertRule(RulePojo pojo) {
        if (pojo == null) {
            return null;
        }
        RuleEntity entity = new RuleEntity();

        if (StringUtils.isNotBlank(pojo.getId())) {
            entity.setId(new ObjectId(pojo.getId()));
        }
        entity.setAppId(pojo.getAppId());
        entity.setEntityId(pojo.getEntityId());
        entity.setRuleType(pojo.getRuleType());
        entity.setTriggerTypes(pojo.getTriggerTypes());
        entity.setConditionPattern(pojo.getConditionPattern());
        entity.setConditions(convertRuleCriteriaPojo(pojo.getConditions()));
        entity.setSourceWorkflowId(pojo.getSourceWorkflowId());
        return entity;
    }

    public static List<RulePojo> convertRuleEntity(List<RuleEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Lists.newArrayList();
        }
        List<RulePojo> pojoList = new ArrayList();
        for (RuleEntity entity : entityList) {
            RulePojo pojo = convertRule(entity);
            pojoList.add(pojo);
        }
        return pojoList;
    }

    public static List<RuleEntity> convertRulePojo(List<RulePojo> pojoList) {
        if (CollectionUtils.isEmpty(pojoList)) {
            return Lists.newArrayList();
        }
        List<RuleEntity> entityList = new ArrayList();
        for (RulePojo pojo : pojoList) {
            RuleEntity entity = convertRule(pojo);
            entityList.add(entity);
        }
        return entityList;
    }

    public static CriteriaPojo convertRuleCriteria(CriteriaEntity entity) {
        if (entity == null) {
            return null;
        }
        CriteriaPojo pojo = new CriteriaPojo();
        pojo.setRowNo(entity.getRowNo());
        pojo.setFieldName(entity.getFieldName());
        pojo.setFieldType(entity.getFieldType());
        pojo.setOperator(entity.getOperator());
        pojo.setValue(entity.getValue());
        return pojo;
    }

    public static CriteriaEntity convertRuleCriteria(CriteriaPojo pojo) {
        if (pojo == null) {
            return null;
        }
        CriteriaEntity entity = new CriteriaEntity();
        entity.setRowNo(pojo.getRowNo());
        entity.setFieldName(pojo.getFieldName());
        entity.setFieldType(pojo.getFieldType());
        entity.setOperator(pojo.getOperator());
        entity.setValue(pojo.getValue());
        return entity;
    }

    public static List<CriteriaPojo> convertRuleCriteriaEntity(List<CriteriaEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Lists.newArrayList();
        }
        List<CriteriaPojo> pojoList = new ArrayList();
        for (CriteriaEntity entity : entityList) {
            CriteriaPojo pojo = convertRuleCriteria(entity);
            pojoList.add(pojo);
        }
        return pojoList;
    }

    public static List<CriteriaEntity> convertRuleCriteriaPojo(List<CriteriaPojo> pojoList) {
        if (CollectionUtils.isEmpty(pojoList)) {
            return Lists.newArrayList();
        }
        List<CriteriaEntity> entityList = new ArrayList();
        for (CriteriaPojo pojo : pojoList) {
            CriteriaEntity entity = convertRuleCriteria(pojo);
            entityList.add(entity);
        }
        return entityList;
    }

}
