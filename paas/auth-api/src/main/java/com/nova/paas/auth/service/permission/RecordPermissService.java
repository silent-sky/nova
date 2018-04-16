package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.pojo.permission.EntityObjects;
import com.nova.paas.auth.pojo.permission.QueryRecordPermissObjectFilter;
import com.nova.paas.auth.pojo.permission.RecordPermissObjectPojo;
import com.nova.paas.auth.pojo.permission.RecordPermissPojo;
import com.nova.paas.auth.pojo.permission.RecordPermissTeamPojo;
import com.nova.paas.auth.pojo.permission.TeamPojo;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.pojo.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 15:28
 */
public interface RecordPermissService {

    /**
     * 添加记录权限
     *
     * @param context            请求上下文
     * @param recordPermissTeams 记录权限列表
     */
    void addRecordPermiss(
            CommonContext context, List<RecordPermissTeamPojo> recordPermissTeams) throws AuthServiceException;

    /**
     * 添加记录权限
     *
     * @param context             请求上下文
     * @param recordPermissObject 记录信息实体对象
     */
    void createRecordPermiss(CommonContext context, RecordPermissObjectPojo recordPermissObject) throws AuthServiceException;

    /**
     * 查询记录
     *
     * @param context  请求上下文
     * @param entityId 对象ID列表
     * @param depts    部门
     * @param owners   记录拥有人员
     * @param objects  记录Id
     * @param union    是否取并集
     * @param page     分页信息
     */
    List<RecordPermissPojo> queryRecordPermiss(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects, boolean union, PageInfo page)
            throws AuthServiceException;

    /**
     * 查询记录列表
     *
     * @param context  请求上下文
     * @param entityId 对象ID列表
     * @param depts    部门
     * @param owners   记录拥有人员
     * @param objects  记录Id
     * @param union    是否取并集
     * @param page     分页信息
     */
    List<String> queryRecordPermissObjects(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects, boolean union, PageInfo page)
            throws AuthServiceException;

    /**
     * 查询对象记录
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param filters  过滤条件列表
     * @param pageInfo 分页信息
     */
    List<String> queryRecordPermissObject(
            CommonContext context, String entityId, List<QueryRecordPermissObjectFilter> filters, PageInfo pageInfo) throws AuthServiceException;

    /**
     * 更新记录权限
     *
     * @param context            请求上下文
     * @param recordPermissTeams 记录权限
     */
    void updateRecordPermiss(
            CommonContext context, List<RecordPermissTeamPojo> recordPermissTeams) throws AuthServiceException;

    /**
     * 删除对象记录权限
     *
     * @param context           请求上下文
     * @param entityObjectsList 待删除的记录列表
     */
    void delRecordPermiss(CommonContext context, List<EntityObjects> entityObjectsList) throws AuthServiceException;

    /**
     * 删除记录权限(不删除记录团队)
     *
     * @param context  请求上下文
     * @param entityId 对象ID
     * @param depts    部门列表
     * @param owners   用户列表
     * @param objects  记录列表
     */
    void delRecordPermiss(
            CommonContext context, String entityId, Set<String> depts, Set<String> owners, Set<String> objects) throws AuthServiceException;

    /**
     * 更新记录负责人
     *
     * @param context  请求上下文
     * @param entityId 对象id
     * @param objects  记录列表
     * @param owner    负责人
     * @param newOwner 新负责人
     */
    void updateRecordPermissOwner(
            CommonContext context, String entityId, Set<String> objects, String owner, String newOwner) throws AuthServiceException;

    /**
     * 更新记录所属部门
     *
     * @param context  请求上下文
     * @param entityId 对象实体ID
     * @param objects  记录ID列表
     * @param dept     部门
     * @param newDept  新部门
     */
    void updateRecordPermissDept(
            CommonContext context, String entityId, Set<String> objects, String dept, String newDept) throws AuthServiceException;

    /**
     * 查询对象记录的权限
     *
     * @param context  请求上下文
     * @param entityId 对象实体
     * @param objects  记录列表
     */
    Map<String, List<TeamPojo>> queryRecordPermissTeam(
            CommonContext context, String entityId, Set<String> objects) throws AuthServiceException;

    /**
     * 删除企业数据
     */
    void delTenant(CommonContext context) throws AuthServiceException;

}
