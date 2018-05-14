package com.nova.paas.auth.service.impl.permission;

import com.nova.paas.auth.entity.permission.RecordPermiss;
import com.nova.paas.auth.entity.permission.Team;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.RecordPermissMapper;
import com.nova.paas.auth.mapper.permission.TeamMapper;
import com.nova.paas.auth.pojo.permission.EntityObjects;
import com.nova.paas.auth.pojo.permission.QueryRecordPermissObjectFilter;
import com.nova.paas.auth.pojo.permission.RecordPermissObjectPojo;
import com.nova.paas.auth.pojo.permission.RecordPermissPojo;
import com.nova.paas.auth.pojo.permission.RecordPermissTeamPojo;
import com.nova.paas.auth.pojo.permission.TeamMembersPojo;
import com.nova.paas.auth.pojo.permission.TeamPojo;
import com.nova.paas.auth.service.permission.RecordPermissService;
import com.nova.paas.auth.support.CommonParamsCheckUtil;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
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
public class RecordPermissServiceImpl implements RecordPermissService {

    @Override
    public void addRecordPermiss(CommonContext context, List<RecordPermissTeamPojo> recordPermissTeams) throws AuthServiceException {

    }

    @Override
    public void createRecordPermiss(CommonContext context, RecordPermissObjectPojo recordPermissObject) throws AuthServiceException {

    }

    @Override
    public List<RecordPermissPojo> queryRecordPermiss(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects, boolean union, PageInfo page)
            throws AuthServiceException {
        return null;
    }

    @Override
    public List<String> queryRecordPermissObjects(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects, boolean union, PageInfo page)
            throws AuthServiceException {
        return null;
    }

    @Override
    public List<String> queryRecordPermissObject(
            CommonContext context, String entityId, List<QueryRecordPermissObjectFilter> filters, PageInfo pageInfo) throws AuthServiceException {
        return null;
    }

    @Override
    public void updateRecordPermiss(CommonContext context, List<RecordPermissTeamPojo> recordPermissTeams) throws AuthServiceException {

    }

    @Override
    public void delRecordPermiss(CommonContext context, List<EntityObjects> entityObjectsList) throws AuthServiceException {

    }

    @Override
    public void delRecordPermiss(CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects)
            throws AuthServiceException {

    }

    @Override
    public void updateRecordPermissOwner(CommonContext context, String entityId, Set<String> objects, String owner, String newOwner)
            throws AuthServiceException {

    }

    @Override
    public void updateRecordPermissDept(CommonContext context, String entityId, Set<String> objects, String dept, String newDept)
            throws AuthServiceException {

    }

    @Override
    public Map<String, List<TeamPojo>> queryRecordPermissTeam(CommonContext context, String entityId, Set<String> objects)
            throws AuthServiceException {
        return null;
    }

    @Override
    public void delTenant(CommonContext context) throws AuthServiceException {

    }
}
