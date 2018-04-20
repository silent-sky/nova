package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.EntityFieldSharePojo;
import com.nova.paas.auth.pojo.permission.EntityFieldShareReceivePojo;
import com.nova.paas.auth.pojo.permission.FieldShareRulePojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 17:19
 */
public interface EntityFieldShareService {

    /**
     * 创建条件共享
     *
     * @param context          请求上下文
     * @param entityFieldShare 条件共享
     */
    String create(CommonContext context, EntityFieldSharePojo entityFieldShare) throws AuthServiceException;

    /**
     * 更新条件共享
     */
    String update(CommonContext context, EntityFieldSharePojo entityFieldShare) throws AuthServiceException;

    /**
     * 删除条件共享规则
     *
     * @param status 规则状态(满足状态才能删除)
     */
    void delete(CommonContext context, String entityId, Set<String> ruleCodes, Integer status) throws AuthServiceException;

    /**
     * 共享规则查询
     */
    List<EntityFieldSharePojo> query(
            CommonContext context,
            String entityId,
            String ruleName,
            Integer status,
            Set<String> ruleCodes,
            Map<Integer, Set<String>> receivesWithType,
            Set<String> receives,
            Integer permission,
            PageInfo pageInfo) throws AuthServiceException;

    /**
     * 更新规则状态
     */
    void updateEntityFieldShareStatus(CommonContext context, String entityId, Set<String> ruleCodes, Integer status) throws AuthServiceException;

    /**
     * 查询共享给用户的数据条件sql列表
     */
    List<String> fieldShareReceiveSql(CommonContext context, String entityId) throws AuthServiceException;

    /**
     * 用户匹配的规则
     */
    List<EntityFieldShareReceivePojo> userReceivedRule(CommonContext context, String entityId) throws AuthServiceException;

    /**
     * 数据规则匹配
     */
    Map<String, Map<String, Object>> dataRuleExpressionPattern(
            CommonContext context, String entityId, Set<String> ruleCodes, Set<String> dataIds) throws AuthServiceException;

    /**
     * 查询对象字段设置的规则
     */
    List<FieldShareRulePojo> entityFieldRule(CommonContext context, String entity, Map<String, List<String>> fields) throws AuthServiceException;
}
