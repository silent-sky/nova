package com.nova.paas.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.auth.service.RoleService;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.entity.Role;
import com.nova.paas.auth.service.entity.UserRole;
import com.nova.paas.auth.service.mapper.UserRoleMapper;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service("userRoleService")
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    RoleService roleService;
    @Autowired
    private CacheManager cacheManager;
    @Value("${USER_ROLE_EXPIRE_SECOND}")
    private int USER_ROLE_EXPIRE_SECOND;

    @Override
    public List<String> queryRoleCodeListByUserId(CommonContext context) throws AuthServiceException {
        Set<String> roles;
        try {
            roles = (Set) cacheManager.getHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE,
                    context.getUserId());

        } catch (Exception e) {
            return this.queryRoleCodeListByUserIdFromDB(context);
        }

        //缓存未命中,重构
        if (roles == null) {
            List<String> roleList = this.queryRoleCodeListByUserIdFromDB(context);
            try {
                cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE,
                        context.getUserId(),
                        new HashSet<>(roleList));
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);
                return roleList;

            } catch (Exception e) {
                return roleList;
            }
        }
        return new ArrayList<>(roles);
    }

    /**
     * 查询角色下绑定的用户
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     */
    @Override
    public List<String> queryUserListByRoleCode(CommonContext context, String roleCode, PageInfo pageInfo) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            return new ArrayList<>();
        }
        return this.queryMatchedUserIdPrivate(context, roleCode, null, null, pageInfo);
    }

    /**
     * 查询用户绑定的角色
     *
     * @param authContext 请求上下文
     * @param userId      用户ID
     */
    @Override
    public List<RolePojo> queryRoleListByUserId(CommonContext authContext, String userId) throws AuthServiceException {
        List<RolePojo> rolePojoList = new ArrayList<>();
        if (StringUtils.isBlank(userId)) {
            return rolePojoList;
        }
        try {
            List<Role> roleList =
                    userRoleMapper.queryRoleByUser(authContext.getTenantId(), authContext.getAppId(), userId, AuthConstant.orgType.USER);
            return this.roleConvertToRolePojo(roleList);
        } catch (Exception e) {
            log.error("===auth.queryRoleListByUserId() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    @Override
    @Transactional
    public void delUserFromRole(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException {

        log.info("[Request], method:{},context:{},roleCode:{},users:{}",
                "delUserFromRole",
                JSON.toJSONString(context),
                JSON.toJSONString(roleCode),
                JSON.toJSONString(users));

        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (users != null) {
            users.remove(null);
        }
        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //角色校验
        this.rolesIsExist(context, Collections.singleton(roleCode));

        this.delRoleFromUserAndUpdateCache(context, roleCode, users);
        this.delDeptCacheKey(context);
    }

    @Override
    @Transactional
    public void addUserToRole(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{},users:{}",
                "addUserToRole",
                JSON.toJSONString(context),
                JSON.toJSONString(roleCode),
                JSON.toJSONString(users));

        //校验
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (users.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //角色校验
        this.rolesIsExist(context, Collections.singleton(roleCode));

        List<UserRole> userRoleList = new LinkedList<>();
        List<String> existUsers = this.queryUserListByRoleCode(context, roleCode, null);
        Set<String> existUserSet = new HashSet<>(existUsers);
        for (String userId : users) {
            if (!existUserSet.contains(userId)) {
                this.checkUserId(userId);
                UserRole userRole = new UserRole(IdUtil.generateId(),
                        context.getTenantId(),
                        context.getAppId(),
                        userId,
                        roleCode,
                        AuthConstant.orgType.USER,
                        context.getUserId(),
                        System.currentTimeMillis(),
                        Boolean.FALSE);
                userRoleList.add(userRole);
            }
        }

        if (CollectionUtils.isNotEmpty(userRoleList)) {
            this.batchInsertUserRole(context, userRoleList);
            this.addRoleToUserUpdateCache(context, Collections.singleton(roleCode), users);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    @Override
    @Transactional
    public void updateUserRole(CommonContext context, String userId, Set<String> roles) throws AuthServiceException {
        log.info("[Request], method:{},context:{},userId:{},roles:{}",
                "updateUserRole",
                JSON.toJSONString(context),
                JSON.toJSONString(userId),
                JSON.toJSONString(roles));
        this.checkUserId(userId);
        if (roles == null || roles.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //校验角色是否存在
        this.rolesIsExist(context, roles);

        try {
            //删除用户以前的角色,重新赋值新角色
            userRoleMapper.batchDel(context.getTenantId(),
                    context.getAppId(),
                    null,
                    Collections.singletonList(userId),
                    AuthConstant.orgType.USER,
                    context.getUserId(),
                    System.currentTimeMillis());
            List<UserRole> userRoleList = new LinkedList<>();
            roles.forEach(role -> {
                UserRole userRole = new UserRole(IdUtil.generateId(),
                        context.getTenantId(),
                        context.getAppId(),
                        userId,
                        role,
                        AuthConstant.orgType.USER,
                        context.getUserId(),
                        System.currentTimeMillis(),
                        Boolean.FALSE);
                userRoleList.add(userRole);
            });
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                //                userRoleMapper.batchInsert(userRoleList);
            }
            cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, userId, roles);
            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);

        } catch (Exception e) {
            log.error("===auth.updateUserRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    @Override
    @Transactional
    public void batchAddUserToRole(CommonContext context, Set<String> users, Set<String> roles) throws AuthServiceException {

        log.info("[Request], method:{},context:{},users:{},roles:{}",
                "batchAddUserToRole",
                JSON.toJSONString(context),
                JSON.toJSONString(users),
                JSON.toJSONString(roles));

        //入参校验
        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(roles)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (users.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (roles.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //校验角色是否存在
        this.rolesIsExist(context, roles);
        List<UserRole> userRoleList;
        try {
            //返回角色的用户
            userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), roles, null, AuthConstant.orgType.USER, false);
        } catch (Exception e) {
            log.error("===auth.batchAddUserToRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        Map<String, Set<String>> roleUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            userRoleList.forEach(userRole -> {
                if (roleUserMap.get(userRole.getRoleCode()) == null) {
                    roleUserMap.put(userRole.getRoleCode(), new HashSet<>());
                }
                roleUserMap.get(userRole.getRoleCode()).add(userRole.getOrgId());
            });
        }

        List<UserRole> userRoles = new LinkedList<>();
        roles.forEach(role -> {
            users.forEach(user -> {
                if (roleUserMap.get(role) == null || (!roleUserMap.get(role).contains(user))) {
                    this.checkUserId(user);
                    UserRole userRole = new UserRole(IdUtil.generateId(),
                            context.getTenantId(),
                            context.getAppId(),
                            user,
                            role,
                            AuthConstant.orgType.USER,
                            context.getUserId(),
                            System.currentTimeMillis(),
                            Boolean.FALSE);
                    userRoles.add(userRole);
                }
            });
        });

        if (CollectionUtils.isNotEmpty(userRoles)) {
            this.batchInsertUserRole(context, userRoles);
            this.addRoleToUserUpdateCache(context, roles, users);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    /**
     * 查询所有用户角色实体列表
     */
    @Override
    public List<UserRolePojo> getAllEmployeeRoleRelationEntities(CommonContext context) throws AuthServiceException {
        try {
            List<UserRole> userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, null, null, Boolean.FALSE);
            return this.UserRole2UserRolePojo(userRoleList);
        } catch (Exception e) {
            log.error("===auth.getAllEmployeeRoleRelationEntities() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 根据角色列表查询所有用户角色实体列表
     */
    @Override
    public List<UserRolePojo> getUserRoleRelationEntitiesByRoles(CommonContext context, Set<String> roles) throws AuthServiceException {
        if (roles != null) {
            roles.remove(null);
        }
        if (CollectionUtils.isEmpty(roles)) {
            return new LinkedList<>();
        }
        try {
            List<UserRole> userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), roles, null, null, Boolean.FALSE);
            return this.UserRole2UserRolePojo(userRoleList);
        } catch (Exception e) {
            log.error("===auth.getUserRoleRelationEntitiesByRoles() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 根据用户列表查询所有用户角色实体列表
     */
    @Override
    public List<UserRolePojo> getUserRoleRelationEntitiesByUsers(CommonContext context, Set<String> users) throws AuthServiceException {
        if (users != null) {
            users.remove(null);
        }
        if (CollectionUtils.isEmpty(users)) {
            return new ArrayList<>();
        }
        try {
            List<UserRole> userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, users, AuthConstant.orgType.USER, false);
            return this.UserRole2UserRolePojo(userRoleList);
        } catch (Exception e) {
            log.error("===auth.getUserRoleRelationEntitiesByUsers() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    @Override
    public List<String> queryUsers(CommonContext context, String roleCode, Set<String> users, PageInfo pageInfo) throws AuthServiceException {
        if (users != null && users.isEmpty()) {
            return new ArrayList<>();
        }
        return this.queryMatchedUserIdPrivate(context, roleCode, null, users, pageInfo);
    }

    @Override
    public Map<String, List<RolePojo>> queryRoleListByUsers(
            CommonContext context, String roleCode, Set<String> excludeRoles, Set<String> users, PageInfo pageInfo) throws AuthServiceException {
        Map<String, RolePojo> rolePojoMap = new HashMap<>();
        Map<String, Set<String>> userRoleMap = new HashMap<>();
        Map<String, List<RolePojo>> userRolePojoMap = new HashMap<>();

        if (users != null && users.isEmpty()) {
            if (pageInfo != null) {
                pageInfo.setPageNum(1);
                pageInfo.setTotal(0);
                pageInfo.setTotalPage(0);
            }
            return userRolePojoMap;
        }

        List<String> dbUsers = this.queryMatchedUserIdPrivate(context, roleCode, excludeRoles, users, pageInfo);//结果必须根据user分页

        if (CollectionUtils.isEmpty(dbUsers)) {
            return userRolePojoMap;
        }
        dbUsers.forEach(userId -> {
            userRolePojoMap.put(userId, new ArrayList<>());
        });
        try {
            List<UserRole> userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, dbUsers, AuthConstant.orgType.USER, false);

            //查询用户角色
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                userRoleList.forEach(userRole -> {
                    if (null == userRoleMap.get(userRole.getOrgId())) {
                        userRoleMap.put(userRole.getOrgId(), new HashSet<>());
                    }
                    userRoleMap.get(userRole.getOrgId()).add(userRole.getRoleCode());
                });
            }

            //查询企业所有角色（优化-只查需要的rolePojo？）
            List<RolePojo> rolePojoList = roleService.queryRole(context, null, null, null, null);
            if (CollectionUtils.isNotEmpty(rolePojoList)) {
                rolePojoList.forEach(rolePojo -> {
                    rolePojoMap.put(rolePojo.getRoleCode(), rolePojo);
                });
            }

            //构建用户角色信息
            userRoleMap.forEach((userId, roles) -> {
                roles.forEach(role -> {
                    if (null != rolePojoMap.get(role)) {
                        userRolePojoMap.get(userId).add(rolePojoMap.get(role));
                    }
                });
            });
        } catch (Exception e) {
            log.error("===auth.queryRoleListByUsers() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return userRolePojoMap;
    }

    @Override
    public Map<String, Set<String>> queryRoleUsersByRoles(CommonContext context, Set<String> roles) throws AuthServiceException {
        Map<String, Set<String>> roleUsersMap = new HashMap<>();
        if (roles != null) {
            roles.remove(null);
        }
        if (CollectionUtils.isEmpty(roles)) {
            return roleUsersMap;
        }
        roles.forEach(role -> {
            roleUsersMap.put(role, new HashSet<>());
        });
        try {
            List<UserRole> userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), roles, null, null, Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                userRoleList.forEach(userRole -> {
                    if (roleUsersMap.get(userRole.getRoleCode()) != null) {
                        roleUsersMap.get(userRole.getRoleCode()).add(userRole.getOrgId());
                    }
                });
            }
        } catch (Exception e) {
            log.error("===auth.queryRoleUsersByRoles() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return roleUsersMap;
    }

    @Override
    public Map<String, Set<String>> queryUserRoleCodesByUsers(CommonContext context, Set<String> users) {
        Map<String, Set<String>> userRoleCodes = new HashMap<>();
        if (users != null) {
            users.remove(null);
        }
        if (CollectionUtils.isEmpty(users)) {
            return userRoleCodes;
        }
        users.forEach(user -> {
            userRoleCodes.put(user, new HashSet<>());
        });
        try {
            List<String> userList = new LinkedList<>(users);
            //            List<Set<String>> userRole = (List) cacheManager.getMultiObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, userList);
            List<Set<String>> userRole = new ArrayList<>();
            Set<String> temp;
            if (CollectionUtils.isNotEmpty(userRole)) {
                int len = userRole.size();
                Set<String> nullRoleUser = new HashSet<>();
                for (int index = 0; index < len; index++) {
                    temp = userRole.get(index);
                    if (temp == null) {
                        nullRoleUser.add(userList.get(index));
                    } else {
                        userRoleCodes.put(userList.get(index), temp);
                    }
                }
                if (CollectionUtils.isNotEmpty(nullRoleUser)) {
                    Map<String, Set<String>> dbUserRoles = this.queryUserRoleCodesByUsersFromDB(context, nullRoleUser);
                    cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE,
                            (Map) dbUserRoles);
                    //                    cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);
                    userRoleCodes.putAll(dbUserRoles);
                }
            }

        } catch (Exception e) {
            return this.queryUserRoleCodesByUsersFromDB(context, users);
        }
        return userRoleCodes;
    }

    @Override
    @Transactional
    public void updateUserRoles(CommonContext context, Set<String> users, Set<String> roles) throws AuthServiceException {
        log.info("[Request], method:{},context:{},users:{},roles:{}",
                "updateUserRoles",
                JSON.toJSONString(context),
                JSON.toJSONString(users),
                JSON.toJSONString(roles));

        if (users == null || users.contains(null) || CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (roles == null || roles.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //校验角色是否存在,是否需要更新数据库
        boolean needInsertToDBFlag = false;
        if (CollectionUtils.isNotEmpty(roles)) {
            this.rolesIsExist(context, roles);
            needInsertToDBFlag = true;
        }
        try {
            //删除所有用户的角色
            userRoleMapper.batchDel(context.getTenantId(),
                    context.getAppId(),
                    null,
                    users,
                    AuthConstant.orgType.USER,
                    context.getUserId(),
                    System.currentTimeMillis());
            Map<String, Set<String>> cacheUpdate = new HashMap<>();
            List<UserRole> userRoleList = new LinkedList<>();

            for (String userId : users) {
                this.checkUserId(userId);
                cacheUpdate.put(userId, roles);
                if (needInsertToDBFlag) {
                    roles.forEach(roleCode -> {
                        UserRole userRole = new UserRole(IdUtil.generateId(),
                                context.getTenantId(),
                                context.getAppId(),
                                userId,
                                roleCode,
                                AuthConstant.orgType.USER,
                                context.getUserId(),
                                System.currentTimeMillis(),
                                Boolean.FALSE);
                        userRoleList.add(userRole);
                    });
                }
            }

            //更新用户角色
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                //                userRoleMapper.batchInsert(userRoleList);
            }

            //更新缓存
            cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, (Map) cacheUpdate);
            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);

        } catch (Exception e) {
            log.error("===auth.updateUserRoles() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    @Override
    @Transactional
    public void updateRoleToUser(CommonContext context, Set<String> users, Set<String> roles, String defaultRole) throws AuthServiceException {
        log.info("[Request], method:{},context:{},users:{},roles:{},defaultRole:{},time:{}",
                "updateRoleToUser",
                JSON.toJSONString(context),
                JSON.toJSONString(users),
                JSON.toJSONString(roles),
                defaultRole,
                System.currentTimeMillis());

        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (roles == null || roles.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isNotEmpty(roles) && StringUtils.isBlank(defaultRole)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isNotEmpty(roles) && (!roles.contains(defaultRole))) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isNotEmpty(roles)) {
            roles.forEach(role -> {
                if (StringUtils.isBlank(role)) {
                    throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
                }
            });
        }

        this.batchCheckUserId(users);

        //校验角色是否存在,是否需要更新数据库
        boolean needInsertToDBFlag = false;
        if (CollectionUtils.isNotEmpty(roles)) {
            this.rolesIsExist(context, roles);
            needInsertToDBFlag = true;
        }

        try {
            //删除所有用户的角色
            userRoleMapper.batchDel(context.getTenantId(),
                    context.getAppId(),
                    null,
                    users,
                    AuthConstant.orgType.USER,
                    context.getUserId(),
                    System.currentTimeMillis());
            Map<String, Set<String>> cacheUpdate = new HashMap<>();
            List<UserRole> userRoleList = new LinkedList<>();

            for (String userId : users) {
                cacheUpdate.put(userId, roles);

                if (needInsertToDBFlag) {
                    roles.forEach(roleCode -> {
                        UserRole userRole = new UserRole(IdUtil.generateId(),
                                context.getTenantId(),
                                context.getAppId(),
                                userId,
                                roleCode,
                                AuthConstant.orgType.USER,
                                context.getUserId(),
                                System.currentTimeMillis(),
                                Boolean.FALSE);
                        if (roleCode.equals(defaultRole)) {
                            userRole.setDefaultRole(Boolean.TRUE);
                        } else {
                            userRole.setDefaultRole(Boolean.FALSE);
                        }
                        userRoleList.add(userRole);
                    });
                }
            }

            //更新用户角色
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                //                userRoleMapper.batchInsert(userRoleList);
            }

            //更新缓存
            cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, (Map) cacheUpdate);
            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);

        } catch (Exception e) {
            log.error("===auth.updateRoleToUser() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    /**
     * 给用户添加角色
     *
     * @param context           请求上下文
     * @param users             用户列表
     * @param roles             角色列表
     * @param defaultRole       主角色
     * @param updateDefaultRole 若user已有主角色，是否更新主角色
     */
    @Override
    @Transactional
    public void addRoleToUser(CommonContext context, Set<String> users, Set<String> roles, String defaultRole, boolean updateDefaultRole)
            throws AuthServiceException {

        log.info("[Request], method:{},context:{},users:{},roles:{},defaultRole:{},updateDefaultRole:{}",
                "addRoleToUser",
                JSON.toJSONString(context),
                JSON.toJSONString(users),
                JSON.toJSONString(roles),
                defaultRole,
                updateDefaultRole);

        //入参校验
        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(roles)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (users.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (roles.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (StringUtils.isBlank(defaultRole) || (!roles.contains(defaultRole))) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isNotEmpty(roles)) {
            roles.forEach(role -> {
                if (StringUtils.isBlank(role)) {
                    throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
                }
            });
        }

        this.batchCheckUserId(users);

        //校验角色是否存在
        this.rolesIsExist(context, roles);
        List<UserRole> userRoleList;
        try {
            //返回角色的用户
            userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, users, AuthConstant.orgType.USER, false);
        } catch (Exception e) {
            log.error("===auth.addRoleToUser() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        Map<String, Set<String>> roleUserMap = new HashMap<>();
        Set<String> updateDefaultRoleId = new HashSet<>();
        Set<String> updateNoDefaultRoleId = new HashSet<>();

        if (CollectionUtils.isNotEmpty(userRoleList)) {
            userRoleList.forEach(userRole -> {
                if (roleUserMap.get(userRole.getOrgId()) == null) {
                    roleUserMap.put(userRole.getOrgId(), new HashSet<>());
                }
                roleUserMap.get(userRole.getOrgId()).add(userRole.getRoleCode());

                if (updateDefaultRole && userRole.getDefaultRole() != null && userRole.getDefaultRole()
                        && (!defaultRole.equals(userRole.getRoleCode()))) {
                    updateNoDefaultRoleId.add(userRole.getId());
                }
                if (updateDefaultRole && (userRole.getDefaultRole() == null || (!userRole.getDefaultRole()))
                        && defaultRole.equals(userRole.getRoleCode())) {
                    updateDefaultRoleId.add(userRole.getId());
                }
            });
        }

        List<UserRole> userRoles = new LinkedList<>();
        roles.forEach(role -> {
            users.forEach(user -> {
                if (roleUserMap.get(user) == null || (!roleUserMap.get(user).contains(role))) {
                    UserRole userRole = new UserRole(IdUtil.generateId(),
                            context.getTenantId(),
                            context.getAppId(),
                            user,
                            role,
                            AuthConstant.orgType.USER,
                            context.getUserId(),
                            System.currentTimeMillis(),
                            Boolean.FALSE);

                    if ((roleUserMap.get(user) == null || updateDefaultRole) && defaultRole.equals(role)) {//user之前没有任何角色
                        userRole.setDefaultRole(Boolean.TRUE);
                    } else {
                        userRole.setDefaultRole(Boolean.FALSE);
                    }
                    userRoles.add(userRole);
                }
            });
        });

        if (updateDefaultRole && CollectionUtils.isNotEmpty(updateDefaultRoleId)) {
            this.setDefaultRoleFlag(context, updateDefaultRoleId, true);
        }
        if (updateDefaultRole && CollectionUtils.isNotEmpty(updateNoDefaultRoleId)) {
            this.setDefaultRoleFlag(context, updateNoDefaultRoleId, false);
        }

        if (CollectionUtils.isNotEmpty(userRoles)) {
            this.batchInsertUserRole(context, userRoles);
            this.addRoleToUserUpdateCache(context, roles, users);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    /**
     * 删除角色下某些user
     *
     * @param context  请求上下文
     * @param roleCode 角色
     * @param users    用户列表
     */
    @Override
    @Transactional
    public void delRoleUser(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException {
        log.info("[Request], method:{},context:{},users:{},roleCode:{}",
                "delRoleUser",
                JSON.toJSONString(context),
                JSON.toJSONString(users),
                roleCode);
        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Set<String> check = this.checkRoleUser(context, roleCode, users);//校验这里进行
        if (CollectionUtils.isNotEmpty(check)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        this.delRoleFromUserAndUpdateCache(context, roleCode, users);

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    /**
     * 校验是否有user把当前角色设为主角色，而且有其他角色
     *
     * @param context  请求上下文
     * @param roleCode 角色
     * @param users    用户列表
     */
    @Override
    public Set<String> checkRoleUser(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (users != null) {
            users.remove(null);
        }
        //校验角色是否存在
        this.rolesIsExist(context, Collections.singleton(roleCode));

        if (CollectionUtils.isEmpty(users)) {
            users = new HashSet<>(this.queryMatchedUserIdPrivate(context, roleCode, null, null, null));
        }

        List<UserRole> userRoleList;
        try {
            userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, users, AuthConstant.orgType.USER, false);
        } catch (Exception e) {
            log.error("===auth.checkRoleUser() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        if (CollectionUtils.isEmpty(userRoleList)) {
            return new HashSet<>();
        }

        Map<String, Set<String>> userRoleMap = new HashMap<>();
        Set<String> userInDefaultRole = new HashSet<>();//把校验的角色设为主角色的用户

        userRoleList.forEach(userRole -> {
            if (userRoleMap.get(userRole.getOrgId()) == null) {
                userRoleMap.put(userRole.getOrgId(), new HashSet<>());
            }
            userRoleMap.get(userRole.getOrgId()).add(userRole.getRoleCode());

            if (userRole.getDefaultRole() != null && userRole.getDefaultRole() && roleCode.equals(userRole.getRoleCode())) {
                userInDefaultRole.add(userRole.getOrgId());
            }
        });

        Set<String> res = new HashSet<>();

        userInDefaultRole.forEach(userId -> {
            if (userRoleMap.get(userId).size() > 1) {
                res.add(userId);
            }
        });

        return res;
    }

    /**
     * 把当前角色设置为users的主角色
     *
     * @param context  请求上下文
     * @param roleCode 角色
     * @param users    用户列表
     */
    @Override
    @Transactional
    public void updateUserDefaultRole(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException {
        log.info("[Request], method:{},context:{},users:{},roleCode:{}",
                "updateUserDefaultRole",
                JSON.toJSONString(context),
                JSON.toJSONString(users),
                roleCode);
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //校验角色是否存在
        this.rolesIsExist(context, Collections.singleton(roleCode));
        this.batchCheckUserId(users);
        List<UserRole> userRoleList;
        try {
            //返回角色的用户
            userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, users, AuthConstant.orgType.USER, false);
        } catch (Exception e) {
            log.error("===auth.updateUserDefaultRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        Map<String, Set<String>> roleUserMap = new HashMap<>();
        Set<String> updateDefaultRoleId = new HashSet<>();
        Set<String> updateNoDefaultRoleId = new HashSet<>();

        if (CollectionUtils.isNotEmpty(userRoleList)) {
            userRoleList.forEach(userRole -> {
                if (roleUserMap.get(userRole.getOrgId()) == null) {
                    roleUserMap.put(userRole.getOrgId(), new HashSet<>());
                }
                roleUserMap.get(userRole.getOrgId()).add(userRole.getRoleCode());

                if (userRole.getDefaultRole() != null && userRole.getDefaultRole() && (!roleCode.equals(userRole.getRoleCode()))) {
                    updateNoDefaultRoleId.add(userRole.getId());
                }
                if ((userRole.getDefaultRole() == null || (!userRole.getDefaultRole())) && roleCode.equals(userRole.getRoleCode())) {
                    updateDefaultRoleId.add(userRole.getId());
                }
            });
        }

        List<UserRole> userRoles = new LinkedList<>();
        Set<String> needAddUsers = new HashSet<>();
        users.forEach(user -> {
            if (roleUserMap.get(user) == null || (!roleUserMap.get(user).contains(roleCode))) {
                UserRole userRole = new UserRole(IdUtil.generateId(),
                        context.getTenantId(),
                        context.getAppId(),
                        user,
                        roleCode,
                        AuthConstant.orgType.USER,
                        context.getUserId(),
                        System.currentTimeMillis(),
                        Boolean.FALSE);

                userRole.setDefaultRole(Boolean.TRUE);
                userRoles.add(userRole);
                needAddUsers.add(user);
            }
        });

        if (CollectionUtils.isNotEmpty(updateDefaultRoleId)) {
            this.setDefaultRoleFlag(context, updateDefaultRoleId, true);
        }
        if (CollectionUtils.isNotEmpty(updateNoDefaultRoleId)) {
            this.setDefaultRoleFlag(context, updateNoDefaultRoleId, false);
        }

        if (CollectionUtils.isNotEmpty(userRoles)) {
            this.batchInsertUserRole(context, userRoles);
            this.addRoleToUserUpdateCache(context, Collections.singleton(roleCode), users);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(context);
    }

    /**
     * 批量新加用户角色关联关系
     *
     * @param authContext 请求上下文
     * @param pojoList    用户角色关联列表
     */
    @Override
    @Transactional
    public void batchUpdateUserRole(CommonContext authContext, List<UserRolePojo> pojoList) throws AuthServiceException {
        log.info("[Request], method:{},context:{},pojoList:{}", "batchUpdateUserRole", JSON.toJSONString(authContext), JSON.toJSONString(pojoList));

        if (CollectionUtils.isEmpty(pojoList)) {
            return;
        }

        Map<String, Set<String>> roleUserMap = new HashMap<>();
        Map<String, String> defaultRoleMap = new HashMap<>();
        List<UserRole> entityList = new ArrayList<>();
        Set<String> roleList = new HashSet<>();

        pojoList.forEach(pojo -> {
            if (StringUtils.isAnyBlank(pojo.getRoleCode(), pojo.getOrgId())) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            if (roleUserMap.get(pojo.getOrgId()) != null && roleUserMap.get(pojo.getOrgId()).contains(pojo.getRoleCode())) {//一个user下的role不能重复
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            if (pojo.getDefaultRole() != null && pojo.getDefaultRole() && defaultRoleMap.get(pojo.getOrgId()) != null) {//一个user只能有一个主角色
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }

            this.checkUserId(pojo.getOrgId());
            if (roleUserMap.get(pojo.getOrgId()) == null) {
                roleUserMap.put(pojo.getOrgId(), new HashSet<>());
            }
            roleUserMap.get(pojo.getOrgId()).add(pojo.getRoleCode());

            UserRole userRole = new UserRole(IdUtil.generateId(),
                    authContext.getTenantId(),
                    authContext.getAppId(),
                    pojo.getOrgId(),
                    pojo.getRoleCode(),
                    AuthConstant.orgType.USER,
                    authContext.getUserId(),
                    System.currentTimeMillis(),
                    Boolean.FALSE);
            if (pojo.getDefaultRole() != null && pojo.getDefaultRole()) {
                userRole.setDefaultRole(Boolean.TRUE);
                defaultRoleMap.put(pojo.getOrgId(), pojo.getRoleCode());
            } else {
                userRole.setDefaultRole(Boolean.FALSE);
            }
            entityList.add(userRole);

            roleList.add(pojo.getRoleCode());
        });

        this.rolesIsExist(authContext, roleList);
        Set<String> userSet = roleUserMap.keySet();

        userSet.forEach(user -> {
            if (defaultRoleMap.get(user) == null) {//每个user都要有主角色
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        });

        try {
            userRoleMapper.batchDel(authContext.getTenantId(),
                    authContext.getAppId(),
                    null,
                    userSet,
                    AuthConstant.orgType.USER,
                    authContext.getUserId(),
                    System.currentTimeMillis());
            //            userRoleMapper.batchInsert(entityList);

            //更新缓存
            cacheManager.putAll(authContext.getTenantId() + ":" + authContext.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE,
                    (Map) roleUserMap);
            //            cacheManager.expire(authContext.getTenantId() + ":" + authContext.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);

        } catch (Exception e) {
            log.error("===auth.batchUpdateUserRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        //删除dept有关的缓存
        this.delDeptCacheKey(authContext);
    }

    /**
     * 查询用户角色
     *
     * @param context      请求上下文
     * @param users        用户ID列表
     * @param roleCode     角色code
     * @param excludeRoles 排除的角色
     * @param pageInfo     分页
     * @return <String,UserRoleInfoPojo>
     */
    @Override
    public Map<String, List<UserRolePojo>> queryRoleInfoListByUsers(
            CommonContext context, String roleCode, Set<String> excludeRoles, Set<String> users, PageInfo pageInfo) throws AuthServiceException {
        Map<String, String> rolePojoMap = new HashMap<>();
        Map<String, List<UserRolePojo>> userRolePojoMap = new HashMap<>();

        if (users != null && users.isEmpty()) {
            if (pageInfo != null) {
                pageInfo.setPageNum(1);
                pageInfo.setTotal(0);
                pageInfo.setTotalPage(0);
            }
            return userRolePojoMap;
        }

        List<String> dbUsers = this.queryMatchedUserIdPrivate(context, roleCode, excludeRoles, users, pageInfo);//结果必须根据user分页

        if (CollectionUtils.isEmpty(dbUsers)) {
            return userRolePojoMap;
        }
        dbUsers.forEach(userId -> {
            userRolePojoMap.put(userId, new ArrayList<>());
        });
        try {
            List<UserRole> userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, dbUsers, AuthConstant.orgType.USER, false);

            //查询企业所有角色
            List<RolePojo> rolePojoList = roleService.queryRole(context, null, null, null, null);
            if (CollectionUtils.isNotEmpty(rolePojoList)) {
                rolePojoList.forEach(rolePojo -> {
                    rolePojoMap.put(rolePojo.getRoleCode(), rolePojo.getRoleName());
                });
            }

            //查询用户角色
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                userRoleList.forEach(userRole -> {
                    UserRolePojo pojo = new UserRolePojo();
                    pojo.setOrgId(userRole.getOrgId());
                    pojo.setOrgType(userRole.getOrgType());
                    pojo.setRoleCode(userRole.getRoleCode());
                    //                    pojo.setRoleName(rolePojoMap.get(userRole.getRoleCode()));
                    pojo.setDefaultRole(userRole.getDefaultRole());
                    pojo.setTenantId(context.getTenantId());
                    pojo.setAppId(context.getAppId());

                    userRolePojoMap.get(userRole.getOrgId()).add(pojo);
                });
            }
        } catch (Exception e) {
            log.error("===auth.queryRoleInfoListByUsers() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return userRolePojoMap;
    }

    /**
     * 更新用户角色关系的所属部门
     *
     * @param context  请求上下文
     * @param userId   用户ID
     * @param roleCode 角色code
     * @param deptIds  部门
     */
    @Override
    @Transactional
    public void updateUserRoleDeptId(CommonContext context, String roleCode, String userId, Set<String> deptIds) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{},userId:{},deptIds:{}",
                "updateUserRoleDeptId",
                JSON.toJSONString(context),
                roleCode,
                userId,
                JSON.toJSONString(deptIds));

        if (StringUtils.isAnyBlank(roleCode, userId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        this.rolesIsExist(context, Collections.singleton(roleCode));

        String dept = "";
        if (CollectionUtils.isNotEmpty(deptIds)) {
            StringBuilder builder = new StringBuilder();
            deptIds.forEach(deptId -> {
                if (StringUtils.isNotBlank(deptId)) {
                    builder.append(deptId).append(',');
                }
            });
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
                dept = builder.toString();
            }
        }

        int line;
        try {
            line = userRoleMapper.updateUserRoleDeptId(context.getTenantId(),
                    context.getAppId(),
                    AuthConstant.orgType.USER,
                    roleCode,
                    userId,
                    dept,
                    context.getUserId(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            log.error("updateUserRoleDeptId error:", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        if (line < 1) {//不存在这个关联
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        this.delDeptCache(context, roleCode, userId);
    }

    /**
     * 查询用户角色的所属部门
     *
     * @param context  请求上下文
     * @param userId   用户ID
     * @param roleCode 角色code
     * @return 部门set
     */
    @Override
    public Set<String> queryDeptIdsByRoleUser(CommonContext context, String roleCode, String userId) throws AuthServiceException {

        if (StringUtils.isAnyBlank(roleCode, userId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<String> roles = this.queryRoleCodeListByUserId(context);
        if (!roles.contains(roleCode)) {
            return null;
        }

        Set<String> deptIds;
        try {
            deptIds =
                    (Set<String>) cacheManager.getHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_DEPT,
                            userId + ":" + roleCode);

        } catch (Exception e) {
            return this.queryDeptIdByUserRole(context, roleCode, AuthConstant.orgType.USER, userId);
        }

        //缓存未命中,重构
        if (deptIds == null) {
            deptIds = this.queryDeptIdByUserRole(context, roleCode, AuthConstant.orgType.USER, userId);
            try {
                cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_DEPT,
                        userId + ":" + roleCode,
                        deptIds);
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_DEPT, USER_ROLE_EXPIRE_SECOND);

            } catch (Exception e) {
                return deptIds;
            }
        }
        return deptIds;
    }

    /**
     * 查询用户角色的所属部门
     *
     * @param context  请求上下文
     * @param roleCode 角色code
     * @return user、部门set
     */
    @Override
    public Map<String, Set<String>> queryUserDeptByRole(CommonContext context, String roleCode, PageInfo pageInfo) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Map<String, Set<String>> res = new HashMap<>();

        List<UserRole> entityList;

        if (pageInfo != null) {
            try {
                int total = userRoleMapper.queryEntityByUserRoleCount(context.getTenantId(),
                        context.getAppId(),
                        Collections.singleton(roleCode),
                        AuthConstant.orgType.USER,
                        null);
                pageInfo.setTotal(total);
                int totalPage = total / pageInfo.getPageSize();
                if (total % pageInfo.getPageSize() > 0) {
                    totalPage = totalPage + 1;
                }
                pageInfo.setTotalPage(totalPage);
                if (pageInfo.getPageNum() > totalPage) {
                    pageInfo.setPageNum(totalPage);
                }
                int start = 0;
                if (pageInfo.getPageNum() > 1) {
                    start = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
                }
                entityList = userRoleMapper.queryEntityByUserRole(context.getTenantId(),
                        context.getAppId(),
                        Collections.singleton(roleCode),
                        AuthConstant.orgType.USER,
                        null,
                        start,
                        pageInfo.getPageSize());
            } catch (Exception e) {
                log.error("queryDeptIdByUserRole error:", e);
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
            }
        } else {
            //无分页请求,返回所有数据
            try {
                entityList = (List<UserRole>) cacheManager.getHashObject(
                        context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_ROLE_INFO,
                        roleCode);
            } catch (Exception e) {
                log.error("redis error", e);
                entityList = this.queryEntityByUserRole(context, Collections.singleton(roleCode), AuthConstant.orgType.USER, null);
            }

            if (entityList == null) {
                entityList = this.queryEntityByUserRole(context, Collections.singleton(roleCode), AuthConstant.orgType.USER, null);
                try {
                    cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_ROLE_INFO,
                            roleCode,
                            entityList);
                    //                    cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_ROLE_INFO, USER_ROLE_EXPIRE_SECOND);
                } catch (Exception e) {
                    log.error("roleInfo putHashObject error:", e);
                }
            }
        }

        if (CollectionUtils.isEmpty(entityList)) {
            return res;
        }
        entityList.forEach(entity -> {
            res.put(entity.getOrgId(), this.getDeptSetByString(entity.getDeptId()));
        });

        return res;
    }

    /**
     * 添加用户角色关系的所属部门
     *
     * @param context  请求上下文
     * @param userId   用户ID
     * @param roleCode 角色code
     * @param deptIds  部门
     */
    @Override
    @Transactional
    public void addUserRoleDeptId(CommonContext context, String roleCode, String userId, Set<String> deptIds) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{},userId:{},deptIds:{}",
                "addUserRoleDeptId",
                JSON.toJSONString(context),
                roleCode,
                userId,
                JSON.toJSONString(deptIds));

        if (StringUtils.isAnyBlank(roleCode, userId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        this.rolesIsExist(context, Collections.singleton(roleCode));

        Set<String> existData = this.queryDeptIdsByRoleUser(context, roleCode, userId);
        if (existData == null) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        deptIds.addAll(existData);

        this.updateUserRoleDeptId(context, roleCode, userId, deptIds);

    }

    /**
     * 根据角色、部门查询用户
     *
     * @param context  请求上下文
     * @param roleCode 角色code
     * @param deptIds  部门
     */
    @Override
    public Set<String> queryUsersByRoleDept(CommonContext context, String roleCode, Set<String> deptIds) throws AuthServiceException {

        if (StringUtils.isBlank(roleCode) || CollectionUtils.isEmpty(deptIds)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        Set<String> users = new HashSet<>();
        Map<String, Set<String>> userDeptMap = this.queryUserDeptByRole(context, roleCode, null);

        userDeptMap.forEach((user, depts) -> {
            for (String dept : depts) {
                if (deptIds.contains(dept)) {
                    users.add(user);
                    break;
                }
            }
        });

        return users;
    }

    /**
     * 根据一个部门、多个角色过滤人
     *
     * @param context 请求上下文
     * @param deptId  部门id
     * @param roles   角色code
     * @return 人员set
     */
    public Set<String> queryUserIdsByRoleAndDepts(CommonContext context, Set<String> roles, String deptId) throws AuthServiceException {

        if (CollectionUtils.isEmpty(roles) || StringUtils.isBlank(deptId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        Set<String> users = new HashSet<>();
        try {
            users = userRoleMapper.queryUsersByRoleDept(context.getTenantId(), context.getAppId(), roles, AuthConstant.orgType.USER, deptId);
        } catch (Exception e) {
            log.error("queryUsersByRoleDept error:", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        return users;
    }

    /**
     * 查询某个企业、某角色下，deptId不为空的user数量
     *
     * @param context  请求上下文
     * @param roleCode 角色code
     * @return 人员数量
     */
    @Override
    public int queryDeptUserNumByRole(CommonContext context, String roleCode) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        try {
            return userRoleMapper.queryValidUserNumByRole(context.getTenantId(), context.getAppId(), roleCode);
        } catch (Exception e) {
            log.error("===auth.queryDeptUserNumByRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * db查询用户的角色
     *
     * @param context 上下文
     * @param users   用户
     * @return link
     */
    private Map<String, Set<String>> queryUserRoleCodesByUsersFromDB(CommonContext context, Set<String> users) {
        Map<String, Set<String>> userRoleMap = new HashMap<>();
        if (users != null) {
            users.remove(null);
        }
        if (CollectionUtils.isEmpty(users)) {
            return userRoleMap;
        }
        users.forEach(user -> {
            userRoleMap.put(user, new HashSet<>());
        });
        try {
            List<UserRole> userRoleList =
                    userRoleMapper.queryUserRoleProvider(context.getTenantId(), context.getAppId(), null, users, null, Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                userRoleList.forEach(userRole -> {
                    if (userRoleMap.get(userRole.getOrgId()) != null) {
                        userRoleMap.get(userRole.getOrgId()).add(userRole.getRoleCode());
                    }
                });
            }
        } catch (Exception e) {
            log.error("===auth.queryUserRoleCodesByUsersFromDB() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return userRoleMap;
    }

    /**
     * 校验用户ID是否合法
     */
    private void checkUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void batchCheckUserId(Set<String> userIdList) {
        if (CollectionUtils.isNotEmpty(userIdList)) {
            userIdList.forEach(this::checkUserId);
        }
    }

    /**
     * UserRole entity to UserRolePojo
     */
    private List<UserRolePojo> UserRole2UserRolePojo(List<UserRole> userRoleList) {
        List<UserRolePojo> userRolePojoList = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            try {
                for (UserRole userRole : userRoleList) {
                    UserRolePojo userRolePojo = new UserRolePojo();
                    PropertyUtils.copyProperties(userRolePojo, userRole);
                    userRolePojoList.add(userRolePojo);
                }
            } catch (Exception e) {
                log.error("UserRole2UserRolePojo userRole convert to userRolePojo error {}", e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
            }
        }
        return userRolePojoList;
    }

    /**
     * 检测角色是否存在
     *
     * @param authContext 请求上下文
     * @param roles       角色列表
     */
    private void rolesIsExist(CommonContext authContext, Set<String> roles) throws AuthServiceException {
        if (CollectionUtils.isNotEmpty(roles)) {
            if (roleService.roleCodeOrRoleNameExists(authContext, roles, null) != roles.size()) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
    }

    /**
     * 清除用户角色缓存
     */
    private void delUserRoleCache(CommonContext context) {
        try {
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE);
        } catch (Exception e) {
            log.error("===auth.delUserRoleCache() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 分页查询用户
     *
     * @param context  请求上下文
     * @param roleCode 角色编号
     * @param users    需要匹配的用户
     * @param pageInfo 分页
     */
    @SuppressWarnings("unchecked")
    private List<String> queryMatchedUserIdPrivate(
            CommonContext context, String roleCode, Set<String> excludeRoles, Set<String> users, PageInfo pageInfo) {
        List<String> dbUsers;
        try {
            Integer start = null;
            if (pageInfo != null) {
                int total = userRoleMapper.queryOrgIdsOrRolesCount(context.getTenantId(),
                        context.getAppId(),
                        roleCode,
                        users,
                        AuthConstant.orgType.USER,
                        false,
                        "orgId",
                        excludeRoles);
                pageInfo.setTotal(total);
                int totalPage = total / pageInfo.getPageSize();
                if (total % pageInfo.getPageSize() > 0) {
                    totalPage = totalPage + 1;
                }
                pageInfo.setTotalPage(totalPage);
                if (pageInfo.getPageNum() > totalPage) {
                    pageInfo.setPageNum(totalPage);
                }

                if (total == 0) {
                    return new ArrayList<>();
                }

                start = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
            }
            dbUsers = userRoleMapper.queryOrgIdsOrRoles(context.getTenantId(),
                    context.getAppId(),
                    roleCode,
                    users,
                    AuthConstant.orgType.USER,
                    false,
                    "orgId",
                    excludeRoles,
                    pageInfo,
                    start); //无分页请求,返回所有数据
        } catch (Exception e) {
            log.error("===auth.queryMatchedUserIdPrivate() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return dbUsers;
    }

    /**
     * 角色转换为返回的pojo
     */
    private List<RolePojo> roleConvertToRolePojo(List<Role> roleList) {
        List<RolePojo> rolePojoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleList)) {
            try {
                for (Role role : roleList) {
                    RolePojo rolePojo = new RolePojo();
                    PropertyUtils.copyProperties(rolePojo, role);
                    rolePojoList.add(rolePojo);
                }
            } catch (Exception e) {
                log.warn("roleConvertToRolePojo  role convert to rolePojo error roleList{}" + JSON.toJSONString(roleList), e);
            }
        }
        return rolePojoList;
    }

    @SuppressWarnings("unchecked")
    private List<String> queryRoleCodeListByUserIdFromDB(CommonContext context) {
        try {
            return userRoleMapper.queryRoleCodeListByUserId(context.getTenantId(),
                    context.getAppId(),
                    context.getUserId(),
                    AuthConstant.orgType.USER);
        } catch (Exception e) {
            log.error("===auth.queryRoleCodeListByUserIdFromDB() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private void setDefaultRoleFlag(CommonContext context, Set<String> idList, boolean flag) {
        try {
            userRoleMapper.setDefaultRoleFlag(context.getTenantId(),
                    context.getAppId(),
                    idList,
                    AuthConstant.orgType.USER,
                    flag,
                    context.getUserId(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.setDefaultRoleFlag() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private void delRoleFromUserAndUpdateCache(CommonContext context, String roleCode, Set<String> users) {
        try {
            userRoleMapper.batchDel(context.getTenantId(),
                    context.getAppId(),
                    roleCode,
                    users,
                    AuthConstant.orgType.USER,
                    context.getUserId(),
                    System.currentTimeMillis());
            //清理缓存
            List<String> userList = new LinkedList<>(users);
            //            List<Set<String>> userRoleList = (List) cacheManager.getMultiObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, userList);
            List<Set<String>> userRoleList = new ArrayList<>();
            Set<String> temp = null;
            Map<String, Set<String>> needUpdate = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                int len = userRoleList.size();
                for (int index = 0; index < len; index++) {
                    temp = userRoleList.get(index);
                    if (CollectionUtils.isNotEmpty(temp)) {
                        temp.remove(roleCode);
                        needUpdate.put(userList.get(index), temp);
                    }
                }
            }
            if (!needUpdate.isEmpty()) {
                cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, (Map) needUpdate);
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);
            }

        } catch (Exception e) {
            log.error("===auth.delRoleFromUserAndUpdateCache() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private void batchInsertUserRole(CommonContext context, List<UserRole> entityList) {
        try {
            //            userRoleMapper.batchInsert(entityList);
        } catch (Exception e) {
            log.error("===auth.batchInsertUserRole() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private void addRoleToUserUpdateCache(CommonContext context, Set<String> roles, Set<String> users) {
        try {
            //更新缓存
            List<String> userList = new LinkedList<>(users);
            //            List<Set<String>> userRole = (List) cacheManager.getMultiObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, userList);
            List<Set<String>> userRole = new ArrayList<>();
            Set<String> temp;
            Map<String, Set<String>> needUpdate = new HashMap<>();
            int len = userRole.size();
            for (int index = 0; index < len; index++) {
                temp = userRole.get(index);
                if (temp != null) {
                    temp.addAll(roles);
                    needUpdate.put(userList.get(index), temp);
                }
            }
            if (!needUpdate.isEmpty()) {
                cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, (Map) needUpdate);
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE, USER_ROLE_EXPIRE_SECOND);
            }

        } catch (Exception e) {
            log.error("===auth.addRoleToUserUpdateCache() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private Set<String> queryDeptIdByUserRole(CommonContext context, String roleCode, Integer orgType, String orgId) {
        List<UserRole> entityList = this.queryEntityByUserRole(context, Collections.singleton(roleCode), orgType, orgId);

        if (CollectionUtils.isEmpty(entityList)) {
            return new HashSet<>();
        }
        String dept = entityList.get(0).getDeptId();
        return this.getDeptSetByString(dept);
    }

    private Set<String> getDeptSetByString(String dept) {
        if (StringUtils.isBlank(dept)) {
            return new HashSet<>();
        }
        String[] array = dept.split(",");
        return new HashSet<>(Arrays.asList(array));
    }

    private void delDeptCache(CommonContext context, String roleCode, String userId) {
        try {
            //            cacheManager.delObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_DEPT, userId + ":" + roleCode);
            //            cacheManager.delObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_ROLE_INFO, roleCode);
        } catch (Exception e) {
            log.error("delDeptCache error:", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private List<UserRole> queryEntityByUserRole(CommonContext context, Set<String> roleCodes, Integer orgType, String orgId) {
        List<UserRole> res;
        try {
            res = userRoleMapper.queryEntityByUserRole(context.getTenantId(), context.getAppId(), roleCodes, orgType, orgId, null, null);
        } catch (Exception e) {
            log.error("queryDeptIdByUserRole error:", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        return res;
    }

    private void delDeptCacheKey(CommonContext context) {
        try {
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_DEPT);
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_ROLE_INFO);
        } catch (Exception e) {
            log.error("delDeptCacheKey error:", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

}
