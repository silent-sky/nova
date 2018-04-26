package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.common.pojo.CommonContext;

import java.util.Map;

/**
 * 角色、字段权限的关联服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface FieldAccessService {

    void update(CommonContext context, String roleId, String entityId, Map<String, Integer> fieldPermission) throws AuthServiceException;

    Map<String, Integer> findFieldAccessByRole(CommonContext context, String roleId, String entityId) throws AuthServiceException;

    Map<String, Integer> findFieldAccessByUser(CommonContext context, String userId, String entityId) throws AuthServiceException;

}
