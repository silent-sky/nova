package com.nova.paas.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.nova.paas.auth.entity.FieldAccess;
import com.nova.paas.auth.entity.FuncAccess;
import com.nova.paas.auth.entity.Function;
import com.nova.paas.auth.entity.RecordTypeAccess;
import com.nova.paas.auth.entity.Role;
import com.nova.paas.auth.entity.UserRole;
import com.nova.paas.auth.entity.ViewAccess;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.mapper.FieldAccessMapper;
import com.nova.paas.auth.mapper.FunctionAccessMapper;
import com.nova.paas.auth.mapper.FunctionMapper;
import com.nova.paas.auth.mapper.RecordTypeAccessMapper;
import com.nova.paas.auth.mapper.RoleMapper;
import com.nova.paas.auth.mapper.UserRoleMapper;
import com.nova.paas.auth.mapper.ViewAccessMapper;
import com.nova.paas.auth.pojo.FunctionAccessPojo;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.auth.service.FunctionService;
import com.nova.paas.auth.service.TenantService;
import com.nova.paas.auth.support.TenantServiceUtil;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.support.CacheManager;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service
@Slf4j
public class TenantServiceImpl implements TenantService {

    @Autowired
    FunctionMapper funcMapper;
    @Autowired
    FunctionAccessMapper functionAccessMapper;
    @Autowired
    FieldAccessMapper fieldAccessMapper;
    @Autowired
    ViewAccessMapper viewAccessMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    RecordTypeAccessMapper recordTypeAccessMapper;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    FunctionService functionService;
    @Autowired
    TenantServiceUtil tenantServiceUtil;

    @Transactional
    public void clear(CommonContext context) {
        log.info("[Request], method:{},context:{}", "clear", JSON.toJSONString(context));

        //清空缓存
        this.cacheClear(context);

        //清空数据库
        this.dbClear(context);

    }

    /**
     * 重构企业缓存
     */
    public void cacheInit(CommonContext context) {
        log.info("[Request], method:{}, context:{}", "cacheInit", JSON.toJSONString(context));
        //缓存重构
        this.cacheReset(context);
    }

    /**
     * 企业缓存清空
     */
    public void cacheClear(CommonContext context) {
        log.info("[Request], method:{},context:{}", "cacheClear", JSON.toJSONString(context));
        //删除字段权限
        try {
            Set<String> entityIdSet = fieldAccessMapper.queryEntityList(context.getTenantId(), context.getAppId(), null);
            if (CollectionUtils.isNotEmpty(entityIdSet)) {
                for (String entityId : entityIdSet) {
                    //                    cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FIELD + ":" + entityId);
                }
            }

            //删除视图权限
            entityIdSet = viewAccessMapper.queryEntityList(context.getTenantId(), context.getAppId(), null);
            if (CollectionUtils.isNotEmpty(entityIdSet)) {
                for (String entityId : entityIdSet) {
                    //                    cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_VIEW + ":" + entityId);
                }
            }

            //删除功能权限
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION);

            //企业功能
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION);

            //用户角色
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE);

            //部门相关
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_DEPT);
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_ROLE_INFO);

        } catch (Exception e) {
            log.error("===auth.cacheClear() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 批量添加功能(无校验)
     */
    @Transactional
    public void batchAddFuncInit(CommonContext context, List<FunctionPojo> functionPojos) {
        log.info("[Request], method:{},context:{}", "batchAddFuncInit", JSON.toJSONString(context));
        if (CollectionUtils.isEmpty(functionPojos)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        try {
            List<Function> functions = new LinkedList<>();
            functionPojos.forEach(functionPojo -> {
                Function function = new Function();
                function.setId(IdUtil.generateId());
                function.setTenantId(context.getTenantId());
                function.setAppId(context.getAppId());
                function.setFuncName(functionPojo.getFuncName());
                function.setFuncCode(functionPojo.getFuncCode());
                function.setFuncType(functionPojo.getFuncType());
                function.setFuncOrder(functionPojo.getFuncOrder());
                function.setLevelCode(functionPojo.getLevelCode());
                function.setParentCode(functionPojo.getParentCode());
                function.setCreator(context.getUserId());
                function.setCreateTime(System.currentTimeMillis());
                function.setDelFlag(Boolean.FALSE);

                functions.add(function);
            });
            //            funcMapper.batchInsert(functions);
        } catch (Exception e) {
            log.error("===auth.batchAddFuncInit() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 批量添加角色(无校验)
     */
    @Transactional
    public void batchAddRoleInit(CommonContext context, List<RolePojo> rolePojos) {
        log.info("[Request], method:{}, context:{}", "batchAddRoleInit", JSON.toJSONString(context));
        if (CollectionUtils.isEmpty(rolePojos)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<Role> roleList;
        try {
            roleList = roleMapper.queryRole(context.getTenantId(), context.getAppId(), null, null, null, Boolean.FALSE);
        } catch (Exception e) {
            log.error("===auth.batchAddRoleInit() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        if (CollectionUtils.isNotEmpty(roleList)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        try {
            List<Role> roles = new LinkedList<>();
            rolePojos.forEach(rolePojo -> {
                Role role = new Role();
                role.setId(IdUtil.generateId());
                role.setTenantId(context.getTenantId());
                role.setAppId(context.getAppId());
                role.setRoleName(rolePojo.getRoleName());
                role.setRoleCode(rolePojo.getRoleCode());
                role.setRoleType(rolePojo.getRoleType());
                role.setDescription(rolePojo.getDescription());
                role.setCreator(context.getUserId());
                role.setCreateTime(System.currentTimeMillis());
                role.setDelFlag(Boolean.FALSE);

                roles.add(role);
            });
            //            roleMapper.batchInsert(roles);
        } catch (Exception e) {
            log.error("===auth.batchAddRoleInit() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 批量添加角色功能权限(无校验)
     */
    @Transactional
    public void batchAddFuncAccessInit(CommonContext context, List<FunctionAccessPojo> functionAccessPojos) {
        log.info("[Request], method:{},context:{}", "batchAddFuncAccessInit", JSON.toJSONString(context));
        if (CollectionUtils.isEmpty(functionAccessPojos)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        try {
            List<FuncAccess> funcAccesses = new LinkedList<>();
            functionAccessPojos.forEach(functionAccessPojo -> {
                FuncAccess funcAccess = new FuncAccess();
                funcAccess.setId(IdUtil.generateId());
                funcAccess.setTenantId(context.getTenantId());
                funcAccess.setAppId(context.getAppId());
                funcAccess.setRoleCode(functionAccessPojo.getRoleCode());
                funcAccess.setFuncCode(functionAccessPojo.getFuncCode());
                funcAccess.setDelFlag(Boolean.FALSE);

                funcAccesses.add(funcAccess);
            });
            //            funcAccessMapper.batchInsert(funcAccesses);
        } catch (Exception e) {
            log.error("===auth.batchAddFuncAccessInit() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 批量添加用户角色
     */
    @Transactional
    public void batchAddUserToRoleInit(CommonContext context, Map<String, Set<String>> roleUsers) {
        log.info("[Request],  method:{},context:{}", "batchAddUserToRoleInit", JSON.toJSONString(context));
        List<UserRole> userRoleList = new LinkedList<>();
        if (roleUsers == null || roleUsers.isEmpty()) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        try {
            roleUsers.forEach((role, users) -> {
                if (CollectionUtils.isNotEmpty(users)) {
                    users.forEach(user -> {
                        UserRole userRole = new UserRole().builder()
                                .id(IdUtil.generateId())
                                .tenantId(context.getTenantId())
                                .appId(context.getAppId())
                                .roleCode(role)
                                .orgId(user)
                                .orgType(AuthConstant.orgType.USER)
                                .delFlag(Boolean.FALSE)
                                .modifier(context.getUserId())
                                .modifyTime(System.currentTimeMillis())
                                .build();
                        userRoleList.add(userRole);
                    });
                }
            });
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                //                userRoleMapper.batchInsert(userRoleList);
            }
        } catch (Exception e) {
            log.error("===auth.batchAddUserToRoleInit() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 企业数据复制
     *
     * @param context               请求上下文
     * @param fromEnterpriseAccount 源企业EI
     * @param toEnterpriseAccount   目标企业EI
     * @param filterMetaData        是否过滤掉自定义对象数据
     */
    public void enterpriseCopy(CommonContext context, String fromEnterpriseAccount, String toEnterpriseAccount, boolean filterMetaData)
            throws AuthException {
        if (StringUtils.isAnyBlank(fromEnterpriseAccount, toEnterpriseAccount)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<Function> functions = funcMapper.queryFunctionByTenant(toEnterpriseAccount, context.getAppId());
        //目标企业已存在不复制
        if (CollectionUtils.isNotEmpty(functions)) {
            return;
        }

        functions = funcMapper.queryFunctionByTenant(fromEnterpriseAccount, context.getAppId());
        List<Role> roles = roleMapper.queryRole(fromEnterpriseAccount, context.getAppId(), null, null, null, Boolean.FALSE);
        List<UserRole> userRoles = userRoleMapper.queryUserRoleProvider(fromEnterpriseAccount, context.getAppId(), null, null, null, Boolean.FALSE);
        List<FuncAccess> funcAccesses = functionAccessMapper.queryFuncAccessEntitys(fromEnterpriseAccount, context.getAppId(), null, null, Boolean.FALSE);

        //字段权限
        Set<String> defultEntitys = null;

        List<FieldAccess> fieldAccesses =
                fieldAccessMapper.queryFieldAccess(fromEnterpriseAccount, context.getAppId(), null, defultEntitys, null, Boolean.FALSE);

        //视图权限
        List<ViewAccess> viewAccesses = viewAccessMapper.queryViewAccessByEntitys(fromEnterpriseAccount, context.getAppId(), defultEntitys);
        //版本分类
        List<RecordTypeAccess> recordTypeAccesses =
                recordTypeAccessMapper.queryRoleRecordTypeByEntitys(fromEnterpriseAccount, context.getAppId(), defultEntitys);

        List<Function> functionList = null;
        List<Role> roleList = null;
        List<UserRole> userRoleList = null;
        List<FuncAccess> funcAccessList = null;
        List<FieldAccess> fieldAccessList = null;
        List<ViewAccess> viewAccessList = null;
        List<RecordTypeAccess> recordTypeAccessList = null;

        try {
            //功能
            Set<String> funcCodes = new HashSet<>();
            if (CollectionUtils.isNotEmpty(functions)) {
                functionList = new ArrayList<>(functions.size());
                for (Function function : functions) {
                    if (filterMetaData && (function.getFuncType().equals(AuthConstant.FuncType.CUSTOMIZED))) {
                        continue;
                    }
                    funcCodes.add(function.getFuncCode());
                    Function functionTemp = new Function();
                    PropertyUtils.copyProperties(functionTemp, function);
                    functionTemp.setId(IdUtil.generateId());
                    functionTemp.setTenantId(toEnterpriseAccount);
                    functionList.add(functionTemp);
                }
            }

            Map<String, String> roleMap = new HashMap<>();
            //角色
            String roleId;
            if (CollectionUtils.isNotEmpty(roles)) {
                roleList = new ArrayList<>(roles.size());
                for (Role role : roles) {
                    Role roleTemp = new Role();
                    PropertyUtils.copyProperties(roleTemp, role);
                    roleId = IdUtil.generateId();
                    if (role.getRoleType().equals(AuthConstant.RoleType.CUSTOMIZED)) {
                        roleMap.put(role.getId(), roleId);
                        roleTemp.setRoleCode(roleId);
                    }
                    roleTemp.setId(roleId);
                    roleTemp.setTenantId(toEnterpriseAccount);
                    roleList.add(roleTemp);
                }
            }

            //用户角色
            if (CollectionUtils.isNotEmpty(userRoles)) {
                userRoleList = new ArrayList<>(userRoles.size());
                for (UserRole userRole : userRoles) {
                    UserRole userRoleTemp = new UserRole();
                    PropertyUtils.copyProperties(userRoleTemp, userRole);
                    String roleCode = roleMap.get(userRole.getRoleCode());
                    if (roleCode != null) {
                        userRoleTemp.setRoleCode(roleCode);
                    }
                    userRoleTemp.setId(IdUtil.generateId());
                    userRoleTemp.setTenantId(toEnterpriseAccount);
                    userRoleList.add(userRoleTemp);
                }
            }

            //角色功能权限
            if (CollectionUtils.isNotEmpty(funcAccesses)) {
                funcAccessList = new ArrayList<>(funcAccesses.size());
                for (FuncAccess funcAccess : funcAccesses) {
                    if (filterMetaData && !funcCodes.contains(funcAccess.getFuncCode())) {
                        continue;
                    }
                    FuncAccess funcAccessTemp = new FuncAccess();
                    PropertyUtils.copyProperties(funcAccessTemp, funcAccess);
                    String roleCode = roleMap.get(funcAccess.getRoleCode());
                    if (roleCode != null) {
                        funcAccessTemp.setRoleCode(roleCode);
                    }
                    funcAccessTemp.setId(IdUtil.generateId());
                    funcAccessTemp.setTenantId(toEnterpriseAccount);
                    funcAccessList.add(funcAccessTemp);
                }
            }

            //字段权限
            if (CollectionUtils.isNotEmpty(fieldAccesses)) {
                fieldAccessList = new ArrayList<>(fieldAccesses.size());
                for (FieldAccess fieldAccess : fieldAccesses) {
                    FieldAccess fieldAccessTemp = new FieldAccess();
                    PropertyUtils.copyProperties(fieldAccessTemp, fieldAccess);
                    String roleCode = roleMap.get(fieldAccess.getRoleCode());
                    if (roleCode != null) {
                        fieldAccessTemp.setRoleCode(roleCode);
                    }
                    fieldAccessTemp.setId(IdUtil.generateId());
                    fieldAccessTemp.setTenantId(toEnterpriseAccount);
                    fieldAccessList.add(fieldAccessTemp);
                }
            }

            //视图权限
            if (CollectionUtils.isNotEmpty(viewAccesses)) {
                viewAccessList = new ArrayList<>(viewAccesses.size());
                for (ViewAccess viewAccess : viewAccesses) {
                    ViewAccess viewAccessTemp = new ViewAccess();
                    PropertyUtils.copyProperties(viewAccessTemp, viewAccess);
                    String roleCode = roleMap.get(viewAccess.getRoleCode());
                    if (roleCode != null) {
                        viewAccessTemp.setRoleCode(roleCode);
                    }
                    viewAccessTemp.setId(IdUtil.generateId());
                    viewAccessTemp.setTenantId(toEnterpriseAccount);
                    viewAccessList.add(viewAccessTemp);
                }
            }

            //业务类型权限
            if (CollectionUtils.isNotEmpty(recordTypeAccesses)) {
                recordTypeAccessList = new ArrayList<>(recordTypeAccesses.size());
                for (RecordTypeAccess recordTypeAccess : recordTypeAccesses) {
                    RecordTypeAccess recordTypeAccessTemp = new RecordTypeAccess();
                    PropertyUtils.copyProperties(recordTypeAccessTemp, recordTypeAccess);
                    String roleCode = roleMap.get(recordTypeAccess.getRoleCode());
                    if (roleCode != null) {
                        recordTypeAccessTemp.setRoleCode(roleCode);
                    }
                    recordTypeAccessTemp.setId(IdUtil.generateId());
                    recordTypeAccessTemp.setTenantId(toEnterpriseAccount);
                    recordTypeAccessList.add(recordTypeAccessTemp);
                }
            }
        } catch (Exception e) {
            log.error("enterpriseCopy ", e);
        }
        tenantServiceUtil.enterpriseCopyBatchInsert(toEnterpriseAccount,
                functionList,
                roleList,
                userRoleList,
                funcAccessList,
                fieldAccessList,
                viewAccessList,
                recordTypeAccessList);

    }

    @Transactional
    public void dbClear(CommonContext context) {
        try {
            userRoleMapper.batchDel(context.getTenantId(), context.getAppId(), null, null, null, context.getUserId(), System.currentTimeMillis());
            funcMapper.batchDel(context.getTenantId(), context.getAppId(), context.getUserId(), null, System.currentTimeMillis());
            roleMapper.batchDel(context.getTenantId(), context.getAppId(), null, null, null, context.getUserId(), System.currentTimeMillis());
            functionAccessMapper.batchDel(context.getTenantId(), context.getAppId(), null, context.getUserId(), null, System.currentTimeMillis());
            fieldAccessMapper.batchDel(context.getTenantId(), context.getAppId(), null, null, null, context.getUserId(), System.currentTimeMillis());
            viewAccessMapper.batchDel(context.getTenantId(), context.getAppId(), null, null, context.getUserId(), System.currentTimeMillis());
            recordTypeAccessMapper.deleteRoleRecordType(context.getTenantId(),
                    context.getAppId(),
                    null,
                    null,
                    null,
                    null,
                    context.getUserId(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.dbClear() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 批量添加用户角色-新
     */
    @Override
    @Transactional
    public void batchAddUserRoleInit(CommonContext context, List<UserRolePojo> roleUsers) {
        log.info("[Request],  method:{},context:{}", "batchAddUserRoleInit", JSON.toJSONString(context));
        List<UserRole> userRoleList = new LinkedList<>();
        if (roleUsers == null || roleUsers.isEmpty()) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        try {
            roleUsers.forEach(pojo -> {
                UserRole userRole = new UserRole();
                userRole.setId(IdUtil.generateId());
                userRole.setTenantId(context.getTenantId());
                userRole.setAppId(context.getAppId());
                userRole.setRoleCode(pojo.getRoleCode());
                userRole.setOrgId(pojo.getOrgId());
                userRole.setOrgType(AuthConstant.orgType.USER);
                userRole.setDeptId(pojo.getDeptId());
                userRole.setDelFlag(Boolean.FALSE);
                userRole.setModifier(context.getUserId());
                userRole.setModifyTime(System.currentTimeMillis());
                userRoleList.add(userRole);
            });
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                //                userRoleMapper.batchInsert(userRoleList);
            }
        } catch (Exception e) {
            log.error("===auth.batchAddUserRoleInit() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void cacheReset(CommonContext context) {

        try {
            //企业功能
            List<Function> functionList = funcMapper.queryFunctionByTenant(context.getTenantId(), context.getAppId());
            if (CollectionUtils.isNotEmpty(functionList)) {
                List<FunctionPojo> functionPojoList = new LinkedList<>();

                for (Function function : functionList) {
                    FunctionPojo functionPojo = new FunctionPojo();
                    PropertyUtils.copyProperties(functionPojo, function);
                    functionPojoList.add(functionPojo);
                }
                cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION,
                        context.getAppId(),
                        functionPojoList);

            }

            //功能权限
            List<FuncAccess> funcAccesseList =
                    functionAccessMapper.queryFuncAccessProvider(context.getTenantId(), context.getAppId(), null, null, Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(funcAccesseList)) {
                Map<String, Set<String>> funcAccessMap = new HashMap<>();
                funcAccesseList.forEach(funcAccess -> {
                    if (funcAccessMap.get(funcAccess.getRoleCode()) == null) {
                        funcAccessMap.put(funcAccess.getRoleCode(), new HashSet<>());
                    }
                    funcAccessMap.get(funcAccess.getRoleCode()).add(funcAccess.getFuncCode());
                });

                cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
                        (Map) funcAccessMap);

            }

            //字段权限
            List<FieldAccess> fieldAccessList =
                    fieldAccessMapper.queryFieldAccess(context.getTenantId(), context.getAppId(), null, null, null, Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(fieldAccessList)) {
                Map<String, Map<String, Map<String, Integer>>> fieldAccessMap = new HashMap<>();
                fieldAccessList.forEach(fieldAccess -> {
                    if (fieldAccessMap.get(fieldAccess.getEntityId()) == null) {
                        fieldAccessMap.put(fieldAccess.getEntityId(), new HashMap<>());
                    }
                    if (fieldAccessMap.get(fieldAccess.getEntityId()).get(fieldAccess.getRoleCode()) == null) {
                        fieldAccessMap.get(fieldAccess.getEntityId()).put(fieldAccess.getRoleCode(), new HashMap<>());
                    }
                    fieldAccessMap.get(fieldAccess.getEntityId())
                            .get(fieldAccess.getRoleCode())
                            .put(fieldAccess.getFieldId(), fieldAccess.getPermission());
                });

                if (CollectionUtils.isNotEmpty(fieldAccessMap.keySet())) {
                    fieldAccessMap.keySet().forEach(entity -> {
                        cacheManager.putAll(entity, (Map) fieldAccessMap.get(entity));
                    });
                }

            }

            //视图权限
            List<ViewAccess> viewAccessList =
                    viewAccessMapper.queryViewAccessProvider(context.getTenantId(), context.getAppId(), null, null, null, null, Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(viewAccessList)) {
                Map<String, Map<String, Set<String>>> viewAccessMap = new HashMap<>();
                viewAccessList.forEach(viewAccess -> {
                    if (viewAccessMap.get(viewAccess.getEntityId()) == null) {
                        viewAccessMap.put(viewAccess.getEntityId(), new HashMap<>());
                    }
                    if (viewAccessMap.get(viewAccess.getEntityId()).get(viewAccess.getRoleCode()) == null) {
                        viewAccessMap.get(viewAccess.getEntityId()).put(viewAccess.getRoleCode(), new HashSet<>());
                    }
                    viewAccessMap.get(viewAccess.getEntityId()).get(viewAccess.getRoleCode()).add(viewAccess.getViewId());
                });

                if (CollectionUtils.isNotEmpty(viewAccessMap.keySet())) {
                    viewAccessMap.keySet().forEach(entity -> {
                        cacheManager.putAll(entity, (Map) viewAccessMap.get(entity));
                    });
                }
            }

        } catch (Exception e) {
            log.error("===auth.cacheReset() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }
}
