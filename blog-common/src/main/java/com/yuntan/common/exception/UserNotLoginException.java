package com.yuntan.common.exception;

public class UserNotLoginException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public UserNotLoginException() {
        super();
    }

    public UserNotLoginException(String msg) {
        super(msg);
    }

    public UserNotLoginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserNotLoginException(Throwable cause) {
        super(cause);
    }

}
