package com.nova.saas.hr.controller;

import com.alibaba.fastjson.JSON;
import com.nova.paas.common.pojo.Result;
import com.nova.paas.workflow.arg.BpmDeployArg;
import com.nova.paas.workflow.exception.WorkflowErrorMsg;
import com.nova.paas.workflow.exception.WorkflowServiceException;
import com.nova.paas.workflow.pojo.WorkflowPojo;
import com.nova.paas.workflow.service.BpmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * zhenghaibo
 * 2018/5/16 16:47
 */
@RestController
@RequestMapping("/paas")
@CrossOrigin
@Slf4j
public class WorkflowController {
    @Inject
    private BpmService bpmService;

    @PostMapping(value = "/bpm/deploy")
    public Result<WorkflowPojo> deploy(@RequestBody BpmDeployArg arg) {
        log.info("BPM deploy:{}", JSON.toJSONString(arg));
        Result result = new Result<>();
        WorkflowPojo workflowPojo = new WorkflowPojo();
        try {
            workflowPojo =
                    bpmService.deploy(arg.getContext(), arg.isNewFlag(), JSON.toJSONString(arg.getWorkflowJson()), JSON.toJSONString(arg.getRuleJson()));
        } catch (WorkflowServiceException e) {
            log.error(e.getErrorMsg().getMessage(), e);
            result.setErrCode(e.getErrorMsg().getCode());
            result.setErrMessage(e.getErrorMsg().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setErrCode(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION.getCode());
            result.setErrMessage(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION.getMessage());
        }
        result.setResult(workflowPojo);
        log.info("BPM deploy result:{}", result);
        return result;
    }
}
