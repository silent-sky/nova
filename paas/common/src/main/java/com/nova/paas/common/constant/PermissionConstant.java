package com.nova.paas.common.constant;

/**
 * zhenghaibo
 * 2018/4/13 18:10
 */
public interface PermissionConstant {

    interface EntityOpennessType {

        //私密
        int PRIVATE = 0;
        //公开只读
        int PUBLIC_READ_ONLY = 1;
        //公开读写
        int PUBLIC_READ_AND_WRITE = 2;
    }

    /********************************************************************/

    interface DataRightsSceneType {
        String ALL = "all";
        String SHARE_SCENE = "share";
        String RESPONSIBLE_DEPT_SCENE = "dept";
        String USER_SUBORDINATE_SCENE = "sub";
        String USER_SCENE = "user";
    }


    interface SystemValue {
        String DEFAULT_TENANT = "SYSTEM";
        String DEFAULT_APP = "SYSTEM";
        String DEFAULT_USER = "-1000";
    }


    interface EntityOpennessScope {

        //公开到公司
        int PUBLIC_ALL = 0;

        //公开到部门
        int PUBLIC_DEPT = 1;

        //私有
        int PRIVATE = 2;

        //纯私有
        int OWNER_PRIVATE = 3;

    }


    interface EntityOpennessPermiss {
        //只读
        int READ_ONLY = 1;
        //读写
        int READ_AND_WRITE = 2;
    }


    //数据库表字段长度
    interface FieldLengthConstant {
        int RECORD_ROLE_TYPE = 64;
        int RECORD_MEMBER_ID = 64;
        int ENTITY_ID = 64;
        int OBJECT_ID = 64;
        int USER_ID = 64;
        int DEPT_ID = 64;
        int SHARE_RECEIVE_ID = 64;
        int SHARE_SHARE_ID = 64;
    }


    //支持绑定类型
    interface RecordMemberType {
        //用户
        int USER = 0;
        //用户组
        int GROUP = 1;
        //部门
        int DEPT = 2;
    }


    //支持的成员权限
    interface RecorderMemberPermissType {
        //只读
        int READ_ONLY = 1;
        //读写
        int READ_AND_WRITE = 2;
    }


    interface DataRightsOrgType {
        //用户
        int USER = 0;
        //用户组
        int GROUP = 1;
        //部门
        int DEPT = 2;
    }


    //共享规则更新类型
    interface EntityShareType {
        //用户
        int USER = 0;
        //用户组
        int GROUP = 1;
        //部门
        int DEPT = 2;
        //角色
        int ROLE = 4;
    }


    //共享规则权限类型
    interface EntitySharePermissType {
        //只读
        int READ_ONLY = 1;
        //读写
        int READ_AND_WRITE = 2;
    }


    //共享规则状态
    interface EntityShareStatusType {
        //开启
        int OPEN = 1;
        //关闭
        int CLOSE = 0;
    }


    interface EmployeeDeptRelationType {

        //部门上级
        int SUPERIOR = 1;

        //部门直属用户
        int DIRECTLY = 0;

        //部门负责人
        int DEPT_DIRECT_LEADER = 2;

    }


    interface UserLeaderRelationType {
        int SUPERIOR = 1;
        int DIRECT_LEADER = 0;
    }


    interface OrgModifyEventType {

        //2 用户组
        int GROUP = 1;

        //5 更新用户用户组
        int USER_GROUP = 2;

    }


    interface RoleUserModifyEventType {
        int ROLE_DELETE_USER = 1; // 角色下删除用户
        int ROLE_ADD_USER = 2; // 角色下添加用户
        int USER_ROLE_UPDATE = 3;// 用户角色更新
        int DELETE_ROLE = 4; // 删除角色
    }


    interface OrgModifyEventStatus {
        // 1 启用, 2 停用, 3 删除
        int INVALID = 2;
        int VALID = 1;
        int DELETE = 3;

    }


    interface OrgSyncEventFlag {

        //组织架构更新的信息事件
        int ORG_MODIFY_EVENT_FLAG = 1;

        //组织架构表更新事件
        int ORG_TABLE_MODIFY_EVENT_FLAG = 2;

        //协同用户同步事件
        int XT_ORG_EMPLOYEE_FLAG = 3;

        //协同部门同步事件
        int XT_ORG_DEPARTMENT_FLAG = 4;

        //共享规则同步事件
        int SFA_ENTITY_SHARE_FLAG = 5;

        //基础数据权限同步事件
        int SFA_ENTITY_OPENNESS_FLAG = 6;

        //用户角色更新
        int AUTH_ROLE_USER_FLAG = 7;

        //业务数据更新事件
        int BUSSINESS_UPDATE_FLAG = 8;

    }


    interface DataRightsRedisKey {
        String RIGHTS_ENETITY = "entitys";
    }

}
