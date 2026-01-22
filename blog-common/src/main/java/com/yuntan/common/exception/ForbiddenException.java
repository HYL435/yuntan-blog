package com.yuntan.common.exception;

public class ForbiddenException extends CommonException {

    private static final long serialVersionUID = 1L;
    private static final int STATUS = 403;

    public ForbiddenException() {
        super("Forbidden", STATUS);
    }

    public ForbiddenException(String message) {
        super(message, STATUS);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }

    public ForbiddenException(Throwable cause) {
        super(cause, STATUS);
    }
}
