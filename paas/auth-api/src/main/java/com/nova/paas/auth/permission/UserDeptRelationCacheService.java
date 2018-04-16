package com.nova.paas.auth.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.UserDeptRelationCachePojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:28
 */
public interface UserDeptRelationCacheService {

    /**
     * 用户负责的部门下的用户
     *
     * @param context 请求上下文
     * @return userId
     */
    Set<String> userResponsibleDeptUsers(CommonContext context) throws AuthServiceException;

    /**
     * 组织架构权限缓存重置
     *
     * @param context context
     */
    void userDeptRelationCacheReset(CommonContext context) throws AuthServiceException;

    /**
     * 初始化部门数据
     *
     * @param context context
     * @param deptIds 部门id
     */
    void initDeptCache(CommonContext context, Set<String> deptIds) throws AuthServiceException;

    /**
     * 删除
     *
     * @param context              请求上下文
     * @param depts                部门列表
     * @param users                用户列表
     * @param userDeptRelationFlag 用户和部门标识
     */
    void delUserDeptRelationCache(
            CommonContext context, Set<String> depts, Set<String> users, Integer userDeptRelationFlag) throws AuthServiceException;

    /**
     * 添加部门下的员工
     *
     * @param context              请求上下文
     * @param deptUserMap          map
     * @param userDeptRelationFlag 用户和部门标识
     */
    void addUserDeptCache(
            CommonContext context, Map<String, Set<String>> deptUserMap, Integer userDeptRelationFlag) throws AuthServiceException;

    /**
     * 删除部门的员工
     *
     * @param context              请求上下文
     * @param userDeptMap          map
     * @param userDeptRelationFlag 用户和部门标识
     */
    void deleteUserDeptCache(
            CommonContext context, Map<String, Set<String>> userDeptMap, Integer userDeptRelationFlag) throws AuthServiceException;

    /**
     * 更新部门负责人cache
     *
     * @param context 请求上下文
     * @param deptIds 部门列表
     */
    void updateLeaderCache(CommonContext context, Set<String> deptIds) throws AuthServiceException;

    /**
     * 初始化企业组织架构缓存数据
     *
     * @param tenants 企业列表
     */
    void initTenantDeptUserRelationCache(Set<String> tenants, int currentPage) throws AuthServiceException;

    /**
     * 查询用户部门关系缓存
     */
    List<UserDeptRelationCachePojo> userDeptRelationCache(
            CommonContext context, Set<String> depts, Set<String> users, Integer relationType, PageInfo pageInfo) throws AuthServiceException;

}
