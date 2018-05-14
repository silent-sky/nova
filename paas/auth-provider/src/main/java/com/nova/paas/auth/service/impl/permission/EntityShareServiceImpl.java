package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.permission.EntityShare;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityShareMapper;
import com.nova.paas.auth.pojo.permission.EntitySharePojo;
import com.nova.paas.auth.service.permission.EntityShareCacheService;
import com.nova.paas.auth.service.permission.EntityShareService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityShareServiceImpl implements EntityShareService {

    @Override
    public void createEntityShare(CommonContext context, List<EntitySharePojo> shareList) throws AuthServiceException {

    }

    @Override
    public void delEntityShare(CommonContext context, Set<String> entityShareIds, Integer status) throws AuthServiceException {

    }

    @Override
    public void delEntityShareByEntitys(CommonContext context, Set<String> entitys) throws AuthServiceException {

    }

    @Override
    public List<EntitySharePojo> queryEntityShareByReceives(
            CommonContext context, String entityId, Map<Integer, Set<String>> receives, Integer status) throws AuthServiceException {
        return null;
    }

    @Override
    public List<EntitySharePojo> queryEntityShare(
            CommonContext context,
            Set<String> ids,
            Set<String> entitys,
            Set<Integer> sourceTypes,
            Set<String> sources,
            Set<Integer> receiveTypes,
            Set<String> receives,
            Integer permission,
            Integer status,
            Set<String> sharesOrReceives,
            Map<Integer, Set<String>> sharesId,
            Map<Integer, Set<String>> receivesId,
            Map<Integer, Set<String>> sharesOrReceivesId,
            PageInfo page) throws AuthServiceException {
        return null;
    }

    @Override
    public void updateEntityShare(CommonContext context, EntitySharePojo pojo) throws AuthServiceException {

    }

    @Override
    public void updateEntitySharePermissionByRule(CommonContext context, EntitySharePojo pojo) throws AuthServiceException {

    }

    @Override
    public void updateEntityShareStatus(CommonContext context, Set<String> entityShareIds, Integer status) throws AuthServiceException {

    }

    @Override
    public Set<String> queryRuleIdsByOrg(CommonContext context, Set<String> orgIds, Integer type, Integer status, Boolean asShare)
            throws AuthServiceException {
        return null;
    }

    @Override
    public List<EntitySharePojo> queryRulePojoByOrgId(CommonContext context, Set<String> orgIds, Integer type, Integer status, Boolean asShare)
            throws AuthServiceException {
        return null;
    }

    @Override
    public List<EntitySharePojo> queryRuleByShares(
            CommonContext context, String entityId, Integer shareType, Set<String> shares, Integer status) throws AuthServiceException {
        return null;
    }
}
