package com.nova.paas.auth;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.RoleViewPojo;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Set;

/**
 * 角色视图权限服务接口
 * zhenghaibo
 * 2018/4/8 19:30
 */
public interface ViewAccessService {

    /**
     * 新增角色的对象视图权限
     *
     * @param context       上下文
     * @param roleViewPojos 角色视图关联pojo列表
     */
    void addRoleViewAccess(CommonContext context, List<RoleViewPojo> roleViewPojos) throws AuthServiceException;

    /**
     * 查询角色的对象视图权限
     *
     * @param context      上下文
     * @param entityId     实体id
     * @param recordTypeId 业务类型id
     * @param roleCode     角色码
     */
    List<RoleViewPojo> queryRoleViewAccess(CommonContext context, String entityId, String recordTypeId, String roleCode) throws AuthServiceException;

    /**
     * 删除角色的对象视图权限
     *
     * @param context  上下文
     * @param roleCode 角色码
     * @param entityId 实体id
     */
    void delRoleViewPermiss(CommonContext context, String roleCode, String entityId) throws AuthServiceException;

    void delRoleViewAccess(CommonContext context, String roleCode) throws AuthServiceException;

    /**
     * 查询角色的对象视图权限
     *
     * @param context      上下文
     * @param entityIds    实体ids
     * @param recordTypeId 业务类型id
     * @param roleCode     角色码
     */
    List<RoleViewPojo> queryViewAccess(CommonContext context, Set<String> entityIds, String recordTypeId, String roleCode)
            throws AuthServiceException;

}
