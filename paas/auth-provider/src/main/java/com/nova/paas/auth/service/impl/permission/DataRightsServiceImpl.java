package com.nova.paas.auth.service.impl.permission;

import com.nova.paas.auth.entity.permission.EntityShareCache;
import com.nova.paas.auth.entity.permission.Team;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityFieldShareReceiveMapper;
import com.nova.paas.auth.mapper.permission.EntityShareCacheMapper;
import com.nova.paas.auth.mapper.permission.TeamMapper;
import com.nova.paas.auth.mapper.permission.UserDeptRelationCacheMapper;
import com.nova.paas.auth.mapper.permission.UserLeaderCacheMapper;
import com.nova.paas.auth.pojo.permission.EntityFieldShareReceivePojo;
import com.nova.paas.auth.pojo.permission.EntityOpennessPojo;
import com.nova.paas.auth.pojo.permission.EntitySharePojo;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.permission.DataRightsService;
import com.nova.paas.auth.service.permission.EntityFieldShareService;
import com.nova.paas.auth.service.permission.EntityOpennessService;
import com.nova.paas.auth.service.permission.EntityShareService;
import com.nova.paas.auth.service.permission.TeamService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.SetUtil;
import com.nova.paas.org.service.GroupMemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 17:19
 */
@Slf4j
@Service
public class DataRightsServiceImpl implements DataRightsService {

    @Inject
    private GroupMemService groupMemService;
    //    @Inject
    //    private OrganizationService organizationService;
    @Inject
    private EntityShareService entityShareService;
    @Inject
    private EntityFieldShareService entityFieldShareService;
    @Inject
    private EntityOpennessService entityOpennessService;
    @Inject
    private TeamService teamService;
    @Inject
    private UserRoleService userRoleService;
    @Inject
    private TeamMapper teamMapper;
    @Inject
    private EntityShareCacheMapper entityShareCacheMapper;
    @Inject
    private UserDeptRelationCacheMapper userDeptRelationCacheMapper;
    @Inject
    private UserLeaderCacheMapper userLeaderCacheMapper;
    @Inject
    private EntityFieldShareReceiveMapper entityFieldShareReceiveMapper;

    private final static Integer TEAM_MEMBER_FLAG = -1;

    private final static String SQL_UNION = " union ";

    /**
     * @param context                         请求上下文
     * @param entityId                        对象实体Id
     * @param userSubordinatesCascade         级联用户下属
     * @param deptConvertToUser               部门解析到具体的人
     * @param userParentDeptCascade           用户所属部门向上级联
     * @param userResponsibleDeptUsersCascade 用户负责的部门是否包含递归
     * @param userDeputyDept                  包含用户的副部门
     */
    public Map<Integer, Set<String>> userAccessData(
            CommonContext context,
            String entityId,
            boolean deptConvertToUser,
            boolean userParentDeptCascade,
            boolean userSubordinatesCascade,
            boolean userResponsibleDeptUsersCascade,
            boolean userDeputyDept) throws AuthServiceException {

        Map<Integer, Set<String>> dataRights = new HashMap<>();
        dataRights.put(PermissionConstant.DataRightsOrgType.USER, new HashSet<>());
        dataRights.put(PermissionConstant.DataRightsOrgType.DEPT, new HashSet<>());
        dataRights.put(PermissionConstant.DataRightsOrgType.GROUP, new HashSet<>());
        dataRights.put(TEAM_MEMBER_FLAG, new HashSet<>());
        if (StringUtils.isBlank(entityId)) {
            return dataRights;
        }

        Integer scope = this.queryEntityOpennessScope(context, entityId);

        //公开到部门,查看所属部门的数据
        if (scope == null || scope == PermissionConstant.EntityOpennessScope.PRIVATE) {
            ;
        } else if (scope == PermissionConstant.EntityOpennessScope.PUBLIC_DEPT) {
            Set<String> depts = this.userDept(context, context.getUserId(), false, userDeputyDept);
            if (CollectionUtils.isNotEmpty(depts)) {
                if (deptConvertToUser) {
                    dataRights.get(PermissionConstant.DataRightsOrgType.USER).addAll(this.deptUsers(context, depts, false));
                } else {
                    dataRights.get(PermissionConstant.DataRightsOrgType.DEPT).addAll(depts);
                }
            }
        } else if (scope == PermissionConstant.EntityOpennessScope.PUBLIC_ALL) {
            //全公司公开,查看所有数据
            dataRights.put(PermissionConstant.DataRightsOrgType.USER, null);
            dataRights.put(PermissionConstant.DataRightsOrgType.DEPT, null);
            dataRights.put(PermissionConstant.DataRightsOrgType.GROUP, null);
            dataRights.put(TEAM_MEMBER_FLAG, null);
            return dataRights;
        }

        //查询共享给我的(共享给下属的上级看不到)
        Map<Integer, Set<String>> shareToMe = this.userSharedData(context, entityId, deptConvertToUser, userParentDeptCascade, userDeputyDept);
        dataRights.get(PermissionConstant.DataRightsOrgType.USER).addAll(shareToMe.get(PermissionConstant.DataRightsOrgType.USER));
        dataRights.get(PermissionConstant.DataRightsOrgType.DEPT).addAll(shareToMe.get(PermissionConstant.DataRightsOrgType.DEPT));

        //我负责部门的
        Set<String> userResponsibleDeptUsers = this.userResponsibleDeptUsers(context, userResponsibleDeptUsersCascade);
        dataRights.get(PermissionConstant.DataRightsOrgType.USER).addAll(userResponsibleDeptUsers);
        dataRights.get(TEAM_MEMBER_FLAG).addAll(userResponsibleDeptUsers);

        //owner
        dataRights.get(PermissionConstant.DataRightsOrgType.USER).add(context.getUserId());
        dataRights.get(TEAM_MEMBER_FLAG).add(context.getUserId());

        //我下属
        Set<String> subordinates = this.userSubordinates(context, userSubordinatesCascade);
        dataRights.get(PermissionConstant.DataRightsOrgType.USER).addAll(subordinates);
        dataRights.get(TEAM_MEMBER_FLAG).addAll(subordinates);

        return dataRights;
    }

    /**
     * 共享给我的
     *
     * @param context               请求上下文
     * @param entityId              对象实体
     * @param deptConvertToUser     部门解析到具体的人
     * @param userParentDeptCascade 用户所属部门向上级联
     * @param userDeputyDept        包含用户的副部门
     */
    public Map<Integer, Set<String>> userSharedData(
            CommonContext context, String entityId, boolean deptConvertToUser, boolean userParentDeptCascade, boolean userDeputyDept)
            throws AuthServiceException {

        Map<Integer, Set<String>> dataRights = new HashMap<>();
        dataRights.put(PermissionConstant.DataRightsOrgType.USER, new HashSet<>());
        dataRights.put(PermissionConstant.DataRightsOrgType.DEPT, new HashSet<>());
        dataRights.put(PermissionConstant.DataRightsOrgType.GROUP, new HashSet<>());
        if (StringUtils.isBlank(entityId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //user group
        Set<String> groups = this.userGroup(context, Collections.singletonList(context.getUserId()));

        //user department
        Set<String> depts = this.userDept(context, context.getUserId(), userParentDeptCascade, userDeputyDept);

        //share rule
        Map<Integer, Set<String>> receives = new HashMap<>();
        receives.put(PermissionConstant.EntityShareType.USER, Collections.singleton(context.getUserId()));
        receives.put(PermissionConstant.EntityShareType.GROUP, groups);
        receives.put(PermissionConstant.EntityShareType.DEPT, depts);
        List<EntitySharePojo> entityShareList =
                entityShareService.queryEntityShareByReceives(context, entityId, receives, PermissionConstant.EntityShareStatusType.OPEN);
        groups.clear();
        depts.clear();
        Set<String> users = new HashSet<>();
        if (CollectionUtils.isNotEmpty(entityShareList)) {
            Integer shareType;
            String shareId;
            for (EntitySharePojo entitySharePojo : entityShareList) {
                shareType = entitySharePojo.getShareType();
                shareId = entitySharePojo.getShareId();
                if (shareType == PermissionConstant.EntityShareType.GROUP) {
                    groups.add(shareId);
                } else if (shareType == PermissionConstant.EntityShareType.DEPT) {
                    depts.add(shareId);
                } else {
                    users.add(shareId);
                }
            }
        }

        //group convert to user
        if (CollectionUtils.isNotEmpty(groups)) {
            users.addAll(this.groupMember(context, groups));
        }

        //department users
        if (CollectionUtils.isNotEmpty(depts)) {
            if (deptConvertToUser) {
                users.addAll(this.deptUsers(context, depts, true));
            } else {
                dataRights.put(PermissionConstant.DataRightsOrgType.DEPT, depts);
            }
        }
        dataRights.put(PermissionConstant.DataRightsOrgType.USER, users);
        return dataRights;
    }

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
    public Map<String, Integer> dataPermission(
            CommonContext context,
            String entityId,
            Set<String> dataOwners,
            boolean userParentDeptCascade,
            boolean userDeputyDept,
            boolean deptUsersCascade,
            boolean userSubordinatesCascade,
            boolean userResponsibleDeptUsersCascade) throws AuthServiceException {

        SetUtil.removeBlankElement(dataOwners);
        if (StringUtils.isBlank(entityId) || CollectionUtils.isEmpty(dataOwners)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Map<String, Integer> ownersPermission = new HashMap<>();
        Set<String> dataOwnersCopy = new HashSet<>(); //存储没有读写权限的owner
        dataOwners.forEach(dataOwner -> {
            ownersPermission.put(dataOwner, null);
            dataOwnersCopy.add(dataOwner);
        });

        //我负责的
        if (dataOwners.contains(context.getUserId())) {
            ownersPermission.put(context.getUserId(), PermissionConstant.EntityOpennessPermiss.READ_AND_WRITE);
            dataOwnersCopy.remove(context.getUserId());
            if (dataOwners.size() == 1) {
                return ownersPermission;
            }
        }

        //对象级权限
        entityOpennessPermission(context, entityId, dataOwners, userDeputyDept, dataOwnersCopy, ownersPermission);

        //我下属负责的
        if (CollectionUtils.isNotEmpty(dataOwnersCopy)) {
            Set<String> userSub = this.userSubordinates(context, userSubordinatesCascade);
            Iterator<String> dataOwnersCopyIter = dataOwnersCopy.iterator();
            while (dataOwnersCopyIter.hasNext()) {
                String dataOwner = dataOwnersCopyIter.next();
                if (userSub.contains(dataOwner)) {
                    ownersPermission.put(dataOwner, PermissionConstant.EntityOpennessPermiss.READ_AND_WRITE);
                    dataOwnersCopyIter.remove();
                }
            }
        }

        //共享给我的
        if (CollectionUtils.isNotEmpty(dataOwnersCopy)) {

            Map<Integer, Map<Integer, Set<String>>> sharePermission = getEntityShareMap(context, entityId, userParentDeptCascade, userDeputyDept);
            Set<String> groups = new HashSet<>();
            Set<String> depts = new HashSet<>();
            Set<String> readAndWritePermissionUsers = new HashSet<>();
            Set<String> readPermissionUsers = new HashSet<>();

            //读写权限
            Map<Integer, Set<String>> readAndWritePermission = sharePermission.get(PermissionConstant.EntitySharePermissType.READ_AND_WRITE);
            analysisShareId(groups, depts, readAndWritePermissionUsers, readAndWritePermission);

            //只读权限
            Map<Integer, Set<String>> readOnlyPermission = sharePermission.get(PermissionConstant.EntitySharePermissType.READ_ONLY);
            analysisShareId(groups, depts, readPermissionUsers, readOnlyPermission);

            //权限匹配
            analysisPermission(context,
                    dataOwners,
                    ownersPermission,
                    dataOwnersCopy,
                    groups,
                    depts,
                    readAndWritePermissionUsers,
                    readPermissionUsers,
                    readAndWritePermission,
                    readOnlyPermission);
        }

        //我负责的部门
        if (CollectionUtils.isNotEmpty(dataOwnersCopy)) {
            Set<String> users = this.userResponsibleDeptUsers(context, userResponsibleDeptUsersCascade);
            Iterator<String> dataOwnerCopyIter = dataOwnersCopy.iterator();
            while (dataOwnerCopyIter.hasNext()) {
                String userId = dataOwnerCopyIter.next();
                if (users.contains(userId)) {
                    ownersPermission.put(userId, PermissionConstant.EntitySharePermissType.READ_AND_WRITE);
                }
            }
        }
        return ownersPermission;
    }

    /**
     * 用户的下属
     *
     * @param context        请求赛文
     * @param userSubCascade 用户下属 true 递归 false不递归
     */
    public Set<String> userSubordinates(CommonContext context, boolean userSubCascade) throws AuthServiceException {
        Set<String> users = new HashSet<>();
        //        List<UserPojo> userInfos = organizationService.queryCascadeUserBySupervisor(this.dataRightsContextToOrgContext(context), context.getUserId(), userSubCascade);
        //        if (CollectionUtils.isNotEmpty(userInfos)) {
        //            userInfos.forEach(userInfo -> {
        //                if (userInfo != null) {
        //                    users.add(userInfo.getId());
        //                }
        //            });
        //        }
        return users;
    }

    /**
     * 用户负责的部门下的用户
     *
     * @param context     请求上下文
     * @param deptCascade 是否级联
     */
    public Set<String> userResponsibleDeptUsers(CommonContext context, boolean deptCascade) throws AuthServiceException {
        //        List<UserPojo> users = organizationService.queryCascadeUserByLeader(this.dataRightsContextToOrgContext(context), context.getUserId(), deptCascade);
        Set<String> userSet = new HashSet<>();
        //        if (CollectionUtils.isNotEmpty(users)) {
        //            users.forEach(userInfo -> {
        //                userSet.add(userInfo.getId());
        //            });
        //        }
        return userSet;
    }

    /**
     * 删除对象的数据权限
     *
     * @param context 请求上下文
     * @param entitys 对象实体列表
     */
    @Transactional
    public void delDataRights(CommonContext context, Set<String> entitys) throws AuthServiceException {
        SetUtil.removeBlankElement(entitys);
        if (CollectionUtils.isNotEmpty(entitys)) {
            //基础数据权限
            entityOpennessService.delEntityOpenness(context, entitys);
            //共享规则
            entityShareService.delEntityShareByEntitys(context, entitys);
            //相关团队
            //teamService.delTenantEntitys(context, entitys);
        }
    }

    /**
     * 查询用户所属的用户组
     *
     * @param context 请求上下文
     * @param users   用户列表
     * @returns
     */
    private Set<String> userGroup(CommonContext context, List<String> users) {
        List<String> groupList = null;
        //        List<String> groupList = groupMemService.queryGroupByUserIds(context, false, true, 0, users, null);
        if (groupList == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(groupList);
        }
    }

    /**
     * 查询用户组下的成员
     *
     * @param context 请求上下文
     * @param groups  用户组
     */
    private Set<String> groupMember(CommonContext context, Set<String> groups) {
        Set<String> members = new HashSet<>();
        //        Map<String, Set<String>> groupMembers = groupMemService.queryGroupMembers(context, new ArrayList<>(groups), false, true, 0);
        //        if (MapUtils.isNotEmpty(groupMembers)) {
        //            groupMembers.forEach((group, memb) -> {
        //                members.addAll(memb);
        //            });
        //        }
        return members;
    }

    /**
     * 查询用户组下的成员
     *
     * @param context 请求上下文
     * @param groups  用户组
     */
    private Map<String, Set<String>> groupMemberMap(CommonContext context, Set<String> groups) {
        Map<String, Set<String>> groupMemberMap = null;
        //        Map<String, Set<String>> groupMemberMap = groupMemService.queryGroupMembers(context, new ArrayList<>(groups), false, true, 0);
        return groupMemberMap;
    }

    /**
     * 查询用户所属部门(一级主副部门)
     */
    private Set<String> userDept(CommonContext context, String user, boolean cascade, boolean userDeputyDept) {
        Set<String> depts = new HashSet<>();
        //        List<DeptPojo> deptInfoList = organizationService.getDeptsByUserId(context, context.getUserId());
        //        if (CollectionUtils.isNotEmpty(deptInfoList)) {
        //            deptInfoList.forEach(deptInfo -> {
        //                if (deptInfo != null) {
        //                    depts.add(deptInfo.getId());
        //                }
        //            });
        //        }
        return depts;
    }

    /**
     * 部门底下的用户
     */
    private Set<String> deptUsers(CommonContext context, Set<String> depts, boolean cascade) {
        Set<String> users = new HashSet<>();
        //        Map<String, List<UserPojo>> deptUsers = organizationService.getEmployeesByDeptIdList(context, new ArrayList<>(depts));
        //        if (MapUtils.isNotEmpty(deptUsers)) {
        //            deptUsers.forEach((dept, userInfos) -> {
        //                if (dept != null && CollectionUtils.isNotEmpty(userInfos)) {
        //                    userInfos.forEach(userInfo -> {
        //                        users.add(userInfo.getId());
        //                    });
        //                }
        //            });
        //        }
        return users;
    }

    /**
     * 部门底下的用户
     */
    private Map<String, Set<String>> deptUsersMap(CommonContext context, Set<String> depts, boolean cascade) {
        Map<String, Set<String>> deptUsersMap = new HashMap<>();
        //        Map<String, List<UserPojo>> deptUsers = organizationService.getEmployeesByDeptIdList(context, new ArrayList<>(depts));
        //        if (MapUtils.isNotEmpty(deptUsers)) {
        //            deptUsers.forEach((dept, userInfos) -> {
        //                if (dept != null && CollectionUtils.isNotEmpty(userInfos)) {
        //                    deptUsersMap.put(dept, new HashSet<>());
        //                    userInfos.forEach(userInfo -> {
        //                        deptUsersMap.get(dept).add(userInfo.getId());
        //                    });
        //                }
        //            });
        //        }
        return deptUsersMap;
    }

    private Integer queryEntityOpennessScope(CommonContext context, String entityId) throws AuthServiceException {
        return entityOpennessService.queryEntityOpennessScopeByEntity(context, entityId);
    }

    private Map<Integer, Map<Integer, Set<String>>> getEntityShareMap(
            CommonContext context, String entityId, boolean userParentDeptCascade, boolean userDeputyDept) throws AuthServiceException {

        Map<Integer, Map<Integer, Set<String>>> sharePermission = new HashMap<>();
        //user group
        Set<String> groups = this.userGroup(context, Collections.singletonList(context.getUserId()));

        //user department
        Set<String> depts = this.userDept(context, context.getUserId(), userParentDeptCascade, userDeputyDept);

        //share rule
        Map<Integer, Set<String>> receives = new HashMap<>();
        receives.put(PermissionConstant.EntityShareType.USER, Collections.singleton(context.getUserId()));
        receives.put(PermissionConstant.EntityShareType.GROUP, groups);
        receives.put(PermissionConstant.EntityShareType.DEPT, depts);

        List<EntitySharePojo> entityShareList =
                entityShareService.queryEntityShareByReceives(context, entityId, receives, PermissionConstant.EntityShareStatusType.OPEN);
        if (CollectionUtils.isNotEmpty(entityShareList)) {
            Integer shareType;
            String shareId;
            Integer permission;
            for (EntitySharePojo entitySharePojo : entityShareList) {
                permission = entitySharePojo.getPermission();
                shareId = entitySharePojo.getShareId();
                shareType = entitySharePojo.getShareType();
                if (sharePermission.get(permission) == null) {
                    sharePermission.put(permission, new HashMap<>());
                }
                if (sharePermission.get(permission).get(shareType) == null) {
                    sharePermission.get(permission).put(shareType, new HashSet<>());
                }
                sharePermission.get(permission).get(shareType).add(shareId);
            }
        }
        return sharePermission;
    }

    private void entityOpennessPermission(
            CommonContext context,
            String entityId,
            Set<String> dataOwners,
            boolean userDeputyDept,
            Set<String> dataOwnersCopy,
            Map<String, Integer> ownersPermission) throws AuthServiceException {
        EntityOpennessPojo entityOpennessPojo = this.queryEntityOpenness(context, entityId); //update by penghj 2017/12/11 redis
        Integer scope = entityOpennessPojo.getScope();
        Integer permission = entityOpennessPojo.getPermission();
        if (scope == null || permission == null) {
            return;
        }

        //公开读写删
        if (scope == PermissionConstant.EntityOpennessScope.PUBLIC_ALL) {
            dataOwners.forEach(dataOwner -> {
                ownersPermission.put(dataOwner, permission);
            });
            if (permission == PermissionConstant.EntityOpennessPermiss.READ_AND_WRITE) {
                dataOwnersCopy.clear();
                return;
            }
        } else if (scope == PermissionConstant.EntityOpennessScope.PUBLIC_DEPT) {
            Set<String> depts = this.userDept(context, context.getUserId(), false, userDeputyDept);
            Set<String> users = new HashSet<>();
            if (CollectionUtils.isNotEmpty(depts)) {
                users = this.deptUsers(context, depts, false);
            }
            users.forEach(userId -> {
                if (dataOwners.contains(userId)) {
                    ownersPermission.put(userId, permission);
                }
            });
            if (permission == PermissionConstant.EntityOpennessPermiss.READ_AND_WRITE) {
                dataOwnersCopy.removeAll(users);
            }
        }
    }

    private void analysisPermission(
            CommonContext context,
            Set<String> dataOwners,
            Map<String, Integer> ownersPermission,
            Set<String> dataOwnersCopy,
            Set<String> groups,
            Set<String> depts,
            Set<String> readAndWritePermissionUsers,
            Set<String> readPermissionUsers,
            Map<Integer, Set<String>> readAndWritePermission,
            Map<Integer, Set<String>> readOnlyPermission) {
        if (CollectionUtils.isNotEmpty(groups)) {
            Map<String, Set<String>> groupUsersMap = this.groupMemberMap(context, groups);
            analysisGroupsOrDeptUsers(readAndWritePermissionUsers, readAndWritePermission, groupUsersMap, PermissionConstant.EntityShareType.GROUP);
            analysisGroupsOrDeptUsers(readPermissionUsers, readOnlyPermission, groupUsersMap, PermissionConstant.EntityShareType.GROUP);
        }

        if (CollectionUtils.isNotEmpty(depts)) {
            Map<String, Set<String>> deptUsersMap = this.deptUsersMap(context, depts, true);
            analysisGroupsOrDeptUsers(readPermissionUsers, readOnlyPermission, deptUsersMap, PermissionConstant.EntityShareType.DEPT);
            analysisGroupsOrDeptUsers(readAndWritePermissionUsers, readAndWritePermission, deptUsersMap, PermissionConstant.EntityShareType.DEPT);
        }

        if (CollectionUtils.isNotEmpty(readAndWritePermissionUsers)) {
            Iterator<String> dataOwnersCopyIter = dataOwnersCopy.iterator();
            while (dataOwnersCopyIter.hasNext()) {
                String dataOwner = dataOwnersCopyIter.next();
                if (readAndWritePermissionUsers.contains(dataOwner)) {
                    ownersPermission.put(dataOwner, PermissionConstant.EntityOpennessPermiss.READ_AND_WRITE);
                    dataOwnersCopyIter.remove();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(readPermissionUsers) && CollectionUtils.isNotEmpty(dataOwnersCopy)) {
            dataOwnersCopy.forEach(owner -> {
                if (readPermissionUsers.contains(owner)) {
                    ownersPermission.put(owner, PermissionConstant.EntitySharePermissType.READ_ONLY);
                }
            });
        }
    }

    private void analysisGroupsOrDeptUsers(
            Set<String> permissionUsers, Map<Integer, Set<String>> permissionMap, Map<String, Set<String>> groupUsersMap, Integer shareType) {
        Set<String> groupSet;
        if (MapUtils.isNotEmpty(permissionMap)) {
            groupSet = permissionMap.get(shareType);
            if (CollectionUtils.isNotEmpty(groupSet)) {
                groupSet.forEach(group -> {
                    if (groupUsersMap.get(group) != null) {
                        permissionUsers.addAll(groupUsersMap.get(group));
                    }
                });
            }
        }
    }

    private void analysisShareId(Set<String> groups, Set<String> depts, Set<String> permissionUsers, Map<Integer, Set<String>> shareIdsMap) {
        if (MapUtils.isNotEmpty(shareIdsMap)) {
            shareIdsMap.forEach((shareType, shares) -> {
                if (shareType == PermissionConstant.EntityShareType.GROUP) {
                    groups.addAll(shareIdsMap.get(PermissionConstant.EntityShareType.GROUP));
                } else if (shareType == PermissionConstant.EntityShareType.DEPT) {
                    depts.addAll(shareIdsMap.get(PermissionConstant.EntityShareType.DEPT));
                } else {
                    permissionUsers.addAll(shareIdsMap.get(PermissionConstant.EntityShareType.USER));
                }
            });
        }
    }

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
    public String dataRightsSql(
            CommonContext context, String entityId, String sceneType, String roleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException {
        if (StringUtils.isAnyBlank(entityId, sceneType)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //全部
        if (sceneType.equals(PermissionConstant.DataRightsSceneType.ALL)) {
            return this.userAccessData(context, entityId, roleType, cascadeDept, cascadeSubordinates);
        }
        //下属
        if (sceneType.equals(PermissionConstant.DataRightsSceneType.USER_SUBORDINATE_SCENE)) {
            return this.userSubordinatesInvolvedData(context, entityId, roleType, cascadeSubordinates);
        }
        //负责的部门
        if (sceneType.equals(PermissionConstant.DataRightsSceneType.RESPONSIBLE_DEPT_SCENE)) {
            return this.userResponsibleDeptData(context, entityId, roleType, cascadeDept);
        }
        //共享
        if (sceneType.equals(PermissionConstant.DataRightsSceneType.SHARE_SCENE)) {
            return this.sharedToUserData(context, entityId, roleType);
        }
        //自己参与的
        if (sceneType.equals(PermissionConstant.DataRightsSceneType.USER_SCENE)) {
            return this.userInvolvedData(context, entityId, roleType);
        }
        return this.accessNoneData(context, entityId);

    }

    /**
     * 查询用户对对象记录的权限
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param objects  对象记录列表
     */
    public Map<String, Integer> entityObjectsPermission(
            CommonContext context, String entityId, Set<String> objects, String ownerRoleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException {
        Map<String, Integer> objectsPermissMap = new HashMap<>();
        if (StringUtils.isAnyBlank(entityId, ownerRoleType)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(objects)) {
            return objectsPermissMap;
        }

        Set<String> untreatedObject = new HashSet<>(objects);
        /**
         * 1. 查询记录的团队(重点关注owner)
         * 2. 查询基础数据权限
         * 3. 查看共享规则
         * 4. 查看用户下属
         * 5. 查看用户负责的部门
         */

        //基础数据权限(不支持部门)
        EntityOpennessPojo entityOpennessPojo = this.queryEntityOpenness(context, entityId);

        //团队成员
        Map<String, Set<String>> ownerObjects = new HashMap<>();
        Set<String> objectOwners = new HashSet<>();

        List<Team> objectTeam = this.queryTeam(context, entityId, objects);
        if (CollectionUtils.isNotEmpty(objectTeam)) {
            objectTeam.forEach(team -> {
                if (ownerRoleType.equals(team.getRoleType())) {
                    ownerObjects.computeIfAbsent(team.getMemberId(), k -> new HashSet<>()).add(team.getObjectId());
                    objectOwners.add(team.getMemberId());
                }

                if (team.getMemberType() == PermissionConstant.RecordMemberType.USER && team.getMemberId().equals(context.getUserId())) {
                    if (objectsPermissMap.get(team.getObjectId()) == null || team.getPermission() > objectsPermissMap.get(team.getObjectId())) {
                        objectsPermissMap.put(team.getObjectId(), team.getPermission());
                        if (team.getPermission() == PermissionConstant.RecorderMemberPermissType.READ_AND_WRITE) {
                            untreatedObject.remove(team.getObjectId());
                        }
                    }
                }
            });
        }

        //共享
        if (needContinueCalculateObjectsPermiss(objectOwners, untreatedObject)) {
            calculateUsersSharePermiss(context, entityId, objectsPermissMap, untreatedObject, ownerObjects, objectOwners);
        } else {
            return objectsPermissMap;
        }

        //个人纯私有
        if (entityOpennessPojo != null && (entityOpennessPojo.getScope() == PermissionConstant.EntityOpennessScope.OWNER_PRIVATE)) {
            return objectsPermissMap;
        }

        //用户负责的部门下的人
        if (needContinueCalculateObjectsPermiss(objectOwners, untreatedObject)) {
            Set<String> users = this.deptLeaderSubordinatesIncludeUsers(context, objectOwners, cascadeDept);
            calculateOwnersPermission(objectsPermissMap, untreatedObject, ownerObjects, objectOwners, users);
        } else {
            return objectsPermissMap;
        }

        //用户下属
        if (needContinueCalculateObjectsPermiss(objectOwners, untreatedObject)) {
            Set<String> users = this.userLeaderSubordinatesIncludeUsers(context, objectOwners, cascadeSubordinates);
            calculateOwnersPermission(objectsPermissMap, untreatedObject, ownerObjects, objectOwners, users);
        }

        return objectsPermissMap;
    }

    public Map<String, Integer> objectsPermissionCalculate(
            CommonContext context, String entityId, Set<String> objects, String ownerRoleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException {
        Map<String, Integer> objectsPermissMap = new HashMap<>();

        if (StringUtils.isAnyBlank(entityId, ownerRoleType)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(objects)) {
            return objectsPermissMap;
        }
        /**
         * 1. 查询记录的团队(重点关注owner)
         * 2. 查询基础数据权限
         * 3. 查看共享规则
         * 4. 查看用户下属
         * 5. 查看用户负责的部门
         */

        //基础数据权限(不支持部门)
        EntityOpennessPojo entityOpennessPojo = this.queryEntityOpenness(context, entityId);
        if (entityOpennessPojo != null && entityOpennessPojo.getScope() != null) {
            if (entityOpennessPojo.getScope() == PermissionConstant.EntityOpennessScope.PUBLIC_ALL) {
                objects.forEach(objectId -> {
                    objectsPermissMap.put(objectId, entityOpennessPojo.getPermission());
                });
                if (entityOpennessPojo.getPermission() == PermissionConstant.EntityOpennessPermiss.READ_AND_WRITE) {
                    return objectsPermissMap;
                }
            }
        }

        //获取objects team 存储在Map<String,Map<String,Integer>>
        //判断用户是否是所有objects的owner
        //根据objects的member用户下属,用户负责的部门下是否有该用户
        //查询所有共享规则
        Set<String> objectOwners = new HashSet<>();
        Map<String, Set<String>> ownerObjects = new HashMap<>();
        Set<String> untreatedObject = new HashSet<>(objects);
        Map<String, Map<String, Integer>> teamMemberObjectsPermission = new HashMap<>();
        List<Team> objectsTeam = this.queryTeam(context, entityId, objects);
        if (CollectionUtils.isNotEmpty(objectsTeam)) {
            for (Team team : objectsTeam) {
                //object owner
                if (ownerRoleType.equals(team.getRoleType())) {
                    objectOwners.add(team.getMemberId());
                    ownerObjects.computeIfAbsent(team.getMemberId(), k -> new HashSet<>()).add(team.getObjectId());
                }
                //team member as objects team member permission
                teamMemberObjectsPermission.computeIfAbsent(team.getMemberId(), k -> new HashMap<>()).put(team.getObjectId(), team.getPermission());
                if (team.getPermission() > teamMemberObjectsPermission.get(team.getMemberId()).get(team.getObjectId())) {
                    teamMemberObjectsPermission.get(team.getMemberId()).put(team.getObjectId(), team.getPermission());
                }
            }

            //calculate request user as object team members permission
            Map<String, Integer> requestUserPermission = teamMemberObjectsPermission.get(context.getUserId());
            if (requestUserPermission != null) {
                requestUserPermission.forEach((objectId, permission) -> {
                    objectsPermissMap.put(objectId, permission);
                    if (permission == PermissionConstant.RecorderMemberPermissType.READ_AND_WRITE) {
                        untreatedObject.remove(objectId); //移除不需要计算的对象
                    }
                });
            }

            //用户负责的部门,用户的下属
            if (needContinueCalculateObjectsPermiss(objectOwners, untreatedObject)) {
                calculateUserSubUSersAndLeadDeptUsersPermission(context,
                        teamMemberObjectsPermission,
                        objectsPermissMap,
                        untreatedObject,
                        cascadeDept,
                        cascadeSubordinates);
            }

            //根据owner查询共享规则
            if (needContinueCalculateObjectsPermiss(objectOwners, untreatedObject)) {
                calculateUsersSharePermiss(context, entityId, objectsPermissMap, untreatedObject, ownerObjects, objectOwners);
            }

            //条件共享
            if (needContinueCalculateObjectsPermiss(objectOwners, untreatedObject)) {
                calculateFieldSharePermiss(context, entityId, objectsPermissMap, untreatedObject);
            }
        }
        return objectsPermissMap;
    }

    private Set<String> calculateUserSubUSersAndLeadDeptUsers(
            CommonContext context, Set<String> users, boolean cascadeDept, boolean cascadeSubordinates) {
        //查询用户负责的部门和下属的用户id
        Integer userDeptRelationFlag = PermissionConstant.EmployeeDeptRelationType.DEPT_DIRECT_LEADER;

        Integer userLeaderRelationFlag = PermissionConstant.UserLeaderRelationType.DIRECT_LEADER;
        if (cascadeDept) {
            userDeptRelationFlag = PermissionConstant.EmployeeDeptRelationType.SUPERIOR;
        }
        if (cascadeSubordinates) {
            userLeaderRelationFlag = null;
        }
        return userDeptRelationCacheMapper.userSubUsersAndLeaderDeptUsers(context.getTenantId(),
                users,
                context.getUserId(),
                userDeptRelationFlag,
                userLeaderRelationFlag);
    }

    private void calculateUserSubUSersAndLeadDeptUsersPermission(
            CommonContext context,
            Map<String, Map<String, Integer>> teamMemberObjectsPermission/*团队成员权限*/,
            Map<String, Integer> objectsPermissMap/*记录权限*/,
            Set<String> untreatedObject,
            boolean cascadeDept,
            boolean cascadeSubordinates) {
        Set<String> suberUsers =
                this.calculateUserSubUSersAndLeadDeptUsers(context, teamMemberObjectsPermission.keySet(), cascadeDept, cascadeSubordinates);
        if (CollectionUtils.isNotEmpty(suberUsers)) {
            for (String userId : suberUsers) {
                Map<String, Integer> userObjectsPermission = teamMemberObjectsPermission.get(userId);
                if (userObjectsPermission != null) {
                    userObjectsPermission.forEach((object, permission) -> {
                        if (objectsPermissMap.get(object) == null || permission > objectsPermissMap.get(object)) {
                            objectsPermissMap.put(object, permission);
                            if (permission == PermissionConstant.RecorderMemberPermissType.READ_AND_WRITE) {
                                untreatedObject.remove(object);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 用户能够访问的数据SQL
     */
    private String userAccessData(CommonContext context, String entityId, String roleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException {
        if (StringUtils.isAnyBlank(entityId, roleType)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder();
        /**
         * 1. 基础数据权限
         * 2. 用户参与的
         * 3. 共享给用户的
         * 4. 用户下属的
         * 5. 用户负责的部门的
         */
        Integer scope = entityScope(context, entityId);
        if (scope != null) {
            if (scope == PermissionConstant.EntityOpennessScope.PUBLIC_ALL) {
                return this.accessAllData(context, entityId);
            } else if (scope == PermissionConstant.EntityOpennessScope.OWNER_PRIVATE /*纯私有(自己的数据,共享给用户的数据)*/) {
                //用户参与的
                sql.append(this.userInvolvedData(context, entityId, StringUtils.EMPTY));
                sql.append(SQL_UNION);
                //共享给用户的
                sql.append(this.sharedToUserData(context, entityId, roleType));
                return sql.toString();
            }
        }

        //用户参与的
        sql.append(this.userInvolvedData(context, entityId, StringUtils.EMPTY));
        sql.append(SQL_UNION);

        //用户下属参与的
        sql.append(this.userSubordinatesInvolvedData(context, entityId, StringUtils.EMPTY, cascadeSubordinates));
        sql.append(SQL_UNION);

        //用户负责的部门参与的
        sql.append(this.userResponsibleDeptData(context, entityId, StringUtils.EMPTY, cascadeDept));
        sql.append(SQL_UNION);

        //共享给用户的
        sql.append(this.sharedToUserData(context, entityId, roleType));

        return sql.toString();

    }

    /**
     * 共享给用户的数据SQL
     */
    private String sharedToUserData(CommonContext context, String entityId, String roleType) throws AuthServiceException {
        StringBuilder sql = new StringBuilder();

        sql.append(" select object_id from dt_team as team inner join dt_entity_share_cache as entity_share "
                + " on team.tenant_id = entity_share.tenant_id " + " and team.package = entity_share.app_id "
                + " and team.object_describe_api_name = entity_share.entity_id " + " and team.member_id = entity_share.share_user "
                + "  and team.member_type = ")
                .append(PermissionConstant.RecordMemberType.USER)
                .append(" and team.role_type = ")
                .append('\'')
                .append(roleType)
                .append('\'')
                .append(" and team.is_deleted = 0 ")
                .append(" where entity_share.tenant_id = ")
                .append('\'')
                .append(context.getTenantId())
                .append('\'')
                .append(" and entity_share.app_id=")
                .append('\'')
                .append(context.getAppId())
                .append('\'')
                .append(" and entity_share.entity_id = ")
                .append('\'')
                .append(entityId)
                .append('\'');
        sql.append(" and ");
        sql.append(" ( (entity_share.receive_user = '")
                .append(context.getUserId())
                .append("' and entity_share.receive_type = ")
                .append(PermissionConstant.EntityShareType.USER)
                .append(')')
                .append("    or (entity_share.receive_user in (select group_user.group_id from org_group_user group_user where group_user.tenant_id = '")
                .append(context.getTenantId())
                .append("' and group_user.package = '")
                .append(context.getAppId())
                .append("' and  group_user.user_id = '")
                .append(context.getUserId())
                .append("' and group_user.is_deleted = 0 ) and entity_share.receive_type = ")
                .append(PermissionConstant.EntityShareType.GROUP)
                .append(')')
                .append("    or (entity_share.receive_user in (select dept_user.dept_id from org_dept_user dept_user where dept_user.tenant_id = '")
                .append(context.getTenantId())
                .append("'  and dept_user.user_id = '")
                .append(context.getUserId())
                .append("' and dept_user.is_deleted = 0) and entity_share.receive_type = ")
                .append(PermissionConstant.EntityShareType.DEPT)
                .append(')');
        List<String> userRoles = userRoleService.queryRoleCodeListByUserId(context);
        if (CollectionUtils.isNotEmpty(userRoles)) {
            sql.append(" or ( entity_share.receive_user in (");
            int roleLen = userRoles.size();
            int lenFlag = 0;
            for (String role : userRoles) {
                sql.append('\'').append(role).append('\'');
                if (lenFlag + 1 < roleLen) {
                    sql.append(',');
                }
                lenFlag++;
            }
            sql.append(')');
            sql.append(" and entity_share.receive_type =").append(PermissionConstant.EntityShareType.ROLE);
            sql.append(')');
        }
        sql.append(" ) ");

        String fieldShare = this.fieldShareToUserData(context, entityId);
        if (StringUtils.isNotBlank(fieldShare)) {
            sql.append(SQL_UNION);
            sql.append(fieldShare);
        }
        return sql.toString();
    }

    private String fieldShareToUserData(CommonContext context, String entityId) throws AuthServiceException {
        StringBuilder sql = new StringBuilder();
        List<String> sqls = entityFieldShareService.fieldShareReceiveSql(context, entityId);
        if (CollectionUtils.isNotEmpty(sqls)) {
            int len = sqls.size();
            int lenFlag = 0;
            for (String s : sqls) {
                sql.append(s);
                if (lenFlag + 1 < len) {
                    sql.append(SQL_UNION);
                }
                lenFlag++;
            }
        }
        return sql.toString();
    }

    /**
     * 用户负责的部门的数据SQL
     */
    private String userResponsibleDeptData(CommonContext context, String entityId, String roleType, boolean cascadeDept) {
        this.checkEntityId(entityId);
        StringBuilder sql = new StringBuilder();
        sql.append(" select object_id from dt_team as team " + " inner join dt_dept_user_cache as dept_user"
                + " on  team.tenant_id = dept_user.tenant_id ");
        sql.append(" and team.package = ")
                .append('\'')
                .append(context.getAppId())
                .append('\'')
                .append(" and team.object_describe_api_name = ")
                .append('\'')
                .append(entityId)
                .append('\'')
                .append(" and team.member_id = dept_user.user_id")
                .append(" and team.member_type = ")
                .append(PermissionConstant.RecordMemberType.USER);
        if (StringUtils.isNotBlank(roleType)) {
            sql.append(" and team.role_type = ").append('\'').append(roleType).append('\'');
        }
        sql.append(" and team.is_deleted = 0 ");
        sql.append(" where dept_user.tenant_id = ")
                .append('\'')
                .append(context.getTenantId())
                .append('\'')
                .append(" and dept_user.dept_id  in ")
                .append("  ( select dt_dept_user_cache.dept_id from dt_dept_user_cache where dt_dept_user_cache.tenant_id = ")
                .append('\'')
                .append(context.getTenantId())
                .append('\'')
                .append(" and dt_dept_user_cache.user_id = ")
                .append('\'')
                .append(context.getUserId())
                .append('\'');
        if (cascadeDept) {
            sql.append(" and relation_type = ").append(PermissionConstant.EmployeeDeptRelationType.SUPERIOR);
        } else {
            sql.append(" and relation_type = ").append(PermissionConstant.EmployeeDeptRelationType.DEPT_DIRECT_LEADER);
        }
        sql.append(") and dept_user.relation_type = 0 ");
        return sql.toString();
    }

    /**
     * 用户参与的
     */
    private String userInvolvedData(CommonContext context, String entityId, String roleType/*角色*/) {
        this.checkEntityId(entityId);
        StringBuilder sql = new StringBuilder();
        sql.append(" select object_id from dt_team where tenant_id = ").append('\'').append(context.getTenantId()).append('\'');
        sql.append(" and package = ").append('\'').append(context.getAppId()).append('\'');
        sql.append(" and object_describe_api_name = ").append('\'').append(entityId).append('\'');
        sql.append(" and member_type = ").append(PermissionConstant.RecordMemberType.USER);
        sql.append(" and member_id = ").append('\'').append(context.getUserId()).append('\'');
        if (StringUtils.isNotBlank(roleType)) {
            sql.append(" and role_type = ").append('\'').append(roleType).append('\'');
        }
        sql.append(" and is_deleted = 0 ");
        return sql.toString();
    }

    /**
     * 下属参与的
     *
     * @param context  请求上下文
     * @param roleType 角色类型
     * @return 返权限sql
     */
    private String userSubordinatesInvolvedData(CommonContext context, String entityId, String roleType, boolean cascadeSubordinates) {
        this.checkEntityId(entityId);
        StringBuilder sql = new StringBuilder();
        sql.append(" select object_id from dt_team team inner join dt_user_leader_cache leader "
                + " on leader.tenant_id = team.tenant_id and leader.user_id = team.member_id ");
        sql.append(" and team.package = ").append('\'').append(context.getAppId()).append('\'');
        sql.append(" and team.object_describe_api_name = ").append('\'').append(entityId).append('\'');
        sql.append(" and team.member_type = ").append(PermissionConstant.RecordMemberType.USER);

        if (StringUtils.isNotBlank(roleType)) {
            sql.append(" and  team.role_type = ").append('\'').append(roleType).append('\'');
        }
        sql.append(" and team.is_deleted = 0 ");
        sql.append(" where leader.tenant_id = ").append('\'').append(context.getTenantId()).append('\'');
        sql.append(" and leader.leader_id = ").append('\'').append(context.getUserId()).append('\'');
        if (!cascadeSubordinates) {
            sql.append(" and leader.relation_type = ").append(PermissionConstant.UserLeaderRelationType.DIRECT_LEADER);
        }
        return sql.toString();
    }

    private String accessAllData(CommonContext context, String entityId) {
        StringBuilder sql =
                new StringBuilder(" select object_id from dt_team where tenant_id = ").append('\'').append(context.getTenantId()).append('\'');
        sql.append(" and package = ").append('\'').append(context.getAppId()).append('\'');
        sql.append(" and object_describe_api_name = ").append('\'').append(entityId).append('\'');
        sql.append(" and is_deleted = 0 ");
        return sql.toString();
    }

    private String accessNoneData(CommonContext context, String entityId) {
        return " select object_id from dt_team where  1 = 2 ";
    }

    private void calculateUsersSharePermiss(
            CommonContext context,
            String entityId,
            Map<String, Integer> objectsPermissMap,
            Set<String> untreatedObject,
            Map<String, Set<String>> ownerObjects,
            Set<String> objectOwners) throws AuthServiceException {
        List<String> userRoles = userRoleService.queryRoleCodeListByUserId(context);
        List<EntityShareCache> entityShareCacheList = entityShareCacheMapper.queryEntityShareCacheByReceiveAndShareUsers(context.getTenantId(),
                context.getAppId(),
                entityId,
                objectOwners,
                userRoles,
                context.getUserId());
        if (CollectionUtils.isNotEmpty(entityShareCacheList)) {
            Map<String, Integer> userMaxPermssion = new HashMap<>();
            entityShareCacheList.forEach(entityShareCache -> {
                if (userMaxPermssion.get(entityShareCache.getShareUser()) == null
                        || userMaxPermssion.get(entityShareCache.getShareUser()) < entityShareCache.getPermission()) {
                    userMaxPermssion.put(entityShareCache.getShareUser(), entityShareCache.getPermission());
                }
            });
            for (Map.Entry<String, Integer> entry : userMaxPermssion.entrySet()) {
                if (objectOwners.contains(entry.getKey())) {
                    Integer sharePermission = entry.getValue();
                    Set<String> objectsTemp = ownerObjects.get(entry.getKey());
                    for (String object : objectsTemp) {
                        if (sharePermission == PermissionConstant.EntitySharePermissType.READ_AND_WRITE) {
                            untreatedObject.remove(object); //去除最大权限object
                            objectOwners.remove(entry.getKey()); //去除用户
                        }
                        if (objectsPermissMap.get(object) == null || sharePermission > objectsPermissMap.get(object)) {
                            objectsPermissMap.put(object, sharePermission);
                        }
                    }
                }
            }
        }
    }

    private void calculateFieldSharePermiss(
            CommonContext context, String entityId, Map<String, Integer> objectsPermissMap, Set<String> untreatedObject) throws AuthServiceException {
        Map<String, Integer> rulePermissionMap = this.receiveRuleCodesPermission(context, entityId);
        if (MapUtils.isNotEmpty(rulePermissionMap)) {
            Map<String/*dataid*/, Map<String/*ruleCode*/, Object/*true/false*/>> dataRuleExpressionPatternMap;
            try {
                dataRuleExpressionPatternMap =
                        entityFieldShareService.dataRuleExpressionPattern(context, entityId, rulePermissionMap.keySet(), untreatedObject);
            } catch (Exception e) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            if (MapUtils.isNotEmpty(dataRuleExpressionPatternMap)) {
                dataRuleExpressionPatternMap.forEach((dataId, rulePatternMap) -> {
                    if (rulePatternMap != null) {
                        rulePatternMap.forEach((ruleCode, pattern) -> {
                            Boolean rulePatternBoolean = (Boolean) pattern;
                            if (rulePatternBoolean != null && rulePatternBoolean) {
                                if (objectsPermissMap.get(dataId) == null || rulePermissionMap.get(ruleCode) > objectsPermissMap.get(dataId)) {
                                    objectsPermissMap.put(dataId, rulePermissionMap.get(ruleCode));
                                    if (rulePermissionMap.get(ruleCode) == PermissionConstant.EntitySharePermissType.READ_AND_WRITE) {
                                        untreatedObject.remove(dataId); //去除最大权限object
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private Map<String, Integer> receiveRuleCodesPermission(CommonContext context, String entityId) throws AuthServiceException {
        List<EntityFieldShareReceivePojo> receivePojos = entityFieldShareService.userReceivedRule(context, entityId);
        Map<String, Integer> rulePermissionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(receivePojos)) {
            receivePojos.forEach(receivePojo -> {
                if (rulePermissionMap.get(receivePojo.getRuleCode()) == null
                        || rulePermissionMap.get(receivePojo.getRuleCode()) < receivePojo.getPermission()) {
                    rulePermissionMap.put(receivePojo.getRuleCode(), receivePojo.getPermission());
                }
            });
        }
        return rulePermissionMap;
    }

    private boolean needContinueCalculateObjectsPermiss(Set<String> objectOwners, Set<String> untreatedObject) {
        if (CollectionUtils.isEmpty(objectOwners) || CollectionUtils.isEmpty(untreatedObject)) {
            return false;
        }
        return true;
    }

    /**
     * 计算owner的对象记录权限
     */
    private void calculateOwnersPermission(
            Map<String, Integer> objectsPermissMap,
            Set<String> untreatedObject,
            Map<String, Set<String>> ownerObjects,
            Set<String> objectOwners,
            Set<String> users) {
        if (CollectionUtils.isNotEmpty(users)) {
            users.forEach(user -> {
                if (objectOwners.contains(user)) {
                    Set<String> objectsTemp = ownerObjects.get(user);
                    for (String object : objectsTemp) {
                        objectsPermissMap.put(object, PermissionConstant.RecorderMemberPermissType.READ_AND_WRITE);
                        untreatedObject.remove(object); //去除最大权限object
                    }
                    objectOwners.remove(user); //去除用户
                }
            });
        }

    }

    private void checkEntityId(String entityId) {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private List<Team> queryTeam(CommonContext context, String entityId, Set<String> objects) {
        return teamMapper.queryTeam(context.getTenantId(), context.getAppId(), entityId, objects);
    }

    /**
     * 用户的汇报对象下属是否包含users
     */
    private Set<String> userLeaderSubordinatesIncludeUsers(CommonContext context, Set<String> users, boolean cascadeSubordinates) {
        Integer userLeaderRelationType = null;
        if (!cascadeSubordinates) {
            userLeaderRelationType = PermissionConstant.UserLeaderRelationType.DIRECT_LEADER;
        }
        return userLeaderCacheMapper.userLeaderCacheUserId(context.getTenantId(),
                users,
                Collections.singleton(context.getUserId()),
                userLeaderRelationType);
    }

    /**
     * 用户负责的部门是否包含users
     */
    private Set<String> deptLeaderSubordinatesIncludeUsers(CommonContext context, Set<String> users, boolean cascadeDept) {
        Integer userDeptRelationType = PermissionConstant.EmployeeDeptRelationType.DEPT_DIRECT_LEADER;
        if (cascadeDept) {
            userDeptRelationType = PermissionConstant.EmployeeDeptRelationType.SUPERIOR;
        }
        return userDeptRelationCacheMapper.deptLeaderSubordinates(context.getTenantId(), users, context.getUserId(), userDeptRelationType);
    }

    private EntityOpennessPojo queryEntityOpenness(CommonContext context, String entityId) throws AuthServiceException {
        return entityOpennessService.queryEntityOpennessByEntity(context, entityId);
    }

    private Integer entityScope(CommonContext context, String entityId) throws AuthServiceException {
        EntityOpennessPojo pojo = this.queryEntityOpenness(context, entityId);
        if (pojo != null) {
            return pojo.getScope();
        }
        return null;
    }
}

