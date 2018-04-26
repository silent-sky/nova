package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.FieldAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface FieldAccessMapper {

    void batchInsert(List<FieldAccess> fieldAccessList);

    void batchDelete(
            @Param("tenantId") String tenantId,
            @Param("roleId") String roleId,
            @Param("entityId") String entityId,
            @Param("modifiedBy") String modifiedBy,
            @Param("modifiedAt") Long modifiedAt);

    List<FieldAccess> findFieldAccessByRole(@Param("tenantId") String tenantId, @Param("roleId") String roleId, @Param("entityId") String entityId);

    List<FieldAccess> findFieldAccessByRoles(
            @Param("tenantId") String tenantId, @Param("roleIds") Set<String> roleIds, @Param("entityId") String entityId);

}
