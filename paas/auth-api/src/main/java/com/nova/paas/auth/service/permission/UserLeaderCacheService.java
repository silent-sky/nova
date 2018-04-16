package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.UserLeaderCachePojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:28
 */
public interface UserLeaderCacheService {

    /**
     * 企业缓存更新
     */
    void tenantUserLeaderCacheReset(CommonContext context) throws AuthServiceException;

    /**
     * 更新用户leader
     */
    void userLeaderUpdate(
            CommonContext context, Set<String> users) throws AuthServiceException;

    /**
     * 用户停用
     *
     * @param users 用户列表
     */
    void userStop(CommonContext context, Set<String> users) throws AuthServiceException;

    /**
     * 用户启用
     */
    void userStart(CommonContext context, Set<String> users) throws AuthServiceException;

    /**
     * 初始化用户汇报对象缓存
     */
    void initUserLeaderCache(CommonContext context, Set<String> tenants, int currentPage) throws AuthServiceException;

    /**
     * 查询用户汇报对象缓存
     */
    List<UserLeaderCachePojo> userLeaderCache(CommonContext context, Set<String> users, Set<String> leaders, Integer relationType, PageInfo pageInfo)
            throws AuthServiceException;

}
