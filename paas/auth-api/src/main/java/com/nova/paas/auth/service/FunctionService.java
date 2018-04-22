package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.param.QryFunctionParam;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Set;

/**
 * 业务功能服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface FunctionService {

    List<FunctionPojo> queryFunction(CommonContext context, QryFunctionParam param) throws AuthServiceException;

    void addFunc(CommonContext context, FunctionPojo pojo) throws AuthServiceException;

    void batchDeleteFunc(CommonContext context, Set<String> idSet) throws AuthServiceException;

    /**
     * 更新功能节点信息(只支持更新funcName,parentId,funcOrder)
     *
     * @param context      请求上下文
     * @param functionPojo 待更新的pojo
     */
    void updateFunc(CommonContext context, FunctionPojo functionPojo) throws AuthServiceException;

    //    /**
    //     * 查询企业应用所有的功能
    //     *
    //     * @param context 请求上下文
    //     * @return 功能列表
    //     */
    //    List<FunctionPojo> queryFunctionByTenant(CommonContext context) throws AuthServiceException;
    //
    //
    //
    //    /**
    //     * 更新功能节点信息(只支持更新funcName,parentCode,levelCode,funcOrder)
    //     *
    //     * @param context      请求上下文
    //     * @param functionPojo 待更新的pojo
    //     */
    //    void updateFunc(CommonContext context, FunctionPojo functionPojo) throws AuthServiceException;
    //
    //    /**
    //     * 删除业务功能
    //     *
    //     * @param context 请求上下文
    //     * @param funcId  功能ID
    //     */
    //    void delFunc(CommonContext context, String funcId) throws AuthServiceException;
    //
    //    /**
    //     * 功能查询
    //     */
    //    List<FunctionPojo> queryFunction(
    //            CommonContext context,
    //            Set<String> ids,
    //            Set<String> funcCodes,
    //            String funcName,
    //            String funcOrder,
    //            String levelCode,
    //            String parentCode,
    //            Integer funcType) throws AuthServiceException;
    //
    //    /**
    //     * 删除业务功能
    //     *
    //     * @param context 请求上下文
    //     * @param funcSet 功能code列表
    //     */
    //    void batchDelFunc(CommonContext context, Set<String> funcSet) throws AuthServiceException;

}
