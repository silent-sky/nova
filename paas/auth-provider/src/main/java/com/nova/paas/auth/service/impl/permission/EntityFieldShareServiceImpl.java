package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.permission.EntityFieldShareReceive;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityFieldShareReceiveMapper;
import com.nova.paas.auth.pojo.permission.EntityFieldSharePojo;
import com.nova.paas.auth.pojo.permission.EntityFieldShareReceivePojo;
import com.nova.paas.auth.pojo.permission.FieldShareRulePojo;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.permission.EntityFieldShareService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.util.IdUtil;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityFieldShareServiceImpl implements EntityFieldShareService {

    @Override
    public String create(CommonContext context, EntityFieldSharePojo entityFieldShare) throws AuthServiceException {
        return null;
    }

    @Override
    public String update(CommonContext context, EntityFieldSharePojo entityFieldShare) throws AuthServiceException {
        return null;
    }

    @Override
    public void delete(CommonContext context, String entityId, Set<String> ruleCodes, Integer status) throws AuthServiceException {

    }

    @Override
    public List<EntityFieldSharePojo> query(
            CommonContext context,
            String entityId,
            String ruleName,
            Integer status,
            Set<String> ruleCodes,
            Map<Integer, Set<String>> receivesWithType,
            Set<String> receives,
            Integer permission,
            PageInfo pageInfo) throws AuthServiceException {
        return null;
    }

    @Override
    public void updateEntityFieldShareStatus(CommonContext context, String entityId, Set<String> ruleCodes, Integer status)
            throws AuthServiceException {

    }

    @Override
    public List<String> fieldShareReceiveSql(CommonContext context, String entityId) throws AuthServiceException {
        return null;
    }

    @Override
    public List<EntityFieldShareReceivePojo> userReceivedRule(CommonContext context, String entityId) throws AuthServiceException {
        return null;
    }

    @Override
    public Map<String, Map<String, Object>> dataRuleExpressionPattern(
            CommonContext context, String entityId, Set<String> ruleCodes, Set<String> dataIds) throws AuthServiceException {
        return null;
    }

    @Override
    public List<FieldShareRulePojo> entityFieldRule(
            CommonContext context, String entity, Map<String, List<String>> fields) throws AuthServiceException {
        return null;
    }
}
