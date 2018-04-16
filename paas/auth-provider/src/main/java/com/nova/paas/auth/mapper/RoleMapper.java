package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.Role;
import com.nova.paas.common.pojo.PageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 11:23
 */
@Mapper
public interface RoleMapper {

    @Update("update auth_role set  del_flag=true,modifier=#{modifier},modify_time=#{modifyTime} where tenant_id=#{tenantId}  and  app_id=#{appId} and role_code=#{roleCode} and del_flag=false")
    boolean delRole(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    @Update("update auth_role set role_name=#{roleName},description=#{description},modifier=#{modifier},modify_time=#{modifyTime} where tenant_id=#{tenantId}  and  app_id=#{appId} and role_code=#{roleCode} and del_flag=false")
    boolean updateRole(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("roleName") String roleName,
            @Param("description") String description,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    @Select("select * from auth_role where tenant_id=#{tenantId}  and  app_id=#{appId}  and   role_code=#{roleCode} and del_flag=false")
    Role queryRoleByCode(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode);

    List<Role> queryRole(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("roleName") String roleName,
            @Param("roleType") Integer roleType,
            @Param("delFlag") Boolean delFlag);

    List<Role> queryRoleByPage(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("roleName") String roleName,
            @Param("roleType") Integer roleType,
            @Param("start") Integer start,
            @Param("page") PageInfo page);

    int queryRole2Count(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("roleName") String roleName,
            @Param("roleType") Integer roleType);

    Set<String> roleNameOrRoleCodeExists(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Set<String> roleCodes,
            @Param("roleNames") Set<String> roleNames,
            @Param("delFlag") Boolean delFlag);

    void batchDel(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("roleName") String roleName,
            @Param("roleType") Integer roleType,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    List<Role> queryMatchRole(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("key") String key,
            @Param("roleType") Integer roleType,
            @Param("start") Integer start,
            @Param("page") PageInfo page);

    int queryMatchRoleCount(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("key") String key,
            @Param("roleType") Integer roleType);

}
