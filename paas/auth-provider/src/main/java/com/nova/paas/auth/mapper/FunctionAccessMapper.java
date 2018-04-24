package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.FuncAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface FunctionAccessMapper {

    void batchInsert(List<FuncAccess> funcAccessList);

    void batchDelete(
            @Param("tenantId") String tenantId,
            @Param("roleId") String roleId,
            @Param("funcIds") Set<String> funcIds,
            @Param("modifiedBy") String modifiedBy,
            @Param("modifiedAt") Long modifiedAt);

    List<FuncAccess> findFuncAccessByRole(@Param("tenantId") String tenantId, @Param("roleId") String roleId);

    List<FuncAccess> findFuncAccessByFunc(@Param("tenantId") String tenantId, @Param("funcId") String funcId);

    Set<String> findFuncIdsByRoles(@Param("tenantId") String tenantId, @Param("roleIds") Set<String> roleIds);
}
