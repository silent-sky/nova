package com.nova.saas.hr.controller;

import com.nova.paas.auth.arg.CreateFuncArg;
import com.nova.paas.auth.arg.DeleteFuncArg;
import com.nova.paas.auth.arg.QryFunctionArg;
import com.nova.paas.auth.arg.UpdateFuncArg;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.param.QryFunctionParam;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.auth.service.FunctionService;
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

    @PostMapping(value = "/func/list")
    public Result findFuncList(@RequestBody QryFunctionArg arg) {
        Result result = new Result<>();
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

    @PostMapping(value = "/func/create")
    public Result createFunction(@RequestBody CreateFuncArg arg) {
        Result<String> result = new Result<>();
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
        Result<String> result = new Result<>();
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
        Result<String> result = new Result<>();
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

}
