package com.yuntan.common.exception;

import java.io.Serial;

public class UnauthorizedException extends CommonException {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int STATUS = 401;

    public UnauthorizedException() {
        super("Unauthorized", STATUS);
    }

    public UnauthorizedException(String message) {
        super(message, STATUS);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause, STATUS);
    }
}
