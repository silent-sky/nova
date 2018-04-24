package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.FunctionAccessPojo;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色、字段权限的关联服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface FunctionAccessService {

    void addFuncToRole(CommonContext context, String roleId, Set<String> funcIds) throws AuthServiceException;

    void batchDelete(CommonContext context, String roleId, Set<String> funcIds) throws AuthServiceException;

    List<FunctionAccessPojo> findFuncAccessByRole(CommonContext context, String roleId) throws AuthServiceException;

    List<FunctionAccessPojo> findFuncAccessByFunc(CommonContext context, String funcId) throws AuthServiceException;

    List<FunctionPojo> queryFuncListByUser(CommonContext context, String userId) throws AuthServiceException;

    Set<String> queryFuncIdsByUser(CommonContext context, String userId) throws AuthServiceException;

    Map<String, Boolean> checkFuncPermission(CommonContext context, String userId, Set<String> funcIds) throws AuthServiceException;

}
