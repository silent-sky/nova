package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.FuncAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface FunctionAccessMapper {

    @Select("select  *  from  auth_func_access  where  tenant_id=#{tenantId}   and   app_id=#{appId}  and   role_code=#{roleCode} and del_flag=false ")
    List<FuncAccess> queryFuncAccess(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode);

    List<FuncAccess> queryFuncAccessProvider(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roles") Collection<String> roles,
            @Param("codes") Collection<String> codes,
            @Param("delFlag") Boolean delFlag);

    List<FuncAccess> queryFuncAccessEntitys(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roles") Collection<String> roles,
            @Param("codes") Collection<String> codes,
            @Param("delFlag") Boolean delFlag);

    @Update("update  auth_func_access  set  del_flag=true,modifier=#{modifier},modify_time=#{modifyTime}  where tenant_id=#{tenantId}   and   app_id=#{appId}  and   role_code=#{roleCode} and del_flag=false")
    void delFuncAccess(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("modifier") String modifier,
            @Param("modifyTime") long modifyTime);

    @Select("select  id  from  auth_func_access  where  del_flag=false  and  tenant_id=#{tenantId}   and   app_id=#{appId}  and   role_code=#{roleCode}  and   func_code=#{funcCode}")
    String selectId(
            @Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode, @Param("funcCode") String funcCode);

    @Select("select  func_code  from  auth_func_access  where tenant_id=#{tenantId}   and   app_id=#{appId}  and   role_code=#{roleCode} and del_flag=false ")
    List<String> queryRoleFuncCodePermiss(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("roleCode") String roleCode);

    void batchDel(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCode") String roleCode,
            @Param("userId") String userId,
            @Param("funcCodeList") Collection<String> funcCodeList,
            @Param("modifyTime") long modifyTime);

    void batchDelFuncAccess(
            @Param("tenantId") String tenantId,
            @Param("appId") String appId,
            @Param("roleCodes") Set<String> roleCodes,
            @Param("userId") String userId,
            @Param("funcCodes") Set<String> funcCodes,
            @Param("modifyTime") long modifyTime);

}
