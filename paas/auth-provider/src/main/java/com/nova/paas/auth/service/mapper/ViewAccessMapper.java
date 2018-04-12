package com.nova.paas.auth.service.mapper;

import com.nova.paas.auth.pojo.RoleViewPojo;
import com.nova.paas.auth.service.entity.ViewAccess;
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
public interface ViewAccessMapper {

    @Select("select * from auth_view_access where tenant_id=#{tenantId} and app_id=#{appId} and role_code=#{roleCode} and entity_id=#{entityId} and del_flag=false")
    List<ViewAccess> queryViewAccess(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode, @Param("entityId") String entityId);

    @Update("update auth_view_access set del_flag=true,modifier=#{modifier},modify_time=#{modifyTime} where tenant_id=#{tenantId} and app_id=#{appId} and role_code=#{roleCode} and entity_id=#{entityId} and del_flag=false")
    boolean delViewAccess(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("entityId") String entityId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    @Select("select view_id from auth_view_access where tenant_id=#{tenantId} and app_id=#{appId} and role_code=#{roleCode} and entity_id=#{entityId} and del_flag=false")
    Set<String> queryView(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode, @Param("entityId") String entityId);

    void batchDel(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("entityId") String entityId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    List<ViewAccess> queryViewAccessProvider(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("entityId") String entityId,
            @Param("recordTypeId") String recordTypeId,
            @Param("viewId") String viewId,
            @Param("delFlag") Boolean delFlag);

    @Select("select  distinct entity_id from  auth_view_access  where  tenant_id=#{tenantId}   and  app_id=#{appId}   and role_code=#{roleCode} and del_flag=false")
    Set<String> queryEntityList(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode);

    void delRoleViewAccess(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleViewPojos") List<RoleViewPojo> roleViewPojos,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    void deleteViewAccess(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("roleCode") String roleCode,
            @Param("recordTypeId") String recordTypeId,
            @Param("viewId") String viewId,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    List<ViewAccess> queryViewAccessByEntitys(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("entitys") Collection<String> entitys);

    List<ViewAccess> queryViewAccessByEntityIdBatch(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityIds") Set<String> entityIds,
            @Param("roleCodes") Set<String> roleCodes,
            @Param("recordTypeId") String recordTypeId);

}
