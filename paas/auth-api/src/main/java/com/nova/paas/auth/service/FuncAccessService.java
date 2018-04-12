package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色、字段权限的关联服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface FuncAccessService {

    /**
     * 查询用户的功能权限
     *
     * @param context 请求上下文
     * @return 功能权限列表
     * @author penghj
     */
    List<FunctionPojo> queryFuncAccessByUser(CommonContext context) throws AuthServiceException;

    /**
     * 查询角色的功能权限
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @return 功能权限列表
     */
    List<FunctionPojo> queryFuncAccessByRole(CommonContext context, String roleCode) throws AuthServiceException;

    /**
     * 查询角色的功能权限(唯一标识)
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @return 功能唯一标识列表
     */
    Set<String> queryFuncAccessCodeByRole(CommonContext context, String roleCode) throws AuthServiceException;

    /**
     * 批量查询角色的功能权限
     *
     * @param context 请求上下文
     * @param roles   角色编码列表
     */
    Map<String, Set<String>> queryFuncAccessByRoles(CommonContext context, Set<String> roles) throws AuthServiceException;

    /**
     * 查询哪些角色拥有某功能
     *
     * @param context      请求上下文
     * @param funcCodeList 功能唯一标识列表
     */
    Map<String, Set<String>> queryFuncAccessByFuncCode(CommonContext context, Set<String> funcCodeList) throws AuthServiceException;

    /**
     * 校验用户功能权限
     *
     * @param context     请求上下文
     * @param funcCodeSet 权限列表
     * @return 权限校验结果MAP
     */
    Map<String, Boolean> userFuncPermissionCheck(CommonContext context, Set<String> funcCodeSet) throws AuthServiceException;

    /**
     * 角色功能权限更新
     *
     * @param context     请求上下文
     * @param roleCode    角色编码
     * @param funcCodeSet 功能权限列表
     */
    void updateRoleFuncPermission(CommonContext context, String roleCode, Set<String> funcCodeSet) throws AuthServiceException;

    /**
     * 查询角色的功能权限
     *
     * @param context 请求上下文
     * @param roles   角色编码列表
     */
    Map<String, Set<String>> queryRolesFuncAccess(CommonContext context, List<String> roles) throws AuthServiceException;

    /**
     * 删除角色功能权限
     */
    void delRoleFuncPermiss(CommonContext context, String roleCode) throws AuthServiceException;

    /**
     * 角色功能权限更新
     *
     * @param context        请求上下文
     * @param roleCode       角色编码
     * @param addFuncCodeSet 增加的功能权限列表
     * @param delFuncCodeSet 删除的功能权限列表
     */
    void updateRoleModifiedFuncPermission(CommonContext context, String roleCode, Set<String> addFuncCodeSet, Set<String> delFuncCodeSet)
            throws AuthServiceException;

    /**
     * 多个角色、针对一个funcCode的处理
     *
     * @param context    请求上下文
     * @param funcCode   角色编码
     * @param addRoleSet 增加该功能的角色列表
     * @param delRoleSet 删除该功能的角色列表
     */
    void updateFuncRolePermission(CommonContext context, String funcCode, Set<String> addRoleSet, Set<String> delRoleSet) throws AuthServiceException;

}
