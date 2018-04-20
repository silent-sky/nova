package com.nova.paas.auth.service;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.RoleRecordTypePojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Set;

/**
 * 多业务类型接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface RecordTypeAccessService {

    /**
     * 添加角色业务类型
     *
     * @param context         请求上下文
     * @param recordTypePojos 业务类型pojo
     */
    void addRoleRecordType(CommonContext context, String entityId, String recordTypeId, List<RoleRecordTypePojo> recordTypePojos)
            throws AuthServiceException;

    /**
     * 更新角色业务类型
     *
     * @param context         请求上下文
     * @param recordTypePojos 业务类型pojo
     */
    void updateRoleRecordType(CommonContext context, String entityId, List<RoleRecordTypePojo> recordTypePojos) throws AuthServiceException;

    /**
     * 校验recordTypeId是否被引用
     *
     * @param context      请求上下文
     * @param entityId     实体id
     * @param recordTypeId 业务类型id
     */
    boolean checkRecordType(CommonContext context, String entityId, String recordTypeId) throws AuthServiceException;

    /**
     * 查询角色相关的业务类型
     *
     * @param context  请求上下文
     * @param entityId 实体id
     * @param roleCode 角色码
     * @return 多业务类型列表
     */
    List<RoleRecordTypePojo> queryRoleRecordType(CommonContext context, String entityId, String roleCode) throws AuthServiceException;

    /**
     * 指定role、给多个entity添加recordType
     *
     * @param context      请求上下文
     * @param roleCode     角色
     * @param entityId     对象
     * @param recordTypeId 对象
     */
    void batchAddRoleRecordType(CommonContext context, Set<String> entityId, String recordTypeId, String roleCode) throws AuthServiceException;

    /**
     * 查询角色相关的业务类型
     *
     * @param context   请求上下文
     * @param entityIds 实体id
     * @param roleCodes 角色码
     * @return 多业务类型列表
     */
    List<RoleRecordTypePojo> batchQueryRoleRecordType(CommonContext context, Set<String> entityIds, Set<String> roleCodes)
            throws AuthServiceException;

}
