package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.FieldAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface FieldAccessMapper {

    @Select("select  *  from  auth_field_access  where  tenant_id=#{tenantId}   and  app_id=#{appId}   and role_code=#{roleCode}  and  entity_id=#{entityId} and del_flag = false ")
    List<FieldAccess> queryEntityFieldPermission(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode, @Param("entityId") String entityId);

    @Update("update auth_field_access set del_flag = true,modifier = #{modifier},modify_time = #{modifyTime} where  tenant_id=#{tenantId}   and  app_id=#{appId}   and role_code=#{roleCode}  and  entity_id=#{entityId} and del_flag=false")
    int delEntityFieldPermission(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("entityId") String entityId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    void batchDel(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("entityId") String entityId,
            @Param("fieldId") String fieldId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    void batchDelSupportRoles(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roles") Collection roles,
            @Param("entityId") String entityId,
            @Param("fieldId") String fieldId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    List<FieldAccess> queryFieldAccess(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCode,
            @Param("entitys") Collection<String> entitys,
            @Param("fields") Collection<String> fields,
            @Param("delFlag") Boolean delFlag);

    @Select("select  distinct entity_id from  auth_field_access  where  tenant_id=#{tenantId}   and  app_id=#{appId}   and role_code=#{roleCode} and del_flag=false")
    Set<String> queryEntityList(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode);

    List<FieldAccess> queryFieldAccessByEntityRolesFilter(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("entityRoles") Map entityRoles);

}
