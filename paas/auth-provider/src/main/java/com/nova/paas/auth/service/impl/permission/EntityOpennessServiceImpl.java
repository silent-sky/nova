package com.nova.paas.auth.service.impl.permission;

import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.EntityOpennessPojo;
import com.nova.paas.auth.service.entity.permission.EntityOpenness;
import com.nova.paas.auth.service.mapper.permission.EntityOpennessMapper;
import com.nova.paas.auth.service.permission.EntityOpennessService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.support.CacheManager;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("entityOpennessService")
@Slf4j
public class EntityOpennessServiceImpl implements EntityOpennessService {

    @Inject
    private EntityOpennessMapper entityOpennessMapper;
    @Inject
    private CacheManager cacheManager;

    @Value("${ENTITY_EXPIRE_SECOND}")
    private int ENTITY_EXPIRE_SECOND;

    private static final Set<Integer> entityOpennessPermissType;

    static {
        entityOpennessPermissType = new HashSet<>();
        entityOpennessPermissType.add(PermissionConstant.EntityOpennessPermiss.READ_AND_WRITE);
        entityOpennessPermissType.add(PermissionConstant.EntityOpennessPermiss.READ_ONLY);
    }

    private static final Set<Integer> entityOpennessScope;

    static {
        entityOpennessScope = new HashSet<>();
        entityOpennessScope.add(PermissionConstant.EntityOpennessScope.PRIVATE);
        entityOpennessScope.add(PermissionConstant.EntityOpennessScope.PUBLIC_DEPT);
        entityOpennessScope.add(PermissionConstant.EntityOpennessScope.PUBLIC_ALL);
        entityOpennessScope.add(PermissionConstant.EntityOpennessScope.OWNER_PRIVATE);
    }

    @Transactional
    @Override
    public void createEntityOpenness(CommonContext context, List<EntityOpennessPojo> entityOpennessPojoList) throws AuthServiceException {
        if (CollectionUtils.isEmpty(entityOpennessPojoList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Map<String, EntityOpennessPojo> entityOpennessPojoMap = new HashMap<>();
        entityOpennessPojoList.forEach(entityOpennessPojo -> {
            this.entityOpennessPojoCheck(entityOpennessPojo);
            entityOpennessPojoMap.put(entityOpennessPojo.getEntityId(), entityOpennessPojo); //去重
        });
        entityOpennessMapper.delEntityOpenness(context.getTenantId(), context.getAppId(), entityOpennessPojoMap.keySet(), context.getUserId());
        this.batchAddEntityOpenness(context, new ArrayList<>(entityOpennessPojoMap.values()));
    }

    @Transactional
    @Override
    public void updateEntityOpenness(CommonContext context, List<EntityOpennessPojo> entityOpennessPojoList) throws AuthServiceException {
        if (CollectionUtils.isEmpty(entityOpennessPojoList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Map<String, EntityOpennessPojo> entityOpennessPojoMap = new HashMap<>();
        entityOpennessPojoList.forEach(entityOpennessPojo -> {
            this.entityOpennessPojoCheck(entityOpennessPojo);
            entityOpennessPojoMap.put(entityOpennessPojo.getEntityId(), entityOpennessPojo);
        });
        List<EntityOpenness> entityOpennessList = this.queryEntityOpennessPrivate(context, entityOpennessPojoMap.keySet(), null, null, null);
        if (CollectionUtils.isNotEmpty(entityOpennessList)) {
            List<EntityOpenness> needUpdateEntityOpenness = new ArrayList<>();
            //差异化更新
            entityOpennessList.forEach(entityOpenness -> {
                EntityOpennessPojo pojo = entityOpennessPojoMap.get(entityOpenness.getEntityId());
                entityOpenness.setScope(pojo.getScope());
                entityOpenness.setPermission(pojo.getPermission());
                entityOpenness.setModifier(context.getUserId());
                entityOpenness.setModifyTime(System.currentTimeMillis());
                needUpdateEntityOpenness.add(entityOpenness);
            });
            if (CollectionUtils.isNotEmpty(needUpdateEntityOpenness)) {
                //                entityOpennessMapper.batchUpdate(needUpdateEntityOpenness);
                //                cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + DataRightsRedisKey.RIGHTS_ENETITY);
            }
        }
    }

    @Override
    public List<EntityOpennessPojo> queryEntityOpenness(
            CommonContext context, Set<String> entitys, Integer permission, Integer scope, PageInfo pageInfo) throws AuthServiceException {
        List<EntityOpenness> entityOpennessList = this.queryEntityOpennessPrivate(context, entitys, permission, scope, pageInfo);
        return this.convertEntityToPojo(entityOpennessList);
    }

    public Integer queryEntityOpennessScopeByEntity(CommonContext context, String entityId) throws AuthServiceException {
        List<EntityOpenness> entityOpennessList = this.queryEntityOpennessPrivate(context, Collections.singleton(entityId), null, null, null);
        if (CollectionUtils.isNotEmpty(entityOpennessList)) {
            return entityOpennessList.get(0).getScope();
        }
        return null;
    }

    @Transactional
    @Override
    public void delEntityOpenness(CommonContext context, Set<String> entitys) throws AuthServiceException {
        SetUtil.removeBlankElement(entitys);
        if (CollectionUtils.isEmpty(entitys)) {
            return;
        }
        entityOpennessMapper.delEntityOpenness(context.getTenantId(), context.getAppId(), entitys, context.getUserId());
        //                cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY);
    }

    /**
     * 根据对象id查询对象基础数据权限(走缓存)
     */
    public EntityOpennessPojo queryEntityOpennessByEntity(CommonContext context, String entityId) throws AuthServiceException {

        EntityOpennessPojo entityOpennessPojo;
        try {
            entityOpennessPojo = (EntityOpennessPojo) cacheManager.getHashObject(
                    context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY,
                    entityId);
        } catch (Exception e) {
            log.error("", e);
            return this.queryEntityOpennessPrivate(context, entityId);
        }
        if (entityOpennessPojo == null) {
            try {
                entityOpennessPojo = queryEntityOpennessPrivate(context, entityId);
                cacheManager.putHashObject(
                        context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY,
                        entityId,
                        entityOpennessPojo);
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY, ENTITY_EXPIRE_SECOND);
            } catch (Exception e) {
                log.error("cache error ", e);
                return entityOpennessPojo;
            }
        }
        return entityOpennessPojo;
    }

    /**
     * 移除基础数据权限缓存
     */
    public void removeEntityOpennessRedisCache(CommonContext context, Set<String> entitys) throws AuthServiceException {
        SetUtil.removeBlankElement(entitys);
        if (CollectionUtils.isEmpty(entitys)) {
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY);
        } else {
            entitys.forEach(entityId -> {
                //                cacheManager.delObject(context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY, entityId);
            });
        }
    }

    private EntityOpennessPojo queryEntityOpennessPrivate(CommonContext context, String entityId) {
        EntityOpennessPojo pojo = new EntityOpennessPojo();
        List<EntityOpenness> entityOpennessList = this.queryEntityOpennessPrivate(context, Collections.singleton(entityId), null, null, null);
        if (CollectionUtils.isNotEmpty(entityOpennessList)) {
            try {
                PropertyUtils.copyProperties(pojo, entityOpennessList.get(0));
            } catch (Exception e) {
                log.error("entityOPenness convert entityOpennessPojo error ", e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
        return pojo;
    }

    private void entityOpennessPojoCheck(EntityOpennessPojo pojo) {
        if (pojo == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        this.entityIdCheck(pojo.getEntityId());
        this.scopeCheck(pojo.getScope());
        this.permissionCheck(pojo.getPermission());
    }

    private void scopeCheck(Integer scope) {
        if (scope == null || !entityOpennessScope.contains(scope)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void permissionCheck(Integer permission) {
        if (permission == null || !entityOpennessPermissType.contains(permission)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void entityIdCheck(String entityId) {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private List<EntityOpennessPojo> convertEntityToPojo(List<EntityOpenness> entityOpennessList) {
        List<EntityOpennessPojo> entityOpennessPojoList = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(entityOpennessList)) {
            try {
                for (EntityOpenness entityOpenness : entityOpennessList) {
                    EntityOpennessPojo pojo = new EntityOpennessPojo();
                    PropertyUtils.copyProperties(pojo, entityOpenness);
                    entityOpennessPojoList.add(pojo);
                }
            } catch (Exception e) {
                log.error("===permission.convertEntityToPojo() error===", e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
        return entityOpennessPojoList;
    }

    private List<EntityOpenness> queryEntityOpennessPrivate(
            CommonContext context, Set<String> entitys, Integer permission, Integer scope, PageInfo page) throws AuthException {
        List<EntityOpenness> entityOpennessList;
        //        if (page != null) {
        //            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        //            entityOpennessList = entityOpennessMapper.queryEntityOpenness(context.getTenantId(), context.getAppId(), entitys, scope, permission);
        //            PageBean pageBean = new PageBean(entityOpennessList);
        //            page.setTotal(pageBean.getTotal());
        //            page.setTotalPage(pageBean.getPages());
        //            page.setCurrentPage(pageBean.getPageNum());
        //        } else {
        entityOpennessList = entityOpennessMapper.queryEntityOpenness(context.getTenantId(), context.getAppId(), entitys, scope, permission);
        //        }
        if (entityOpennessList == null) {
            return Collections.emptyList();
        }
        return entityOpennessList;
    }

    private void batchAddEntityOpenness(CommonContext context, List<EntityOpennessPojo> entityOpennessPojoList) {
        if (CollectionUtils.isNotEmpty(entityOpennessPojoList)) {
            List<EntityOpenness> entityOpennesses = new ArrayList<>();
            Map<String, EntityOpennessPojo> cacheEntityOpenness = new HashMap<>();
            entityOpennessPojoList.forEach(entityOpennessPojo -> {
                EntityOpenness entityOpenness = EntityOpenness.builder()
                        .tenantId(context.getTenantId())
                        .appId(context.getAppId())
                        .entityId(entityOpennessPojo.getEntityId())
                        .permission(entityOpennessPojo.getPermission())
                        .scope(entityOpennessPojo.getScope())
                        .creator(context.getUserId())
                        .createTime(System.currentTimeMillis())
                        .modifier(context.getUserId())
                        .modifyTime(System.currentTimeMillis())
                        .delFlag(0)
                        .build();
                entityOpenness.setId(IdUtil.generateId());
                entityOpennesses.add(entityOpenness);
            });
            for (EntityOpenness entityOpenness : entityOpennesses) {
                try {
                    EntityOpennessPojo pojo = new EntityOpennessPojo();
                    PropertyUtils.copyProperties(pojo, entityOpenness);
                    cacheEntityOpenness.put(entityOpenness.getEntityId(), pojo);
                } catch (Exception e) {
                    log.error(" entityOPenness convert entityOpennessPojo error ", e);
                }

            }
            //            entityOpennessMapper.batchInsert(entityOpennesses);
            cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY,
                    (Map) cacheEntityOpenness);
            try {
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + PermissionConstant.DataRightsRedisKey.RIGHTS_ENETITY, ENTITY_EXPIRE_SECOND);
            } catch (Exception e) {
                log.error("set cache expire error", e);
            }
        }
    }
}
