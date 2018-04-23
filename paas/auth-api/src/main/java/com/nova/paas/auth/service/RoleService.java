package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.param.QryRoleParam;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Set;

/**
 * 角色的操作接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface RoleService {

    void addRole(CommonContext context, RolePojo pojo) throws AuthServiceException;

    void batchDeleteRole(CommonContext context, Set<String> idSet) throws AuthServiceException;

    void updateRole(CommonContext context, RolePojo pojo) throws AuthServiceException;

    List<RolePojo> queryRoleListByPage(CommonContext context, QryRoleParam param) throws AuthServiceException;
}
