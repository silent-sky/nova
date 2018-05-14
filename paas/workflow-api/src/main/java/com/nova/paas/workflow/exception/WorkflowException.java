package com.nova.paas.workflow.exception;

/**
 * 内部服务抛出此异常
 * zhenghaibo
 * 2018/5/8 19:53
 */
public class WorkflowException extends RuntimeException {
    private static final long serialVersionUID = 1423386124128431838L;
    private WorkflowErrorMsg errorMsg;

    public WorkflowException(WorkflowErrorMsg errorMsg, String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = errorMsg;
    }

    public WorkflowException(WorkflowErrorMsg errorMsg, String message) {
        super(message);
        this.errorMsg = errorMsg;
    }

    public WorkflowException(WorkflowErrorMsg errorMsg, Throwable cause) {
        super(cause);
        this.errorMsg = errorMsg;
    }

    public WorkflowException(WorkflowErrorMsg errorMsg) {
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
