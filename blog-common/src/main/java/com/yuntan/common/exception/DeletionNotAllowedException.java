package com.yuntan.common.exception;

public class DeletionNotAllowedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public DeletionNotAllowedException() {
        super();
    }

    public DeletionNotAllowedException(String msg) {
        super(msg);
    }

    public DeletionNotAllowedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DeletionNotAllowedException(Throwable cause) {
        super(cause);
    }

}
