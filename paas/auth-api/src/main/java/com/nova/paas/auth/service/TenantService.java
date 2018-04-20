package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.FunctionAccessPojo;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 租户初始化接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface TenantService {

    /**
     * 清空企业数据
     *
     * @param context 清空企业数据
     */
    void clear(CommonContext context);

    /**
     * 重构企业缓存
     */
    void cacheInit(CommonContext context);

    /**
     * 企业缓存清空
     */
    void cacheClear(CommonContext context);

    /**
     * 批量添加功能(无校验)
     */
    void batchAddFuncInit(CommonContext context, List<FunctionPojo> functionPojos);

    /**
     * 批量添加角色(无校验)
     */
    void batchAddRoleInit(CommonContext context, List<RolePojo> rolePojos);

    /**
     * 批量添加角色功能权限(无校验)
     */
    void batchAddFuncAccessInit(CommonContext context, List<FunctionAccessPojo> functionAccessPojos);

    /**
     * 批量添加用户角色
     */
    void batchAddUserToRoleInit(CommonContext context, Map<String, Set<String>> roleUsers);

    /**
     * 企业数据复制
     *
     * @param context               请求上下文
     * @param fromEnterpriseAccount 源企业EI
     * @param toEnterpriseAccount   目标企业EI
     * @param filterMetaData        是否过滤掉自定义对象数据
     */
    void enterpriseCopy(CommonContext context, String fromEnterpriseAccount, String toEnterpriseAccount, boolean filterMetaData)
            throws AuthServiceException;

    /**
     * 批量添加用户角色-新
     */
    void batchAddUserRoleInit(CommonContext context, List<UserRolePojo> roleUsers);
}
