package com.nova.paas.auth.service.mapper.permission;

import com.nova.paas.auth.service.entity.permission.RecordPermiss;
import com.nova.paas.auth.service.entity.permission.Team;
import com.nova.paas.common.pojo.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

/**
 * zhenghaibo
 * 2018/4/13 15:32
 */
public interface RecordPermissMapper {

    @Update("update dt_record_permission set del_flag=1,modifier=#{modifier},modify_time=#{modifyTime} where tenant_id = #{tenantId} and app_id = #{appId} and del_flag = 0")
    void delTenant(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    //  @SelectProvider(type = RecordPermissSQLProvider.class, method = "queryRecordObjectTeams")
    List<Team> queryRecordObjectTeams(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects);

    //  @SelectProvider(type = RecordPermissSQLProvider.class, method = "queryRecordPermiss")
    List<RecordPermiss> queryRecordPermiss(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects);

    //  @SelectProvider(type = RecordPermissSQLProvider.class, method = "queryRecordPermissObjects")
    List<String> queryRecordPermissObjects(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("filters") Collection filters,
            @Param("pageInfo") PageInfo pageInfo);

    //  @SelectProvider(type = RecordPermissSQLProvider.class, method = "queryRecordPermissObjectsCount")
    long queryRecordPermissObjectsCount(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("filters") Collection filters);

    //  @SelectProvider(type = RecordPermissSQLProvider.class, method = "delRecordPermiss")
    void delRecordPermiss(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects,
            @Param("modifier") String modifier);

    //  @SelectProvider(type = RecordPermissSQLProvider.class, method = "delRecordObjectsTeamAllMember")
    void delRecordObjectsTeamAllMember(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects,
            @Param("modifier") String modifier);

    //  @SelectProvider(type = RecordPermissSQLProvider.class, method = "recordPermiss")
    List<RecordPermiss> recordPermiss(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("depts") Collection depts,
            @Param("owners") Collection owners,
            @Param("objects") Collection objects,
            @Param("union") Boolean union);

    //  @UpdateProvider(type = RecordPermissSQLProvider.class, method = "updateRecordPermissOwner")
    void updateRecordPermissOwner(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects,
            @Param("owner") String owner,
            @Param("newOwner") String newOwner);

    //  @UpdateProvider(type = RecordPermissSQLProvider.class, method = "updateRecordPermissDept")
    void updateRecordPermissDept(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("objects") Collection objects,
            @Param("dept") String dept,
            @Param("newDept") String newDept);

}
