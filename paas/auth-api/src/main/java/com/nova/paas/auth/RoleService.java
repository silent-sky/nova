package com.nova.paas.auth;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Set;

/**
 * 角色的操作接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface RoleService {

    /**
     * 根据角色ID查询角色
     *
     * @param context  请求上下文
     * @param roleCode 角色码
     * @return 角色pojo
     */
    RolePojo queryRoleByCode(CommonContext context, String roleCode) throws AuthServiceException;

    /**
     * 角色查询
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @param roleName 角色名
     * @param roleType 角色类型
     * @param pageInfo 分页信息
     * @return 角色列表
     */
    List<RolePojo> queryRole(CommonContext context, String roleCode, String roleName, Integer roleType, PageInfo pageInfo)
            throws AuthServiceException;

    /**
     * 角色查询(支持多roleCode查询)
     */
    List<RolePojo> queryRole2(CommonContext context, Set<String> roleCodes, String roleName, Integer roleType, PageInfo pageInfo)
            throws AuthServiceException;

    /**
     * 创建角色
     *
     * @param context  请求上下文
     * @param rolePojo 角色pojo
     */
    String createRole(CommonContext context, RolePojo rolePojo) throws AuthServiceException;

    /**
     * 更新角色信息
     *
     * @param context  请求上下文
     * @param rolePojo 角色Pojo对象
     */
    void updateRole(CommonContext context, RolePojo rolePojo) throws AuthServiceException;

    /**
     * 删除角色
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     */
    void delRole(CommonContext context, String roleCode) throws AuthServiceException;

    /**
     * 角色权限复制
     *
     * @param context        请求上下文
     * @param sourceRoleCode 源角色
     * @param destRoleCode   目标角色
     */
    void rolePermissCopy(CommonContext context, String sourceRoleCode, String destRoleCode) throws AuthServiceException;

    /**
     * 校验角色是否已存在
     *
     * @param context   请求上下文
     * @param roleNames 角色名
     * @param roleCodes 角色唯一标识
     */
    long roleCodeOrRoleNameExists(CommonContext context, Set<String> roleCodes, Set<String> roleNames) throws AuthServiceException;

    /**
     * 删除自定义角色
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     */
    void delDefinedRole(CommonContext context, String roleCode) throws AuthServiceException;

    /**
     * 角色模糊搜索(支持多roleCode查询)
     */
    List<RolePojo> queryMatchRole(CommonContext context, Set<String> roleCodes, String key, Integer roleType, PageInfo pageInfo)
            throws AuthServiceException;

}
