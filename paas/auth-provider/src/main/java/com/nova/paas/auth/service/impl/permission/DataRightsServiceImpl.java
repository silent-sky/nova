package com.nova.paas.auth.service.impl.permission;

import com.nova.paas.auth.entity.permission.EntityShareCache;
import com.nova.paas.auth.entity.permission.Team;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityFieldShareReceiveMapper;
import com.nova.paas.auth.mapper.permission.EntityShareCacheMapper;
import com.nova.paas.auth.mapper.permission.TeamMapper;
import com.nova.paas.auth.mapper.permission.UserDeptRelationCacheMapper;
import com.nova.paas.auth.mapper.permission.UserLeaderCacheMapper;
import com.nova.paas.auth.pojo.permission.EntityFieldShareReceivePojo;
import com.nova.paas.auth.pojo.permission.EntityOpennessPojo;
import com.nova.paas.auth.pojo.permission.EntitySharePojo;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.permission.DataRightsService;
import com.nova.paas.auth.service.permission.EntityFieldShareService;
import com.nova.paas.auth.service.permission.EntityOpennessService;
import com.nova.paas.auth.service.permission.EntityShareService;
import com.nova.paas.common.constant.PermissionConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 17:19
 */
@Slf4j
@Service
public class DataRightsServiceImpl implements DataRightsService {

    @Override
    public Map<String, Integer> dataPermission(
            CommonContext context,
            String entityId,
            Set<String> dataOwners,
            boolean userParentDeptCascade,
            boolean userDeputyDept,
            boolean deptUsersCascade,
            boolean userSubordinatesCascade,
            boolean userResponsibleDeptUsersCascade) throws AuthServiceException {
        return null;
    }

    @Override
    public Map<Integer, Set<String>> userAccessData(
            CommonContext context,
            String entityId,
            boolean deptConvertToUser,
            boolean userParentDeptCascade,
            boolean userSubordinatesCascade,
            boolean userResponsibleDeptUsersCascade,
            boolean userDeputyDept) throws AuthServiceException {
        return null;
    }

    @Override
    public Map<Integer, Set<String>> userSharedData(
            CommonContext context, String entityId, boolean deptConvertToUser, boolean userParentDeptCascade, boolean userDeputyDept)
            throws AuthServiceException {
        return null;
    }

    @Override
    public Set<String> userSubordinates(CommonContext context, boolean userSubCascade) throws AuthServiceException {
        return null;
    }

    @Override
    public Set<String> userResponsibleDeptUsers(CommonContext context, boolean deptCascade) throws AuthServiceException {
        return null;
    }

    @Override
    public void delDataRights(CommonContext context, Set<String> entitys) throws AuthServiceException {

    }

    @Override
    public Map<String, Integer> entityObjectsPermission(
            CommonContext context, String entityId, Set<String> objects, String ownerRoleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException {
        return null;
    }

    @Override
    public Map<String, Integer> objectsPermissionCalculate(
            CommonContext context, String entityId, Set<String> objects, String ownerRoleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException {
        return null;
    }

    @Override
    public String dataRightsSql(
            CommonContext context, String entityId, String sceneType, String roleType, boolean cascadeDept, boolean cascadeSubordinates)
            throws AuthServiceException {
        return null;
    }
}

