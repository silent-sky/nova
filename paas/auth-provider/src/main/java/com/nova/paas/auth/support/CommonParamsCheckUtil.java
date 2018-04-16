package com.nova.paas.auth.support;

import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.pojo.permission.TeamPojo;
import com.nova.paas.common.constant.PermissionConstant;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CommonParamsCheckUtil implements Serializable {

    private static final long serialVersionUID = 8698893678774101139L;

    public static final Set<Integer> recordMemberPermissType;

    static {
        recordMemberPermissType = new HashSet<>();
        recordMemberPermissType.add(PermissionConstant.RecorderMemberPermissType.READ_AND_WRITE);
        recordMemberPermissType.add(PermissionConstant.RecorderMemberPermissType.READ_ONLY);
    }

    private static final Set<Integer> recordMemberType;

    static {
        recordMemberType = new HashSet<>();
        recordMemberType.add(PermissionConstant.RecordMemberType.USER);
        recordMemberType.add(PermissionConstant.RecordMemberType.DEPT);
        recordMemberType.add(PermissionConstant.RecordMemberType.GROUP);
    }

    public static void teamPojoCheck(TeamPojo teamPojo) {
        if (teamPojo == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        if (StringUtils.isBlank(teamPojo.getMemberId())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (StringUtils.isNotBlank(teamPojo.getRoleType())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (teamPojo.getPermission() == null || (!recordMemberPermissType.contains(teamPojo.getPermission()))) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        if (teamPojo.getMemberType() == null || (!recordMemberType.contains(teamPojo.getMemberType()))) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

    }

    public static void entityIdCheck(String entityId) {
        if (StringUtils.isBlank(entityId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

    }

    public static void objectIdCheck(String objectId) {
        if (StringUtils.isBlank(objectId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

    }

    public static void userIdCheck(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

    }

    public static void deptIdCheck(String deptId) {
        if (StringUtils.isBlank(deptId)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

    }
}
