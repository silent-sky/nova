package com.nova.paas.auth.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.EntitySharePojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:28
 */
public interface EntityShareService {

    /**
     * 批量创建共享规则
     *
     * @param context 请求上下文
     */
    void createEntityShare(CommonContext context, List<EntitySharePojo> shareList) throws AuthServiceException;

    /**
     * 删除共享规则
     *
     * @param context        请求上下文
     * @param entityShareIds 共享规则ID列表
     * @param status         规则状态
     */
    void delEntityShare(CommonContext context, Set<String> entityShareIds, Integer status) throws AuthServiceException;

    /**
     * 删除共享规则
     *
     * @param context 请求上下文
     * @param entitys 共享对象列表
     */
    void delEntityShareByEntitys(CommonContext context, Set<String> entitys) throws AuthServiceException;

    /**
     * 根据共享规则的被共享方查询更新规则(并集)
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param receives 被共享方
     * @param status   状态
     */
    List<EntitySharePojo> queryEntityShareByReceives(
            CommonContext context, String entityId, Map<Integer, Set<String>> receives, Integer status) throws AuthServiceException;

    /**
     * 查询数据更新规则
     *
     * @param context          请求上下文
     * @param ids              id列表
     * @param entitys          对象实体列表
     * @param sourceTypes      共享者类型
     * @param sources          共享者列表
     * @param receiveTypes     被共享者类型
     * @param receives         被共享者列表
     * @param permission       权限
     * @param status           状态
     * @param sharesOrReceives 分享和被分享者id列表
     * @param page             分页信息
     */
    List<EntitySharePojo> queryEntityShare(
            CommonContext context,
            Set<String> ids,
            Set<String> entitys,
            Set<Integer> sourceTypes,
            Set<String> sources,
            Set<Integer> receiveTypes,
            Set<String> receives,
            Integer permission,
            Integer status,
            Set<String> sharesOrReceives,
            Map<Integer, Set<String>> sharesId,
            Map<Integer, Set<String>> receivesId,
            Map<Integer, Set<String>> sharesOrReceivesId,
            PageInfo page) throws AuthServiceException;

    /**
     * 更新共享规则信息
     *
     * @param context 请求上下文
     * @param pojo    更新规则对象
     */
    void updateEntityShare(CommonContext context, EntitySharePojo pojo) throws AuthServiceException;

    /**
     * 更新共享规则信息
     *
     * @param context 请求上下文
     * @param pojo    更新规则对象
     */
    void updateEntitySharePermissionByRule(CommonContext context, EntitySharePojo pojo) throws AuthServiceException;

    /**
     * 批量更新共享规则状态
     *
     * @param context        请求上下文
     * @param entityShareIds 规则ID列表
     * @param status         状态
     */
    void updateEntityShareStatus(
            CommonContext context, Set<String> entityShareIds, Integer status) throws AuthServiceException;

    /**
     * 查询org所在的Id
     *
     * @param context context  上下文
     * @param orgIds  orgId
     * @param type    org类型
     * @return id列表
     */
    Set<String> queryRuleIdsByOrg(
            CommonContext context, Set<String> orgIds, Integer type, Integer status, Boolean asShare) throws AuthServiceException;

    /**
     * 查询org所在的rule
     *
     * @param context context  上下文
     * @param orgIds  组织架构id
     * @param type    org类型
     * @return list
     */
    List<EntitySharePojo> queryRulePojoByOrgId(
            CommonContext context, Set<String> orgIds, Integer type, Integer status, Boolean asShare) throws AuthServiceException;

    /**
     * 查询共享方的共享规则
     *
     * @param context   请求上下文
     * @param entityId  对象id
     * @param shareType 共享类型
     * @param shares    共享源id
     * @param status    状态
     */
    List<EntitySharePojo> queryRuleByShares(CommonContext context, String entityId, Integer shareType, Set<String> shares, Integer status)
            throws AuthServiceException;

}
