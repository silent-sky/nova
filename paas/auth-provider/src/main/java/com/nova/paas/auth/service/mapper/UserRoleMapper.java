package com.nova.paas.auth.service.mapper;

import com.nova.paas.auth.service.entity.Role;
import com.nova.paas.auth.service.entity.UserRole;
import com.nova.paas.common.pojo.PageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 11:23
 */
@Mapper
public interface UserRoleMapper {

    @Select("select  distinct(org_id)   from  auth_user_role  where   tenant_id=#{tenantId}   and  app_id=#{appId}  and role_code=#{roleCode} and org_type = #{orgType}  and del_flag=false ")
    List<String> queryUserIdByRoleCode(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode, @Param("orgType") Integer orgType);

    @Select("SELECT role_code FROM auth_user_role WHERE tenant_id = #{tenantId} and app_id = #{appId} and org_id = #{orgId} and org_type = #{orgType} and del_flag = false   ")
    List<String> queryRoleCodeListByUserId(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("orgId") String orgId, @Param("orgType") Integer orgType);

    @Select("select * from auth_role where tenant_id = #{tenantId} and app_id = #{appId} and del_flag = false and role_code in"
                    + " (select role_code FROM auth_user_role where tenant_id = #{tenantId} and app_id = #{appId} and org_id = #{orgId} and org_type = #{orgType} and del_flag = false )")
    List<Role> queryRoleByUser(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("orgId") String orgId, @Param("orgType") Integer orgType);

    @Select("select * from auth_user_role where tenant_id = #{tenantId} and app_id = #{appId} and role_code = #{roleCode} and org_type = #{orgType} and del_flag = false  ")
    List<UserRole> queryUserRole(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode, @Param("orgType") Integer orgType);

    void batchDel(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("orgIdList") Collection<String> orgIdList,
            @Param("orgType") Integer orgType,
            @Param("userId") String userId,
            @Param("modifyTime") long modifyTime);

    List<UserRole> queryUserRoleProvider(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roles") Collection<String> roles,
            @Param("orgIdList") Collection<String> orgIdList,
            @Param("orgType") Integer orgType,
            @Param("delFlag") Boolean delFlag);

    List<String> queryOrgIdsOrRoles(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("orgIdList") Collection<String> orgIdList,
            @Param("orgType") Integer orgType,
            @Param("delFlag") Boolean delFlag,
            @Param("result") String result,
            @Param("excludeRoles") Collection<String> excludeRoles,
            @Param("page") PageInfo page,
            @Param("start") Integer start);

    int queryOrgIdsOrRolesCount(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("orgIdList") Collection<String> orgIdList,
            @Param("orgType") Integer orgType,
            @Param("delFlag") Boolean delFlag,
            @Param("result") String result,
            @Param("excludeRoles") Collection<String> excludeRoles);

    void setDefaultRoleFlag(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("idList") Collection<String> idList,
            @Param("orgType") Integer orgType,
            @Param("flag") boolean flag,
            @Param("userId") String userId,
            @Param("modifyTime") long modifyTime);

    int updateUserRoleDeptId(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("orgType") Integer orgType,
            @Param("roleCode") String roleCode,
            @Param("orgId") String orgId,
            @Param("dept") String dept,
            @Param("userId") String userId,
            @Param("modifyTime") long modifyTime);

    List<UserRole> queryEntityByUserRole(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Set<String> roleCode,
            @Param("orgType") Integer orgType,
            @Param("orgId") String orgId,
            @Param("start") Integer start,
            @Param("pageSize") Integer pageSize);

    int queryEntityByUserRoleCount(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Set<String> roleCode,
            @Param("orgType") Integer orgType,
            @Param("orgId") String orgId);

    Set<String> queryUsersByRoleDept(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Set<String> roleCode,
            @Param("orgType") Integer orgType,
            @Param("deptId") String deptId);

    @Select("select count(id) from auth_user_role where tenant_id = #{tenantId} and app_id = #{appId} and role_code = #{roleCode} and del_flag=0 and dept_id is not null and dept_id!='' and dept_id!='0'")
    int queryValidUserNumByRole(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode);

}
