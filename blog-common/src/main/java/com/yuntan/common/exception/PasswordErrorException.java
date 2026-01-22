package com.yuntan.common.exception;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public PasswordErrorException() {
        super();
    }

    public PasswordErrorException(String msg) {
        super(msg);
    }

    public PasswordErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PasswordErrorException(Throwable cause) {
        super(cause);
    }

}
