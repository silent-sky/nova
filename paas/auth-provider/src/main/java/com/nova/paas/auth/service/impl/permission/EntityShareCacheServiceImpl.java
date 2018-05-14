package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.permission.EntityShare;
import com.nova.paas.auth.entity.permission.EntityShareCache;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityShareCacheMapper;
import com.nova.paas.auth.mapper.permission.EntityShareMapper;
import com.nova.paas.auth.pojo.permission.EntityShareCachePojo;
import com.nova.paas.auth.pojo.permission.EntitySharePojo;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.permission.DataRightsService;
import com.nova.paas.auth.service.permission.EntityShareCacheService;
import com.nova.paas.auth.service.permission.EntityShareService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import com.nova.paas.org.pojo.DeptPojo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityShareCacheServiceImpl implements EntityShareCacheService {

    @Override
    public void delEntityShareCache(CommonContext context, Set<String> entityShareIds) throws AuthServiceException {

    }

    @Override
    public void delEntityShareCacheByEntitys(CommonContext context, Set<String> entitys) throws AuthServiceException {

    }

    @Override
    public void entityShareCacheReset(CommonContext context, Set<String> entityShareIds) throws AuthServiceException {

    }

    @Override
    public void tenantEntityShareCacheReset(CommonContext context) throws AuthServiceException {

    }

    @Override
    public void addUserToDeptCache(CommonContext context, Set<String> deptSet, Set<String> userSet, boolean noRoot) throws AuthServiceException {

    }

    @Override
    public void delUserFromDeptCache(CommonContext context, String deptId, Set<String> userSet) throws AuthServiceException {

    }

    @Override
    public void delRuleCacheBySet(CommonContext context, Set<String> deptSet, String userId) throws AuthServiceException {

    }

    @Override
    public void initAppEntityShareCache(String appId, Set<String> tenants, int currentPage) throws AuthServiceException {

    }

    @Override
    public void updatePermissionByRuleId(CommonContext context, Set<String> ruleIds, Integer permission) throws AuthServiceException {

    }

    @Override
    public List<EntityShareCachePojo> entityShareCache(
            CommonContext context, String entityId, String shareId, String shareUser, String receiveUser, PageInfo pageInfo)
            throws AuthServiceException {
        return null;
    }

    @Override
    public void addUserToGroupCache(CommonContext context, String groupId, Set<String> userSet) throws AuthServiceException {

    }

    @Override
    public void delUserFromGroupCache(CommonContext context, String groupId, Set<String> userSet) throws AuthServiceException {

    }

    @Override
    public void addUserToRoleCache(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException {

    }

    @Override
    public void delUserFromRoleCache(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException {

    }

    @Override
    public void updateUserRoles(CommonContext context, Set<String> roles, Set<String> users) throws AuthServiceException {

    }
}
