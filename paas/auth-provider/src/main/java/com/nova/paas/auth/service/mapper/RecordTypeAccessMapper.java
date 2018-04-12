package com.nova.paas.auth.service.mapper;

import com.nova.paas.auth.service.entity.RecordTypeAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface RecordTypeAccessMapper {

    List<RecordTypeAccess> queryRecordTypeAccessProvider(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("entityId") Set<String> entityId,
            @Param("recordTypeId") String recordTypeId);

    List<RecordTypeAccess> queryRoleRecordTypeByEntitys(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("entitys") Collection<String> entitys);

    void deleteRoleRecordType(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("entityId") String entityId,
            @Param("recordTypeId") String recordTypeId,
            @Param("defaultType") Boolean defaultType,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    void setTypeDefault(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Collection<String> roleCodes,
            @Param("entityId") String entityId,
            @Param("recordTypeId") String recordTypeId,
            @Param("modifier") String modifier);

}
