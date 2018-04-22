package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.permission.EntityFieldShareReceive;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityFieldShareReceiveMapper;
import com.nova.paas.auth.pojo.permission.EntityFieldSharePojo;
import com.nova.paas.auth.pojo.permission.EntityFieldShareReceivePojo;
import com.nova.paas.auth.pojo.permission.FieldShareRulePojo;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.permission.EntityFieldShareService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityFieldShareServiceImpl implements EntityFieldShareService {

    //    @Autowired
    //    private RuleGroupService ruleGroupService;
    @Autowired
    private EntityFieldShareReceiveMapper entityFieldShareReceiveMapper;
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 创建条件共享
     *
     * @param context          请求上下文
     * @param entityFieldShare 条件共享
     */
    @Transactional
    @Override
    public String create(CommonContext context, EntityFieldSharePojo entityFieldShare) throws AuthServiceException {
        if (entityFieldShare == null) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(entityFieldShare.getReceives())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(entityFieldShare.getRules())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        this.entityIdCheck(entityFieldShare.getEntityId());

        String ruleCode = StringUtils.isBlank(entityFieldShare.getRuleCode()) ? IdUtil.generateId() : entityFieldShare.getRuleCode();
        entityFieldShare.setRuleCode(ruleCode);

        //        RuleEngineContext ruleEngineContext = ComponentContextConvert.dataRightsContext2RuleEngineContext(context);
        //        RuleGroupPojo ruleGroup = this.entityFieldSharePojoToRuleGroupPojo(entityFieldShare);
        //        ruleGroup.setSqlSelectFields(Lists.newArrayList("_id"));
        //
        //        if (ruleGroupService.ruleGroupRuleNameisExist(ruleEngineContext, null, ruleGroup.getRuleName())) {
        //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        //        }

        //        try {
        //            this.batchInsertReceives(context, entityFieldShare.getEntityId(), ruleCode, entityFieldShare.getStatus(), entityFieldShare.getReceives());
        //            ruleGroupService.create(ruleEngineContext, ruleGroup);
        //        } catch (RuleServiceException ruleServiceException) {
        //            log.warn("ruleGroupService.create error", ruleServiceException);
        //            throw new AuthServiceException(ruleServiceException.getCode(), ruleServiceException.getMessage());
        //        }
        return ruleCode;
    }

    /**
     * 更新条件共享
     */
    @Transactional
    @Override
    public String update(CommonContext context, EntityFieldSharePojo entityFieldShare) throws AuthServiceException {
        if (entityFieldShare == null) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(entityFieldShare.getReceives())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(entityFieldShare.getRules())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (StringUtils.isBlank(entityFieldShare.getRuleCode())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        this.entityIdCheck(entityFieldShare.getEntityId());

        //        RuleEngineContext ruleEngineContext = ComponentContextConvert.dataRightsContext2RuleEngineContext(context);
        //
        //        if (ruleGroupService.ruleGroupRuleNameisExist(ruleEngineContext, entityFieldShare.getRuleCode(), entityFieldShare.getRuleName())) {
        //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        //        }

        List<EntityFieldShareReceivePojo> receives = entityFieldShare.getReceives();
        entityFieldShareReceiveMapper.deleteReceives(context.getTenantId(),
                context.getAppId(),
                entityFieldShare.getEntityId(),
                Sets.newHashSet(entityFieldShare.getRuleCode()));
        this.batchInsertReceives(context, entityFieldShare.getEntityId(), entityFieldShare.getRuleCode(), entityFieldShare.getStatus(), receives);

        //        RuleGroupPojo ruleGroup = this.entityFieldSharePojoToRuleGroupPojo(entityFieldShare);
        //        ruleGroup.setSqlSelectFields(Lists.newArrayList("_id"));
        //
        //        try {
        //            ruleGroupService.update(ruleEngineContext, ruleGroup);
        //        } catch (RuleServiceException ruleServiceException) {
        //            log.warn("ruleGroupService.update error", ruleServiceException);
        //            throw new AuthServiceException(ruleServiceException.getCode(), ruleServiceException.getMessage());
        //        }
        return entityFieldShare.getRuleCode();
    }

    /**
     * 删除条件共享规则
     *
     * @param status 规则状态(满足状态才能删除)
     */
    @Transactional
    @Override
    public void delete(CommonContext context, String entityId, Set<String> ruleCodes, Integer status) throws AuthServiceException {

        //        RuleEngineContext ruleEngineContext = ComponentContextConvert.dataRightsContext2RuleEngineContext(context);
        SetUtil.removeBlankElement(ruleCodes);
        if (StringUtils.isBlank(entityId) && CollectionUtils.isEmpty(ruleCodes)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //        if (status != null) {
        //            RuleGroupPageContent pageContent = ruleGroupService.query(ruleEngineContext, entityId, null, ruleCodes, null, null);
        //            List<RuleGroupPojo> ruleGroupPojos = pageContent.getContent();
        //            if (CollectionUtils.isNotEmpty(ruleGroupPojos)) {
        //                ruleGroupPojos.forEach(ruleGroupPojo -> {
        //                    if (ruleGroupPojo.getStatus() != null && ruleGroupPojo.getStatus().intValue() != status) {
        //                        throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        //                    }
        //                });
        //            }
        //        }
        entityFieldShareReceiveMapper.deleteReceives(context.getTenantId(), context.getAppId(), entityId, ruleCodes);
        //        try {
        //            ruleGroupService.delete(ruleEngineContext, entityId, ruleCodes);
        //        } catch (RuleServiceException ruleServiceException) {
        //            log.warn("ruleGroupService.delete error", ruleServiceException);
        //            throw new AuthServiceException(ruleServiceException.getCode(), ruleServiceException.getMessage());
        //        }
    }

    /**
     * 共享规则查询
     */
    public List<EntityFieldSharePojo> query(
            CommonContext context,
            String entityId,
            String ruleName,
            Integer status,
            Set<String> ruleCodes,
            Map<Integer, Set<String>> receivesWithType,
            Set<String> receives,
            Integer permission,
            PageInfo pageInfo) {

        List<EntityFieldSharePojo> entityFieldShare = new ArrayList<>();

        Set<String> codes = Sets.newHashSet();
        List<String> rules = entityFieldShareReceiveMapper.queryEntityFieldShareReceiveRuleCodes(context.getTenantId(),
                context.getAppId(),
                entityId,
                ruleCodes,
                receivesWithType,
                receives,
                permission,
                status);
        if (CollectionUtils.isEmpty(rules)) {
            return entityFieldShare;
        }
        codes.addAll(rules);

        //        RuleEngineContext ruleEngineContext = ComponentContextConvert.dataRightsContext2RuleEngineContext(context);
        //
        //        RuleGroupPageContent pageContent =
        //                ruleGroupService.queryRuleGroupResultWithRules(ruleEngineContext, entityId, ruleName, codes, status, pageInfo);
        //
        //        codes.clear();
        //        for (RuleGroupPojo ruleGroupPojo : pageContent.getContent()) {
        //            codes.add(ruleGroupPojo.getRuleCode());
        //            EntityFieldSharePojo entityFieldSharePojo = this.ruleGroupPojoToEntityFieldSharePojo(ruleGroupPojo);
        //            entityFieldShare.add(entityFieldSharePojo);
        //        }

        List<EntityFieldShareReceive> entityFieldShareReceives = entityFieldShareReceiveMapper.queryEntityFieldShareReceives(context.getTenantId(),
                context.getAppId(),
                entityId,
                codes,
                null,
                null,
                null);
        Map<String, List<EntityFieldShareReceivePojo>> ruleReceiveMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(entityFieldShareReceives)) {
            entityFieldShareReceives.forEach(entityFieldShareReceive -> {
                ruleReceiveMap.computeIfAbsent(entityFieldShareReceive.getRuleCode(), k -> new LinkedList<>());
                ruleReceiveMap.get(entityFieldShareReceive.getRuleCode()).add(this.entityFieldShareReceiveToPojo(entityFieldShareReceive));
            });
        }

        entityFieldShare.forEach(entityFieldSharePojo -> {
            entityFieldSharePojo.setReceives(ruleReceiveMap.get(entityFieldSharePojo.getRuleCode()));
        });

        //        if (pageInfo != null) {
        //            pageInfo.setTotal(pageContent.getPageInfo().getTotal());
        //            pageInfo.setTotalPage(pageContent.getPageInfo().getTotalPage());
        //        }

        return entityFieldShare;
    }

    /**
     * 更新规则状态
     */
    @Transactional
    @Override
    public void updateEntityFieldShareStatus(CommonContext context, String entityId, Set<String> ruleCodes, Integer status)
            throws AuthServiceException {
        //        RuleEngineContext ruleEngineContext = ComponentContextConvert.dataRightsContext2RuleEngineContext(context);
        SetUtil.removeNull(ruleCodes);
        if (CollectionUtils.isEmpty(ruleCodes)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        entityFieldShareReceiveMapper.updateStatus(context.getTenantId(), context.getAppId(), entityId, ruleCodes, status);
        //        try {
        //            ruleGroupService.updateRuleGroupStatus(ruleEngineContext, entityId, ruleCodes, status);
        //        } catch (RuleServiceException ruleServiceException) {
        //            log.warn("ruleGroupService.updateEntityFieldShareStatus error", ruleServiceException);
        //            throw new AuthServiceException(ruleServiceException.getCode(), ruleServiceException.getMessage());
        //        }
    }

    /**
     * 查询共享给用户的数据条件sql列表
     */
    public List<String> fieldShareReceiveSql(CommonContext context, String entityId) {
        List<String> roles = null;
        //        List<String> roles = userRoleService.queryRoleCodeListByUserId(context);
        return entityFieldShareReceiveMapper.fieldShareReceiveSql(context.getTenantId(), context.getAppId(), entityId, roles, context.getUserId());
    }

    /**
     * 用户匹配的规则
     */
    public List<EntityFieldShareReceivePojo> userReceivedRule(CommonContext context, String entityId) {
        List<String> roles = null;
        //        List<String> roles = userRoleService.queryRoleCodeListByUserId(context);
        List<EntityFieldShareReceive> receives =
                entityFieldShareReceiveMapper.userReceivedRule(context.getTenantId(), context.getAppId(), entityId, roles, context.getUserId());
        return this.entityFieldShareReceivesToPojos(receives);
    }

    /**
     * 数据规则匹配
     */
    public Map<String, Map<String, Object>> dataRuleExpressionPattern(
            CommonContext context, String entityId, Set<String> ruleCodes, Set<String> dataIds) {
        //        return ruleGroupService.dataRuleExpressionPattern(context, entityId, ruleCodes, dataIds);
        return null;
    }

    /**
     * 查询对象字段设置的规则
     */
    public List<FieldShareRulePojo> entityFieldRule(CommonContext context, String entity, Map<String, List<String>> fields) {
        //        RuleEngineContext ruleEngineContext = ComponentContextConvert.dataRightsContext2RuleEngineContext(context);
        //        List<RulePojo> rulePojos = ruleGroupService.entityFieldRule(ruleEngineContext, entity, fields);
        //        return this.rulGroupRuleToFieldRule(rulePojos);
        return null;
    }

    //    private RuleGroupPojo entityFieldSharePojoToRuleGroupPojo(EntityFieldSharePojo entityFieldSharePojo) {
    //        RuleGroupPojo ruleGroupPojo = new RuleGroupPojo();
    //        ruleGroupPojo.setId(entityFieldSharePojo.getId());
    //        ruleGroupPojo.setTenantId(entityFieldSharePojo.getTenantId());
    //        ruleGroupPojo.setAppId(entityFieldSharePojo.getAppId());
    //        ruleGroupPojo.setEntityId(entityFieldSharePojo.getEntityId());
    //        ruleGroupPojo.setRuleCode(entityFieldSharePojo.getRuleCode());
    //        ruleGroupPojo.setRuleName(entityFieldSharePojo.getRuleName());
    //        ruleGroupPojo.setRuleParse(entityFieldSharePojo.getRuleParse());
    //        ruleGroupPojo.setRules(this.fieldRuleToRuleGroupRule(entityFieldSharePojo.getRules()));
    //        ruleGroupPojo.setStatus(entityFieldSharePojo.getStatus());
    //        return ruleGroupPojo;
    //    }

    //    private EntityFieldSharePojo ruleGroupPojoToEntityFieldSharePojo(RuleGroupPojo ruleGroupPojo) {
    //        EntityFieldSharePojo entityFieldSharePojo = new EntityFieldSharePojo();
    //
    //        entityFieldSharePojo.setId(ruleGroupPojo.getId());
    //        entityFieldSharePojo.setTenantId(ruleGroupPojo.getTenantId());
    //        entityFieldSharePojo.setAppId(ruleGroupPojo.getAppId());
    //        entityFieldSharePojo.setEntityId(ruleGroupPojo.getEntityId());
    //        entityFieldSharePojo.setRuleName(ruleGroupPojo.getRuleName());
    //        entityFieldSharePojo.setRuleCode(ruleGroupPojo.getRuleCode());
    //        entityFieldSharePojo.setRuleParse(ruleGroupPojo.getRuleParse());
    //        entityFieldSharePojo.setRules(this.rulGroupRuleToFieldRule(ruleGroupPojo.getRules()));
    //        entityFieldSharePojo.setStatus(ruleGroupPojo.getStatus());
    //        entityFieldSharePojo.setCreator(ruleGroupPojo.getCreatedBy());
    //        entityFieldSharePojo.setCreateTime(ruleGroupPojo.getCreateTime());
    //        entityFieldSharePojo.setModifier(ruleGroupPojo.getLastModifiedBy());
    //        entityFieldSharePojo.setModifyTime(ruleGroupPojo.getLastModifiedTime());
    //
    //        return entityFieldSharePojo;
    //    }

    //    private List<FieldShareRulePojo> rulGroupRuleToFieldRule(List<RulePojo> ruleGroupRules) {
    //        List<FieldShareRulePojo> fieldShareRules = Lists.newLinkedList();
    //        if (CollectionUtils.isNotEmpty(ruleGroupRules)) {
    //            ruleGroupRules.forEach(ruleGroupRule -> {
    //                FieldShareRulePojo fieldShareRulePojo = new FieldShareRulePojo();
    //                fieldShareRulePojo.setId(ruleGroupRule.getId());
    //                fieldShareRulePojo.setTenantId(ruleGroupRule.getTenantId());
    //                fieldShareRulePojo.setAppId(ruleGroupRule.getAppId());
    //                fieldShareRulePojo.setEntityId(ruleGroupRule.getEntityId());
    //                fieldShareRulePojo.setRuleCode(ruleGroupRule.getRuleCode());
    //                fieldShareRulePojo.setRuleOrder(ruleGroupRule.getRuleOrder());
    //                fieldShareRulePojo.setFieldName(ruleGroupRule.getFieldName());
    //                fieldShareRulePojo.setFieldType(ruleGroupRule.getFieldType());
    //                fieldShareRulePojo.setFieldValue(ruleGroupRule.getFieldValue());
    //                fieldShareRulePojo.setOperate(ruleGroupRule.getOperate());
    //                fieldShareRules.add(fieldShareRulePojo);
    //            });
    //        }
    //        return fieldShareRules;
    //    }

    //    private List<RulePojo> fieldRuleToRuleGroupRule(List<FieldShareRulePojo> fieldShareRules) {
    //        List<RulePojo> ruleGroupRules = Lists.newLinkedList();
    //        if (CollectionUtils.isNotEmpty(fieldShareRules)) {
    //            fieldShareRules.forEach(fieldShareRule -> {
    //                RulePojo rulePojo = new RulePojo();
    //                rulePojo.setId(fieldShareRule.getId());
    //                rulePojo.setTenantId(fieldShareRule.getTenantId());
    //                rulePojo.setAppId(fieldShareRule.getAppId());
    //                rulePojo.setEntityId(fieldShareRule.getEntityId());
    //                rulePojo.setRuleCode(fieldShareRule.getRuleCode());
    //                rulePojo.setRuleOrder(fieldShareRule.getRuleOrder());
    //                rulePojo.setFieldName(fieldShareRule.getFieldName());
    //                rulePojo.setFieldType(fieldShareRule.getFieldType());
    //                rulePojo.setFieldValue(fieldShareRule.getFieldValue());
    //                rulePojo.setOperate(fieldShareRule.getOperate());
    //                ruleGroupRules.add(rulePojo);
    //            });
    //        }
    //        return ruleGroupRules;
    //    }

    private void batchInsertReceives(
            CommonContext context, String entityId, String ruleCode, Integer status, List<EntityFieldShareReceivePojo> receivePojos) {
        List<EntityFieldShareReceive> receives = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(receivePojos)) {
            receivePojos.forEach(receivePojo -> {
                this.entityFieldShareReceivePojoCheck(receivePojo);
                EntityFieldShareReceive receive = EntityFieldShareReceive.builder()
                        .tenantId(context.getTenantId())
                        .entityId(entityId)
                        .ruleCode(ruleCode)
                        .receiveType(receivePojo.getReceiveType())
                        .receiveId(receivePojo.getReceiveId())
                        .permission(receivePojo.getPermission())
                        .status(status)
                        .build();
                //                receive.setId(IdUtil.generateId());
                receives.add(receive);
            });
            //            entityFieldShareReceiveMapper.batchInsert(receives);
        }
    }

    private void entityIdCheck(String entityId) {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void entityFieldShareReceivePojoCheck(EntityFieldShareReceivePojo receivePojo) {
        if (receivePojo == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (receivePojo.getPermission() == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (receivePojo.getReceiveType() == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (StringUtils.isBlank(receivePojo.getReceiveId())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (receivePojo.getReceiveId().length() > PermissionConstant.FieldLengthConstant.SHARE_RECEIVE_ID) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private EntityFieldShareReceivePojo entityFieldShareReceiveToPojo(EntityFieldShareReceive entityFieldShareReceive) {
        try {
            EntityFieldShareReceivePojo pojo = new EntityFieldShareReceivePojo();
            PropertyUtils.copyProperties(pojo, entityFieldShareReceive);
            return pojo;
        } catch (Exception e) {
            log.error("entityFieldShareReceiveToPojo error", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private List<EntityFieldShareReceivePojo> entityFieldShareReceivesToPojos(List<EntityFieldShareReceive> entityFieldShareReceives) {
        List<EntityFieldShareReceivePojo> pojos = Lists.newLinkedList();
        if (CollectionUtils.isNotEmpty(entityFieldShareReceives)) {
            entityFieldShareReceives.forEach(entityFieldShareReceive -> {
                pojos.add(this.entityFieldShareReceiveToPojo(entityFieldShareReceive));
            });
        }
        return pojos;
    }

}
