package com.nova.paas.auth.service.impl.permission;

import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.RecordPermissMapper;
import com.nova.paas.auth.permission.RecordPermissService;
import com.nova.paas.auth.permission.TeamService;
import com.nova.paas.auth.pojo.permission.EntityObjects;
import com.nova.paas.auth.pojo.permission.QueryRecordPermissObjectFilter;
import com.nova.paas.auth.pojo.permission.RecordPermissObjectPojo;
import com.nova.paas.auth.pojo.permission.RecordPermissPojo;
import com.nova.paas.auth.pojo.permission.RecordPermissTeamPojo;
import com.nova.paas.auth.pojo.permission.TeamMembersPojo;
import com.nova.paas.auth.pojo.permission.TeamPojo;
import com.nova.paas.auth.entity.permission.RecordPermiss;
import com.nova.paas.auth.entity.permission.Team;
import com.nova.paas.auth.mapper.permission.TeamMapper;
import com.nova.paas.auth.support.CommonParamsCheckUtil;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("recordPermissService")
@Slf4j
public class RecordPermissServiceImpl implements RecordPermissService {

    @Inject
    private RecordPermissMapper recordPermissMapper;
    @Inject
    private TeamMapper teamMapper;
    @Inject
    private TeamService teamService;
    @Value("${COMPANY_ORG_ID}")
    private String COMPANY_ORG_ID;

    /**
     * 添加记录权限
     *
     * @param context            请求上下文
     * @param recordPermissTeams 记录权限列表
     */
    @Transactional
    @Override
    public void addRecordPermiss(CommonContext context, List<RecordPermissTeamPojo> recordPermissTeams) throws AuthServiceException {
        if (CollectionUtils.isEmpty(recordPermissTeams)) {
            return;
        }
        for (RecordPermissTeamPojo team : recordPermissTeams) {
            if (team == null) {
                continue;
            }
            CommonParamsCheckUtil.entityIdCheck(team.getEntityId());
            CommonParamsCheckUtil.objectIdCheck(team.getObjectId());
            this.addTeamMembers(context, team.getEntityId(), team.getObjectId(), team.getTeam(), false);
        }
    }

    /**
     * 查询对象记录
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param filters  过滤条件列表
     * @param pageInfo 分页信息
     */
    @Override
    public List<String> queryRecordPermissObject(
            CommonContext context, String entityId, List<QueryRecordPermissObjectFilter> filters, PageInfo pageInfo) throws AuthServiceException {
        List<String> objects = new ArrayList<>();
        if (StringUtils.isBlank(entityId)) {
            return objects;
        }
        try {
            return recordPermissMapper.queryRecordPermissObjects(context.getTenantId(), context.getAppId(), entityId, filters, pageInfo);
        } catch (Exception e) {
            log.error("===permission.queryRecordPermissObject() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    /**
     * 更新记录权限
     *
     * @param context            请求上下文
     * @param recordPermissTeams 记录权限
     */
    @Transactional
    @Override
    public void updateRecordPermiss(CommonContext context, List<RecordPermissTeamPojo> recordPermissTeams) throws AuthServiceException {
        if (CollectionUtils.isEmpty(recordPermissTeams)) {
            return;
        }
        for (RecordPermissTeamPojo team : recordPermissTeams) {
            if (team == null) {
                continue;
            }
            CommonParamsCheckUtil.entityIdCheck(team.getEntityId());
            CommonParamsCheckUtil.objectIdCheck(team.getObjectId());
            try {
                recordPermissMapper.delRecordObjectsTeamAllMember(context.getTenantId(),
                        context.getAppId(),
                        team.getEntityId(),
                        Collections.singletonList(team.getObjectId()),
                        context.getUserId());
            } catch (Exception e) {
                log.error("===permission.updateRecordPermiss() error===", e);
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);

            }
            //添加新成员
            this.addTeamMembers(context, team.getEntityId(), team.getObjectId(), team.getTeam(), true);
        }
    }

    /**
     * 删除对象记录权限
     *
     * @param context           请求上下文
     * @param entityObjectsList 待删除的记录列表
     */
    @Transactional
    @Override
    public void delRecordPermiss(CommonContext context, List<EntityObjects> entityObjectsList) throws AuthServiceException {
        if (CollectionUtils.isEmpty(entityObjectsList)) {
            return;
        }
        try {
            for (EntityObjects entityObjects : entityObjectsList) {
                if (entityObjects == null || StringUtils.isBlank(entityObjects.getEntityId())
                        || CollectionUtils.isEmpty(entityObjects.getObjects())) {
                    continue;
                }
                recordPermissMapper.delRecordPermiss(context.getTenantId(),
                        context.getAppId(),
                        entityObjects.getEntityId(),
                        entityObjects.getObjects(),
                        context.getUserId());
                teamMapper.delTeams(context.getTenantId(),
                        context.getAppId(),
                        entityObjects.getEntityId(),
                        entityObjects.getObjects(),
                        context.getUserId());
            }
        } catch (Exception e) {
            log.error("===permission.delRecordPermiss() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    /**
     * 查询对象记录的权限
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param objects  记录列表
     */
    public Map<String, List<TeamPojo>> queryRecordPermissTeam(CommonContext context, String entityId, Set<String> objects)
            throws AuthServiceException {
        Map<String, List<TeamPojo>> objectTeams = new HashMap<>();
        if (objects != null) {
            objects.remove(null);
        }
        if (StringUtils.isBlank(entityId) || CollectionUtils.isEmpty(objects)) {
            return objectTeams;
        }
        objects.forEach(objectID -> {
            objectTeams.put(objectID, new ArrayList<>());
        });
        try {
            List<Team> teamList = teamMapper.queryTeam(context.getTenantId(), context.getAppId(), entityId, objects);
            if (CollectionUtils.isNotEmpty(teamList)) {
                for (Team team : teamList) {
                    TeamPojo teamPojo = new TeamPojo();
                    PropertyUtils.copyProperties(teamPojo, team);
                    objectTeams.get(team.getObjectId()).add(teamPojo);
                }
            }
        } catch (Exception e) {
            log.error("===permission.queryRecordPermissTeam()===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        return objectTeams;
    }

    /**
     * 查询记录
     *
     * @param context  请求上下文
     * @param entityId 对象ID列表
     * @param depts    部门
     * @param owners   记录拥有人员
     * @param objects  记录Id
     * @param union    是否取并集
     * @param pageInfo 分页信息
     */
    public List<RecordPermissPojo> queryRecordPermiss(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects, boolean union, PageInfo pageInfo)
            throws AuthServiceException {
        List<RecordPermiss> recordPermissList = this.queryRecordPermissFromDB(context, entityId, depts, owners, objects, union, pageInfo);
        return this.convertEntity2Pojo(recordPermissList);
    }

    /**
     * 查询记录列表
     *
     * @param context  请求上下文
     * @param entityId 对象ID列表
     * @param depts    部门
     * @param owners   记录拥有人员
     * @param objects  记录Id
     * @param union    是否取并集
     * @param pageInfo 分页信息
     */
    @Override
    public List<String> queryRecordPermissObjects(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects, boolean union, PageInfo pageInfo)
            throws AuthServiceException {
        List<RecordPermiss> recordPermissList = this.queryRecordPermissFromDB(context, entityId, depts, owners, objects, union, pageInfo);
        List<String> objectIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(recordPermissList)) {
            recordPermissList.forEach(recordPermiss -> {
                objectIds.add(recordPermiss.getObjectId());
            });
        }
        return objectIds;
    }

    /**
     * 添加记录权限
     *
     * @param context             请求上下文
     * @param recordPermissObject 记录信息实体对象
     */
    @Transactional
    @Override
    public void createRecordPermiss(CommonContext context, RecordPermissObjectPojo recordPermissObject) throws AuthServiceException {

        CommonParamsCheckUtil.entityIdCheck(recordPermissObject.getEntityId());
        CommonParamsCheckUtil.objectIdCheck(recordPermissObject.getObjectId());
        CommonParamsCheckUtil.userIdCheck(recordPermissObject.getOwner());
        CommonParamsCheckUtil.deptIdCheck(recordPermissObject.getDept());

        RecordPermiss record = RecordPermiss.builder()
                .tenantId(context.getTenantId())
                .appId(context.getAppId())
                .entityId(recordPermissObject.getEntityId())
                .objectId(recordPermissObject.getObjectId())
                .owner(recordPermissObject.getOwner())
                .dept(recordPermissObject.getDept())
                .delFlag(0)
                .creator(context.getUserId())
                .createTime(System.currentTimeMillis())
                .modifier(context.getUserId())
                .modifyTime(System.currentTimeMillis())
                .build();
        record.setId(IdUtil.generateId());
        //        recordPermissMapper.insert(record);
        if (CollectionUtils.isNotEmpty(recordPermissObject.getTeam())) {

            List<TeamMembersPojo> teamMemberses = new ArrayList<>();
            TeamMembersPojo teamMembers = new TeamMembersPojo();
            teamMembers.setObjectId(recordPermissObject.getObjectId());
            teamMembers.setTeam(recordPermissObject.getTeam());
            teamMemberses.add(teamMembers);
            teamService.addMemberToTeam(context, recordPermissObject.getEntityId(), teamMemberses, Boolean.FALSE);
        }
    }

    /**
     * 删除记录权限(不删除记录团队)
     *
     * @param context  请求上下文
     * @param entityId 对象ID
     * @param depts    部门列表
     * @param owners   用户列表
     * @param objects  记录列表
     */
    @Transactional
    @Override
    public void delRecordPermiss(CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects)
            throws AuthServiceException {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        SetUtil.removeNull(depts);
        SetUtil.removeNull(owners);
        SetUtil.removeNull(objects);

        recordPermissMapper.delRecordPermiss(context.getTenantId(), context.getAppId(), entityId, objects, context.getUserId());
    }

    /**
     * 更新记录负责人
     *
     * @param context  请求上下文
     * @param entityId 对象id
     * @param objects  记录列表
     * @param owner    负责人
     * @param newOwner 新负责人
     */
    @Transactional
    @Override
    public void updateRecordPermissOwner(CommonContext context, String entityId, Set<String> objects, String owner, String newOwner)
            throws AuthServiceException {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isBlank(newOwner)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        recordPermissMapper.updateRecordPermissOwner(context.getTenantId(), context.getAppId(), entityId, objects, owner, newOwner);

    }

    /**
     * 更新记录所属部门
     *
     * @param context  请求上下文
     * @param entityId 对象实体ID
     * @param objects  记录ID列表
     * @param dept     部门
     * @param newDept  新部门
     */
    @Transactional
    @Override
    public void updateRecordPermissDept(CommonContext context, String entityId, Set<String> objects, String dept, String newDept)
            throws AuthServiceException {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isBlank(newDept)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        recordPermissMapper.updateRecordPermissDept(context.getTenantId(), context.getAppId(), entityId, objects, dept, newDept);
    }

    /**
     * 删除企业数据
     */
    @Transactional
    @Override
    public void delTenant(CommonContext context) throws AuthServiceException {
        recordPermissMapper.delTenant(context.getTenantId(), context.getAppId(), context.getUserId(), System.currentTimeMillis());
        teamMapper.delTenant(context.getTenantId(), context.getAppId(), context.getUserId(), System.currentTimeMillis());
    }

    /**
     * 添加记录团队成员
     *
     * @param context   请求上下文
     * @param entityId  实体对象
     * @param objectId  记录ID
     * @param teamPojos 团队成员
     */
    private void addTeamMembers(CommonContext context, String entityId, String objectId, List<TeamPojo> teamPojos, boolean updateFlag) {
        Set<String> teamMemberSet = new HashSet<>();
        StringBuilder tempStr = new StringBuilder();
        List<RecordPermiss> recordPermissList;
        try {
            recordPermissList =
                    recordPermissMapper.queryRecordPermiss(context.getTenantId(), context.getAppId(), entityId, Collections.singletonList(objectId));
        } catch (Exception e) {
            log.error("===permission.addTeamMembers() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        //记录不存在
        if (CollectionUtils.isEmpty(recordPermissList)) {
            if (updateFlag) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            //添加记录
            RecordPermiss recordPermiss = new RecordPermiss(IdUtil.generateId(),
                    context.getTenantId(),
                    context.getAppId(),
                    entityId,
                    objectId,
                    context.getUserId(),
                    this.getUserDeptId(context),
                    0,
                    context.getUserId(),
                    System.currentTimeMillis(),
                    context.getUserId(),
                    System.currentTimeMillis());
            try {
                //                recordPermissMapper.insert(recordPermiss);
            } catch (Exception e) {
                log.error("===permission.addTeamMembers() error===", e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }

        } else {  //记录存在
            List<Team> teamList = this.queryRecordPermissTeamList(context, entityId, Collections.singletonList(objectId));
            if (CollectionUtils.isNotEmpty(teamList)) {
                for (Team teamEntity : teamList) {
                    tempStr.append(String.valueOf(teamEntity.getMemberType())).append('_');
                    tempStr.append(teamEntity.getMemberId()).append('_');
                    tempStr.append(teamEntity.getRoleType());
                    teamMemberSet.add(tempStr.toString());
                    tempStr.delete(0, tempStr.length());
                }
            }
        }
        //添加团队成员
        if (CollectionUtils.isNotEmpty(teamPojos)) {
            for (TeamPojo teamPojo : teamPojos) {
                //校验成员是否已存在
                CommonParamsCheckUtil.teamPojoCheck(teamPojo);
                tempStr.append(String.valueOf(teamPojo.getMemberType())).append('_');
                tempStr.append(teamPojo.getMemberId()).append('_');
                tempStr.append(teamPojo.getRoleType());
                if (teamMemberSet.contains(tempStr.toString())) {
                    throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
                }
                teamPojo.setObjectId(objectId);
                teamMemberSet.add(tempStr.toString());
                tempStr.delete(0, tempStr.length());
            }
            List<Team> teams = new ArrayList<>();
            for (TeamPojo teamPojo : teamPojos) {
                Team team = Team.builder()
                        .tenantId(context.getTenantId())
                        .appId(context.getAppId())
                        .objectDescribeApiName(entityId)
                        .objectId(objectId)
                        .memberType(teamPojo.getMemberType())
                        .memberId(teamPojo.getMemberId())
                        .roleType(teamPojo.getRoleType())
                        .permission(teamPojo.getPermission())
                        .lastModifiedBy(context.getUserId())
                        .lastModifiedTime(System.currentTimeMillis())
                        .isDeleted(0)
                        .build();
                team.setId(IdUtil.generateId());
                teams.add(team);
            }
            try {
                //                teamMapper.batchInsert(teams);
            } catch (Exception e) {
                log.error("===permission. addTeamMembers() error===", e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
    }

    /**
     * 获取用户的主部门,如果不存在取默认值
     */
    private String getUserDeptId(CommonContext context) {
        return COMPANY_ORG_ID;
    }

    private List<Team> queryRecordPermissTeamList(CommonContext context, String entityId, List<String> objects) {
        try {
            return teamMapper.queryTeam(context.getTenantId(), context.getAppId(), entityId, objects);
        } catch (Exception e) {
            log.error("===permission.queryRecordPermissTeamList() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private List<RecordPermiss> queryRecordPermissFromDB(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects, boolean union, PageInfo pageInfo) {
        if (StringUtils.isBlank(entityId)) {
            return Collections.emptyList();
        }
        List<RecordPermiss> recordPermissList;
        //        if (pageInfo != null) {
        //            PageHelper.startPage(pageInfo.getCurrentPage(), pageInfo.getPageSize());
        //            recordPermissList = recordPermissMapper.setTenantId(context.getTenantId())
        //                    .recordPermiss(context.getTenantId(), context.getAppId(), entityId, depts, owners, objects, union);
        //            PageBean pageBean = new PageBean(recordPermissList);
        //            pageInfo.setTotalPage(pageBean.getPages());
        //            pageInfo.setTotal(pageBean.getTotal());
        //        } else {
        recordPermissList = recordPermissMapper.recordPermiss(context.getTenantId(), context.getAppId(), entityId, depts, owners, objects, union);
        //        }
        return recordPermissList;
    }

    private List<RecordPermissPojo> convertEntity2Pojo(List<RecordPermiss> recordPermissList) {
        List<RecordPermissPojo> recordPermissPojoList = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(recordPermissList)) {
            try {
                for (RecordPermiss recordPermiss : recordPermissList) {
                    RecordPermissPojo pojo = new RecordPermissPojo();
                    PropertyUtils.copyProperties(pojo, recordPermiss);
                    recordPermissPojoList.add(pojo);
                }
            } catch (Exception e) {
                log.error("entity convert to pojo error", e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
        return recordPermissPojoList;
    }

}
