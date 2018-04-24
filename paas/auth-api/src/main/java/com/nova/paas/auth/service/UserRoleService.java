package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Set;

/**
 * 用户和角色的关联服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface UserRoleService {

    /**
     * 角色中批量添加用户
     *
     * @param context 请求上下文
     * @param roleId  角色
     * @param users   用户列表
     */
    void addUserToRole(CommonContext context, String roleId, Set<String> users) throws AuthServiceException;

    /**
     * 删除角色下某些user
     *
     * @param context 请求上下文
     * @param userIds 用户列表
     * @param roleId  角色
     */
    void delRoleUserByUsers(CommonContext context, String roleId, Set<String> userIds) throws AuthServiceException;

    void delRoleUserByRoles(CommonContext context, String userId, Set<String> roleIds) throws AuthServiceException;

    /**
     * 更新用户绑定的角色
     *
     * @param context 请求上下文
     * @param userId  用户ID
     * @param roleIds 角色编码列表
     */
    void updateUserRole(CommonContext context, String userId, Set<String> roleIds) throws AuthServiceException;

    List<UserRolePojo> getUserRoleRelationByRole(CommonContext context, String roleId, Integer targetType) throws AuthServiceException;

    List<UserRolePojo> getUserRoleRelationByUser(CommonContext context, String targetId) throws AuthServiceException;

}
