package com.nova.paas.auth.exception;

/**
 * 内部服务抛出此异常
 * zhenghaibo
 * 2018/4/8 19:53
 */
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 1423386124128431838L;
    private AuthErrorMsg errorMsg;

    public AuthException(AuthErrorMsg errorMsg, String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = errorMsg;
    }

    public AuthException(AuthErrorMsg errorMsg, String message) {
        super(message);
        this.errorMsg = errorMsg;
    }

    public AuthException(AuthErrorMsg errorMsg, Throwable cause) {
        super(cause);
        this.errorMsg = errorMsg;
    }

    public AuthException(AuthErrorMsg errorMsg) {
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
