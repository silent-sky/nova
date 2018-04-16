package com.nova.paas.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.nova.paas.auth.RoleService;
import com.nova.paas.auth.ViewAccessService;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.ViewAccessMapper;
import com.nova.paas.auth.pojo.RoleViewPojo;
import com.nova.paas.auth.entity.ViewAccess;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.support.CacheManager;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service("viewAccessService")
@Slf4j
public class ViewAccessServiceImpl implements ViewAccessService {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ViewAccessMapper viewAccessMapper;
    @Autowired
    private RoleService roleService;
    @Value("${VIEW_PERMISS_EXPIRE_SECOND}")
    private int VIEW_PERMISS_EXPIRE_SECOND;

    @Override
    @Transactional
    public void addRoleViewAccess(CommonContext context, List<RoleViewPojo> roleViewPojos) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleViewPojos:{}",
                "addRoleViewAccess",
                JSON.toJSONString(context),
                JSON.toJSONString(roleViewPojos));
        if (CollectionUtils.isEmpty(roleViewPojos)) {
            return;
        }

        this.pojoVerify(context, roleViewPojos);

        try {
            viewAccessMapper.delRoleViewAccess(context.getTenantId(),
                    context.getAppId(),
                    roleViewPojos,
                    context.getUserId(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.addRoleViewAccess() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        List<ViewAccess> list = new ArrayList<>();
        Map<String, String> cacheUpdate = new HashMap<>();

        roleViewPojos.forEach(pojo -> {
            ViewAccess roleView = new ViewAccess();
            roleView.setId(IdUtil.generateId());
            roleView.setTenantId(context.getTenantId());
            roleView.setAppId(context.getAppId());
            roleView.setEntityId(pojo.getEntityId());
            roleView.setRecordTypeId(pojo.getRecordTypeId());
            roleView.setRoleCode(pojo.getRoleCode());
            roleView.setViewId(pojo.getViewId());

            roleView.setModifier(context.getUserId());
            roleView.setModifyTime(System.currentTimeMillis());
            roleView.setDelFlag(Boolean.FALSE);
            list.add(roleView);

            cacheUpdate.put(pojo.getRoleCode() + pojo.getRecordTypeId(), pojo.getViewId());
        });

        try {
            //            viewAccessMapper.batchInsert(list);

            cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + roleViewPojos.get(0)
                    .getEntityId(), (Map) cacheUpdate);
            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + roleViewPojos.get(0).getEntityId(), VIEW_PERMISS_EXPIRE_SECOND);

        } catch (Exception e) {
            log.error("===auth.addRoleViewAccess() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    @Override
    public List<RoleViewPojo> queryRoleViewAccess(CommonContext context, String entityId, String recordTypeId, String roleCode)
            throws AuthServiceException {

        this.entityVerify(entityId);

        List<ViewAccess> entityList;

        if (StringUtils.isNotBlank(roleCode) && StringUtils.isNotBlank(recordTypeId)) {//查询一条记录
            String key = roleCode + recordTypeId;
            String viewId;
            try {
                viewId = (String) cacheManager.getHashObject(
                        context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + entityId,
                        key);
            } catch (Exception e) {
                entityList = this.queryRoleViewPermissFromDB(context, Collections.singletonList(roleCode), entityId, recordTypeId);
                return this.convertToPojo(entityList);
            }

            if (viewId == null) {//未命中缓存
                entityList = this.queryRoleViewPermissFromDB(context, Collections.singletonList(roleCode), entityId, recordTypeId);
                if (CollectionUtils.isEmpty(entityList)) {
                    viewId = "";
                } else {
                    viewId = entityList.get(0).getViewId();
                }

                try {//更新缓存
                    cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + entityId,
                            key,
                            viewId);
                    //                    cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + entityId, VIEW_PERMISS_EXPIRE_SECOND);

                } catch (Exception e) {
                    return this.convertToPojo(entityList);
                }
            }

            if (StringUtils.isBlank(viewId)) {
                return new ArrayList<>();
            }
            RoleViewPojo pojo = new RoleViewPojo();
            pojo.setEntityId(entityId);
            pojo.setRecordTypeId(recordTypeId);
            pojo.setRoleCode(roleCode);
            pojo.setViewId(viewId);
            pojo.setTenantId(context.getTenantId());
            pojo.setAppId(context.getAppId());

            return Collections.singletonList(pojo);
        }

        if (StringUtils.isBlank(roleCode)) {//查询多条记录--用于管理界面
            entityList = this.queryRoleViewPermissFromDB(context, null, entityId, recordTypeId);
        } else {
            entityList = this.queryRoleViewPermissFromDB(context, Collections.singletonList(roleCode), entityId, recordTypeId);
        }
        return this.convertToPojo(entityList);
    }

    /**
     * 删除角色的对象视图权限
     */
    @Override
    @Transactional
    public void delRoleViewPermiss(CommonContext context, String roleCode, String entityId) {
        log.info("[Request], method:{},context:{},roleCode:{},entityId:{}",
                "delRoleViewPermiss",
                JSON.toJSONString(context),
                JSON.toJSONString(entityId),
                JSON.toJSONString(entityId));
        this.delRoleViewPermissCache(context, roleCode, entityId);
        try {
            viewAccessMapper.batchDel(context.getTenantId(), context.getAppId(), roleCode, entityId, context.getUserId(), System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.delRoleViewPermiss() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    @Override
    @Transactional
    public void delRoleViewAccess(CommonContext context, String roleCode) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{}", "delRoleViewAccess", JSON.toJSONString(context), roleCode);
        this.roleCodeVerify(roleCode);

        try {
            Set<String> entityIdSet = viewAccessMapper.queryEntityList(context.getTenantId(), context.getAppId(), roleCode);

            entityIdSet.forEach(entity -> {
                Map<String, String> map = (Map<String, String>) cacheManager.getHashEntries(
                        context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + entity);

                if (map != null && map.size() > 0) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (entry.getKey().contains(roleCode)) {
                            //                            cacheManager.delObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + entity, entry.getKey());
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error("===auth.delRoleViewAccess() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        this.delViewAccess(context, null, roleCode, null, null);
    }

    @Override
    public List<RoleViewPojo> queryViewAccess(CommonContext context, Set<String> entityIds, String recordTypeId, String roleCode)
            throws AuthServiceException {
        if (entityIds != null) {
            entityIds.remove(null);
        }
        if (CollectionUtils.isEmpty(entityIds)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        this.roleCodeVerify(roleCode);
        this.recordTypeIdVerify(recordTypeId);

        return this.convertToPojo(this.queryViewAccessByEntityIdBatch(context, entityIds, Collections.singleton(roleCode), recordTypeId));
    }

    private void pojoVerify(CommonContext context, List<RoleViewPojo> pojos) throws AuthServiceException{
        if (CollectionUtils.isEmpty(pojos)) {
            return;
        }

        Set<String> unionSet = new HashSet<>();
        Set<String> roleCodes = new HashSet<>();
        String entityId = pojos.get(0).getEntityId();
        pojos.forEach(pojo -> {
            if (pojo == null) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }

            this.entityVerify(pojo.getEntityId());
            if (!entityId.equals(pojo.getEntityId())) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }

            this.roleCodeVerify(pojo.getRoleCode());
            roleCodes.add(pojo.getRoleCode());
            this.recordTypeIdVerify(pojo.getRecordTypeId());
            this.viewIdVerify(pojo.getViewId());

            String union = pojo.getRoleCode() + pojo.getEntityId() + pojo.getRecordTypeId();
            if (unionSet.contains(union)) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            unionSet.add(union);
        });

        this.rolesIsExist(context, roleCodes);
    }

    /**
     * 对象实体校验
     */
    private void entityVerify(String entityId) {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void viewIdVerify(String viewId) {
        if (StringUtils.isBlank(viewId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void recordTypeIdVerify(String recordTypeId) {
        if (StringUtils.isBlank(recordTypeId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void roleCodeVerify(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void rolesIsExist(CommonContext context, Set<String> roles) throws AuthServiceException {
        if (CollectionUtils.isNotEmpty(roles)) {
            if (roleService.roleCodeOrRoleNameExists(context, roles, null) != roles.size()) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
    }

    private List<ViewAccess> queryRoleViewPermissFromDB(CommonContext context, List<String> roles, String entityId, String recordTypeId) {
        List<ViewAccess> res;

        try {
            res = viewAccessMapper.queryViewAccessProvider(context.getTenantId(),
                    context.getAppId(),
                    roles,
                    entityId,
                    recordTypeId,
                    null,
                    Boolean.FALSE);
        } catch (Exception e) {
            log.error("===auth.queryRoleViewPermissFromDB() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return res;
    }

    /**
     * 删除角色的对象视图权限
     */
    private void delRoleViewPermissCache(CommonContext context, String roleCode, String entityId) {
        Set<String> entityIdSet = new HashSet<>();
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        try {
            if (entityId == null) {
                entityIdSet = viewAccessMapper.queryEntityList(context.getTenantId(), context.getAppId(), roleCode);
            } else {
                entityIdSet.add(entityId);
            }

            if (CollectionUtils.isNotEmpty(entityIdSet)) {
                for (String entity : entityIdSet) {
                    //                    cacheManager.delObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + entity, roleCode);
                }

            }
        } catch (Exception e) {
            log.error("===auth.delRoleViewPermissCache() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private void delViewAccess(CommonContext context, String entityId, String roleCode, String recordTypeId, String viewId) {
        try {
            viewAccessMapper.deleteViewAccess(context.getTenantId(),
                    context.getAppId(),
                    entityId,
                    roleCode,
                    recordTypeId,
                    viewId,
                    context.getUserId(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.delViewAccess() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private List<RoleViewPojo> convertToPojo(List<ViewAccess> viewAccessList) {
        List<RoleViewPojo> res = new ArrayList<>();
        if (CollectionUtils.isEmpty(viewAccessList)) {
            return res;
        }

        viewAccessList.forEach(entity -> {
            RoleViewPojo pojo = new RoleViewPojo();
            try {
                PropertyUtils.copyProperties(pojo, entity);
            } catch (Exception e) {
                log.error("copyProperties error.para:{}", JSON.toJSONString(entity), e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            res.add(pojo);
        });

        return res;
    }

    private List<ViewAccess> queryViewAccessByEntityIdBatch(
            CommonContext context, Set<String> entityIds, Set<String> roleCodes, String recordTypeId) {
        List<ViewAccess> viewAccesses;
        try {
            viewAccesses =
                    viewAccessMapper.queryViewAccessByEntityIdBatch(context.getTenantId(), context.getAppId(), entityIds, roleCodes, recordTypeId);
        } catch (Exception e) {
            log.error("===auth.queryViewAccessByEntityIdBatch() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return viewAccesses;
    }
}
