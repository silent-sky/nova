package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.Function;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface FunctionMapper {

    void insert(Function function);

    void batchDelete(
            @Param("tenantId") String tenantId,
            @Param("modifiedBy") String modifiedBy,
            @Param("funcIds") Set<String> funcIds,
            @Param("modifiedAt") long modifiedAt);

    void update(Function function);

    List<Function> queryFunction(
            @Param("tenantId") String tenantId,
            @Param("ids") Set<String> ids,
            @Param("funcName") String funcName,
            @Param("parentId") String parentId,
            @Param("funcType") Integer funcType);

}
