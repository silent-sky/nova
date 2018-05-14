package com.nova.paas.auth.mapper.permission;

import com.nova.paas.auth.entity.permission.EntityOpenness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

/**
 * zhenghaibo
 * 2018/4/13 15:32
 */
public interface EntityOpennessMapper {
//    /**
//     * 更新企业下某个对象的权限
//     */
//    @Update("update dt_entity_openness set openness_type=#{opennessType},modifier=#{modifier}, modify_time=#{modifyTime} where tenant_id=#{tenantId} and entity_id=#{entityId}")
//    Integer updateEntityDeleted(
//            @Param("tenantId") String tenantId,
//            @Param("entityId") String entityId,
//            @Param("opennessType") Integer opennessType,
//            @Param("modifier") String modifier,
//            @Param("modifyTime") Long modifyTime);
//
//    //    @UpdateProvider(type = EntityOpennessSQLProvider.class, method = "delEntityOpenness")
//    void delEntityOpenness(
//            @Param("tenantId") String tenantId,
//            @Param("appId") String appId,
//            @Param("entitys") Collection entitys,
//            @Param("modifier") String modifier);
//
//    //    @UpdateProvider(type = EntityOpennessSQLProvider.class, method = "updateEntityOpenness")
//    void updateEntityOpenness(
//            @Param("tenantId") String tenantId,
//            @Param("appId") String appId,
//            @Param("entitys") Collection entitys,
//            @Param("scope") Integer scope,
//            @Param("permission") Integer permission,
//            @Param("modifier") String modifier,
//            @Param("modifyTime") Long modifyTime);
//
//    //    @SelectProvider(type = EntityOpennessSQLProvider.class, method = "queryEntityOpenness")
//    List<EntityOpenness> queryEntityOpenness(
//            @Param("tenantId") String tenantId,
//            @Param("appId") String appId,
//            @Param("entitys") Collection entitys,
//            @Param("scope") Integer scope,
//            @Param("permission") Integer permission);
}
