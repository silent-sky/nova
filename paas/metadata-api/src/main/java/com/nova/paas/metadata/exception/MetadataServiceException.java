package com.nova.paas.metadata.exception;

/**
 * 所有service的方法需要抛出此异常
 * zhenghaibo
 * 2018/4/8 19:53
 */
public class MetadataServiceException extends Exception {
    private static final long serialVersionUID = 5343755180485796195L;
    private MetadataErrorCode errorCode;

    public MetadataServiceException(MetadataErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public MetadataServiceException(MetadataErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MetadataErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        }
        return getErrorCode().getMessage();
    }
}
