package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.EntityOpennessPojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:28
 */
public interface EntityOpennessService {

    /**
     * 创建对象级权限
     *
     * @param context                请求上下文
     * @param entityOpennessPojoList 对象级权限对象
     */
    void createEntityOpenness(
            CommonContext context, List<EntityOpennessPojo> entityOpennessPojoList) throws AuthServiceException;

    /**
     * 对象级权限查询
     *
     * @param context 请求上下文
     */
    List<EntityOpennessPojo> queryEntityOpenness(
            CommonContext context, Set<String> entitys, Integer permission, Integer scope, PageInfo page) throws AuthServiceException;

    /**
     * 对象级权限查询
     *
     * @param context  请求上下文
     * @param entityId 查询的对象
     */
    Integer queryEntityOpennessScopeByEntity(CommonContext context, String entityId) throws AuthServiceException;

    /**
     * 删除对象级权限
     *
     * @param context 请求上下文
     * @param entitys 对象列表
     */
    void delEntityOpenness(CommonContext context, Set<String> entitys) throws AuthServiceException;

    /**
     * 更新对象级数据权限
     *
     * @param context            请求上下文
     * @param entityOpennessList 对象级权限列表
     */
    void updateEntityOpenness(
            CommonContext context, List<EntityOpennessPojo> entityOpennessList) throws AuthServiceException;

    /**
     * 根据对象id查询对象基础数据权限(走缓存)
     */
    EntityOpennessPojo queryEntityOpennessByEntity(CommonContext context, String entityId) throws AuthServiceException;

    /**
     * 移除基础数据权限缓存
     */
    void removeEntityOpennessRedisCache(CommonContext context, Set<String> entitys) throws AuthServiceException;

}
