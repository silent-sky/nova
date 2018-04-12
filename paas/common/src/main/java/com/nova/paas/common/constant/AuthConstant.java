package com.nova.paas.common.constant;

/**
 * zhenghaibo
 * 2018/1/11 19:07
 */
public interface AuthConstant {
    interface EntityOpennessType {

        //私密
        int PRIVATE = 0;
        //公开只读
        int PUBLIC_READ_ONLY = 1;
        //公开读写
        int PUBLIC_READ_AND_WRITE = 2;
    }


    interface RoleType {
        //默认角色
        int DEFAULT = 0;
        //自定义角色
        int CUSTOMIZED = 1;
    }


    interface TreeRoot {

        //功能根节点funcCode
        String ROOT = "00000000000000000000000000000000";
    }


    interface FuncType {
        //系统默认功能
        int DEFAULT = 0;
        //自定义功能
        int CUSTOMIZED = 1;
    }


    interface FieldPermissionType {
        //不可见
        int INVISIBLE = 0;
        //只读
        int READ_ONLY = 1;
        //可编辑
        int READ_AND_WRITE = 2;
    }


    interface orgType {
        //用户
        int USER = 0;
        //部门
        int DEPT = 1;
        //组
        int GROUP = 2;
    }


    interface CACHE_EXPIRE {

        //业务功能失效时间
        int FUNCTION = 1800;  //30分钟
    }


    interface AuthType {
        public static final String AUTH_FIELD = "field";  //字段权限
        public static final String AUTH_FUNCTION = "function";  //功能权限
        public static final String AUTH_FUNCTION_PERMISSION = "functionPermission";  //功能权限
        public static final String AUTH_VIEW = "view";  //视图权限
        public static final String AUTH_USER_ROLE = "userRole"; //用户角色

        public static final String AUTH_DEPT = "dept";
        public static final String AUTH_ROLE_INFO = "roleInfo";//角色下的人、部门
    }
}
