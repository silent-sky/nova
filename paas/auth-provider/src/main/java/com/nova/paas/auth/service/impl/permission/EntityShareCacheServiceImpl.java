package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Lists;
import com.nova.paas.auth.entity.permission.EntityShare;
import com.nova.paas.auth.entity.permission.EntityShareCache;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityShareCacheMapper;
import com.nova.paas.auth.mapper.permission.EntityShareMapper;
import com.nova.paas.auth.pojo.permission.EntityShareCachePojo;
import com.nova.paas.auth.pojo.permission.EntitySharePojo;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.permission.DataRightsService;
import com.nova.paas.auth.service.permission.EntityShareCacheService;
import com.nova.paas.auth.service.permission.EntityShareService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import com.nova.paas.org.pojo.DeptPojo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityShareCacheServiceImpl implements EntityShareCacheService {

    @Autowired
    private EntityShareCacheMapper entityShareCacheMapper;
    @Autowired
    private EntityShareMapper entityShareMapper;

    @Autowired
    private DataRightsService dataRightsService;

    @Autowired
    private EntityShareService entityShareService;
    //    @Autowired
    //    private GroupMemService groupMemService;
    //    @Autowired
    //    private DeptService deptService;
    //    @Autowired
    //    private DeptUserService deptUserService;
    @Autowired
    private UserRoleService userRoleService;

    //    @Autowired
    //    TaskExecutor taskExecutor;

    private static final Set<Integer> entitySharePermissType;

    static {
        entitySharePermissType = new HashSet<>();
        entitySharePermissType.add(PermissionConstant.EntitySharePermissType.READ_AND_WRITE);
        entitySharePermissType.add(PermissionConstant.EntitySharePermissType.READ_ONLY);
    }

    private final String ROOT = "0";

    /**
     * 删除共享规则缓存
     *
     * @param context        请求上下文
     * @param entityShareIds 共享规则id列表
     */
    @Override
    @Transactional
    public void delEntityShareCache(CommonContext context, Set<String> entityShareIds) throws AuthServiceException {
        SetUtil.removeBlankElement(entityShareIds);
        if (CollectionUtils.isEmpty(entityShareIds)) {
            return;
        }
        entityShareCacheMapper.delEntityShareCache(context.getTenantId(), null, entityShareIds);
    }

    /**
     * 删除共享规则缓存
     *
     * @param context 请求上下文
     * @param entitys 实体ID列表
     */
    @Override
    @Transactional
    public void delEntityShareCacheByEntitys(CommonContext context, Set<String> entitys) throws AuthServiceException {
        SetUtil.removeBlankElement(entitys);
        if (CollectionUtils.isEmpty(entitys)) {
            return;
        }

        entityShareCacheMapper.delEntityShareCacheByEntitys(context.getTenantId(), context.getAppId(), entitys);
    }

    /**
     * 企业共享缓存数据重置
     *
     * @param context        请求上下文
     * @param entityShareIds 共享规则id列表
     */
    @Transactional
    public void entityShareCacheReset(CommonContext context, Set<String> entityShareIds) throws AuthServiceException {
        SetUtil.removeBlankElement(entityShareIds);
        if (CollectionUtils.isEmpty(entityShareIds)) {
            return;
        }
        List<EntityShare> entityShareList = entityShareMapper.queryEntityShare(context.getTenantId(),
                null,
                entityShareIds,
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
        separateResetEntityShareRule(context, entityShareList, entityShareIds);
    }

    /**
     * 企业共享规则重构
     *
     * @param context 请求上下文
     */
    @Transactional
    public void tenantEntityShareCacheReset(CommonContext context) throws AuthServiceException {
        log.info("tenantEntityShareCacheReset tenantId:{},appId:{}", context.getTenantId(), context.getAppId());
        List<EntityShare> entityShareList = entityShareMapper.queryEntityShare(context.getTenantId(),
                context.getAppId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                PermissionConstant.EntityShareStatusType.OPEN,
                null,
                null,
                null,
                null);
        Set<String> entityShareIds = new HashSet<>();
        for (EntityShare entityShare : entityShareList) {
            entityShareIds.add(entityShare.getId());
        }
        separateResetEntityShareRule(context, entityShareList, entityShareIds);
    }

    /**
     * @param context
     * @param entityShareList
     * @param entityShareRuleIds
     */
    private void separateResetEntityShareRule(CommonContext context, List<EntityShare> entityShareList, Set<String> entityShareRuleIds)
            throws AuthServiceException {
        Set<String> allEntityShareRuleIds = new HashSet<>();
        if (CollectionUtils.isNotEmpty(entityShareList)) {
            Lists.partition(entityShareList, 10).forEach(subList -> {
                List<EntityShareCache> entityShareCacheList = this.convertEntityShareRuleToCache(context, subList);
                Set<String> entityShareIds = new HashSet<>();
                for (EntityShare entityShare : subList) {
                    entityShareIds.add(entityShare.getId());
                }
                try {
                    this.delEntityShareCache(context, entityShareIds);
                } catch (AuthServiceException e) {
                    throw new RuntimeException(e.getErrorMsg().getMessage());
                }
                this.separateBatchInsert(context, entityShareCacheList);
                allEntityShareRuleIds.addAll(entityShareIds);
            });
        }
        //删除补偿
        entityShareRuleIds.removeAll(allEntityShareRuleIds);
        List<String> entityShareRuleList = new ArrayList<>(entityShareRuleIds);
        Lists.partition(entityShareRuleList, 10).forEach(subList -> {
            try {
                this.delEntityShareCache(context, new HashSet<>(subList));
            } catch (AuthServiceException e) {
                throw new RuntimeException(e.getErrorMsg().getMessage());
            }
        });
    }

    /**
     * 增加人-部门的缓存数据
     *
     * @param context 请求上下文
     * @param userSet 员工
     */
    @Override
    @Transactional
    public void addUserToDeptCache(CommonContext context, Set<String> deptSet, Set<String> userSet, boolean noRoot) throws AuthServiceException {
        if (CollectionUtils.isEmpty(userSet) || CollectionUtils.isEmpty(deptSet)) {
            return;
        }
        Set<String> superDeptSet = this.getSuperDeptSet(context, deptSet);
        if (noRoot) {
            superDeptSet.remove(ROOT);
        }
        List<EntitySharePojo> shareRulePojos = entityShareService.queryRulePojoByOrgId(context,
                superDeptSet,
                PermissionConstant.DataRightsOrgType.DEPT,
                PermissionConstant.EntityShareStatusType.OPEN,
                Boolean.TRUE);

        //组装entity
        List<EntityShareCache> cacheList = this.getShareCacheEntityList(shareRulePojos, userSet);
        separateBatchInsert(context, cacheList);
    }

    /**
     * 更改部门上级部门————把移走的部门下的员工从原上级部门中移除，只处理作为共享方的数据
     *
     * @param context 请求上下文
     * @param userSet 员工
     */
    @Override
    @Transactional
    public void delUserFromDeptCache(CommonContext context, String deptId, Set<String> userSet) throws AuthServiceException {
        if (CollectionUtils.isEmpty(userSet) || StringUtils.isBlank(deptId)) {
            return;
        }

        //作为共享方的规则处理、包括上级部门
        Set<String> superDeptSet = this.getSuperDeptSet(context, Collections.singleton(deptId));
        List<EntitySharePojo> rulePojoList =
                entityShareService.queryRulePojoByOrgId(context, superDeptSet, PermissionConstant.DataRightsOrgType.DEPT, null, Boolean.TRUE);

        if (CollectionUtils.isNotEmpty(rulePojoList)) {
            Map<String, Set<String>> ruleIdMap = new HashMap<>();
            for (EntitySharePojo pojo : rulePojoList) {
                ruleIdMap.computeIfAbsent(pojo.getShareId(), k -> new HashSet<>());
                ruleIdMap.get(pojo.getShareId()).add(pojo.getId());
            }

            //过滤不符合条件的数据
            Map<String, Set<String>> needDelData = this.getNeedDelMap(context, ruleIdMap.keySet(), userSet);
            if (MapUtils.isNotEmpty(needDelData)) {
                entityShareCacheMapper.delEntityShareCacheByUserRule(context.getTenantId(), needDelData, ruleIdMap, true);
            }
        }
    }

    /**
     * 删除人的部门-更新缓存数据;使用场景：删除一个人的所属部门
     *
     * @param context 请求上下文
     * @param userId  员工
     */
    @Override
    @Transactional
    public void delRuleCacheBySet(CommonContext context, Set<String> deptSet, String userId) throws AuthServiceException {
        if (StringUtils.isBlank(userId) || CollectionUtils.isEmpty(deptSet)) {
            return;
        }

        Set<String> superDeptSet = this.getSuperDeptSet(context, deptSet);
        deptSet = this.getNeedDelMap(context, superDeptSet, Collections.singleton(userId)).keySet();

        if (CollectionUtils.isEmpty(deptSet)) {
            return;
        }

        List<EntitySharePojo> rulePojoList =
                entityShareService.queryRulePojoByOrgId(context, deptSet, PermissionConstant.DataRightsOrgType.DEPT, null, true);

        if (CollectionUtils.isNotEmpty(rulePojoList)) {
            Set<String> ruleIds = new HashSet<>();
            for (EntitySharePojo pojo : rulePojoList) {
                ruleIds.add(pojo.getId());
            }
            entityShareCacheMapper.delRuleCacheByUserRuleId(context.getTenantId(), ruleIds, Collections.singleton(userId), true);
        }
    }

    /**
     * 应用共享缓存初始化
     *
     * @param appId   应用id
     * @param tenants 企业账号
     */
    public void initAppEntityShareCache(String appId, Set<String> tenants, int currentPage) throws AuthServiceException {
        if (StringUtils.isBlank(appId) || CollectionUtils.isEmpty(tenants)) {
            return;
        }
        if (tenants.contains("all")) {
            tenants.remove("all");
            Set<String> xtTenants = null;

        }
        tenantsEntityShareResetTask(tenants, appId);
        log.info("initAppEntityShareCache complete");
    }

    private void tenantsEntityShareResetTask(Set<String> tenants, String appId) {
        if (CollectionUtils.isNotEmpty(tenants)) {
            for (String tenantId : tenants) {
                CommonContext context = new CommonContext();
                context.setUserId(PermissionConstant.SystemValue.DEFAULT_USER);
                context.setAppId(appId);
                context.setTenantId(tenantId);
                //                taskExecutor.execute(() -> {
                //                    try {
                //                        this.tenantEntityShareCacheReset(context);
                //                    } catch (Exception e) {
                //                        log.error("initAppEntityShareCache error tenantId:{}", context.getTenantId(), e);
                //                    }
                //                });
            }
        }
    }

    /**
     * 应用共享缓存初始化
     *
     * @param context    cotext
     * @param ruleIds    规则id
     * @param permission 权限
     */
    @Override
    @Transactional
    public void updatePermissionByRuleId(CommonContext context, Set<String> ruleIds, Integer permission) throws AuthServiceException {
        if (permission == null || !entitySharePermissType.contains(permission)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        SetUtil.removeBlankElement(ruleIds);
        if (CollectionUtils.isEmpty(ruleIds)) {
            return;
        }
        entityShareCacheMapper.updateEntityShareCachePermission(context.getTenantId(), context.getAppId(), ruleIds, permission);
    }

    /**
     * 共享规则缓存查询服务
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     */
    public List<EntityShareCachePojo> entityShareCache(
            CommonContext context, String entityId, String shareId, String shareUser, String receiveUser, PageInfo page) throws AuthServiceException {
        List<EntityShareCache> entityShareCaches;
        if (StringUtils.isBlank(entityId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //        if (page != null) {
        //            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        //            entityShareCaches = entityShareCacheMapper.setTenantId(context.getTenantId())
        //                    .entityShareCache(context.getTenantId(), context.getAppId(), entityId, shareId, shareUser, receiveUser);
        //            PageBean pageBean = new PageBean(entityShareCaches);
        //            page.setTotal(pageBean.getTotal());
        //            page.setTotalPage(pageBean.getPages());
        //            page.setCurrentPage(pageBean.getPageNum());
        //        } else {
        entityShareCaches =
                entityShareCacheMapper.entityShareCache(context.getTenantId(), context.getAppId(), entityId, shareId, shareUser, receiveUser);
        //        }
        List<EntityShareCachePojo> entityShareCachePojos = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(entityShareCaches)) {
            entityShareCaches.forEach(entityShareCache -> {
                EntityShareCachePojo pojo = EntityShareCachePojo.builder()
                        .id(entityShareCache.getId())
                        .tenantId(entityShareCache.getTenantId())
                        .entityId(entityShareCache.getEntityId())
                        .entityShareId(entityShareCache.getEntityShareId())
                        .receiveUser(entityShareCache.getReceiveUser())
                        .receiveType(entityShareCache.getReceiveType())
                        .shareUser(entityShareCache.getShareUser())
                        .permission(entityShareCache.getPermission())
                        .build();
                entityShareCachePojos.add(pojo);
            });
        }
        return entityShareCachePojos;
    }

    /**
     * 添加组中成员
     *
     * @param context 请求上下文
     * @param userSet 员工
     * @param groupId 组
     */
    public void addUserToGroupCache(CommonContext context, String groupId, Set<String> userSet) throws AuthServiceException {
        if (StringUtils.isBlank(groupId) || CollectionUtils.isEmpty(userSet)) {
            return;
        }

        List<EntitySharePojo> shareRulePojos = entityShareService.queryRulePojoByOrgId(context,
                Collections.singleton(groupId),
                PermissionConstant.DataRightsOrgType.GROUP,
                PermissionConstant.EntityShareStatusType.OPEN,
                Boolean.TRUE);

        //组装entity
        List<EntityShareCache> cacheList = this.getShareCacheEntityList(shareRulePojos, userSet);
        separateBatchInsert(context, cacheList);
    }

    /**
     * 删除组中成员
     *
     * @param context 请求上下文
     * @param userSet 员工
     * @param groupId 组
     */
    public void delUserFromGroupCache(CommonContext context, String groupId, Set<String> userSet) throws AuthServiceException {
        if (StringUtils.isBlank(groupId) || CollectionUtils.isEmpty(userSet)) {
            return;
        }

        List<EntitySharePojo> rulePojoList = entityShareService.queryRulePojoByOrgId(context,
                Collections.singleton(groupId),
                PermissionConstant.DataRightsOrgType.GROUP,
                null,
                true);
        this.delUserFromEntityShares(context, rulePojoList, userSet);
    }

    /**
     * 共享规则中添加角色成员
     *
     * @param context 请求上下文
     * @param roles   角色
     * @param users   用户
     */
    public void addUserToRoleCache(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException {
        if (CollectionUtils.isEmpty(roles) || CollectionUtils.isEmpty(users)) {
            return;
        }
        List<EntitySharePojo> rulePojoList =
                entityShareService.queryRuleByShares(context, null, PermissionConstant.EntityShareType.ROLE, roles, null);
        //组装entity
        List<EntityShareCache> cacheList = this.getShareCacheEntityList(rulePojoList, users);
        separateBatchInsert(context, cacheList);
    }

    /**
     * 共享规则中删除角色成员
     *
     * @param context 请求上下文
     * @param roles   角色
     * @param users   用户
     */
    public void delUserFromRoleCache(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException {
        if (CollectionUtils.isEmpty(roles) || CollectionUtils.isEmpty(users)) {
            return;
        }
        List<EntitySharePojo> rulePojoList =
                entityShareService.queryRuleByShares(context, null, PermissionConstant.EntityShareType.ROLE, roles, null);
        this.delUserFromEntityShares(context, rulePojoList, users);
    }

    /**
     * 更新用户角色
     *
     * @param context 请求上下文
     * @param roles   角色
     * @param users   用户
     */
    public void updateUserRoles(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException {

        //查询所有和角色相关的共享规则,然后直接删除该规则下的用户缓存
        List<EntitySharePojo> rulePojos = entityShareService.queryRuleByShares(context, null, PermissionConstant.EntityShareType.ROLE, null, null);
        this.delUserFromEntityShares(context, rulePojos, users);

        //查询roles相关的share规则,往里面添加用户
        if (CollectionUtils.isNotEmpty(roles)) {
            List<EntitySharePojo> rulePojoList =
                    entityShareService.queryRuleByShares(context, null, PermissionConstant.EntityShareType.ROLE, roles, null);
            //组装entity
            List<EntityShareCache> cacheList = this.getShareCacheEntityList(rulePojoList, users);
            separateBatchInsert(context, cacheList);
        }
    }

    private void delUserFromEntityShares(CommonContext context, List<EntitySharePojo> rulePojos, Set<String> users) {
        if (CollectionUtils.isNotEmpty(rulePojos)) {
            Set<String> ruleIds = new HashSet<>();
            for (EntitySharePojo pojo : rulePojos) {
                ruleIds.add(pojo.getId());
            }
            entityShareCacheMapper.delRuleCacheByUserRuleId(context.getTenantId(), ruleIds, users, true);
        }
    }

    private List<EntityShareCache> convertEntityShareRuleToCache(CommonContext context, List<EntityShare> entityShareList) {
        List<EntityShareCache> entityShareCacheList = new LinkedList<>();
        //        Map<String, Map<String, Set<String>>> appGroupMembers = new HashMap<>();
        //        Map<String, Map<String, Set<String>>> appRoleMembers = new HashMap<>();
        //        Map<String, Set<String>> appGroups = new HashMap<>();
        //        Map<String, Set<String>> appRoles = new HashMap<>();
        //        Map<String, Set<String>> deptUsersShare;
        //        Set<String> entityShareDept = new HashSet<>();
        //        entityShareList.forEach(entityShare -> {
        //            if (entityShare.getShareType() == PermissionConstant.EntityShareType.GROUP) {
        //                appGroups.computeIfAbsent(entityShare.getAppId(), k -> new HashSet<>());
        //                appGroups.get(entityShare.getAppId()).add(entityShare.getShareId());
        //            }
        //            if (entityShare.getShareType() == PermissionConstant.EntityShareType.DEPT) {
        //                entityShareDept.add(entityShare.getShareId());
        //            }
        //            if (entityShare.getShareType() == PermissionConstant.EntityShareType.ROLE) {
        //                appRoles.computeIfAbsent(entityShare.getAppId(), k -> new HashSet<>());
        //                appRoles.get(entityShare.getAppId()).add(entityShare.getShareId());
        //            }
        //        });
        //        String oldAppID = context.getAppId();
        //        if (MapUtils.isNotEmpty(appGroups)) {
        //            appGroups.forEach((appId, groups) -> {
        //                context.setAppId(appId);
        //                //                appGroupMembers.put(appId, groupMemService.queryGroupMembers(context, new ArrayList<>(groups), false, true, 0));
        //            });
        //        }
        //
        //        if (MapUtils.isNotEmpty(appRoles)) {
        //            appRoles.forEach((appId, roles) -> {
        //                context.setAppId(appId);
        //                try {
        //                    appRoleMembers.put(appId, userRoleService.queryRoleUsersByRoles(context, roles));
        //                } catch (AuthServiceException e) {
        //                    throw new RuntimeException(e.getErrorMsg().getMessage());
        //                }
        //            });
        //        }
        //        context.setAppId(oldAppID);
        //
        //        deptUsersShare = queryDeptUsers(context, entityShareDept, Boolean.TRUE);
        //
        //        Set<String> shareUsers = new HashSet<>();
        //
        //        for (EntityShare entityShare : entityShareList) {
        //            if (entityShare.getShareType() == PermissionConstant.EntityShareType.GROUP && appGroupMembers.get(entityShare.getAppId()) != null
        //                    && CollectionUtils.isNotEmpty(appGroupMembers.get(entityShare.getAppId()).get(entityShare.getShareId()))) {
        //                shareUsers.addAll(appGroupMembers.get(entityShare.getAppId()).get(entityShare.getShareId()));
        //            }
        //            if (entityShare.getShareType() == PermissionConstant.EntityShareType.DEPT
        //                    && CollectionUtils.isNotEmpty(deptUsersShare.get(entityShare.getShareId()))) {
        //                shareUsers.addAll(deptUsersShare.get(entityShare.getShareId()));
        //            }
        //            if (entityShare.getShareType() == PermissionConstant.EntityShareType.USER) {
        //                shareUsers.add(entityShare.getShareId());
        //            }
        //            if (entityShare.getShareType() == PermissionConstant.EntityShareType.ROLE && appRoleMembers.get(entityShare.getAppId()) != null
        //                    && CollectionUtils.isNotEmpty(appRoleMembers.get(entityShare.getAppId()).get(entityShare.getShareId()))) {
        //                shareUsers.addAll(appRoleMembers.get(entityShare.getAppId()).get(entityShare.getShareId()));
        //            }
        //            for (String shareUser : shareUsers) {
        //                EntityShareCache entityShareCache = EntityShareCache.builder()
        //                        .tenantId(entityShare.getTenantId())
        //                        .entityId(entityShare.getEntityId())
        //                        .entityShareId(entityShare.getId())
        //                        .shareUser(shareUser)
        //                        .receiveUser(entityShare.getReceiveId())
        //                        .receiveType(entityShare.getReceiveType())
        //                        .permission(entityShare.getPermission())
        //                        .build();
        //                entityShareCache.setId(IdUtil.generateId());
        //                entityShareCacheList.add(entityShareCache);
        //            }
        //            shareUsers.clear(); //如果直接赋值为null,null.addAll()出现NullPointException
        //        }
        return entityShareCacheList;
    }

    private Map<String, Set<String>> queryDeptUsers(CommonContext context, Set<String> deptIds, boolean includeChild) {
        Map<String, Set<String>> deptUsers = new HashMap<>();
        if (CollectionUtils.isEmpty(deptIds)) {
            return deptUsers;
        }

        //        BatchQueryDeptUserMapByDeptIdArg arg = new BatchQueryDeptUserMapByDeptIdArg();
        //        arg.setDeptIds(deptIds);
        //        arg.setUserStatus(DBColumnConstants.EMPLOYEE_STATUS.ALL);
        //        arg.setIncludeLowDept(includeChild);
        //        arg.setDeptUserType(DBColumnConstants.EMPLOYEE_DEPT_TYPE.ALL);

        //        Map<String, List<UserPojo>> deptUserInfoMap = deptUserService.batchQueryUserPojoByDeptId(context, arg);

        //        for (Map.Entry<String, List<UserPojo>> entry : deptUserInfoMap.entrySet()) {
        //            if (CollectionUtils.isNotEmpty(entry.getValue())) {
        //                deptUsers.put(entry.getKey(), new HashSet<>());
        //
        //                for (UserPojo pojo : entry.getValue()) {
        //                    if (pojo != null) {
        //                        deptUsers.get(entry.getKey()).add(pojo.getId());
        //                    }
        //                }
        //            }
        //        }

        return deptUsers;
    }

    //获取需要删除的部门、以及各自的下级员工
    private Map<String, Set<String>> getNeedDelMap(CommonContext context, Set<String> deptSet, Set<String> userSet) {

        Map<String, Set<String>> needDelDeptUserData = new HashMap<>();

        Map<String, Set<String>> employeeIdData = this.queryDeptUsers(context, deptSet, true);

        deptSet.forEach(deptId -> {
            userSet.forEach(userId -> {
                if (employeeIdData.get(deptId) == null || !employeeIdData.get(deptId).contains(userId)) {
                    needDelDeptUserData.computeIfAbsent(deptId, k -> new HashSet<>());
                    needDelDeptUserData.get(deptId).add(userId);
                }
            });
        });

        return needDelDeptUserData;
    }

    //包括root、包括自己
    private Set<String> getSuperDeptSet(CommonContext context, Set<String> deptIds) {
        //        Map<String, List<DeptPojo>> orgData = deptService.batchQuerySuperDeptPathWithSelfByDeptId(context, deptIds);
        Map<String, List<DeptPojo>> orgData = null;

        Set<String> allSuperDept = new HashSet<>();
        if (MapUtils.isNotEmpty(orgData)) {
            orgData.forEach((dept, supers) -> {
                if (CollectionUtils.isNotEmpty(supers)) {
                    supers.forEach(superDept -> {
                        if (superDept != null) {
                            allSuperDept.add(superDept.getId());
                        }
                    });
                }
            });
        }

        return allSuperDept;
    }

    //获取作为共享方的共享规则缓存数据
    private List<EntityShareCache> getShareCacheEntityList(List<EntitySharePojo> shareRulePojos, Set<String> userSet) {
        List<EntityShareCache> cacheList = new ArrayList<>();
        if (CollectionUtils.isEmpty(shareRulePojos)) {
            return cacheList;
        }

        for (EntitySharePojo rulePojo : shareRulePojos) {
            for (String userId : userSet) {
                EntityShareCache cache = new EntityShareCache();
                cache.setId(IdUtil.generateId());
                cache.setTenantId(rulePojo.getTenantId());
                cache.setEntityId(rulePojo.getEntityId());
                cache.setEntityShareId(rulePojo.getId());
                cache.setShareUser(userId);
                cache.setReceiveUser(rulePojo.getReceiveId());
                cache.setReceiveType(rulePojo.getReceiveType());
                cache.setPermission(rulePojo.getPermission());
                cacheList.add(cache);
            }
        }
        return cacheList;
    }

    private void separateBatchInsert(CommonContext context, List<EntityShareCache> entityShareCacheList) {
        if (CollectionUtils.isNotEmpty(entityShareCacheList)) {
            Lists.partition(entityShareCacheList, 500).forEach((subList) -> {
                //                entityShareCacheMapper.batchInsert(subList);
            });
        }
    }

}
