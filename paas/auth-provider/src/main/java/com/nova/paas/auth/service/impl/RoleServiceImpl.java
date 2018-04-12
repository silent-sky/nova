package com.nova.paas.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.auth.service.FieldAccessService;
import com.nova.paas.auth.service.FuncAccessService;
import com.nova.paas.auth.service.FuncService;
import com.nova.paas.auth.service.RoleService;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.auth.service.ViewAccessService;
import com.nova.paas.auth.service.entity.FieldAccess;
import com.nova.paas.auth.service.entity.FuncAccess;
import com.nova.paas.auth.service.entity.RecordTypeAccess;
import com.nova.paas.auth.service.entity.Role;
import com.nova.paas.auth.service.entity.ViewAccess;
import com.nova.paas.auth.service.mapper.FieldAccessMapper;
import com.nova.paas.auth.service.mapper.FuncAccessMapper;
import com.nova.paas.auth.service.mapper.RecordTypeAccessMapper;
import com.nova.paas.auth.service.mapper.RoleMapper;
import com.nova.paas.auth.service.mapper.UserRoleMapper;
import com.nova.paas.auth.service.mapper.ViewAccessMapper;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;
import com.nova.paas.common.support.CacheManager;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
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

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service("roleService")
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    FuncService funcService;
    @Autowired
    FieldAccessService fieldAccessService;
    @Autowired
    ViewAccessService viewAccessService;
    @Autowired
    FuncAccessService funcAccessService;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    FieldAccessMapper fieldAccessMapper;
    @Autowired
    ViewAccessMapper viewAccessMapper;
    @Autowired
    FuncAccessMapper funcAccessMapper;
    @Autowired
    RecordTypeAccessMapper recordTypeAccessMapper;
    @Autowired
    CacheManager cacheManager;

    private static final Set<Integer> legalRoleTypeSet;

    static {
        legalRoleTypeSet = new HashSet<>();
        legalRoleTypeSet.add(AuthConstant.RoleType.DEFAULT);  //系统默认角色
        legalRoleTypeSet.add(AuthConstant.RoleType.CUSTOMIZED); //自定义角色
    }

    @Override
    public RolePojo queryRoleByCode(CommonContext context, String roleCode) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            return null;
        }
        try {
            Role role = roleMapper.queryRoleByCode(context.getTenantId(), context.getAppId(), roleCode);
            if (role != null) {
                RolePojo rolePojo = new RolePojo();
                PropertyUtils.copyProperties(rolePojo, role);
                return rolePojo;
            }
        } catch (Exception e) {
            log.error("===auth.queryRoleByCode() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return null;
    }

    @Override
    public List<RolePojo> queryRole(CommonContext context, String roleCode, String roleName, Integer roleType, PageInfo pageInfo)
            throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            return this.queryRole2(context, null, roleName, roleType, pageInfo);
        }
        return this.queryRole2(context, Collections.singleton(roleCode), roleName, roleType, pageInfo);
    }

    @Override
    public List<RolePojo> queryRole2(CommonContext context, Set<String> roleCodes, String roleName, Integer roleType, PageInfo pageInfo)
            throws AuthServiceException {
        List<RolePojo> rolePojoList = new LinkedList<>();
        List<Role> roleList;
        if (roleCodes != null) {
            roleCodes.remove(null);
        }
        if (roleCodes != null && roleCodes.isEmpty()) {
            return rolePojoList;
        }
        try {
            Integer start = null;
            if (pageInfo != null) {
                int total = roleMapper.queryRole2Count(context.getTenantId(), context.getAppId(), roleCodes, roleName, roleType);
                pageInfo.setTotal(total);
                int totalPage = total / pageInfo.getPageSize();
                if (total % pageInfo.getPageSize() > 0) {
                    totalPage = totalPage + 1;
                }
                pageInfo.setTotalPage(totalPage);
                if (pageInfo.getPageNum() > totalPage) {
                    pageInfo.setPageNum(totalPage);
                }

                if (total == 0) {
                    return rolePojoList;
                }

                start = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
            }
            roleList = roleMapper.queryRoleByPage(context.getTenantId(),
                    context.getAppId(),
                    roleCodes,
                    roleName,
                    roleType,
                    start,
                    pageInfo); //无分页请求,返回所有数据
        } catch (Exception e) {
            log.error("===auth.queryRole2() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return this.roleToRolePojo(roleList);
    }

    @Override
    @Transactional
    public String createRole(CommonContext context, RolePojo rolePojo) throws AuthServiceException {
        log.info("[Request], method:{},context:{},rolePojo:{}", "createRole", JSON.toJSONString(context), JSON.toJSONString(rolePojo));
        this.roleVerify(rolePojo);

        if (rolePojo.getRoleType() == null || !legalRoleTypeSet.contains(rolePojo.getRoleType())) {//只能传0、1
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //指定roleCode
        String id = IdUtil.generateId();
        if (StringUtils.isBlank(rolePojo.getRoleCode())) {
            rolePojo.setRoleCode(id);
        }

        //角色校验
        if (this.roleCodeOrRoleNameExists(context, Collections.singleton(rolePojo.getRoleCode()), Collections.singleton(rolePojo.getRoleName()))
                > 0) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        Role role = new Role();
        role.setId(id);
        role.setTenantId(context.getTenantId());
        role.setAppId(context.getAppId());
        role.setRoleCode(rolePojo.getRoleCode());
        role.setRoleName(rolePojo.getRoleName());
        role.setDescription(rolePojo.getDescription());
        role.setRoleType(rolePojo.getRoleType());
        role.setCreator(context.getUserId());
        role.setCreateTime(System.currentTimeMillis());
        role.setModifier(context.getUserId());
        role.setModifyTime(System.currentTimeMillis());
        role.setDelFlag(Boolean.FALSE);
        try {
            //            roleMapper.insert(role);
        } catch (Exception e) {
            log.error("===auth.createRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return rolePojo.getRoleCode();
    }

    @Override
    @Transactional
    public void updateRole(CommonContext context, RolePojo rolePojo) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{}", "updateRole", JSON.toJSONString(context), JSON.toJSONString(rolePojo));
        this.roleVerify(rolePojo);
        if (StringUtils.isBlank(rolePojo.getRoleCode())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<Role> roleList;
        try {
            roleList = roleMapper.queryRole(context.getTenantId(), context.getAppId(), rolePojo.getRoleCode(), null, null, Boolean.FALSE);
        } catch (Exception e) {
            log.error("===auth.updateRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (CollectionUtils.isEmpty(roleList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        Role role = roleList.get(0);
        if (role.getRoleType() == AuthConstant.RoleType.DEFAULT) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //roleName校验
        if (!rolePojo.getRoleName().equals(role.getRoleName())) {
            try {
                roleList = roleMapper.queryRole(context.getTenantId(), context.getAppId(), null, rolePojo.getRoleName(), null, Boolean.FALSE);
            } catch (Exception e) {
                log.error("===auth.updateRole() error===", e);
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
            }
            if (CollectionUtils.isNotEmpty(roleList)) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }

        try {
            roleMapper.updateRole(context.getTenantId(),
                    context.getAppId(),
                    rolePojo.getRoleCode(),
                    rolePojo.getRoleName(),
                    rolePojo.getDescription(),
                    context.getUserId(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.updateRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    @Override
    @Transactional
    public void delRole(CommonContext context, String roleCode) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{}", "delRole", JSON.toJSONString(context), JSON.toJSONString(roleCode));
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Role role;
        try {
            role = roleMapper.queryRoleByCode(context.getTenantId(), context.getAppId(), roleCode);
        } catch (Exception e) {
            log.error("===auth.delRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (role == null) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (role.getRoleType() == null || role.getRoleType() == AuthConstant.RoleType.DEFAULT) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        this.delRoleAndUpdateCahce(context, roleCode);
    }

    @Override
    @Transactional
    public void rolePermissCopy(CommonContext context, String sourceRoleCode, String destRoleCode) throws AuthServiceException {
        log.info("[Request], method:{},context:{},sourceRoleCode:{},destRoleCode:{}",
                "rolePermissCopy",
                JSON.toJSONString(context),
                JSON.toJSONString(sourceRoleCode),
                JSON.toJSONString(destRoleCode));
        if (StringUtils.isAnyBlank(sourceRoleCode, destRoleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (sourceRoleCode.equals(destRoleCode)) {
            return;
        }
        List<Role> roleList;
        Set<String> roleCodeList = new HashSet<>();
        roleCodeList.add(sourceRoleCode);
        roleCodeList.add(destRoleCode);
        try {
            //校验角色是否存在
            roleList = roleMapper.queryRoleByPage(context.getTenantId(), context.getAppId(), roleCodeList, null, null, null, null);
        } catch (Exception e) {
            log.error("===auth.rolePermissCopy() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        if (CollectionUtils.isEmpty(roleList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Set<String> tempSet = new HashSet<>();
        roleList.forEach(role -> {
            tempSet.add(role.getRoleCode());
        });
        if (!CollectionUtils.isEqualCollection(roleCodeList, tempSet)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        List<FuncAccess> funcAccessList = funcAccessMapper.queryFuncAccessProvider(context.getTenantId(),
                context.getAppId(),
                Collections.singletonList(destRoleCode),
                null,
                Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(funcAccessList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        List<FieldAccess> fieldAccessList = fieldAccessMapper.queryFieldAccess(context.getTenantId(),
                context.getAppId(),
                Collections.singletonList(destRoleCode),
                null,
                null,
                Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(fieldAccessList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        List<ViewAccess> viewAccessList = viewAccessMapper.queryViewAccessProvider(context.getTenantId(),
                context.getAppId(),
                Collections.singletonList(destRoleCode),
                null,
                null,
                null,
                Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(viewAccessList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        List<RecordTypeAccess> recordTypeAccessList = recordTypeAccessMapper.queryRecordTypeAccessProvider(context.getTenantId(),
                context.getAppId(),
                Collections.singleton(destRoleCode),
                null,
                null);
        if (CollectionUtils.isNotEmpty(recordTypeAccessList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        try {
            //1.功能权限 2.字段权限  3.视图权限  (直接查/加数据库,不需要更新缓存)
            funcAccessList = funcAccessMapper.queryFuncAccessProvider(context.getTenantId(),
                    context.getAppId(),
                    Collections.singletonList(sourceRoleCode),
                    null,
                    Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(funcAccessList)) {
                funcAccessList.forEach(funcAccess -> {
                    funcAccess.setId(IdUtil.generateId());
                    funcAccess.setTenantId(context.getTenantId());
                    funcAccess.setAppId(context.getAppId());
                    funcAccess.setRoleCode(destRoleCode);
                    funcAccess.setModifier(context.getUserId());
                    funcAccess.setModifyTime(System.currentTimeMillis());
                    funcAccess.setDelFlag(Boolean.FALSE);
                });
                //                funcAccessMapper.batchInsert(funcAccessList);
            }

            fieldAccessList = fieldAccessMapper.queryFieldAccess(context.getTenantId(),
                    context.getAppId(),
                    Collections.singletonList(sourceRoleCode),
                    null,
                    null,
                    Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(fieldAccessList)) {
                fieldAccessList.forEach(fieldAccess -> {
                    fieldAccess.setId(IdUtil.generateId());
                    fieldAccess.setRoleCode(destRoleCode);
                    fieldAccess.setModifier(context.getUserId());
                    fieldAccess.setModifyTime(System.currentTimeMillis());
                });
                //                fieldAccessMapper.batchInsert(fieldAccessList);
            }

            viewAccessList = viewAccessMapper.queryViewAccessProvider(context.getTenantId(),
                    context.getAppId(),
                    Collections.singletonList(sourceRoleCode),
                    null,
                    null,
                    null,
                    Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(viewAccessList)) {
                viewAccessList.forEach(viewAccess -> {
                    viewAccess.setId(IdUtil.generateId());
                    viewAccess.setRoleCode(destRoleCode);
                    viewAccess.setModifier(context.getUserId());
                    viewAccess.setModifyTime(System.currentTimeMillis());
                });
                //                viewAccessMapper.batchInsert(viewAccessList);
            }

            recordTypeAccessList = recordTypeAccessMapper.queryRecordTypeAccessProvider(context.getTenantId(),
                    context.getAppId(),
                    Collections.singletonList(sourceRoleCode),
                    null,
                    null);
            if (CollectionUtils.isNotEmpty(recordTypeAccessList)) {
                recordTypeAccessList.forEach(recordTypeAccess -> {
                    recordTypeAccess.setId(IdUtil.generateId());
                    recordTypeAccess.setRoleCode(destRoleCode);
                    recordTypeAccess.setModifier(context.getUserId());
                    recordTypeAccess.setModifyTime(System.currentTimeMillis());
                });
                //                recordTypeAccessMapper.batchInsert(recordTypeAccessList);
            }

        } catch (Exception e) {
            log.error("===auth.rolePermissCopy() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    public List<RolePojo> queryMatchRole(CommonContext context, Set<String> roleCodes, String key, Integer roleType, PageInfo pageInfo)
            throws AuthServiceException {
        List<RolePojo> rolePojoList = new LinkedList<>();
        List<Role> roleList;
        if (roleCodes != null) {
            roleCodes.remove(null);
        }
        if (roleCodes != null && roleCodes.isEmpty()) {
            return rolePojoList;
        }
        try {
            Integer start = 0;
            if (pageInfo != null) {
                int total = roleMapper.queryMatchRoleCount(context.getTenantId(), context.getAppId(), roleCodes, key, roleType);
                pageInfo.setTotal(total);
                int totalPage = total / pageInfo.getPageSize();
                if (total % pageInfo.getPageSize() > 0) {
                    totalPage = totalPage + 1;
                }
                pageInfo.setTotalPage(totalPage);
                if (pageInfo.getPageNum() > totalPage) {
                    pageInfo.setPageNum(totalPage);
                }

                if (total == 0) {
                    return rolePojoList;
                }

                start = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
            }
            roleList = roleMapper.queryMatchRole(context.getTenantId(), context.getAppId(), roleCodes, key, roleType, start, pageInfo); //无分页请求,返回所有数据
        } catch (Exception e) {
            log.error("===auth.queryMatchRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return this.roleToRolePojo(roleList);
    }

    /**
     * 角色数据校验
     *
     * @param rolePojo 角色对象
     */
    private void roleVerify(RolePojo rolePojo) {
        if (rolePojo == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isBlank(rolePojo.getRoleName())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    @Override
    public long roleCodeOrRoleNameExists(CommonContext context, Set<String> roleCodes, Set<String> roleNames) {
        Set<String> roles;
        try {
            roles = roleMapper.roleNameOrRoleCodeExists(context.getTenantId(), context.getAppId(), roleCodes, roleNames, Boolean.FALSE);
        } catch (Exception e) {
            log.error("===auth.roleCodeOrRoleNameExists() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return roles.size();
    }

    @Override
    @Transactional
    public void delDefinedRole(CommonContext context, String roleCode) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{}", "delRole", JSON.toJSONString(context), JSON.toJSONString(roleCode));
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Role role;
        try {
            role = roleMapper.queryRoleByCode(context.getTenantId(), context.getAppId(), roleCode);
        } catch (Exception e) {
            log.error("===auth.delDefinedRole() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (role == null) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (role.getRoleType() == null || role.getRoleType() == AuthConstant.RoleType.DEFAULT) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        Set<String> check = userRoleService.checkRoleUser(context, roleCode, null);
        if (CollectionUtils.isNotEmpty(check)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        this.delRoleAndUpdateCahce(context, roleCode);
    }

    /**
     * role转为rolePojo
     */
    private List<RolePojo> roleToRolePojo(List<Role> roleList) {
        List<RolePojo> rolePojoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleList)) {
            try {
                for (Role role : roleList) {
                    RolePojo rolePojo1 = new RolePojo();
                    PropertyUtils.copyProperties(rolePojo1, role);
                    rolePojoList.add(rolePojo1);
                }
            } catch (Exception e) {
                log.error("queryRole{} role convert to rolePojo error roleList{} ", JSON.toJSONString(roleList), e);
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
            }
        }
        return rolePojoList;
    }

    private void delRoleAndUpdateCahce(CommonContext context, String roleCode) {
        try {
            //删除字段权限
            fieldAccessService.delRoleFieldPermiss(context, roleCode, null);
            //删除视图权限
            viewAccessService.delRoleViewAccess(context, roleCode);
            //删除功能权限
            funcAccessService.delRoleFuncPermiss(context, roleCode);
            //删除用户角色
            userRoleMapper.batchDel(context.getTenantId(), context.getAppId(), roleCode, null, null, context.getUserId(), System.currentTimeMillis());

            recordTypeAccessMapper.deleteRoleRecordType(context.getTenantId(),
                    context.getAppId(),
                    Collections.singleton(roleCode),
                    null,
                    null,
                    null,
                    context.getUserId(),
                    System.currentTimeMillis());

            //用户角色
            Map<String, Set<String>> userRoles =
                    (Map) cacheManager.getHashEntries(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE);
            if (userRoles != null && !userRoles.isEmpty()) {
                Map<String, Set<String>> needUpdate = new HashMap<>();
                userRoles.forEach((userId, roles) -> {
                    if (roles.remove(roleCode)) {
                        needUpdate.put(userId, roles);
                    }
                });
                if (!needUpdate.isEmpty()) {
                    cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_USER_ROLE,
                            (Map) needUpdate);
                }
            }
            //dept cache
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_DEPT);
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_ROLE_INFO);

            //删除角色
            roleMapper.delRole(context.getTenantId(), context.getAppId(), roleCode, context.getUserId(), System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.delRoleAndUpdateCahce() error===", e);
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

}
