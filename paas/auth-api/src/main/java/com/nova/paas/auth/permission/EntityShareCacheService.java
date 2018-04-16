package com.nova.paas.auth.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.EntityShareCachePojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:28
 */
public interface EntityShareCacheService {

    /**
     * 删除共享规则缓存
     *
     * @param context        请求上下文
     * @param entityShareIds 共享规则id列表
     */
    void delEntityShareCache(CommonContext context, Set<String> entityShareIds) throws AuthServiceException;

    /**
     * 删除共享规则缓存
     *
     * @param context 请求上下文
     * @param entitys 实体ID列表
     */
    void delEntityShareCacheByEntitys(CommonContext context, Set<String> entitys) throws AuthServiceException;

    /**
     * 企业共享缓存数据重置
     *
     * @param context        请求上下文
     * @param entityShareIds 共享规则id列表
     */
    void entityShareCacheReset(CommonContext context, Set<String> entityShareIds) throws AuthServiceException;

    /**
     * 企业共享规则重构
     *
     * @param context 请求上下文
     */
    void tenantEntityShareCacheReset(CommonContext context) throws AuthServiceException;

    /**
     * 增加人的部门-更新缓存数据
     *
     * @param context 请求上下文
     * @param userSet 员工
     * @param deptSet 部门
     */
    void addUserToDeptCache(
            CommonContext context, Set<String> deptSet, Set<String> userSet, boolean noRoot) throws AuthServiceException;

    /**
     * 更改部门上级部门————把移走的部门下的员工从原上级部门中移除，只处理作为共享方的数据
     *
     * @param context 请求上下文
     * @param userSet 员工
     * @param deptId  部门
     */
    void delUserFromDeptCache(CommonContext context, String deptId, Set<String> userSet) throws AuthServiceException;

    /**
     * 删除人的部门-更新缓存数据
     *
     * @param context 请求上下文
     * @param userId  员工
     */
    void delRuleCacheBySet(CommonContext context, Set<String> deptSet, String userId) throws AuthServiceException;

    /**
     * 应用共享缓存初始化
     *
     * @param appId   应用id
     * @param tenants 企业账号
     */
    void initAppEntityShareCache(String appId, Set<String> tenants, int currentPage) throws AuthServiceException;

    /**
     * 应用共享缓存初始化
     *
     * @param context    cotext
     * @param ruleIds    规则id
     * @param permission 权限
     */
    void updatePermissionByRuleId(
            CommonContext context, Set<String> ruleIds, Integer permission) throws AuthServiceException;

    /**
     * 共享规则缓存查询服务
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     */
    List<EntityShareCachePojo> entityShareCache(
            CommonContext context, String entityId, String shareId, String shareUser, String receiveUser, PageInfo pageInfo)
            throws AuthServiceException;

    /**
     * 添加组中成员
     *
     * @param context 请求上下文
     * @param userSet 员工
     * @param groupId 组
     */
    void addUserToGroupCache(CommonContext context, String groupId, Set<String> userSet) throws AuthServiceException;

    /**
     * 删除组中成员
     *
     * @param context 请求上下文
     * @param userSet 员工
     * @param groupId 组
     */
    void delUserFromGroupCache(CommonContext context, String groupId, Set<String> userSet) throws AuthServiceException;

    /**
     * 共享规则中添加角色成员
     *
     * @param context 请求上下文
     * @param roles   角色
     * @param users   用户
     */
    void addUserToRoleCache(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException;

    /**
     * 共享规则中删除角色成员
     *
     * @param context 请求上下文
     * @param roles   角色
     * @param users   用户
     */
    void delUserFromRoleCache(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException;

    /**
     * 更新用户角色
     *
     * @param context 请求上下文
     * @param roles   角色
     * @param users   用户
     */
    void updateUserRoles(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException;

}
