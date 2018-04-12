package com.nova.paas.auth.exception;

/**
 * 所有service的方法需要抛出此异常
 * zhenghaibo
 * 2018/4/8 19:53
 */
public class AuthServiceException extends Exception {
    private static final long serialVersionUID = 5343755180485796195L;
    private AuthErrorMsg errorMsg;

    public AuthServiceException(AuthErrorMsg errorMsg, String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = errorMsg;
    }

    public AuthServiceException(AuthErrorMsg errorMsg, String message) {
        super(message);
        this.errorMsg = errorMsg;
    }

    public AuthServiceException(AuthErrorMsg errorMsg, Throwable cause) {
        super(cause);
        this.errorMsg = errorMsg;
    }

    public AuthServiceException(AuthErrorMsg errorMsg) {
        this.errorMsg = errorMsg;
    }

    public AuthErrorMsg getErrorMsg() {
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
