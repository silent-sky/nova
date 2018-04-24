package com.nova.paas.auth.service.impl;

import com.google.common.collect.Lists;
import com.nova.paas.auth.entity.UserRole;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.UserRoleMapper;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.auth.service.RoleService;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    RoleService roleService;

    @Transactional
    @Override
    public void addUserToRole(CommonContext context, String roleId, Set<String> users) throws AuthServiceException {
        //校验
        if (StringUtils.isBlank(roleId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(users)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (users.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<UserRole> userRoleList = Lists.newArrayList();
        for (String userId : users) {
            UserRole userRole = new UserRole().builder()
                    .id(IdUtil.generateId())
                    .tenantId(context.getTenantId())
                    .roleId(roleId)
                    .targetId(userId)
                    .targetType(AuthConstant.TargetType.USER)
                    .modifiedBy(context.getUserId())
                    .modifiedAt(System.currentTimeMillis())
                    .build();
            userRoleList.add(userRole);
        }

        userRoleMapper.batchInsert(userRoleList);

    }

    /**
     * 删除角色下某些user
     *
     * @param context 请求上下文
     * @param roleId  角色
     * @param userIds 用户列表
     */
    @Override
    @Transactional
    public void delRoleUserByUsers(CommonContext context, String roleId, Set<String> userIds) throws AuthServiceException {

        userRoleMapper.batchDeleteByUsers(context.getTenantId(),
                roleId,
                userIds,
                AuthConstant.TargetType.USER,
                context.getUserId(),
                System.currentTimeMillis());

    }

    @Override
    @Transactional
    public void delRoleUserByRoles(CommonContext context, String userId, Set<String> roleIds) throws AuthServiceException {

        userRoleMapper.batchDeleteByRoles(context.getTenantId(),
                roleIds,
                userId,
                AuthConstant.TargetType.USER,
                context.getUserId(),
                System.currentTimeMillis());

    }

    @Override
    @Transactional
    public void updateUserRole(CommonContext context, String userId, Set<String> roleIds) throws AuthServiceException {
        if (roleIds == null || roleIds.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        try {
            //删除用户以前的角色,重新赋值新角色
            this.delRoleUserByRoles(context, userId, roleIds);

            List<UserRole> userRoleList = Lists.newArrayList();
            for (String roleId : roleIds) {
                UserRole userRole = new UserRole().builder()
                        .id(IdUtil.generateId())
                        .tenantId(context.getTenantId())
                        .roleId(roleId)
                        .targetId(userId)
                        .targetType(AuthConstant.TargetType.USER)
                        .modifiedBy(context.getUserId())
                        .modifiedAt(System.currentTimeMillis())
                        .build();
                userRoleList.add(userRole);
            }

            userRoleMapper.batchInsert(userRoleList);
        } catch (Exception e) {
            log.error("===auth.updateUserRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

    }

    @Override
    public List<UserRolePojo> getUserRoleRelationByRole(CommonContext context, String roleId, Integer targetType) throws AuthServiceException {
        List<UserRole> list = userRoleMapper.findUserByRole(context.getTenantId(), roleId, targetType);
        List<UserRolePojo> pojoList = this.convertUserRoleToPojos(list);
        return pojoList;
    }

    @Override
    public List<UserRolePojo> getUserRoleRelationByUser(CommonContext context, String targetId) throws AuthServiceException {
        List<UserRole> list = userRoleMapper.findRoleByUser(context.getTenantId(), targetId);
        List<UserRolePojo> pojoList = this.convertUserRoleToPojos(list);
        return pojoList;
    }

    private List<UserRolePojo> convertUserRoleToPojos(List<UserRole> userRoleList) {
        List<UserRolePojo> pojoList = Lists.newArrayList();
        for (UserRole userRole : userRoleList) {
            UserRolePojo pojo = new UserRolePojo();
            try {
                PropertyUtils.copyProperties(pojo, userRole);
            } catch (Exception e) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            pojoList.add(pojo);
        }
        return pojoList;
    }

}
