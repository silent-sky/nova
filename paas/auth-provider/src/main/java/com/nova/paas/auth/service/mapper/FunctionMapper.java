package com.nova.paas.auth.service.mapper;

import com.nova.paas.auth.service.entity.Function;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface FunctionMapper {

    @Select("select * from auth_function where tenant_id = #{tenantId} and app_id = #{appId} and del_flag = false")
    List<Function> queryFunctionByTenant(@Param("tenantId") String tenantId, @Param("appId") String appId);

    @Select("select * from auth_function where tenant_id = #{tenantId} and app_id = #{appId} and id = #{id} and del_flag = false")
    List<Function> queryFunctionById(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("id") String id);

    void batchDel(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("modifier") String modifier,
            @Param("funcCodeList") List<String> funcCodeList,
            @Param("modifyTime") long modifyTime);

    List<Function> queryFunction(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("ids") Collection<String> ids,
            @Param("funcCodes") Collection<String> funcCodes,
            @Param("funcName") String funcName,
            @Param("funcOrder") String funcOrder,
            @Param("levelCode") String levelCode,
            @Param("parentCode") String parentCode,
            @Param("funcType") Integer funcType,
            @Param("delFlag") Boolean delFlag);

    Set<String> queryFunctionCode(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("ids") Collection<String> ids,
            @Param("funcCodes") Collection<String> funcCodes,
            @Param("funcName") String funcName,
            @Param("funcOrder") String funcOrder,
            @Param("levelCode") String levelCode,
            @Param("parentCode") String parentCode,
            @Param("funcType") Integer funcType,
            @Param("delFlag") Boolean delFlag);

    @Select("select func_code,parent_code from auth_function where tenant_id = #{tenantId} and app_id = #{appId} and del_flag = false")
    List<Function> querySimpleFunctionByTenant(@Param("tenantId") String tenantId, @Param("appId") String appId);
}
