package com.nova.saas.hr.controller;

import com.nova.paas.auth.arg.CreateFuncArg;
import com.nova.paas.auth.arg.CreateRoleArg;
import com.nova.paas.auth.arg.CreateUserRoleArg;
import com.nova.paas.auth.arg.DeleteFuncArg;
import com.nova.paas.auth.arg.DeleteRoleArg;
import com.nova.paas.auth.arg.DeleteUserRoleByRolesArg;
import com.nova.paas.auth.arg.DeleteUserRoleByUsersArg;
import com.nova.paas.auth.arg.QryFunctionArg;
import com.nova.paas.auth.arg.QryRoleArg;
import com.nova.paas.auth.arg.QryUserRoleByRoleArg;
import com.nova.paas.auth.arg.QryUserRoleByUserArg;
import com.nova.paas.auth.arg.UpdateFuncArg;
import com.nova.paas.auth.arg.UpdateRoleArg;
import com.nova.paas.auth.arg.UpdateUserRoleArg;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.param.QryFunctionParam;
import com.nova.paas.auth.param.QryRoleParam;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.auth.pojo.RolePojo;
import com.nova.paas.auth.pojo.UserRolePojo;
import com.nova.paas.auth.service.FunctionService;
import com.nova.paas.auth.service.RoleService;
import com.nova.paas.auth.service.UserRoleService;
import com.nova.paas.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * zhenghaibo
 * 2018/4/9 11:21
 */
@RestController
@RequestMapping("/paas/auth")
@Slf4j
public class AuthController {
    @Inject
    private FunctionService functionService;
    @Inject
    private RoleService roleService;
    @Inject
    private UserRoleService userRoleService;

    @PostMapping(value = "/func/create")
    public Result createFunction(@RequestBody CreateFuncArg arg) {
        Result result = new Result<>();
        try {
            functionService.addFunc(arg.getContext(), arg.getPojo());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/func/delete")
    public Result deleteFunction(@RequestBody DeleteFuncArg arg) {
        Result result = new Result<>();
        try {
            functionService.batchDeleteFunc(arg.getContext(), arg.getIds());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/func/update")
    public Result updateFunction(@RequestBody UpdateFuncArg arg) {
        Result result = new Result<>();
        try {
            functionService.updateFunc(arg.getContext(), arg.getPojo());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/func/list")
    public Result findFuncList(@RequestBody QryFunctionArg arg) {
        Result<List<FunctionPojo>> result = new Result<>();
        try {
            QryFunctionParam param = new QryFunctionParam().builder()
                    .funcName(arg.getFuncName())
                    .funcType(arg.getFuncType())
                    .parentId(arg.getParentId())
                    .ids(arg.getIds())
                    .build();
            List<FunctionPojo> list = functionService.queryFunction(arg.getContext(), param);
            result.setResult(list);
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/role/create")
    public Result createRole(@RequestBody CreateRoleArg arg) {
        Result result = new Result<>();
        try {
            roleService.addRole(arg.getContext(), arg.getPojo());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/role/delete")
    public Result deleteRole(@RequestBody DeleteRoleArg arg) {
        Result result = new Result<>();
        try {
            roleService.batchDeleteRole(arg.getContext(), arg.getIds());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/role/update")
    public Result updateRole(@RequestBody UpdateRoleArg arg) {
        Result result = new Result<>();
        try {
            roleService.updateRole(arg.getContext(), arg.getPojo());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/role/list")
    public Result findRoleList(@RequestBody QryRoleArg arg) {
        Result<List<RolePojo>> result = new Result<>();
        try {
            QryRoleParam param = new QryRoleParam().builder()
                    .roleIds(arg.getRoleIds())
                    .roleCode(arg.getRoleCode())
                    .roleName(arg.getRoleName())
                    .roleType(arg.getRoleType())
                    .pageInfo(arg.getPageInfo())
                    .build();
            List<RolePojo> list = roleService.queryRoleListByPage(arg.getContext(), param);
            result.setResult(list);
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/userRole/role/add")
    public Result createUserRole(@RequestBody CreateUserRoleArg arg) {
        Result result = new Result<>();
        try {
            userRoleService.addUserToRole(arg.getContext(), arg.getRoleId(), arg.getUsers());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/userRole/role/delete")
    public Result deleteUserRoleByRoles(@RequestBody DeleteUserRoleByRolesArg arg) {
        Result result = new Result<>();
        try {
            userRoleService.delRoleUserByRoles(arg.getContext(), arg.getUserId(), arg.getRoleIds());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/userRole/user/delete")
    public Result deleteUserRoleByUsers(@RequestBody DeleteUserRoleByUsersArg arg) {
        Result result = new Result<>();
        try {
            userRoleService.delRoleUserByUsers(arg.getContext(), arg.getRoleId(), arg.getUserIds());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/userRole/update")
    public Result updateUserRole(@RequestBody UpdateUserRoleArg arg) {
        Result result = new Result<>();
        try {
            userRoleService.updateUserRole(arg.getContext(), arg.getUserId(), arg.getRoleIds());
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/userRole/role/list")
    public Result findUserRoleListByRole(@RequestBody QryUserRoleByRoleArg arg) {
        Result<List<UserRolePojo>> result = new Result<>();
        try {
            List<UserRolePojo> list = userRoleService.getUserRoleRelationByRole(arg.getContext(), arg.getRoleId(), arg.getTargetType());
            result.setResult(list);
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/userRole/user/list")
    public Result findUserRoleListByUser(@RequestBody QryUserRoleByUserArg arg) {
        Result<List<UserRolePojo>> result = new Result<>();
        try {
            List<UserRolePojo> list = userRoleService.getUserRoleRelationByUser(arg.getContext(), arg.getTargetId());
            result.setResult(list);
        } catch (AuthServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION.getMessage());
        }
        return result;
    }

}
