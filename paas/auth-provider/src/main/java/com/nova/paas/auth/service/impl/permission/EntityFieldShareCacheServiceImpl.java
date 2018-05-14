package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.permission.EntityFieldShareCache;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityFieldShareCacheMapper;
import com.nova.paas.auth.service.permission.EntityFieldShareCacheService;
import com.nova.paas.auth.service.permission.EntityFieldShareCacheVersionService;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityFieldShareCacheServiceImpl implements EntityFieldShareCacheService {

    @Override
    public void updateDataVersions(CommonContext context, String entityId, String dataId, List<String> versions) throws AuthServiceException {

    }

    @Override
    public void addDataVersions(CommonContext context, String entityId, String dataId, List<String> versions) throws AuthServiceException {

    }

    @Override
    public List<String> dataVersions(CommonContext context, String entityId, String dataId) throws AuthServiceException {
        return null;
    }

    @Override
    public void datasAddVersion(CommonContext context, String entityId, String version, List<String> dataIds) throws AuthServiceException {

    }

    @Override
    public void deleteVersion(CommonContext context, String entityId, String version) throws AuthServiceException {

    }

    @Override
    public void calculateRuleCache(CommonContext context, String entityId, String ruleCode) throws AuthServiceException {

    }

    @Override
    public void consumerAppDataUpdate(
            CommonContext context, String entityId, String id, String operator, Map<String, Object> dataMap) throws AuthServiceException {

    }

    @Override
    public void calculateDatasCache(CommonContext context, String entityId, Set<String> dataIds) throws AuthServiceException {

    }

    @Override
    public void deleteDatas(CommonContext context, String entityId, Set<String> dataIds) throws AuthServiceException {

    }
}
