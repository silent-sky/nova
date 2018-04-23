package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.Role;
import com.nova.paas.common.pojo.PageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 11:23
 */
@Mapper
public interface RoleMapper {

    void insert(Role role);

    void batchDelete(
            @Param("tenantId") String tenantId,
            @Param("modifiedBy") String modifiedBy,
            @Param("roleIds") Set<String> funcIds,
            @Param("modifiedAt") long modifiedAt);

    void update(Role role);

    List<Role> queryRoleListByPage(
            @Param("tenantId") String tenantId,
            @Param("ids") Set<String> ids,
            @Param("roleName") String roleName,
            @Param("roleCode") String roleCode,
            @Param("roleType") Integer roleType,
            @Param("page") PageInfo page);

}
