package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.arg.permission.TeamMemberArg;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.QueryRecordPermissObjectFilter;
import com.nova.paas.auth.pojo.permission.TeamMembersPojo;
import com.nova.paas.auth.pojo.permission.TeamPojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:28
 */
public interface TeamService {

    /**
     * 删除团队成员
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param objects  记录ID
     * @param teamIds  成员ID
     */
    void delTeamMembers(
            CommonContext context, String entityId, Set<String> objects, Set<String> teamIds) throws AuthServiceException;

    /**
     * 移除团队成员
     *
     * @param context    请求上下文
     * @param entityId   对象ID
     * @param objects    记录ID
     * @param memberType 成员类型
     * @param members    成员列表
     * @param roleTypes  成员角色列表
     */
    void delMemberFromTeam(
            CommonContext context, String entityId, Set<String> objects, Integer memberType, Set<String> members, Set<String> roleTypes)
            throws AuthServiceException;

    /**
     * 添加多个成员到团队中
     *
     * @param context        请求上下文
     * @param entityId       对象ID
     * @param teamMemberses  团队成员
     * @param repeatCoverage 重复覆盖
     */
    void addMemberToTeam(
            CommonContext context, String entityId, List<TeamMembersPojo> teamMemberses, Boolean repeatCoverage) throws AuthServiceException;

    /**
     * 团队查询
     *
     * @param context  请求上下文
     * @param entityId 对象
     * @param records  团队
     */
    Map<String, List<TeamPojo>> queryRecordsTeam(
            CommonContext context, String entityId, Set<String> records) throws AuthServiceException;

    /**
     * 根据团队成员过滤器查询记录列表(过滤器之间取并集)
     *
     * @param context  请求上下文
     * @param entityId 对象ID
     * @param filters  过滤器列表
     */
    Set<String> queryRecordsByTeamMembers(
            CommonContext context, String entityId, List<QueryRecordPermissObjectFilter> filters) throws AuthServiceException;

    /**
     * 更新记录相关团队
     *
     * @param context        请求上下文
     * @param entityId       对象
     * @param teamMemberses  记录团队成员
     * @param repeatCoverage 重复覆盖
     */
    void updateRecordTeam(
            CommonContext context, String entityId, List<TeamMembersPojo> teamMemberses, Boolean repeatCoverage) throws AuthServiceException;

    /**
     * 团队查询
     *
     * @param context    请求上下文
     * @param entityId   对象实体
     * @param objectIds  记录ID
     * @param memberType 团队成员类型
     * @param memberIds  团队成员ID
     * @param permission 团队成员权限
     * @param roleType   团队成员角色
     * @param percent    团队成员比例
     * @param pageInfo   分页
     */
    List<TeamPojo> queryTeam(
            CommonContext context,
            String entityId,
            Set<String> objectIds,
            Integer memberType,
            Set<String> memberIds,
            Integer permission,
            String roleType,
            BigDecimal percent,
            PageInfo pageInfo) throws AuthServiceException;

    /**
     * 删除企业数据
     *
     * @param context 请求上下文
     */
    void delTenant(CommonContext context) throws AuthServiceException;

    /**
     * 删除对象的相关团队数据
     */
    void delTenantEntitys(CommonContext context, Set<String> entitys) throws AuthServiceException;

    /**
     * 创建相关团队
     *
     * @param context        请求上下文
     * @param entityId       对象实体ID
     * @param entityId       记录ID
     * @param teamMemberArgs 记录ID及其团队成员
     */
    void createTeam(
            CommonContext context, String entityId, String objectId, List<TeamMemberArg> teamMemberArgs) throws AuthServiceException;

    /**
     * 更新团队成员，把原先的团队成员全部替换成参数teamMemberArgs中的成员
     *
     * @param entityId 作为过滤条件
     * @param objectId 作为过滤条件
     */
    void updateTeamMembers(
            CommonContext context, String entityId, String objectId, List<TeamMemberArg> teamMemberArgs) throws AuthServiceException;

    /**
     * 删除相关团队
     */
    void deleteTeams(CommonContext context, String entityId, Set<String> objectIds) throws AuthServiceException;

}
