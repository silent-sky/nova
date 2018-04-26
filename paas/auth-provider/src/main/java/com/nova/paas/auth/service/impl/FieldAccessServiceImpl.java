package com.nova.paas.auth.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.FieldAccess;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.FieldAccessMapper;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.auth.service.FieldAccessService;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service
@Slf4j
public class FieldAccessServiceImpl implements FieldAccessService {

    @Inject
    private FieldAccessMapper fieldAccessMapper;
    @Inject
    private UserRoleService userRoleService;

    @Override
    public void update(CommonContext context, String roleId, String entityId, Map<String, Integer> fieldPermission) throws AuthServiceException {
        fieldAccessMapper.batchDelete(context.getTenantId(), roleId, entityId, context.getUserId(), System.currentTimeMillis());
        List<FieldAccess> fieldAccessList = Lists.newArrayList();
        for (Map.Entry<String, Integer> entry : fieldPermission.entrySet()) {
            FieldAccess fieldAccess = new FieldAccess().builder()
                    .id((IdUtil.generateId()))
                    .tenantId(context.getTenantId())
                    .roleId(roleId)
                    .entityId(entityId)
                    .fieldId(entry.getKey())
                    .permission(entry.getValue())
                    .modifiedBy(context.getUserId())
                    .modifiedAt(System.currentTimeMillis())
                    .build();
            fieldAccessList.add(fieldAccess);
        }
        fieldAccessMapper.batchInsert(fieldAccessList);
    }

    @Override
    public Map<String, Integer> findFieldAccessByRole(CommonContext context, String roleId, String entityId) throws AuthServiceException {
        Map<String, Integer> permissionMap = Maps.newHashMap();
        List<FieldAccess> fieldAccessList = fieldAccessMapper.findFieldAccessByRole(context.getTenantId(), roleId, entityId);
        if (CollectionUtils.isNotEmpty(fieldAccessList)) {
            for (FieldAccess fieldAccess : fieldAccessList) {
                String fieldId = fieldAccess.getFieldId();
                Integer newPermission = fieldAccess.getPermission();
                if (!permissionMap.containsKey(fieldId)) {
                    permissionMap.put(fieldId, newPermission);
                } else {
                    if (newPermission > permissionMap.get(fieldId)) {
                        permissionMap.put(fieldId, newPermission);
                    }
                }
            }
        }
        return permissionMap;
    }

    @Override
    public Map<String, Integer> findFieldAccessByUser(CommonContext context, String userId, String entityId) throws AuthServiceException {
        Map<String, Integer> permissionMap = Maps.newHashMap();
        List<UserRolePojo> pojoList = userRoleService.getUserRoleRelationByUser(context, userId);
        if (CollectionUtils.isNotEmpty(pojoList)) {
            Set<String> roleIds = Sets.newHashSet();
            for (UserRolePojo pojo : pojoList) {
                String roleId = pojo.getRoleId();
                roleIds.add(roleId);
            }
            List<FieldAccess> fieldAccessList = fieldAccessMapper.findFieldAccessByRoles(context.getTenantId(), roleIds, entityId);
            if (CollectionUtils.isNotEmpty(fieldAccessList)) {
                for (FieldAccess fieldAccess : fieldAccessList) {
                    String fieldId = fieldAccess.getFieldId();
                    Integer newPermission = fieldAccess.getPermission();
                    if (!permissionMap.containsKey(fieldId)) {
                        permissionMap.put(fieldId, newPermission);
                    } else {
                        if (newPermission > permissionMap.get(fieldId)) {
                            permissionMap.put(fieldId, newPermission);
                        }
                    }
                }
            }
        }

        return permissionMap;
    }
}
