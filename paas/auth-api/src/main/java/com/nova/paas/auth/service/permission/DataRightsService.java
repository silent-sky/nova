package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.common.pojo.CommonContext;

import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 17:19
 */
public interface DataRightsService {

    /**
     * 查询用户对某个对象记录的权限
     *
     * @param context                         请求上下文
     * @param entityId                        对象实体
     * @param dataOwners                      记录的拥有者
     * @param userParentDeptCascade           用户所属部门向上级联
     * @param userDeputyDept                  包含用户的副部门
     * @param deptUsersCascade                部门下的用户是否递归级联
     * @param userSubordinatesCascade         用户的下属是否递归级联
     * @param userResponsibleDeptUsersCascade 用户负责的部门下的用户是否递归级联
     */
    Map<String, Integer> dataPermission(
            CommonContext context,
            String entityId,
            Set<String> dataOwners,
            boolean userParentDeptCascade,
            boolean userDeputyDept,
            boolean deptUsersCascade,
            boolean userSubordinatesCascade,
            boolean userResponsibleDeptUsersCascade) throws AuthServiceException;

    /**
     * @param context                         请求上下文
     * @param entityId                        对象实体Id
     * @param userSubordinatesCascade         级联用户下属
     * @param deptConvertToUser               部门解析到具体的人
     * @param userParentDeptCascade           用户所属部门向上级联
     * @param userResponsibleDeptUsersCascade 用户负责的部门是否包含递归
     * @param userDeputyDept                  包含用户的副部门
     */
    Map<Integer, Set<String>> userAccessData(
            CommonContext context,
            String entityId,
            boolean deptConvertToUser,
            boolean userParentDeptCascade,
            boolean userSubordinatesCascade,
            boolean userResponsibleDeptUsersCascade,
            boolean userDeputyDept) throws AuthServiceException;

    /**
     * 共享给我的
     *
     * @param context               请求上下文
     * @param entityId              对象实体
     * @param deptConvertToUser     部门解析到具体的人
     * @param userParentDeptCascade 用户所属部门向上级联
     * @param userDeputyDept        包含用户的副部门
     */
    Map<Integer, Set<String>> userSharedData(
            CommonContext context, String entityId, boolean deptConvertToUser, boolean userParentDeptCascade, boolean userDeputyDept)
            throws AuthServiceException;

    /**
     * 用户的下属
     *
     * @param context        请求赛文
     * @param userSubCascade 用户下属 true 递归 false不递归
     */
    Set<String> userSubordinates(CommonContext context, boolean userSubCascade) throws AuthServiceException;

    /**
     * 用户负责的部门下的用户
     *
     * @param context     请求上下文
     * @param deptCascade 是否级联
     */
    Set<String> userResponsibleDeptUsers(CommonContext context, boolean deptCascade) throws AuthServiceException;

    /**
     * 删除对象的数据权限
     *
     * @param context 请求上下文
     * @param entitys 对象实体列表
     */
    void delDataRights(CommonContext context, Set<String> entitys) throws AuthServiceException;

    /**
     * 查询userId能够查看entityId的哪些记录
     */
    //    Set<String> queryReadableObjectIds(CommonContext context, String entityId, String userId) throws AuthServiceException;

    /**
     * 查看userId是否对所有的objectIds都有编辑权限
     */
    //    boolean queryWhetherEditable(CommonContext context, String entityId, Set<String> objectIds, String userId) throws AuthServiceException;

    /**
     * 查询用户对对象记录的权限
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param objects  对象记录列表
     */
    Map<String, Integer> entityObjectsPermission(
            CommonContext context, String entityId, Set<String> objects, String ownerRoleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException;

    Map<String, Integer> objectsPermissionCalculate(
            CommonContext context, String entityId, Set<String> objects, String ownerRoleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException;

    /**
     * 数据权限
     *
     * @param context             请求上下文
     * @param entityId            实体ID
     * @param sceneType           业务场景 (共享给我的,我下属的,我负责的部门的)
     * @param roleType            角色
     * @param cascadeDept         我负责的部门是否级联
     * @param cascadeSubordinates 我的下属是否级联
     */
    String dataRightsSql(
            CommonContext context, String entityId, String sceneType, String roleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException;

}
