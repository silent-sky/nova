package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 11:23
 */
@Mapper
public interface UserRoleMapper {

    void batchInsert(List<UserRole> userRoleList);

    void batchDeleteByUsers(
            @Param("tenantId") String tenantId,
            @Param("roleId") String roleId,
            @Param("targetIds") Set<String> targetIds,
            @Param("targetType") Integer targetType,
            @Param("modifiedBy") String modifiedBy,
            @Param("modifiedAt") Long modifiedAt);

    void batchDeleteByRoles(
            @Param("tenantId") String tenantId,
            @Param("roleIds") Set<String> roleIds,
            @Param("targetId") String targetId,
            @Param("targetType") Integer targetType,
            @Param("modifiedBy") String modifiedBy,
            @Param("modifiedAt") Long modifiedAt);

    void update(UserRole userRole);

    List<UserRole> findUserByRole(
            @Param("tenantId") String tenantId, @Param("roleId") String roleId, @Param("targetType") Integer targetType);

    List<UserRole> findRoleByUser(
            @Param("tenantId") String tenantId, @Param("targetId") String targetId);

}
