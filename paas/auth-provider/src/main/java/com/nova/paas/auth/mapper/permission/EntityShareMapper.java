package com.nova.paas.auth.mapper.permission;

import com.nova.paas.auth.entity.permission.EntityShare;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:32
 */
public interface EntityShareMapper {

    //  @UpdateProvider(type = EntityShareSQLProvider.class, method = "delEntityShare")
    void delEntityShare(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("ids") Collection ids, @Param("modifier") String modifier);

    //  @UpdateProvider(type = EntityShareSQLProvider.class, method = "delEntityShareByEntitys")
    void delEntityShareByEntitys(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entitys") Collection entitys,
            @Param("modifier") String modifier);

    //  @UpdateProvider(type = EntityShareSQLProvider.class, method = "updateEntityShareStatus")
    void updateEntityShareStatus(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("ids") Collection ids,
            @Param("status") Integer status,
            @Param("modifier") String modifier);

    @Update("update dt_entity_share set share_type=#{shareType},share_id=#{shareId},receive_type=#{receiveType},receive_id=#{receiveId}, "
                    + "status=#{status},permission=#{permission},modifier=#{modifier},modify_time=#{modifyTime} where  tenant_id=#{tenantId} and"
                    + " app_id = #{appId} and entity_id = #{entityId} and id = #{id} and del_flag = 0 ")
    void updateEntityShare(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("id") String id,
            @Param("shareType") Integer shareType,
            @Param("shareId") String shareId,
            @Param("receiveType") Integer receiveType,
            @Param("receiveId") String receiveId,
            @Param("status") Integer status,
            @Param("permission") Integer permission,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    @Select("select distinct(tenant_id) from dt_entity_share where app_id = #{appId} and del_flag = 0 ")
    List<String> queryTenantsByApp(@Param("appId") String appId);

    //  @SelectProvider(type = EntityShareSQLProvider.class, method = "queryEntityShare")
    List<EntityShare> queryEntityShare(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("ids") Collection ids,
            @Param("entitys") Collection entitys,
            @Param("shareTypes") Collection shareTypes,
            @Param("shares") Collection shares,
            @Param("receiveTypes") Collection receiveTypes,
            @Param("receives") Collection receives,
            @Param("sharesOrReceives") Collection sharesOrReceives,
            @Param("status") Integer status,
            @Param("permission") Integer permission,
            @Param("sharesId") Map sharesId,
            @Param("receivesId") Map receivesId,
            @Param("sharesOrReceivesId") Map sharesOrReceivesId);

    //  @SelectProvider(type = EntityShareSQLProvider.class, method = "queryEntityShareByReceives")
    List<EntityShare> queryEntityShareByReceives(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("receives") Map receives,
            @Param("status") Integer status);

    //  @SelectProvider(type = EntityShareSQLProvider.class, method = "queryIdsByOrgIds")
    List<EntityShare> queryRuleIdsByOrg(
            @Param("tenantId") String tenantId,
            @Param("orgIds") Set<String> orgIds,
            @Param("orgType") Integer orgType,
            @Param("status") Integer status,
            @Param("asShare") Boolean asShare);

    //  @SelectProvider(type = EntityShareSQLProvider.class, method = "queryRuleByShares")
    List<EntityShare> queryRuleByShares(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("entityId") String entityId,
            @Param("shares") Set<String> shares,
            @Param("shareType") Integer shareType,
            @Param("status") Integer status);

}
