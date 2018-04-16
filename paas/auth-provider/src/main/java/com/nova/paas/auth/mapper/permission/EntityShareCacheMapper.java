package com.nova.paas.auth.mapper.permission;

import com.nova.paas.auth.entity.permission.EntityShareCache;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:32
 */
public interface EntityShareCacheMapper {

    //  @SelectProvider(type = EntityShareCacheProvider.class, method = "queryReceivedSharedEntityIds")
    Set<String> queryReceivedSharedEntityIds(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("userId") String userId,
            @Param("permission") Integer permission);

    //  @SelectProvider(type = EntityShareCacheProvider.class, method = "queryEntityShareCache")
    List<EntityShareCache> queryEntityShareCache(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("receiveUser") String receiveUser,
            @Param("permission") Boolean permission);

    //  @SelectProvider(type = EntityShareCacheProvider.class, method = "queryEntityShareCacheByReceiveAndShareUsers")
    List<EntityShareCache> queryEntityShareCacheByReceiveAndShareUsers(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("shareUsers") Collection<String> shareUsers,
            @Param("userRoles") Collection<String> userRoles,
            @Param("receiveUser") String receiveUser);

    //  @DeleteProvider(type = EntityShareCacheProvider.class, method = "delEntityShareCache")
    void delEntityShareCache(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("rules") Collection<String> rules);

    //  @DeleteProvider(type = EntityShareCacheProvider.class, method = "delEntityShareCacheByEntitys")
    void delEntityShareCacheByEntitys(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("entitys") Collection<String> entitys);

    //  @DeleteProvider(type = EntityShareCacheProvider.class, method = "delEntityShareCacheByUserRule")
    void delEntityShareCacheByUserRule(
            @Param("tenantId") String tenantId,
            @Param("deptUserMap") Map<String, Set<String>> deptUserMap,
            @Param("deptRuleIdMap") Map<String, Set<String>> deptRuleIdMap,
            @Param("asShare") boolean asShare);

    //  @SelectProvider(type = EntityShareCacheProvider.class, method = "querySharedEntity")
    List<EntityShareCache> queryShareEntity(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("shareIds") Set<String> shareIds,
            @Param("receiveIds") Set<String> receiveIds,
            @Param("ids") Set<String> ids,
            @Param("ruleIds") Set<String> ruleIds);

    //  @UpdateProvider(type = EntityShareCacheProvider.class, method = "updateEntityShareCachePermission")
    void updateEntityShareCachePermission(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("ids") Set<String> ids, @Param("permission") Integer permission);

    //  @DeleteProvider(type = EntityShareCacheProvider.class, method = "delRuleCacheByUserRuleId")
    void delRuleCacheByUserRuleId(
            @Param("tenantId") String tenantId,
            @Param("ruleIds") Set<String> ruleIds,
            @Param("userIds") Set<String> userIds,
            @Param("asShare") boolean asShare);

    //  @SelectProvider(type = EntityShareCacheProvider.class, method = "entityShareCache")
    List<EntityShareCache> entityShareCache(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("entityShareId") String entityShareId,
            @Param("shareUser") String shareUser,
            @Param("receiveUser") String receiveUser);

}
