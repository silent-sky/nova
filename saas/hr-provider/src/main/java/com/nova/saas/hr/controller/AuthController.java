package com.nova.saas.hr.controller;

import com.nova.paas.auth.arg.CreateFuncArg;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.service.FunctionService;
import com.nova.paas.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

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

}
