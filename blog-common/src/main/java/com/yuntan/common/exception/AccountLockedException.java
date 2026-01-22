package com.yuntan.common.exception;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends BusinessException {

    public AccountLockedException() {
        super();
    }

    public AccountLockedException(String msg) {
        super(msg);
    }

}
