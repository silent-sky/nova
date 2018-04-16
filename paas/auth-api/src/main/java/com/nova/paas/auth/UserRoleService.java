package com.nova.paas.auth;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户和角色的关联服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface UserRoleService {

    /**
     * 查询某个用户绑定的角色编码列表
     *
     * @param context 请求上下文
     * @return 角色编码列表
     */
    List<String> queryRoleCodeListByUserId(CommonContext context) throws AuthServiceException;

    /**
     * 查询角色下绑定的用户
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     */
    List<String> queryUserListByRoleCode(CommonContext context, String roleCode, PageInfo pageInfo) throws AuthServiceException;

    /**
     * 查询用户绑定的角色
     *
     * @param context 请求上下文
     * @param userId  用户ID
     */
    List<RolePojo> queryRoleListByUserId(CommonContext context, String userId) throws AuthServiceException;

    /**
     * 角色中删除用户
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @param users    用户ID列表
     */
    void delUserFromRole(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException;

    /**
     * 角色中批量添加用户
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @param users    用户列表
     */
    void addUserToRole(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException;

    /**
     * 更新用户绑定的角色
     *
     * @param context 请求上下文
     * @param userId  用户ID
     * @param roles   角色编码列表
     */
    void updateUserRole(CommonContext context, String userId, Set<String> roles) throws AuthServiceException;

    /**
     * 批量给用户添加多个角色
     *
     * @param context 请求上下文
     * @param users   用户id列表
     * @param roles   角色编码列表
     */
    void batchAddUserToRole(CommonContext context, Set<String> users, Set<String> roles) throws AuthServiceException;

    /**
     * 查询所有用户角色实体列表
     */
    List<UserRolePojo> getAllEmployeeRoleRelationEntities(CommonContext context) throws AuthServiceException;

    /**
     * 根据角色列表查询所有用户角色实体列表
     */
    List<UserRolePojo> getUserRoleRelationEntitiesByRoles(CommonContext context, Set<String> roles) throws AuthServiceException;

    /**
     * 根据用户列表查询所有用户角色实体列表
     */
    List<UserRolePojo> getUserRoleRelationEntitiesByUsers(CommonContext context, Set<String> users) throws AuthServiceException;

    /**
     * 查询用户列表
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @param users    用户ID列表
     * @param pageInfo 分页信息
     */
    List<String> queryUsers(CommonContext context, String roleCode, Set<String> users, PageInfo pageInfo) throws AuthServiceException;

    /**
     * 查询用户角色
     *
     * @param context      请求上下文
     * @param users        用户ID列表
     * @param roleCode     角色code
     * @param excludeRoles 排除的角色
     * @param pageInfo     分页
     */
    Map<String, List<RolePojo>> queryRoleListByUsers(
            CommonContext context, String roleCode, Set<String> excludeRoles, Set<String> users, PageInfo pageInfo) throws AuthServiceException;

    /**
     * 查询角色对应的用户
     *
     * @param context 请求上下文
     * @param roles   角色id
     */
    Map<String, Set<String>> queryRoleUsersByRoles(CommonContext context, Set<String> roles) throws AuthServiceException;

    /**
     * 批量查询用户的角色
     */
    Map<String, Set<String>> queryUserRoleCodesByUsers(CommonContext context, Set<String> users) throws AuthServiceException;

    /**
     * 更新用户角色
     *
     * @param context 请求上下文
     * @param users   用户列表
     * @param roles   角色列表
     */
    void updateUserRoles(CommonContext context, Set<String> users, Set<String> roles) throws AuthServiceException;

    /**
     * 批量更新用户角色
     *
     * @param context     请求上下文
     * @param users       用户列表
     * @param roles       角色列表
     * @param defaultRole 主角色
     */
    void updateRoleToUser(CommonContext context, Set<String> users, Set<String> roles, String defaultRole) throws AuthServiceException;

    /**
     * 给用户添加角色
     *
     * @param context           请求上下文
     * @param users             用户列表
     * @param roles             角色列表
     * @param defaultRole       主角色
     * @param updateDefaultRole 若user已有主角色，是否更新主角色
     */
    void addRoleToUser(CommonContext context, Set<String> users, Set<String> roles, String defaultRole, boolean updateDefaultRole)
            throws AuthServiceException;

    /**
     * 删除角色下某些user
     *
     * @param context  请求上下文
     * @param users    用户列表
     * @param roleCode 角色
     */
    void delRoleUser(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException;

    /**
     * 校验是否有user把当前角色设为主角色，而且有其他角色
     *
     * @param context  请求上下文
     * @param users    用户列表
     * @param roleCode 角色
     */
    Set<String> checkRoleUser(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException;

    /**
     * 把当前角色设置为users的主角色
     *
     * @param context  请求上下文
     * @param users    用户列表
     * @param roleCode 角色
     */
    void updateUserDefaultRole(CommonContext context, String roleCode, Set<String> users) throws AuthServiceException;

    /**
     * 批量更新用户角色关联关系
     *
     * @param context  请求上下文
     * @param pojoList 用户角色关联列表
     */
    void batchUpdateUserRole(CommonContext context, List<UserRolePojo> pojoList) throws AuthServiceException;

    /**
     * 查询用户角色
     *
     * @param context      请求上下文
     * @param users        用户ID列表
     * @param roleCode     角色code
     * @param excludeRoles 排除的角色
     * @param pageInfo     分页
     */
    Map<String, List<UserRolePojo>> queryRoleInfoListByUsers(
            CommonContext context, String roleCode, Set<String> excludeRoles, Set<String> users, PageInfo pageInfo) throws AuthServiceException;

    /**
     * 更新用户角色关系的所属部门
     *
     * @param context  请求上下文
     * @param userId   用户ID
     * @param roleCode 角色code
     * @param deptIds  部门
     */
    void updateUserRoleDeptId(CommonContext context, String roleCode, String userId, Set<String> deptIds) throws AuthServiceException;

    /**
     * 查询用户角色的所属部门
     *
     * @param context  请求上下文
     * @param userId   用户ID
     * @param roleCode 角色code
     * @return 部门set
     */
    Set<String> queryDeptIdsByRoleUser(CommonContext context, String roleCode, String userId) throws AuthServiceException;

    /**
     * 查询用户角色的所属部门
     *
     * @param context  请求上下文
     * @param roleCode 角色code
     * @return user、部门set
     */
    Map<String, Set<String>> queryUserDeptByRole(CommonContext context, String roleCode, PageInfo pageInfo) throws AuthServiceException;

    /**
     * 添加用户角色关系的所属部门
     *
     * @param context  请求上下文
     * @param userId   用户ID
     * @param roleCode 角色code
     * @param deptIds  部门
     */
    void addUserRoleDeptId(CommonContext context, String roleCode, String userId, Set<String> deptIds) throws AuthServiceException;

    /**
     * 根据角色、部门查询用户
     *
     * @param context  请求上下文
     * @param roleCode 角色code
     * @param deptIds  部门
     */
    Set<String> queryUsersByRoleDept(CommonContext context, String roleCode, Set<String> deptIds) throws AuthServiceException;

    /**
     * 根据一个部门、多个角色过滤人
     *
     * @param context 请求上下文
     * @param deptId  部门id
     * @param roles   角色code
     * @return 人员set
     */
    Set<String> queryUserIdsByRoleAndDepts(CommonContext context, Set<String> roles, String deptId) throws AuthServiceException;

    /**
     * 查询某个企业、某角色下，deptId不为空的user数量
     *
     * @param context  请求上下文
     * @param roleCode 角色code
     * @return 人员数量
     */
    int queryDeptUserNumByRole(CommonContext context, String roleCode) throws AuthServiceException;
}
