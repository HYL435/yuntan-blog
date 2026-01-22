package com.yuntan.common.exception;

public class DbException extends CommonException {

    private static final long serialVersionUID = 1L;
    private static final int STATUS = 500;

    public DbException() {
        super("Database error", STATUS);
    }

    public DbException(String message) {
        super(message, STATUS);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }

    public DbException(Throwable cause) {
        super(cause, STATUS);
    }
}
