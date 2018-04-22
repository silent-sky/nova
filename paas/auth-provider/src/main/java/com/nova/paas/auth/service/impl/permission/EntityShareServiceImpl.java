package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.permission.EntityShare;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityShareMapper;
import com.nova.paas.auth.pojo.permission.EntitySharePojo;
import com.nova.paas.auth.service.permission.EntityShareCacheService;
import com.nova.paas.auth.service.permission.EntityShareService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityShareServiceImpl implements EntityShareService {
    @Inject
    private EntityShareMapper entityShareMapper;
    @Inject
    private EntityShareCacheService entityShareCacheService;

    private static final Set<Integer> entitySharePermissType;

    static {
        entitySharePermissType = new HashSet<>();
        entitySharePermissType.add(PermissionConstant.EntitySharePermissType.READ_AND_WRITE);
        entitySharePermissType.add(PermissionConstant.EntitySharePermissType.READ_ONLY);
    }

    private static final Set<Integer> entityShareType;

    static {
        entityShareType = new HashSet<>();
        entityShareType.add(PermissionConstant.EntityShareType.USER);
        entityShareType.add(PermissionConstant.EntityShareType.DEPT);
        entityShareType.add(PermissionConstant.EntityShareType.GROUP);
        entityShareType.add(PermissionConstant.EntityShareType.ROLE);
    }

    private static final Set<Integer> entityShareStatusType;

    static {
        entityShareStatusType = new HashSet<>();
        entityShareStatusType.add(PermissionConstant.EntityShareStatusType.OPEN);
        entityShareStatusType.add(PermissionConstant.EntityShareStatusType.CLOSE);
    }

    /**
     * 批 创建共享规则
     *
     * @param context 请求上下文
     */
    @Transactional
    @Override
    public void createEntityShare(CommonContext context, List<EntitySharePojo> shareList) throws AuthServiceException {
        if (CollectionUtils.isEmpty(shareList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        List<EntityShare> insertRules = new ArrayList<>(); //insert
        Set<String> entityShareIds = new HashSet<>();
        for (EntitySharePojo pojo : shareList) {
            this.pojoCheck(pojo);
            EntityShare entityShare = EntityShare.builder()
                    .tenantId(context.getTenantId())
                    .entityId(pojo.getEntityId())
                    .shareType(pojo.getShareType())
                    .shareId(pojo.getShareId())
                    .receiveType(pojo.getReceiveType())
                    .receiveId(pojo.getReceiveId())
                    .permission(pojo.getPermission())
                    .status(pojo.getStatus())
                    .delFlag(0)
                    .creator(context.getUserId())
                    .createTime(System.currentTimeMillis())
                    .modifier(context.getUserId())
                    .modifyTime(System.currentTimeMillis())
                    .build();
            entityShare.setId(IdUtil.generateId());
            insertRules.add(entityShare);
            if (entityShare.getStatus() == PermissionConstant.EntityShareStatusType.OPEN) {
                entityShareIds.add(entityShare.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(insertRules)) {
            //            entityShareMapper.batchInsert(insertRules);
        }
        if (CollectionUtils.isNotEmpty(entityShareIds)) {
            entityShareCacheService.entityShareCacheReset(context, entityShareIds);
        }
    }

    /**
     * 删除共享规则
     *
     * @param context        请求上下文
     * @param entityShareIds 共享规则ID列
     * @param status         则状态
     */
    @Transactional
    @Override
    public void delEntityShare(CommonContext context, Set<String> entityShareIds, Integer status) throws AuthServiceException {
        SetUtil.removeNull(entityShareIds);
        if (CollectionUtils.isEmpty(entityShareIds)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (status != null) {
            List<EntityShare> entityShareList =
                    this.queryEntitySharePrivate(context, entityShareIds, null, null, null, null, null, null, null, null, null, null, null, null);
            if (CollectionUtils.isNotEmpty(entityShareList)) {
                entityShareList.forEach(entityShare -> {
                    if (status != null && entityShare.getStatus().intValue() != status.intValue()) {
                        throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
                    }
                });
            }
        }
        entityShareMapper.delEntityShare(context.getTenantId(), context.getAppId(), entityShareIds, context.getUserId());
        entityShareCacheService.delEntityShareCache(context, entityShareIds);
    }

    /**
     * 共享规则信息
     *
     * @param context 请求上下文
     * @param pojo    规则对象
     */
    @Transactional
    @Override
    public void updateEntityShare(CommonContext context, EntitySharePojo pojo) throws AuthServiceException {
        this.pojoCheck(pojo);
        if (StringUtils.isBlank(pojo.getId())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //校验共享规则是否存在
        List<EntityShare> entityShareList = this.queryEntitySharePrivate(
                context,
                Collections.singleton(pojo.getId()),
                Collections.singleton(pojo.getEntityId()),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        if (CollectionUtils.isEmpty(entityShareList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        entityShareMapper.updateEntityShare(
                context.getTenantId(),
                context.getAppId(),
                pojo.getEntityId(),
                pojo.getId(),
                pojo.getShareType(),
                pojo.getShareId(),
                pojo.getReceiveType(),
                pojo.getReceiveId(),
                pojo.getStatus(),
                pojo.getPermission(),
                context.getUserId(),
                System.currentTimeMillis());
        entityShareCacheService.entityShareCacheReset(context, Sets.newHashSet(pojo.getId()));
    }

    /**
     * 共享规则信息
     *
     * @param context 请求上下文
     * @param pojo    规则对象
     */
    @Override
    public void updateEntitySharePermissionByRule(CommonContext context, EntitySharePojo pojo) throws AuthServiceException {
        if (pojo == null) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (StringUtils.isBlank(pojo.getEntityId())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (pojo.getShareType() == null || !entityShareType.contains(pojo.getShareType())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (pojo.getReceiveType() == null || !entityShareType.contains(pojo.getReceiveType())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isBlank(pojo.getShareId())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isBlank(pojo.getReceiveId())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (pojo.getPermission() == null || !entitySharePermissType.contains(pojo.getPermission())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<EntityShare> entityShareList = this.queryEntitySharePrivate(
                context,
                null,
                Collections.singleton(pojo.getEntityId()),
                Collections.singleton(pojo.getShareType()),
                Collections.singleton(pojo.getShareId()),
                Collections.singleton(pojo.getReceiveType()),
                Collections.singleton(pojo.getReceiveId()),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        if (CollectionUtils.isEmpty(entityShareList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        EntityShare entityShare = entityShareList.get(0);
        entityShareMapper.updateEntityShare(
                context.getTenantId(),
                context.getAppId(),
                entityShare.getEntityId(),
                entityShare.getId(),
                entityShare.getShareType(),
                entityShare.getShareId(),
                entityShare.getReceiveType(),
                entityShare.getReceiveId(),
                entityShare.getStatus(),
                pojo.getPermission(),
                context.getUserId(),
                System.currentTimeMillis());

        entityShareCacheService.updatePermissionByRuleId(context, Collections.singleton(entityShare.getId()), pojo.getPermission());
    }

    /**
     * 批   共享规则状态
     *
     * @param context        请求上下文
     * @param entityShareIds 则ID列
     * @param status         态
     */
    @Transactional
    @Override
    public void updateEntityShareStatus(CommonContext context, Set<String> entityShareIds, Integer status) throws AuthServiceException {
        SetUtil.removeBlankElement(entityShareIds);
        if (CollectionUtils.isEmpty(entityShareIds)) {
            return;
        }
        if (status == null || !entityShareStatusType.contains(status)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        entityShareMapper.updateEntityShareStatus(context.getTenantId(), context.getAppId(), entityShareIds, status, context.getUserId());

        //处理缓存表
        if (status == PermissionConstant.EntityShareStatusType.OPEN) {
            entityShareCacheService.entityShareCacheReset(context, entityShareIds);
        } else if (status == PermissionConstant.EntityShareStatusType.CLOSE) {
            entityShareCacheService.delEntityShareCache(context, entityShareIds);
        }
    }

    /**
     * 查询共享规则
     *
     * @param context          请求上下文
     * @param ids              id列表
     * @param entitys          对象实体列表
     * @param sourceTypes      共享者类型
     * @param sources          共享者列表
     * @param receiveTypes     被共享者类型
     * @param receives         被共享者列表
     * @param permission       权限
     * @param status           状态
     * @param sharesOrReceives 分享和被分享者id列表
     * @param page             分页信息
     */
    @Override
    public List<EntitySharePojo> queryEntityShare(
            CommonContext context,
            Set<String> ids,
            Set<String> entitys,
            Set<Integer> sourceTypes,
            Set<String> sources,
            Set<Integer> receiveTypes,
            Set<String> receives,
            Integer permission,
            Integer status,
            Set<String> sharesOrReceives,
            Map<Integer, Set<String>> sharesId,
            Map<Integer, Set<String>> receivesId,
            Map<Integer, Set<String>> sharesOrReceivesId,
            PageInfo page) throws AuthServiceException {
        List<EntityShare> entityShareList = this.queryEntitySharePrivate(
                context,
                ids,
                entitys,
                sourceTypes,
                sources,
                receiveTypes,
                receives,
                permission,
                status,
                sharesOrReceives,
                sharesId,
                receivesId,
                sharesOrReceivesId,
                page);
        return this.convertEntityToPojo(entityShareList);
    }

    /**
     * 据共享规则的 共享方查询更新规则(并集)
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param receives 共享方
     */
    @Override
    public List<EntitySharePojo> queryEntityShareByReceives(
            CommonContext context, String entityId, Map<Integer, Set<String>> receives, Integer status) throws AuthServiceException {
        if (StringUtils.isBlank(entityId) || MapUtils.isEmpty(receives)) {
            return Collections.emptyList();
        }
        List<EntityShare> entityShareList =
                entityShareMapper.queryEntityShareByReceives(context.getTenantId(), context.getAppId(), entityId, receives, status);
        return this.convertEntityToPojo(entityShareList);
    }

    /**
     * 删除共享规则
     *
     * @param context 请求上下文
     * @param entitys 共享对象列
     */
    @Transactional
    public void delEntityShareByEntitys(CommonContext context, Set<String> entitys) throws AuthServiceException {
        SetUtil.removeBlankElement(entitys);
        if (CollectionUtils.isEmpty(entitys)) {
            return;
        }
        entityShareMapper.delEntityShareByEntitys(context.getTenantId(), context.getAppId(), entitys, context.getUserId());
        entityShareCacheService.delEntityShareCacheByEntitys(context, entitys);
    }

    /**
     * 查询org所在的rule
     *
     * @param context context  上下文
     * @param orgIds  id
     * @param type    org类型
     * @return map
     */
    public List<EntitySharePojo> queryRulePojoByOrgId(CommonContext context, Set<String> orgIds, Integer type, Integer status, Boolean asShare)
            throws AuthServiceException {

        List<EntitySharePojo> res = new ArrayList<>();
        SetUtil.removeBlankElement(orgIds);
        if (CollectionUtils.isEmpty(orgIds)) {
            return res;
        }
        List<EntityShare> entitys = entityShareMapper.queryRuleIdsByOrg(context.getTenantId(), orgIds, type, status, asShare);
        res = this.convertEntityToPojo(entitys);

        return res;
    }

    /**
     * 查询共享方的共享规则
     *
     * @param context   请求上下文
     * @param entityId  对象id
     * @param shareType 共享类型
     * @param shares    共享源id
     * @param status    状态
     */
    @Override
    public List<EntitySharePojo> queryRuleByShares(
            CommonContext context, String entityId, Integer shareType, Set<String> shares, Integer status) throws AuthServiceException {
        List<EntitySharePojo> res = new ArrayList<>();
        SetUtil.removeBlankElement(shares);
        List<EntityShare> entitys =
                entityShareMapper.queryRuleByShares(context.getTenantId(), context.getAppId(), entityId, shares, shareType, status);
        res = this.convertEntityToPojo(entitys);
        return res;
    }

    /**
     * 查询org所在的Id
     *
     * @param context context  上下文
     * @param orgIds  orgIds
     * @param type    org类型
     * @return id列表
     */
    @Override
    public Set<String> queryRuleIdsByOrg(CommonContext context, Set<String> orgIds, Integer type, Integer status, Boolean asSahre)
            throws AuthServiceException {

        SetUtil.removeBlankElement(orgIds);
        List<EntityShare> entitys = entityShareMapper.queryRuleIdsByOrg(context.getTenantId(), orgIds, type, status, asSahre);

        Set<String> res = new HashSet<>();
        if (CollectionUtils.isNotEmpty(entitys)) {
            entitys.forEach(entity -> {
                res.add(entity.getId());
            });
        }
        return res;
    }

    /**
     * 共享规则参数校验
     *
     * @param pojo 共享规则实体
     */
    private void pojoCheck(EntitySharePojo pojo) {
        if (pojo == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (StringUtils.isBlank(pojo.getEntityId())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (pojo.getShareType() == null || !entityShareType.contains(pojo.getShareType())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (pojo.getReceiveType() == null || !entityShareType.contains(pojo.getReceiveType())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isBlank(pojo.getShareId())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isBlank(pojo.getReceiveId())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (pojo.getPermission() == null || !entitySharePermissType.contains(pojo.getPermission())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (pojo.getStatus() == null || !entityShareStatusType.contains(pojo.getStatus())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    /**
     * 对象 换
     */
    private List<EntitySharePojo> convertEntityToPojo(List<EntityShare> entityShareList) {
        List<EntitySharePojo> pojoList = new ArrayList<>();
        try {
            if (CollectionUtils.isNotEmpty(entityShareList)) {
                for (EntityShare entityShare : entityShareList) {
                    EntitySharePojo pojo = new EntitySharePojo();
                    PropertyUtils.copyProperties(pojo, entityShare);
                    pojoList.add(pojo);
                }
            }
        } catch (Exception e) {
            log.error("===permission.convertEntityToPojo() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        return pojoList;
    }

    /**
     * 询数据更新规则
     *
     * @param context     请求上下文
     * @param entitys     对象实体列
     * @param shareType   共享  型
     * @param shares      共享 列
     * @param receiveType 共享  型
     * @param receives    共享 列
     * @param permission  限
     * @param status      态
     * @param page        分页信息
     */
    private List<EntityShare> queryEntitySharePrivate(
            CommonContext context,
            Set<String> ids,
            Set<String> entitys,
            Set<Integer> shareType,
            Set<String> shares,
            Set<Integer> receiveType,
            Set<String> receives,
            Integer permission,
            Integer status,
            Set<String> sharesOrReceives,
            Map<Integer, Set<String>> sharesId,
            Map<Integer, Set<String>> receivesId,
            Map<Integer, Set<String>> sharesOrReceivesId,
            PageInfo page) {
        List<EntityShare> entityShareList;
        //        if (page != null) {
        //            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        //            entityShareList = entityShareMapper.setTenantId(context.getTenantId()).queryEntityShare(
        //                    context.getTenantId(),
        //                    context.getAppId(),
        //                    ids,
        //                    entitys,
        //                    shareType,
        //                    shares,
        //                    receiveType,
        //                    receives,
        //                    sharesOrReceives,
        //                    status,
        //                    permission,
        //                    sharesId,
        //                    receivesId,
        //                    sharesOrReceivesId);
        //            PageBean pageBean = new PageBean(entityShareList);
        //            page.setTotal(pageBean.getTotal());
        //            page.setTotalPage(pageBean.getPages());
        //            page.setCurrentPage(pageBean.getPageNum());
        //        } else {
        entityShareList = entityShareMapper.queryEntityShare(
                context.getTenantId(),
                context.getAppId(),
                ids,
                entitys,
                shareType,
                shares,
                receiveType,
                receives,
                sharesOrReceives,
                status,
                permission,
                sharesId,
                receivesId,
                sharesOrReceivesId);
        //        }
        if (entityShareList == null) {
            return Collections.EMPTY_LIST;
        }
        return entityShareList;
    }

}
