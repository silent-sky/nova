package com.nova.paas.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.nova.paas.auth.FuncAccessService;
import com.nova.paas.auth.FuncService;
import com.nova.paas.auth.RoleService;
import com.nova.paas.auth.UserRoleService;
import com.nova.paas.auth.entity.FuncAccess;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.FuncAccessMapper;
import com.nova.paas.auth.mapper.FunctionMapper;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.support.CacheManager;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
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
@Service("funcAccessService")
@Slf4j
public class FuncAccessServiceImpl implements FuncAccessService {

    @Autowired
    FuncAccessMapper funcAccessMapper;
    @Autowired
    FunctionMapper funcMapper;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    FuncService funcService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RoleService roleService;
    @Value("${FUNC_PERMISS_EXPIRE_SECOND}")
    private int FUNC_PERMISS_EXPIRE_SECOND;

    /**
     * 查询角色的功能权限
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @return 功能权限列表
     */
    @Override
    public List<FunctionPojo> queryFuncAccessByRole(CommonContext context, String roleCode) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        Set<String> functionPermission = this.queryFuncSetByRoles(context, Collections.singletonList(roleCode));
        if (CollectionUtils.isEmpty(functionPermission)) {
            this.rolesIsExist(context, Collections.singleton(roleCode));
        }
        List<FunctionPojo> functionPojoList = funcService.queryFunctionByTenant(context);//设置缓存失效时间
        return this.structureFuncPermission(functionPermission, functionPojoList);
    }

    /**
     * 查询角色的功能权限(唯一标识)
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @return 功能唯一标识列表
     */
    @Override
    public Set<String> queryFuncAccessCodeByRole(CommonContext context, String roleCode) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            return new HashSet<>();
        }
        return this.queryFuncSetByRoles(context, Collections.singletonList(roleCode));
    }

    /**
     * 批量查询角色的功能权限
     *
     * @param context 请求上下文
     * @param roles   角色编码列表
     */
    @Override
    public Map<String, Set<String>> queryFuncAccessByRoles(CommonContext context, Set<String> roles) throws AuthServiceException {
        if (roles != null) {
            roles.remove(null);
        }
        if (CollectionUtils.isEmpty(roles)) {
            return new HashMap<>();
        }
        return this.queryFuncPermissByRoles(context, new ArrayList<>(roles));
    }

    /**
     * 查询哪些角色拥有某功能
     *
     * @param context      请求上下文
     * @param funcCodeList 功能唯一标识列表
     */
    @Override
    public Map<String, Set<String>> queryFuncAccessByFuncCode(CommonContext context, Set<String> funcCodeList) throws AuthServiceException {
        //入参校验
        Map<String, Set<String>> funcCodeRoles = new HashMap<>();
        if (funcCodeList != null) {
            funcCodeList.remove(null);
        }
        if (CollectionUtils.isEmpty(funcCodeList)) {
            return funcCodeRoles;
        }
        try {
            funcCodeList.forEach(funcCode -> {
                funcCodeRoles.put(funcCode, new HashSet<>());
            });

            //查询
            List<FuncAccess> funcAccessList =
                    funcAccessMapper.queryFuncAccessProvider(context.getTenantId(), context.getAppId(), null, funcCodeList, Boolean.FALSE);

            if (CollectionUtils.isNotEmpty(funcAccessList)) {
                funcAccessList.forEach(funcAccess -> {
                    funcCodeRoles.get(funcAccess.getFuncCode()).add(funcAccess.getRoleCode());
                });
            }
        } catch (Exception e) {
            log.error("===auth.queryFuncAccessByFuncCode() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        return funcCodeRoles;
    }

    /**
     * 查询用户的功能权限
     *
     * @param context 请求上下文
     * @return 功能权限列表
     */
    @Override
    public List<FunctionPojo> queryFuncAccessByUser(CommonContext context) throws AuthServiceException {
        List<String> roleList = userRoleService.queryRoleCodeListByUserId(context);
        Set<String> functionPermission = this.queryFuncSetByRoles(context, roleList);
        List<FunctionPojo> functionPojoList = funcService.queryFunctionByTenant(context);
        return this.structureFuncPermission(functionPermission, functionPojoList);
    }

    /**
     * 校验用户功能权限
     *
     * @param context     请求上下文
     * @param funcCodeSet 权限列表
     * @return 权限校验结果MAP
     */
    @Override
    public Map<String, Boolean> userFuncPermissionCheck(CommonContext context, Set<String> funcCodeSet) throws AuthServiceException {
        Map<String, Boolean> permissionCheckResult = new HashMap<>();

        //入参校验
        if (funcCodeSet != null) {
            funcCodeSet.remove(null);
        }
        if (CollectionUtils.isEmpty(funcCodeSet)) {
            return permissionCheckResult;
        }

        Set<String> functionPermission = this.queryFuncSetByRoles(context, userRoleService.queryRoleCodeListByUserId(context));

        //权限校验
        funcCodeSet.forEach(funcCode -> {
            if (functionPermission.contains(funcCode)) {
                permissionCheckResult.put(funcCode, Boolean.TRUE);
            } else {
                permissionCheckResult.put(funcCode, Boolean.FALSE);
            }
        });
        return permissionCheckResult;
    }

    /**
     * 角色功能权限更新
     *
     * @param context     请求上下文
     * @param roleCode    角色编码
     * @param funcCodeSet 功能权限列表
     */
    @Override
    @Transactional
    public void updateRoleFuncPermission(CommonContext context, String roleCode, Set<String> funcCodeSet) throws AuthServiceException {

        log.info("[Request], method:{},context:{},roleCode:{},funcCodeSet:{}",
                "updateRoleFuncPermission",
                JSON.toJSONString(context),
                JSON.toJSONString(roleCode),
                JSON.toJSONString(funcCodeSet));

        //入参校验
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (funcCodeSet == null || funcCodeSet.contains(null)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        //角色校验
        this.rolesIsExist(context, Collections.singleton(roleCode));
        //funcCode校验
        this.checkFuncCodeExit(context, funcCodeSet);

        Set<String> tempSet = this.queryFuncAccessCodeByRole(context, roleCode);
        Set<String> needDel = new HashSet<>();
        Set<String> needAdd = new HashSet<>();

        if (CollectionUtils.isEmpty(funcCodeSet)) {
            needDel.addAll(tempSet);
        } else {
            //差异化更新
            if (CollectionUtils.isNotEmpty(tempSet)) {
                tempSet.forEach(funcCode -> {
                    if (!funcCodeSet.contains(funcCode)) {
                        needDel.add(funcCode);
                    }
                });
            }
            //添加新权限
            for (String funcCode : funcCodeSet) {
                if (!tempSet.contains(funcCode)) {
                    needAdd.add(funcCode);
                }
            }
        }

        this.partUpdateAccess(context, needAdd, needDel, roleCode, funcCodeSet);
    }

    /**
     * 查询角色的功能权限
     *
     * @param context 请求上下文
     * @param roles   角色列表
     */
    public Map<String, Set<String>> queryRolesFuncAccess(CommonContext context, List<String> roles) throws AuthServiceException {
        return this.queryFuncPermissByRoles(context, roles);
    }

    /**
     * 删除角色功能权限
     */
    @Transactional
    public void delRoleFuncPermiss(CommonContext context, String roleCode) throws AuthServiceException {

        log.info("[Request], method:{},context:{},roleCode:{}", "delRoleFuncPermiss", JSON.toJSONString(context), JSON.toJSONString(roleCode));

        this.delRoleFuncPermissCache(context, roleCode);
        try {
            funcAccessMapper.batchDel(context.getTenantId(), context.getAppId(), roleCode, context.getUserId(), null, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.delRoleFuncPermiss() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 角色功能权限更新
     *
     * @param context        请求上下文
     * @param roleCode       角色编码
     * @param addFuncCodeSet 增加的功能权限列表
     * @param delFuncCodeSet 删除的功能权限列表
     */
    @Transactional
    public void updateRoleModifiedFuncPermission(CommonContext context, String roleCode, Set<String> addFuncCodeSet, Set<String> delFuncCodeSet)
            throws AuthServiceException {

        log.info("[Request], method:{},context:{},roleCode:{},addFuncCodeSet:{},delFuncCodeSet:{}",
                "updateRoleModifiedFuncPermission",
                JSON.toJSONString(context),
                JSON.toJSONString(roleCode),
                JSON.toJSONString(addFuncCodeSet),
                JSON.toJSONString(delFuncCodeSet));

        //入参校验
        if (addFuncCodeSet != null) {
            addFuncCodeSet.remove(null);
        }
        if (delFuncCodeSet != null) {
            delFuncCodeSet.remove(null);
        }
        if (CollectionUtils.isEmpty(addFuncCodeSet) && CollectionUtils.isEmpty(delFuncCodeSet)) {
            return;
        }

        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        //角色校验
        this.rolesIsExist(context, Collections.singleton(roleCode));
        //funcCode校验
        if (CollectionUtils.isNotEmpty(addFuncCodeSet)) {
            this.checkFuncCodeExit(context, addFuncCodeSet);//只校验add的就可以
        }

        Set<String> beforeData = this.queryFuncAccessCodeByRole(context, roleCode);
        if (CollectionUtils.isNotEmpty(addFuncCodeSet)) {
            beforeData.addAll(addFuncCodeSet);
        }
        if (CollectionUtils.isNotEmpty(delFuncCodeSet)) {
            beforeData.removeAll(delFuncCodeSet);
        }

        this.partUpdateAccess(context, addFuncCodeSet, delFuncCodeSet, roleCode, beforeData);
    }

    /**
     * 多个角色、针对一个funcCode的处理
     *
     * @param context    请求上下文
     * @param funcCode   角色编码
     * @param addRoleSet 增加该功能的角色列表
     * @param delRoleSet 删除该功能的角色列表
     */
    @Override
    @Transactional
    public void updateFuncRolePermission(CommonContext context, String funcCode, Set<String> addRoleSet, Set<String> delRoleSet)
            throws AuthServiceException {
        if (StringUtils.isBlank(funcCode) || (CollectionUtils.isEmpty(addRoleSet) && CollectionUtils.isEmpty(delRoleSet))) {
            return;
        }
        log.info("[Request], method:{},context:{},funcCode:{},addRoleSet:{},delRoleSet:{}",
                "updateFuncRolePermission",
                JSON.toJSONString(context),
                funcCode,
                JSON.toJSONString(addRoleSet),
                JSON.toJSONString(delRoleSet));
        if (addRoleSet != null) {
            addRoleSet.remove(null);
        }
        if (delRoleSet != null) {
            delRoleSet.remove(null);
        }
        if (CollectionUtils.isNotEmpty(addRoleSet) && CollectionUtils.isNotEmpty(delRoleSet) && CollectionUtils.containsAny(addRoleSet, delRoleSet)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //校验数据有效性
        this.checkFuncCodeExit(context, Collections.singleton(funcCode));
        this.rolesIsExist(context, addRoleSet);
        this.rolesIsExist(context, delRoleSet);

        Map<String, Set<String>> updateAccessMap = new HashMap<>();//存放用于更新缓存的数据
        List<FuncAccess> accessList = new ArrayList<>();//需要新增到数据库的实体list

        Map<String, Set<String>> addRoleFuncMap = this.queryFuncPermissByRoles(context, new ArrayList<>(addRoleSet));
        for (String role : addRoleSet) {
            if (addRoleFuncMap.get(role) == null || !addRoleFuncMap.get(role).contains(funcCode)) {
                Set<String> funcSet = addRoleFuncMap.get(role);
                funcSet.add(funcCode);
                updateAccessMap.put(role, funcSet);

                accessList.add(new FuncAccess(IdUtil.generateId(),
                        context.getTenantId(),
                        role,
                        context.getAppId(),
                        context.getUserId(),
                        funcCode,
                        System.currentTimeMillis(),
                        Boolean.FALSE));
            }
        }

        Map<String, Set<String>> delRoleFuncMap = this.queryFuncPermissByRoles(context, new ArrayList<>(delRoleSet));
        delRoleFuncMap.forEach((role, set) -> {
            set.remove(funcCode);
            updateAccessMap.put(role, set);
        });

        try {
            if (CollectionUtils.isNotEmpty(accessList)) {
                //                funcAccessMapper.batchInsert(accessList);
            }
            if (CollectionUtils.isNotEmpty(delRoleSet)) {
                funcAccessMapper.batchDelFuncAccess(context.getTenantId(),
                        context.getAppId(),
                        delRoleSet,
                        context.getUserId(),
                        Collections.singleton(funcCode),
                        System.currentTimeMillis());
            }
            cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
                    (Map) updateAccessMap);
            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
            //                    FUNC_PERMISS_EXPIRE_SECOND);
        } catch (Exception e) {
            log.error("updateFuncRolePermission error:", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private Map<String, Set<String>> queryFuncPermissByRoles(CommonContext context, List<String> roles) throws AuthServiceException {
        Map<String, Set<String>> rolesPermissMap = new HashMap<>();
        if (CollectionUtils.isEmpty(roles)) {
            return rolesPermissMap;
        }
        List<Set<String>> rolesPermissList = null;
        try {
            //            rolesPermissList = (List) cacheManager.getMultiObject(
            //                    context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION, roles);
        } catch (Exception e) {
            return this.queryRolesPermissFromDB(context, roles);
        }

        //检测缓存是否全部命中
        List<String> updateRoleCache = new LinkedList<>();
        if (CollectionUtils.isEmpty(rolesPermissList)) {
            updateRoleCache.addAll(roles);
        } else {
            int len = rolesPermissList.size();
            for (int index = 0; index < len; index++) {
                //角色未命中
                if (rolesPermissList.get(index) == null) {
                    updateRoleCache.add(roles.get(index));
                } else {
                    rolesPermissMap.put(roles.get(index), rolesPermissList.get(index));
                }
            }
        }

        //缓存更新
        if (CollectionUtils.isNotEmpty(updateRoleCache)) {
            Map<String, Set<String>> rolesPermissMapFromDB = this.queryRolesPermissFromDB(context, updateRoleCache);
            rolesPermissMap.putAll(rolesPermissMapFromDB);
            try {
                cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
                        (Map) rolesPermissMapFromDB);
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
                //                        FUNC_PERMISS_EXPIRE_SECOND);
            } catch (Exception e) {
                return rolesPermissMap;
            }
        }
        return rolesPermissMap;
    }

    /**
     * 角色权限并集
     *
     * @param context      请求上下文
     * @param roleCodeList 角色编码列表
     */
    private Set<String> queryFuncPermission(CommonContext context, List<String> roleCodeList) throws AuthServiceException {
        Set<String> functionPermission = new HashSet<>();
        if (CollectionUtils.isEmpty(roleCodeList)) {
            return functionPermission;
        }
        Map<String, Set<String>> rolesFuncAccessPermiss = this.queryFuncPermissByRoles(context, roleCodeList);
        //取并集
        if (rolesFuncAccessPermiss != null && !rolesFuncAccessPermiss.isEmpty()) {
            rolesFuncAccessPermiss.forEach((role, rolePermiss) -> {
                functionPermission.addAll(rolePermiss);
            });
        }
        return functionPermission;
    }

    /**
     * 构建功能权限返回列表
     */
    private List<FunctionPojo> structureFuncPermission(Set<String> functionPermission, List<FunctionPojo> functionPojoList) {
        if (CollectionUtils.isEmpty(functionPojoList)) {
            return functionPojoList;
        }
        //        Set<String> tempSet = functionPermission;
        //        if (tempSet == null) {
        //            tempSet = new HashSet<>();
        //        }
        for (FunctionPojo functionPojo : functionPojoList) {
            if (functionPermission.contains(functionPojo.getFuncCode())) {
                functionPojo.setIsEnabled(Boolean.TRUE);
            } else {
                functionPojo.setIsEnabled(Boolean.FALSE);
            }
        }
        return functionPojoList;
    }

    /**
     * 批量查询角色的功能权限
     *
     * @param context 请求上下文
     * @param roles   角色编码列表
     */
    private Map<String, Set<String>> queryRolesPermissFromDB(CommonContext context, Collection<String> roles) throws AuthServiceException {
        Map<String, Set<String>> funcAccessMap = new HashMap<>();
        if (roles == null || roles.isEmpty()) {
            return funcAccessMap;
        }
        try {
            List<FuncAccess> funcAccessList =
                    funcAccessMapper.queryFuncAccessProvider(context.getTenantId(), context.getAppId(), roles, null, Boolean.FALSE);

            roles.forEach(roleCode -> {
                funcAccessMap.put(roleCode, new HashSet<>());
            });
            if (CollectionUtils.isNotEmpty(funcAccessList)) {
                funcAccessList.forEach(funcAccess -> {
                    funcAccessMap.get(funcAccess.getRoleCode()).add(funcAccess.getFuncCode());
                });
            }
        } catch (Exception e) {
            log.error("===auth.queryRolesPermissFromDB() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return funcAccessMap;
    }

    /**
     * 删除角色功能权限
     */
    private void delRoleFuncPermissCache(CommonContext context, String roleCode) throws AuthServiceException {
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        try {
            //            cacheManager.delObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION, roleCode);
        } catch (Exception e) {
            log.error("===auth.delRoleFuncPermissCache() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 检测角色是否存在
     */
    private void rolesIsExist(CommonContext authContext, Set<String> roles) throws AuthServiceException {
        if (CollectionUtils.isNotEmpty(roles)) {
            if (roleService.roleCodeOrRoleNameExists(authContext, roles, null) != roles.size()) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
    }

    //add
    private Set<String> queryFuncSetByRoles(CommonContext context, List<String> roles) throws AuthServiceException {
        Set<String> funcSet = new HashSet<>();
        if (CollectionUtils.isEmpty(roles)) {
            return funcSet;
        }
        List<Set<String>> rolesPermissList = null;
        try {
            //            rolesPermissList = (List) cacheManager.getMultiObject(
            //                    context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION, roles);
        } catch (Exception e) {
            return this.queryRolesAccessSetFromDB(context, roles);
        }

        //检测缓存是否全部命中
        List<String> updateRoleCache = new LinkedList<>();
        if (CollectionUtils.isEmpty(rolesPermissList)) {
            updateRoleCache.addAll(roles);
        } else {
            for (int index = 0; index < rolesPermissList.size(); index++) {
                //角色未命中
                if (rolesPermissList.get(index) == null) {
                    updateRoleCache.add(roles.get(index));
                } else {
                    funcSet.addAll(rolesPermissList.get(index));
                }
            }
        }

        //缓存更新
        if (CollectionUtils.isNotEmpty(updateRoleCache)) {
            Map<String, Set<String>> rolesPermissMapFromDB = this.queryRolesPermissFromDB(context, updateRoleCache);
            rolesPermissMapFromDB.values().forEach(set -> {
                funcSet.addAll(set);
            });
            try {
                cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
                        (Map) rolesPermissMapFromDB);
                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
                //                        FUNC_PERMISS_EXPIRE_SECOND);
            } catch (Exception e) {
                return funcSet;
            }
        }
        return funcSet;
    }

    private Set<String> queryRolesAccessSetFromDB(CommonContext context, Collection<String> roles) throws AuthServiceException {
        Set<String> funcAccessSet = new HashSet<>();
        if (roles == null || roles.isEmpty()) {
            return funcAccessSet;
        }
        try {
            List<FuncAccess> funcAccessList =
                    funcAccessMapper.queryFuncAccessProvider(context.getTenantId(), context.getAppId(), roles, null, Boolean.FALSE);

            if (CollectionUtils.isNotEmpty(funcAccessList)) {
                funcAccessList.forEach(funcAccess -> {
                    funcAccessSet.add(funcAccess.getFuncCode());
                });
            }
        } catch (Exception e) {
            log.error("===auth.queryRolesAccessSetFromDB() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return funcAccessSet;
    }

    private void checkFuncCodeExit(CommonContext context, Set<String> funcCodeSet) throws AuthServiceException {
        Set<String> tempSet;
        try {
            tempSet =
                    funcMapper.queryFunctionCode(context.getTenantId(), context.getAppId(), null, null, null, null, null, null, null, Boolean.FALSE);
        } catch (Exception e) {
            log.error("===auth.checkFuncCodeExit() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        if (CollectionUtils.isEmpty(tempSet)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        for (String funcCode : funcCodeSet) {
            if (!tempSet.contains(funcCode)) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
    }

    private void partUpdateAccess(CommonContext context, Set<String> needAdd, Set<String> needDel, String roleCode, Set<String> funcCodeSet)
            throws AuthServiceException {

        List<FuncAccess> accessList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(needAdd)) {
            for (String funcCode : needAdd) {
                FuncAccess funcAccess = new FuncAccess();
                funcAccess.setId(IdUtil.generateId());
                funcAccess.setTenantId(context.getTenantId());
                funcAccess.setAppId(context.getAppId());
                funcAccess.setRoleCode(roleCode);
                funcAccess.setDelFlag(Boolean.FALSE);
                funcAccess.setFuncCode(funcCode);
                funcAccess.setModifier(context.getUserId());
                funcAccess.setModifyTime(System.currentTimeMillis());
                accessList.add(funcAccess);
            }
        }

        try {
            if (CollectionUtils.isNotEmpty(needDel)) {
                funcAccessMapper.batchDel(context.getTenantId(),
                        context.getAppId(),
                        roleCode,
                        context.getUserId(),
                        needDel,
                        System.currentTimeMillis());
            }
            if (CollectionUtils.isNotEmpty(accessList)) {
                //                funcAccessMapper.batchInsert(accessList);
            }
            cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
                    roleCode,
                    funcCodeSet);
            //            cacheManager.expire(
            //                    context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
            //                    FUNC_PERMISS_EXPIRE_SECOND);
        } catch (Exception e) {
            log.error("===auth.partUpdateAccess() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

}
