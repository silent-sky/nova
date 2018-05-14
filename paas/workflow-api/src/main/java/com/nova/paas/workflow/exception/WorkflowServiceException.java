package com.nova.paas.workflow.exception;

/**
 * 所有service的方法需要抛出此异常
 * zhenghaibo
 * 2018/5/8 19:53
 */
public class WorkflowServiceException extends Exception {
    private static final long serialVersionUID = 5343755180485796195L;
    private WorkflowErrorMsg errorMsg;

    public WorkflowServiceException(WorkflowErrorMsg errorMsg, String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = errorMsg;
    }

    public WorkflowServiceException(WorkflowErrorMsg errorMsg, String message) {
        super(message);
        this.errorMsg = errorMsg;
    }

    public WorkflowServiceException(WorkflowErrorMsg errorMsg, Throwable cause) {
        super(cause);
        this.errorMsg = errorMsg;
    }

    public WorkflowServiceException(WorkflowErrorMsg errorMsg) {
        this.errorMsg = errorMsg;
    }

    public WorkflowErrorMsg getErrorMsg() {
        return errorMsg;
    }

    @Override
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        }
        return getErrorMsg().getMessage();
    }
}
