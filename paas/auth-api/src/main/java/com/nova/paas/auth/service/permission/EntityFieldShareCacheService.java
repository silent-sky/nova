package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 17:19
 */
public interface EntityFieldShareCacheService {

    /**
     * 更新记录的rules
     *
     * @param context  请求上下文
     * @param entityId 对象id
     * @param dataId   数据id
     * @param versions 规则列表
     */
    void updateDataVersions(
            CommonContext context, String entityId, String dataId, List<String> versions) throws AuthServiceException;

    /**
     * dataids中添加rules
     */
    void addDataVersions(
            CommonContext context, String entityId, String dataId, List<String> versions) throws AuthServiceException;

    /**
     * 查询数据规则
     */
    List<String> dataVersions(CommonContext context, String entityId, String dataId) throws AuthServiceException;

    /**
     * 数据中添加规则
     */
    void datasAddVersion(
            CommonContext context, String entityId, String version, List<String> dataIds) throws AuthServiceException;

    /**
     * 删除版本的数据
     */
    void deleteVersion(CommonContext context, String entityId, String version) throws AuthServiceException;

    /**
     * 计算规则缓存
     */
    void calculateRuleCache(CommonContext context, String entityId, String ruleCode) throws AuthServiceException;

    /**
     * 消费数据更新
     */
    void consumerAppDataUpdate(CommonContext context, String entityId, String id, String operator, Map<String, Object> dataMap)
            throws AuthServiceException;

    /**
     * 重新计算数据的规则缓存
     */
    void calculateDatasCache(CommonContext context, String entityId, Set<String> dataIds) throws AuthServiceException;

    /**
     * 删除数据
     */
    void deleteDatas(CommonContext context, String entityId, Set<String> dataIds) throws AuthServiceException;

}
