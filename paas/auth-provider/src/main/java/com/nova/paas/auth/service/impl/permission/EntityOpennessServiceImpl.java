package com.nova.paas.auth.service.impl.permission;

import com.nova.paas.auth.entity.permission.EntityOpenness;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityOpennessMapper;
import com.nova.paas.auth.pojo.permission.EntityOpennessPojo;
import com.nova.paas.auth.service.permission.EntityOpennessService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.support.CacheManager;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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
public class EntityOpennessServiceImpl implements EntityOpennessService {

    @Override
    public void createEntityOpenness(CommonContext context, List<EntityOpennessPojo> entityOpennessPojoList) throws AuthServiceException {

    }

    @Override
    public List<EntityOpennessPojo> queryEntityOpenness(
            CommonContext context, Set<String> entitys, Integer permission, Integer scope, PageInfo page) throws AuthServiceException {
        return null;
    }

    @Override
    public Integer queryEntityOpennessScopeByEntity(CommonContext context, String entityId) throws AuthServiceException {
        return null;
    }

    @Override
    public void delEntityOpenness(CommonContext context, Set<String> entitys) throws AuthServiceException {

    }

    @Override
    public void updateEntityOpenness(CommonContext context, List<EntityOpennessPojo> entityOpennessList) throws AuthServiceException {

    }

    @Override
    public EntityOpennessPojo queryEntityOpennessByEntity(CommonContext context, String entityId) throws AuthServiceException {
        return null;
    }

    @Override
    public void removeEntityOpennessRedisCache(CommonContext context, Set<String> entitys) throws AuthServiceException {

    }
}
