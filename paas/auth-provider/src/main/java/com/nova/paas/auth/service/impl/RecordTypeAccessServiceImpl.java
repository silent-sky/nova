package com.nova.paas.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.nova.paas.auth.entity.RecordTypeAccess;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.RecordTypeAccessMapper;
import com.nova.paas.auth.pojo.RoleRecordTypePojo;
import com.nova.paas.auth.service.RecordTypeAccessService;
import com.nova.paas.auth.service.RoleService;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service
@Slf4j
public class RecordTypeAccessServiceImpl implements RecordTypeAccessService {

    @Autowired
    private RecordTypeAccessMapper recordTypeAccessMapper;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional
    public void addRoleRecordType(CommonContext context, String entityId, String recordTypeId, List<RoleRecordTypePojo> recordTypePojos)
            throws AuthServiceException {
        log.info("[Request], method:{},context:{},entityId:{},recordTypeId:{},recordTypePojos:{}",
                "addRoleRecordType",
                JSON.toJSONString(context),
                entityId,
                recordTypeId,
                JSON.toJSONString(recordTypePojos));

        if (CollectionUtils.isEmpty(recordTypePojos)) {
            return;
        }

        this.entityVerify(entityId);
        this.recordTypeIdVerify(recordTypeId);

        Set<String> roleList = new HashSet<>();
        recordTypePojos.forEach(pojo -> {
            if (pojo == null) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }

            if (StringUtils.isBlank(pojo.getRoleId())) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            if (roleList.contains(pojo.getRoleId())) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            roleList.add(pojo.getRoleId());
        });
        this.rolesIsExist(context, roleList);

        List<RecordTypeAccess> dbData = this.queryRecordTypeAccessFromDB(context, roleList, Collections.singleton(entityId), recordTypeId);

        Set<String> dbRoles = new HashSet<>();
        if (CollectionUtils.isNotEmpty(dbData)) {
            dbData.forEach(entity -> {
                dbRoles.add(entity.getRoleId());
            });
        }

        List<RecordTypeAccess> list = new ArrayList<>();
        recordTypePojos.forEach(pojo -> {
            if (!dbRoles.contains(pojo.getRoleId())) {
                RecordTypeAccess recordTypeAccess = new RecordTypeAccess();
                recordTypeAccess.setId(IdUtil.generateId());
                recordTypeAccess.setTenantId(context.getTenantId());
                recordTypeAccess.setEntityId(entityId);
                recordTypeAccess.setRecordTypeId(recordTypeId);
                recordTypeAccess.setRoleId(pojo.getRoleId());

                recordTypeAccess.setModifiedBy(context.getUserId());
                recordTypeAccess.setModifiedAt(System.currentTimeMillis());
                recordTypeAccess.setDelFlag(Boolean.FALSE);
                list.add(recordTypeAccess);
            }
        });

        try {
            if (CollectionUtils.isNotEmpty(list)) {
                //                recordTypeAccessMapper.batchInsert(list);
            }
        } catch (Exception e) {
            log.error("addRoleRecordType-mapper.batchInsert error:", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

    }

    @Override
    @Transactional
    public void updateRoleRecordType(CommonContext context, String entityId, List<RoleRecordTypePojo> recordTypePojos) throws AuthServiceException {
        log.info("[Request], method:{},context:{},entityId:{},recordTypePojos:{}",
                "updateRoleRecordType",
                JSON.toJSONString(context),
                entityId,
                JSON.toJSONString(recordTypePojos));
        if (CollectionUtils.isEmpty(recordTypePojos)) {
            return;
        }

        this.entityVerify(entityId);

        //角色会重复，角色和一个recordTypeId的关联不能重复，角色的默认类型只能有一个
        Set<String> roleList = new HashSet<>();
        Set<String> only = new HashSet<>();
        recordTypePojos.forEach(pojo -> {
            if (pojo == null) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            if (StringUtils.isAnyBlank(pojo.getRoleId(), pojo.getRecordTypeId())) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            String check = pojo.getRoleId() + pojo.getRecordTypeId();
            if (only.contains(check)) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            only.add(check);

            roleList.add(pojo.getRoleId());

        });
        this.rolesIsExist(context, roleList);

        try {
            recordTypeAccessMapper.deleteRoleRecordType(context.getTenantId(),
                    context.getAppId(),
                    roleList,
                    entityId,
                    null,
                    null,
                    context.getUserId(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            log.error("mapper.deleteRoleRecordType error:", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        List<RecordTypeAccess> list = this.convertToEntity(context, entityId, recordTypePojos);
        try {
            //            recordTypeAccessMapper.batchInsert(list);
        } catch (Exception e) {
            log.error("===auth.updateRoleRecordType() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    @Override
    public boolean checkRecordType(CommonContext context, String entityId, String recordTypeId) throws AuthServiceException {

        this.entityVerify(entityId);
        this.recordTypeIdVerify(recordTypeId);

        return CollectionUtils.isNotEmpty(this.queryRecordTypeAccessFromDB(context, null, Collections.singleton(entityId), recordTypeId));
    }

    @Override
    public List<RoleRecordTypePojo> queryRoleRecordType(CommonContext context, String entityId, String roleCode) throws AuthServiceException {

        this.entityVerify(entityId);
        List<RecordTypeAccess> accessList;
        if (StringUtils.isBlank(roleCode)) {
            accessList = this.queryRecordTypeAccessFromDB(context, null, Collections.singleton(entityId), null);
        } else {
            accessList = this.queryRecordTypeAccessFromDB(context, Collections.singleton(roleCode), Collections.singleton(entityId), null);
        }
        return this.convertToPojo(accessList);
    }

    public void batchAddRoleRecordType(CommonContext context, Set<String> entityIds, String recordTypeId, String roleId)
            throws AuthServiceException {
        log.info("[Request], method:{},context:{},recordTypeId:{},roleCode:{},entityId:{}",
                "batchAddRoleRecordType",
                JSON.toJSONString(context),
                recordTypeId,
                roleId,
                JSON.toJSONString(entityIds));

        if (CollectionUtils.isEmpty(entityIds) || StringUtils.isAnyBlank(recordTypeId, roleId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        this.rolesIsExist(context, Collections.singleton(roleId));

        List<RecordTypeAccess> accessList = new ArrayList<>();
        entityIds.forEach(entity -> {
            if (StringUtils.isNotBlank(entity)) {
                RecordTypeAccess access = new RecordTypeAccess();
                access.setId(IdUtil.generateId());
                access.setTenantId(context.getTenantId());
                access.setEntityId(entity);
                access.setRecordTypeId(recordTypeId);
                access.setRoleId(roleId);
                access.setModifiedBy(context.getUserId());
                access.setModifiedAt(System.currentTimeMillis());
                access.setDelFlag(Boolean.FALSE);

                accessList.add(access);
            }
        });

        List<RecordTypeAccess> dbData = this.queryRecordTypeAccessFromDB(context, Collections.singleton(roleId), entityIds, recordTypeId);
        if (CollectionUtils.isNotEmpty(dbData)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (CollectionUtils.isNotEmpty(accessList)) {
            try {
                //                recordTypeAccessMapper.batchInsert(accessList);
            } catch (Exception e) {
                log.error("batchAddRoleRecordType-recordTypeAccessMapper.batchInsert error:", e);
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
            }
        }
    }

    public List<RoleRecordTypePojo> batchQueryRoleRecordType(CommonContext context, Set<String> entityIds, Set<String> roleCodes)
            throws AuthServiceException {
        if (entityIds != null) {
            entityIds.remove(null);
        }
        if (roleCodes != null) {
            roleCodes.remove(null);
        }
        if (CollectionUtils.isEmpty(entityIds) || CollectionUtils.isEmpty(roleCodes)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<RecordTypeAccess> accessList = this.queryRecordTypeAccessFromDB(context, roleCodes, entityIds, null);

        return this.convertToPojo(accessList);
    }

    private void pojoVerify(CommonContext context, List<RoleRecordTypePojo> pojos) throws AuthServiceException {
        if (CollectionUtils.isEmpty(pojos)) {
            return;
        }

        Set<String> roleCodes = new HashSet<>();
        pojos.forEach(pojo -> {
            if (pojo != null) {
                roleCodes.add(pojo.getRoleId());
            }
        });

        this.rolesIsExist(context, roleCodes);
    }

    /**
     * 对象实体校验
     */
    private void entityVerify(String entityId) {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void recordTypeIdVerify(String recordTypeId) {
        if (StringUtils.isBlank(recordTypeId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void rolesIsExist(CommonContext context, Set<String> roles) throws AuthServiceException {
        if (CollectionUtils.isNotEmpty(roles)) {
            if (roleService.roleCodeOrRoleNameExists(context, roles, null) != roles.size()) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
    }

    private List<RecordTypeAccess> queryRecordTypeAccessFromDB(
            CommonContext context, Collection<String> roleCodes, Set<String> entityId, String recordTypeId) {
        List<RecordTypeAccess> res;
        try {
            res = recordTypeAccessMapper.queryRecordTypeAccessProvider(context.getTenantId(), context.getAppId(), roleCodes, entityId, recordTypeId);
        } catch (Exception e) {
            log.error("mapper.queryViewAccessProvider error:", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return res;
    }

    private List<RoleRecordTypePojo> convertToPojo(List<RecordTypeAccess> typeAccessList) {
        List<RoleRecordTypePojo> res = new ArrayList<>();
        if (CollectionUtils.isEmpty(typeAccessList)) {
            return res;
        }

        typeAccessList.forEach(entity -> {
            RoleRecordTypePojo pojo = new RoleRecordTypePojo();
            try {
                PropertyUtils.copyProperties(pojo, entity);
            } catch (Exception e) {
                log.error("copyProperties error.para:{}", JSON.toJSONString(entity), e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            res.add(pojo);
        });

        return res;
    }

    private List<RecordTypeAccess> convertToEntity(CommonContext context, String entityId, List<RoleRecordTypePojo> pojos) {
        List<RecordTypeAccess> res = new ArrayList<>();
        if (CollectionUtils.isEmpty(pojos)) {
            return res;
        }

        pojos.forEach(pojo -> {
            if (pojo != null) {
                RecordTypeAccess roleView = new RecordTypeAccess();
                roleView.setId(IdUtil.generateId());
                roleView.setTenantId(context.getTenantId());
                roleView.setEntityId(entityId);
                roleView.setRecordTypeId(pojo.getRecordTypeId());
                roleView.setRoleId(pojo.getRoleId());

                roleView.setModifiedBy(context.getUserId());
                roleView.setModifiedAt(System.currentTimeMillis());
                roleView.setDelFlag(Boolean.FALSE);
                res.add(roleView);
            }
        });

        return res;
    }
}
