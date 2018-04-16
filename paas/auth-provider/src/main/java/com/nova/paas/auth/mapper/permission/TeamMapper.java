package com.nova.paas.auth.mapper.permission;

import com.nova.paas.auth.entity.permission.Team;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * zhenghaibo
 * 2018/4/13 15:32
 */
public interface TeamMapper {

    //  @SelectProvider(type = TeamSQLProvider.class, method = "queryTeam")
    List<Team> queryTeam(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objectIds") Collection objectIds);

    //  @UpdateProvider(type = TeamSQLProvider.class, method = "delTenant")
    void delTenant(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    //  @UpdateProvider(type = TeamSQLProvider.class, method = "delTenantEntitys")
    void delTenantEntitys(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entitys") Collection<String> entitys,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    //  @SelectProvider(type = TeamSQLProvider.class, method = "queryTeamRecords")
    List<String> queryTeamRecords(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects);

    //  @UpdateProvider(type = TeamSQLProvider.class, method = "delTeams")
    void delTeams(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objectIds") Collection objectIds,
            @Param("modifier") String modifier);

    //  @UpdateProvider(type = TeamSQLProvider.class, method = "delTeamMember")
    void delTeamMember(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects,
            @Param("memberIds") Collection memberIds,
            @Param("memberType") Integer memberType,
            @Param("roleTypes") Collection roleTypes,
            @Param("modifier") String modifier);

    //  @UpdateProvider(type = TeamSQLProvider.class, method = "updateTeamMemberProperty")
    void updateTeamMemberProperty(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects,
            @Param("memberType") Integer memberType,
            @Param("memberIds") Collection memberIds,
            @Param("permission") Integer permission,
            @Param("roleType") String roleType,
            @Param("percent") Integer percent,
            @Param("modifier") String modifier);

    //  @SelectProvider(type = TeamSQLProvider.class, method = "queryRecordsByTeamMembers")
    List<String> queryRecordsByTeamMembers(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("filters") Collection filters);

    //  @SelectProvider(type = TeamSQLProvider.class, method = "queryTeamByProperty")
    List<Team> queryTeamByProperty(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objectIds") Collection<String> objectIds,
            @Param("memberType") Integer memberType,
            @Param("memberIds") Collection<String> memberIds,
            @Param("permission") Integer permission,
            @Param("roleType") String roleType,
            @Param("percent") BigDecimal percent);

}
