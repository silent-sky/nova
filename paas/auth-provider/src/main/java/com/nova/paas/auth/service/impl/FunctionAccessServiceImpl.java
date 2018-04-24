package com.nova.paas.auth.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.FuncAccess;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.FunctionAccessMapper;
import com.nova.paas.auth.param.QryFunctionParam;
import com.nova.paas.auth.pojo.FunctionAccessPojo;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.auth.service.FunctionAccessService;
import com.nova.paas.auth.service.FunctionService;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class FunctionAccessServiceImpl implements FunctionAccessService {

    @Inject
    private FunctionAccessMapper functionAccessMapper;
    @Inject
    private UserRoleService userRoleService;
    @Inject
    private FunctionService functionService;

    @Override
    public void addFuncToRole(CommonContext context, String roleId, Set<String> funcIds) throws AuthServiceException {
        List<FuncAccess> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(funcIds)) {
            for (String funcId : funcIds) {
                FuncAccess funcAccess = new FuncAccess().builder()
                        .id(IdUtil.generateId())
                        .tenantId(context.getTenantId())
                        .roleId(roleId)
                        .funcId(funcId)
                        .modifiedBy(context.getUserId())
                        .modifiedAt(System.currentTimeMillis())
                        .build();
                list.add(funcAccess);
            }
        }
        functionAccessMapper.batchInsert(list);
    }

    @Override
    public void batchDelete(CommonContext context, String roleId, Set<String> funcIds) throws AuthServiceException {

        functionAccessMapper.batchDelete(context.getTenantId(), roleId, funcIds, context.getUserId(), System.currentTimeMillis());
    }

    @Override
    public List<FunctionAccessPojo> findFuncAccessByRole(CommonContext context, String roleId) throws AuthServiceException {

        List<FuncAccess> list = functionAccessMapper.findFuncAccessByRole(context.getTenantId(), roleId);
        List<FunctionAccessPojo> pojoList = this.convertFuncAccessToPojos(list);
        return pojoList;
    }

    @Override
    public List<FunctionAccessPojo> findFuncAccessByFunc(CommonContext context, String funcId) throws AuthServiceException {
        List<FuncAccess> list = functionAccessMapper.findFuncAccessByFunc(context.getTenantId(), funcId);
        List<FunctionAccessPojo> pojoList = this.convertFuncAccessToPojos(list);
        return pojoList;
    }

    @Override
    public List<FunctionPojo> queryFuncListByUser(CommonContext context, String userId) throws AuthServiceException {
        Set<String> funcIds = this.queryFuncIdsByUser(context, userId);
        List<FunctionPojo> functions = functionService.queryFunction(context, new QryFunctionParam().builder().ids(funcIds).build());

        return functions;
    }

    @Override
    public Set<String> queryFuncIdsByUser(CommonContext context, String userId) throws AuthServiceException {
        if (StringUtils.isBlank(userId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Set<String> funcIds = Sets.newHashSet();
        List<UserRolePojo> userRoleList = userRoleService.getUserRoleRelationByUser(context, userId);
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            Set<String> roleIds = Sets.newHashSet();
            for (UserRolePojo pojo : userRoleList) {
                roleIds.add(pojo.getRoleId());
            }
            funcIds = functionAccessMapper.findFuncIdsByRoles(context.getTenantId(), roleIds);
        }

        return funcIds;
    }

    @Override
    public Map<String, Boolean> checkFuncPermission(CommonContext context, String userId, Set<String> funcIds) throws AuthServiceException {
        Set<String> validFuncIds = this.queryFuncIdsByUser(context, userId);
        Map<String, Boolean> resultMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(validFuncIds)) {
            for (String funcId : funcIds) {
                Boolean value = Boolean.FALSE;
                if (validFuncIds.contains(value)) {
                    value = Boolean.TRUE;
                }
                resultMap.put(funcId, value);
            }
        } else {
            for (String funcId : funcIds) {
                resultMap.put(funcId, Boolean.FALSE);
            }
        }
        return resultMap;
    }

    private List<FunctionAccessPojo> convertFuncAccessToPojos(List<FuncAccess> funcAccessList) {
        List<FunctionAccessPojo> pojoList = Lists.newArrayList();
        for (FuncAccess funcAccess : funcAccessList) {
            FunctionAccessPojo pojo = new FunctionAccessPojo();
            try {
                PropertyUtils.copyProperties(pojo, funcAccess);
            } catch (Exception e) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            pojoList.add(pojo);
        }
        return pojoList;
    }

}
