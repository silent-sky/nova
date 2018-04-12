package com.nova.paas.metadata.exception;

/**
 * 内部服务抛出此异常
 * zhenghaibo
 * 2018/4/8 19:53
 */
public class MetadataException extends RuntimeException {
    private static final long serialVersionUID = 1423386124128431838L;
    private MetadataErrorCode errorCode;

    public MetadataException(MetadataErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public MetadataException(MetadataErrorCode errorCode, String message) {
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
