package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色、字段权限的关联服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface FieldAccessService {

    /**
     * 查询某个角色对某个对象的字段权限
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @param entityId 实体ID
     * @return 字段权限列表
     */
    Map<String, Integer> queryRoleEntityPermission(CommonContext context, String roleCode, String entityId) throws AuthServiceException;

    /**
     * 查询用户的字段权限
     *
     * @param context  请求上下文
     * @param entityId 实体ID
     * @return 字段权限列表
     */
    Map<String, Integer> queryUserEntityPermission(CommonContext context, String entityId) throws AuthServiceException;

    /**
     * 更新角色字段权限
     *
     * @param context            请求上下文
     * @param roleCode           角色编码
     * @param entityId           实体ID
     * @param fieldPermissionMap 字段权限MAP
     */
    void updateEntityFieldPermission(CommonContext context, String roleCode, String entityId, Map<String, Integer> fieldPermissionMap)
            throws AuthServiceException;

    /**
     * 更新对象字段的角色权限
     *
     * @param context          请求上下文
     * @param entityId         对象
     * @param fieldId          字段
     * @param roleFieldPermiss 角色权限
     */
    void updateMultiRoleFieldPermiss(CommonContext context, String entityId, String fieldId, Map<String, Integer> roleFieldPermiss)
            throws AuthServiceException;

    /**
     * 查询对象字段权限
     *
     * @param context  请求上下文
     * @param roles    角色Id列表
     * @param entityId 对象ID
     * @param fieldId  字段ID
     * @return 角色对象的字段权限
     */
    Map<String, Integer> multiRoleFieldPermiss(CommonContext context, Set<String> roles, String entityId, String fieldId) throws AuthServiceException;

    /**
     * 查询角色对象的字段权限
     *
     * @param context  请求上下文
     * @param roles    角色列表
     * @param entityId 对象实体
     */
    Map<String, Map<String, Integer>> queryRolesEntityFieldPermiss(CommonContext context, List<String> roles, String entityId)
            throws AuthServiceException;

    /**
     * 删除角色的对象字段权限
     *
     * @param context  请求上下文
     * @param roleCode 角色唯一标识
     * @param entityId 对象
     */
    void delRoleFieldPermiss(CommonContext context, String roleCode, String entityId) throws AuthServiceException;

    /**
     * 查询用户对对象的字段权限
     *
     * @param context 请求上下文
     * @param entitys 对象实体列表
     */
    Map<String, Map<String, Integer>> userEntitysFieldPermiss(CommonContext context, Set<String> entitys) throws AuthServiceException;
}
