package com.nova.paas.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nova.paas.auth.entity.FieldAccess;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.FieldAccessMapper;
import com.nova.paas.auth.service.FieldAccessService;
import com.nova.paas.auth.service.FunctionAccessService;
import com.nova.paas.auth.service.RoleService;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.support.CacheManager;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
@Slf4j
public class FieldAccessServiceImpl implements FieldAccessService {

    @Autowired
    private FieldAccessMapper mapper;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private FunctionAccessService functionAccessService;

    //    @Value("${FIELD_PERMISS_EXPIRE_SECOND}")
    //    private int FIELD_PERMISS_EXPIRE_SECOND;

    private static final Set<Integer> legalFieldPermissionSet;

    static {
        legalFieldPermissionSet = new HashSet<>();
        legalFieldPermissionSet.add(AuthConstant.FieldPermissionType.INVISIBLE);  //不可见
        legalFieldPermissionSet.add(AuthConstant.FieldPermissionType.READ_ONLY);   //只读
        legalFieldPermissionSet.add(AuthConstant.FieldPermissionType.READ_AND_WRITE);  //读写
    }

    /**
     * 查询某个角色对某个对象的字段权限
     *
     * @param context  请求上下文
     * @param roleCode 角色编码
     * @param entityId 实体ID
     * @return 字段权限列表
     * @throws AuthServiceException exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer> queryRoleEntityPermission(CommonContext context, String roleCode, String entityId) throws AuthServiceException {

        if (StringUtils.isAnyBlank(roleCode, entityId)) {
            return null;
        }

        Map<String, Integer> fieldPermissionMap =
                this.queryRoleFieldPermissionCache(context, Collections.singletonList(roleCode), entityId).get(roleCode);
        if (fieldPermissionMap == null || fieldPermissionMap.isEmpty()) {
            this.rolesIsExist(context, Collections.singleton(roleCode));
        }

        return fieldPermissionMap;
    }

    /**
     * 查询用户的字段权限
     *
     * @param context  请求上下文
     * @param entityId 实体ID
     * @return 字段权限列表
     * @throws AuthServiceException exception
     */
    @Override
    public Map<String, Integer> queryUserEntityPermission(CommonContext context, String entityId) throws AuthServiceException {
        if (StringUtils.isBlank(entityId)) {
            return null;
        }

        //用户角色
//        List<String> roleCodeList = userRoleService.queryRoleIdListByUserId(context);
        List<String> roleCodeList = null;

        //用户无角色
        if (CollectionUtils.isEmpty(roleCodeList)) {
            return null;
        }

        //角色权限，关于角色对"查看列表"的权限校验在queryRolesEntityFieldPermiss中实现
        Map<String, Map<String, Integer>> roleEntityFieldPermission = this.queryRolesEntityFieldPermiss(context, roleCodeList, entityId);
        if (MapUtils.isEmpty(roleEntityFieldPermission)) {
            return null;
        }
        return this.mergeRolesFieldPermiss(roleEntityFieldPermission);
    }

    /**
     * 更新角色字段权限
     *
     * @param context            请求上下文
     * @param roleCode           角色编码
     * @param entityId           实体ID
     * @param fieldPermissionMap 字段权限MAP
     * @throws AuthServiceException exception
     */
    @Override
    @Transactional
    public void updateEntityFieldPermission(CommonContext context, String roleCode, String entityId, Map<String, Integer> fieldPermissionMap)
            throws AuthServiceException {

        log.info("[Request],method:{}, context:{},roleCode:{},entityId:{},fieldPermissionMap:{}",
                "updateEntityFieldPermission",
                JSON.toJSONString(context),
                JSON.toJSONString(roleCode),
                JSON.toJSONString(entityId),
                JSON.toJSONString(fieldPermissionMap));

        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (fieldPermissionMap == null) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        this.entityIdVerify(entityId);
        this.rolesIsExist(context, Collections.singleton(roleCode));

        //新加的权限校验逻辑，即便当前角色没有'查看列表'的功能权限，这里依然可以编辑；当该角色再次拥有'查看列表'的功能权限以后，这里的设置就会生效

        List<FieldAccess> needAdd = this.getFieldAccessEntity(context, roleCode, entityId, fieldPermissionMap);
        try {
            mapper.delEntityFieldPermission(context.getTenantId(),
                    context.getAppId(),
                    roleCode,
                    entityId,
                    context.getUserId(),
                    System.currentTimeMillis());

            if (CollectionUtils.isNotEmpty(needAdd)) {
                //                mapper.batchInsert(needAdd);
            }
            String key = this.getCacheKey(context, roleCode, entityId);
            //            cacheManager.putMap(key, fieldPermissionMap);
            //            cacheManager.expire(key, FIELD_PERMISS_EXPIRE_SECOND);
        } catch (Exception e) {
            log.error("===auth.updateEntityFieldPermission() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 更新对象字段的角色权限   该接口暂时没有rest接口
     *
     * @param context          请求上下文
     * @param entityId         对象
     * @param fieldId          字段
     * @param roleFieldPermiss 角色权限
     * @throws AuthServiceException exception
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void updateMultiRoleFieldPermiss(CommonContext context, String entityId, String fieldId, Map<String, Integer> roleFieldPermiss)
            throws AuthServiceException {

        log.info("[Request],method:{}, context:{},entityId:{},fieldId:{},fieldPermissionMap:{}",
                "updateMultiRoleFieldPermiss",
                JSON.toJSONString(context),
                JSON.toJSONString(entityId),
                JSON.toJSONString(fieldId),
                JSON.toJSONString(roleFieldPermiss));

        //基本数据校验
        if (roleFieldPermiss == null || roleFieldPermiss.isEmpty()) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        this.entityIdVerify(entityId);
        this.fieldIdVerify(fieldId);
        Set<String> roleCodeSet = roleFieldPermiss.keySet();
        this.rolesIsExist(context, roleCodeSet);

        ////新加的权限校验逻辑，这里不做限制

        List<FieldAccess> fieldAccessList = new LinkedList<>();
        roleFieldPermiss.forEach((roleCode, permiss) -> {
            //校验字段权限在否合法
            this.fieldPermissVerify(permiss);
            FieldAccess fieldAccess = new FieldAccess(IdUtil.generateId(),
                    context.getTenantId(),
                    roleCode,
                    entityId,
                    fieldId,
                    permiss,
                    context.getUserId(),
                    System.currentTimeMillis(),
                    Boolean.FALSE);
            fieldAccessList.add(fieldAccess);
        });

        try {
            //删除历史权限
            mapper.batchDelSupportRoles(context.getTenantId(),
                    context.getAppId(),
                    roleCodeSet,
                    entityId,
                    fieldId,
                    context.getUserId(),
                    System.currentTimeMillis());

            if (CollectionUtils.isNotEmpty(fieldAccessList)) {
                //                mapper.batchInsert(fieldAccessList);
            }

            //查询缓存数据，不存在缓存的key不增加
            List<String> roles = new ArrayList<>(roleCodeSet);
            List<String> keys = this.getCacheKeys(context, roles, Collections.singletonList(entityId));

            //            List cacheRoleFieldPermiss = cacheManager.getMultiObject(keys);

            Map<String, Map<String, Integer>> allPermission = new HashMap<>();
            int count = 0;
            while (count < keys.size()) {
                count++;
                //                if (cacheRoleFieldPermiss.get(count) != null) {
                //                    Map<String, Integer> cache = (Map) cacheRoleFieldPermiss.get(count);
                //                    cache.put(fieldId, roleFieldPermiss.get(roles.get(count)));
                //
                //                    allPermission.put(roles.get(count), cache);
                //                }
            }

            this.updateEntityCache(context, entityId, new ArrayList<>(allPermission.keySet()), allPermission);
        } catch (Exception e) {
            log.error("===auth.updateMultiRoleFieldPermiss() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 查询对象字段权限,roles为空，代表所有角色；该接口暂时未对外提供rest接口
     *
     * @param context  请求上下文
     * @param roles    角色Id列表
     * @param entityId 对象ID
     * @param fieldId  字段ID
     * @return 角色对象的字段权
     * @throws AuthServiceException exception
     */
    @Override
    public Map<String, Integer> multiRoleFieldPermiss(CommonContext context, Set<String> roles, String entityId, String fieldId)
            throws AuthServiceException {

        Map<String, Integer> rolePermiss = new HashMap<>();
        if (StringUtils.isAnyBlank(entityId, fieldId)) {
            return rolePermiss;
        }

        //新加的权限校验逻辑
        Map<String, List<String>> entityRoles = this.entityRoles(context, new ArrayList<>(roles), Collections.singleton(entityId));
        if (CollectionUtils.isEmpty(entityRoles.get(entityId))) {
            return null;
        }
        roles = new HashSet<>(entityRoles.get(entityId));

        try {
            List<FieldAccess> fieldAccessList = mapper.queryFieldAccess(context.getTenantId(),
                    context.getAppId(),
                    roles,
                    Collections.singletonList(entityId),
                    Collections.singletonList(fieldId),
                    Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(fieldAccessList)) {
                fieldAccessList.forEach(fieldAccess -> rolePermiss.put(fieldAccess.getRoleId(), fieldAccess.getPermission()));
            }
            return rolePermiss;
        } catch (Exception e) {
            log.error("===auth.multiRoleFieldPermiss() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 查询角色对象的字段权限，无rest接口
     *
     * @param context  请求上下文
     * @param roles    角色列表
     * @param entityId 对象实体
     * @return <role,<field,权限值>>
     * @throws AuthServiceException exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Map<String, Integer>> queryRolesEntityFieldPermiss(CommonContext context, List<String> roles, String entityId)
            throws AuthServiceException {

        Map<String, Map<String, Integer>> rolesEntityFieldPermission = new HashMap<>();
        if (CollectionUtils.isEmpty(roles)) {
            return rolesEntityFieldPermission;
        }

        //新加的权限校验逻辑
        Map<String, List<String>> entityRoles = this.entityRoles(context, roles, Collections.singleton(entityId));
        if (CollectionUtils.isEmpty(entityRoles.get(entityId))) {
            return rolesEntityFieldPermission;
        }

        roles = entityRoles.get(entityId);
        rolesEntityFieldPermission = this.queryRoleFieldPermissionCache(context, roles, entityId);

        return rolesEntityFieldPermission;
    }

    /**
     * 删除角色的对象字段权限
     *
     * @param context  请求上下文
     * @param roleCode 角色唯一标识
     * @param entityId 对象
     * @throws AuthServiceException exception
     */
    @Override
    @Transactional
    public void delRoleFieldPermiss(CommonContext context, String roleCode, String entityId) throws AuthServiceException {
        log.info("[Request], method:{},context:{},roleCode:{},entityId:{}",
                "delRoleFieldPermiss",
                JSON.toJSONString(context),
                JSON.toJSONString(roleCode),
                JSON.toJSONString(entityId));

        this.delRoleFieldPermissCache(context, roleCode, entityId);
        try {
            mapper.batchDel(context.getTenantId(), context.getAppId(), roleCode, entityId, null, context.getUserId(), System.currentTimeMillis());
        } catch (Exception e) {
            log.error("===auth.delRoleFieldPermiss() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    /**
     * 查询用户对对象的字段权限，场景：app客户端用户首次登陆，查询
     *
     * @param context 请求上下文
     * @param entitys 对象实体列表
     * @return <entity,<filed,value>>
     * @throws AuthServiceException exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Integer>> userEntitysFieldPermiss(CommonContext context, Set<String> entitys) throws AuthServiceException {

        Map<String, Map<String, Integer>> userEntitysFieldPermiss = Maps.newHashMap();
        if (entitys != null) {
            entitys.remove(null);
        }
        if (CollectionUtils.isEmpty(entitys)) {
            return userEntitysFieldPermiss;
        }

        //用户角色
//        List<String> roleCodeList = userRoleService.queryRoleIdListByUserId(context);
        List<String> roleCodeList = null;

        //用户无角色,对象字段权限直接返回null
        if (CollectionUtils.isEmpty(roleCodeList)) {
            entitys.forEach(entity -> userEntitysFieldPermiss.put(entity, null));
            return userEntitysFieldPermiss;
        }

        Map<String, Set<String>> needQueryFromDB = new HashMap<>();
        Map<String, Map<String, Map<String, Integer>>> cacheRolesFieldPermiss = new HashMap<>();

        //查询缓存 <entityId,List<role>>
        Map<String, List<String>> entityRoles = this.entityRoles(context, roleCodeList, entitys);//新加的权限校验逻辑

        //所有的entityId
        List<String> entityIds = new ArrayList<>(entitys);
        //会去访问redis的entityId
        List<String> keyEntityIds = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for (String entity : entityIds) {
            roleCodeList = entityRoles.get(entity);
            //初始数据结构
            if (CollectionUtils.isEmpty(roleCodeList)) {
                userEntitysFieldPermiss.put(entity, null);
                continue;
            }

            keyEntityIds.add(entity);
            keys.addAll(this.getCacheKeys(context, roleCodeList, Collections.singletonList(entity)));
        }

        List<Map<String, Integer>> roleFieldPermission = null;
        try {
            //            roleFieldPermission = (List) cacheManager.getMultiObject(keys);
        } catch (Exception e) {
            Map<String, Set<String>> allData = new HashMap<>();
            entityRoles.forEach((entity, roles) -> allData.put(entity, new HashSet<>(roles)));
            needQueryFromDB.putAll(allData);
        }

        if (roleFieldPermission != null) {//未出现异常、处理查询到的缓存
            int cacheIndex = 0;

            for (String entity : keyEntityIds) {
                cacheRolesFieldPermiss.put(entity, new HashMap<>());
                List<String> roles = entityRoles.get(entity);

                for (int index = 0; index < roles.size(); index++) {
                    if (roleFieldPermission.get(cacheIndex) == null) {
                        needQueryFromDB.computeIfAbsent(entity, k -> new HashSet<>());
                        needQueryFromDB.get(entity).add(roles.get(index));
                    } else {
                        if (roleFieldPermission.get(cacheIndex).isEmpty()) {
                            userEntitysFieldPermiss.put(entity, new HashMap<>());
                            needQueryFromDB.remove(entity);
                            cacheRolesFieldPermiss.remove(entity);

                            cacheIndex = cacheIndex + (roles.size() - index); //这个entity对应的剩下的角色的位置补齐
                            break;
                        }
                        cacheRolesFieldPermiss.get(entity).put(roles.get(index), roleFieldPermission.get(cacheIndex));
                    }

                    cacheIndex++;
                }
            }
        }

        //查询数据库
        Map<String, Map<String, Map<String, Integer>>> entityRolesFieldPermiss = new HashMap<>();

        //未命中缓存的key的集合
        List<String> cacheKeys = Lists.newArrayList();

        if (!needQueryFromDB.isEmpty()) {
            entityRolesFieldPermiss = this.queryFieldAccessByFilterFromDB(context, needQueryFromDB);

            needQueryFromDB.forEach((entity, roles) -> {
                cacheKeys.addAll(this.getCacheKeys(context, new ArrayList<>(roles), Collections.singletonList(entity)));
            });

            //数据库中的数据添加到cache中获取的数据
            entityRolesFieldPermiss.forEach((entity, rolesPermission) -> {
                if (cacheRolesFieldPermiss.get(entity) == null) {
                    cacheRolesFieldPermiss.put(entity, rolesPermission);
                } else {
                    cacheRolesFieldPermiss.get(entity).putAll(rolesPermission);
                }
            });
        }

        //合并权限
        if (!cacheRolesFieldPermiss.isEmpty()) {
            cacheRolesFieldPermiss.keySet().forEach(entity -> {
                userEntitysFieldPermiss.put(entity, this.mergeRolesFieldPermiss(cacheRolesFieldPermiss.get(entity))); //取多个角色的权限并集
            });
        }

        //更新缓存
        try {
            this.updateMultiEntityIdsCache(context, cacheKeys, needQueryFromDB, entityRolesFieldPermiss);
        } catch (Exception e) {
            log.error("redis error {}", cacheKeys, e);
            return userEntitysFieldPermiss;
        }

        return userEntitysFieldPermiss;
    }

    private List<FieldAccess> getFieldAccessEntity(
            CommonContext context, String roleId, String entityId, Map<String, Integer> fieldPermissionMap) throws AuthServiceException {
        List<FieldAccess> needAdd = new ArrayList<>();
        if (!fieldPermissionMap.isEmpty()) {
            fieldPermissionMap.forEach((field, permiss) -> {
                this.fieldIdVerify(field);
                this.fieldPermissVerify(permiss);
                FieldAccess fieldAccess = new FieldAccess();
                fieldAccess.setId(IdUtil.generateId());
                fieldAccess.setTenantId(context.getTenantId());
                fieldAccess.setRoleId(roleId);
                fieldAccess.setEntityId(entityId);
                fieldAccess.setFieldId(field);
                fieldAccess.setPermission(permiss);
                fieldAccess.setModifiedBy(context.getUserId());
                fieldAccess.setModifiedAt(System.currentTimeMillis());
                fieldAccess.setDelFlag(Boolean.FALSE);
                needAdd.add(fieldAccess);
            });
        }
        return needAdd;
    }

    private void fieldIdVerify(String fieldId) {
        if (StringUtils.isBlank(fieldId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    private void entityIdVerify(String entityId) throws AuthServiceException {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

    }

    //字段校验权限是否合法
    private void fieldPermissVerify(Integer permission) {
        if (permission == null || !legalFieldPermissionSet.contains(permission)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    /**
     * 查询多个角色对某个对象的字段权限
     *
     * @return <角色，<字段，权限值>>
     */
    private Map<String, Map<String, Integer>> queryRolesEntityFieldPermissFromDB(
            CommonContext context, Collection<String> roleCodes, String entityId) throws AuthServiceException {

        Map<String, Map<String, Integer>> rolesPermission = new HashMap<>();

        if (CollectionUtils.isEmpty(roleCodes)) {
            return rolesPermission;
        }
        roleCodes.forEach(roleCode -> rolesPermission.put(roleCode, new HashMap<>()));
        if (StringUtils.isBlank(entityId)) {
            return rolesPermission;
        }
        try {
            List<FieldAccess> fieldAccessList = mapper.queryFieldAccess(context.getTenantId(),
                    context.getAppId(),
                    roleCodes,
                    Collections.singletonList(entityId),
                    null,
                    Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(fieldAccessList)) {
                fieldAccessList.forEach(fieldAccess -> rolesPermission.get(fieldAccess.getRoleId())
                        .put(fieldAccess.getFieldId(), fieldAccess.getPermission()));
            }
        } catch (Exception e) {
            log.error("===auth.queryRolesEntityFieldPermissFromDB() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
        return rolesPermission;
    }

    //删除角色字段权限的缓存
    private void delRoleFieldPermissCache(CommonContext context, String roleCode, String entityId) throws AuthServiceException {

        Set<String> entityIdSet = new HashSet<>();
        if (StringUtils.isBlank(roleCode)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        try {
            if (entityId == null) {
                entityIdSet = mapper.queryEntityList(context.getTenantId(), context.getAppId(), roleCode);
            } else {
                entityIdSet.add(entityId);
            }

            if (CollectionUtils.isNotEmpty(entityIdSet)) {
                List<String> keys = this.getCacheKeys(context, Collections.singletonList(roleCode), new ArrayList<>(entityIdSet));
                //                cacheManager.delMultiKey(keys);
            }
        } catch (Exception e) {
            log.error("===auth.delRoleFieldPermissCache() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }
    }

    private Map<String, Map<String, Map<String, Integer>>> queryFieldAccessByFilterFromDB(
            CommonContext context, Map<String, Set<String>> needQueryFromDB) throws AuthServiceException {
        Map<String, Map<String, Map<String, Integer>>> entityRolesFieldPermiss = Maps.newHashMap();

        //构建数据结构
        needQueryFromDB.forEach((entity, roles) -> {
            entityRolesFieldPermiss.put(entity, new HashMap<>());
            roles.forEach(role -> {
                entityRolesFieldPermiss.get(entity).put(role, new HashMap<>());
            });
        });

        //查询db数据
        try {
            List<FieldAccess> fieldAccessList =
                    mapper.queryFieldAccessByEntityRolesFilter(context.getTenantId(), context.getAppId(), needQueryFromDB);
            //解析数据
            if (CollectionUtils.isNotEmpty(fieldAccessList)) {
                fieldAccessList.forEach(fieldAccess -> entityRolesFieldPermiss.get(fieldAccess.getEntityId())
                        .get(fieldAccess.getRoleId())
                        .put(fieldAccess.getFieldId(), fieldAccess.getPermission()));
            }
        } catch (Exception e) {
            log.error("===auth.queryFieldAccessByFilterFromDB() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

        return entityRolesFieldPermiss;
    }

    private void updateEntityCache(CommonContext context, String entityId, List<String> allRoles, Map<String, Map<String, Integer>> map) {
        Object[] cacheData = new Object[allRoles.size() * 2];
        List<String> cacheKeys = this.getCacheKeys(context, allRoles, Collections.singletonList(entityId));
        int count = 0;
        for (int index = 0; index < allRoles.size(); index++) {
            cacheData[count] = this.getCacheKey(context, allRoles.get(index), entityId);
            count++;
            cacheData[count] = map.get(allRoles.get(index));
            count++;
        }
        //        cacheManager.putMultiObject(cacheData);
        //        cacheManager.expireMultiKey(cacheKeys, FIELD_PERMISS_EXPIRE_SECOND);
    }

    private void updateMultiEntityIdsCache(
            CommonContext context,
            List<String> cacheKeys,
            Map<String, Set<String>> needQueryFromDB,
            Map<String, Map<String, Map<String, Integer>>> entityRolesFieldPermiss) {
        if (CollectionUtils.isNotEmpty(cacheKeys)) {
            Object[] cacheData = new Object[cacheKeys.size() * 2];

            int count = 0;
            for (Map.Entry<String, Set<String>> entry : needQueryFromDB.entrySet()) {
                for (String role : entry.getValue()) {
                    cacheData[count] = this.getCacheKey(context, role, entry.getKey());
                    count++;
                    cacheData[count] = entityRolesFieldPermiss.get(entry.getKey()).get(role);
                    count++;
                }
            }
            //            cacheManager.putMultiObject(cacheData);
            //            cacheManager.expireMultiKey(cacheKeys, FIELD_PERMISS_EXPIRE_SECOND);
        }
    }

    private List<String> getCacheKeys(CommonContext authContext, List<String> roles, List<String> entityIds) {
        List<String> keys = Lists.newArrayList();
        for (String entityId : entityIds) {
            for (String role : roles) {
                keys.add(Joiner.on(':').join(authContext.getTenantId(), authContext.getAppId(), AuthConstant.AuthType.AUTH_FIELD, role, entityId));
            }
        }
        return keys;
    }

    private String getCacheKey(CommonContext context, String role, String entityId) {
        return Joiner.on(':').join(context.getTenantId(), context.getAppId(), AuthConstant.AuthType.AUTH_FIELD, role, entityId);
    }

    //检测角色是否存在
    private void rolesIsExist(CommonContext context, Set<String> roles) throws AuthServiceException {
        if (CollectionUtils.isNotEmpty(roles)) {
//            if (roleService.roleCodeOrRoleNameExists(context, roles, null) != roles.size()) {
//                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
//            }
        }
    }

    private Map<String, Integer> mergeRolesFieldPermiss(Map<String, Map<String, Integer>> rolesFieldPermission) {
        Map<String, Integer> fieldPermissionMap = new HashMap<>();
        //如果有角色的字段权限没有设置,直接返回
        for (Map.Entry<String, Map<String, Integer>> entry : rolesFieldPermission.entrySet()) {
            if (entry.getValue().isEmpty()) {
                return fieldPermissionMap;
            }
        }
        //取并集
        rolesFieldPermission.forEach((role, rolePermiss) -> rolePermiss.forEach((field, permission) -> {
            if (fieldPermissionMap.get(field) == null) {
                fieldPermissionMap.put(field, permission);
            } else {
                if (permission != null && permission > fieldPermissionMap.get(field)) {
                    fieldPermissionMap.replace(field, permission);
                }
            }
        }));
        return fieldPermissionMap;
    }

    private Map<String, List<String>> entityRoles(CommonContext context, List<String> userRoles, Set<String> entityIds) throws AuthServiceException {
        Map<String, String> entityFuncCode = new HashMap<>();
        entityIds.forEach(entity -> {
            entityFuncCode.put(entity, entity);
        });
        Map<String, List<String>> entityRoles = new HashMap<>();
//        Map<String, Set<String>> rolesFuncCodes = functionAccessService.queryFuncAccessByRoles(context, new HashSet<>(userRoles));//role  funcCodeSet
//        entityFuncCode.forEach((entity, funcCode) -> userRoles.forEach(role -> {
//            if (rolesFuncCodes.get(role) != null && (rolesFuncCodes.get(role).contains(funcCode) || ("5".equals(entity) && rolesFuncCodes.get(role)
//                    .contains("PaymentObj")))) {
//                entityRoles.computeIfAbsent(entity, k -> new ArrayList<>());
//                entityRoles.get(entity).add(role);
//            }
//        }));
        return entityRoles;
    }

    private Map<String, Map<String, Integer>> queryRoleFieldPermissionCache(CommonContext context, List<String> roles, String entityId)
            throws AuthServiceException {
        Map<String, Map<String, Integer>> rolesEntityFieldPermission = Maps.newHashMap();

        List<Map<String, Integer>> roleFieldPermission = null;
        List<String> keys = this.getCacheKeys(context, roles, Collections.singletonList(entityId));

        //查询缓存
        try {
            //            roleFieldPermission = (List) cacheManager.getMultiObject(keys);
        } catch (Exception e) {
            return this.queryRolesEntityFieldPermissFromDB(context, roles, entityId);
        }

        //检测缓存是否全部命中
        List<String> updateRoleCache = new LinkedList<>();
        if (CollectionUtils.isEmpty(roleFieldPermission)) {
            updateRoleCache.addAll(roles);
        } else {
            int len = roles.size();
            for (int index = 0; index < len; index++) {
                if (roleFieldPermission.get(index) != null) {
                    rolesEntityFieldPermission.put(roles.get(index), roleFieldPermission.get(index));
                } else {
                    updateRoleCache.add(roles.get(index));
                }
            }
        }

        //存在未命中数据,查询mysql,更新缓存
        if (CollectionUtils.isNotEmpty(updateRoleCache)) {
            Map<String, Map<String, Integer>> tempRoleEntityFieldPermiss =
                    this.queryRolesEntityFieldPermissFromDB(context, updateRoleCache, entityId);
            rolesEntityFieldPermission.putAll(tempRoleEntityFieldPermiss);

            //处理缓存
            try {
                this.updateEntityCache(context, entityId, updateRoleCache, tempRoleEntityFieldPermiss);
            } catch (Exception e) {
                return rolesEntityFieldPermission;
            }
        }
        return rolesEntityFieldPermission;
    }

}
