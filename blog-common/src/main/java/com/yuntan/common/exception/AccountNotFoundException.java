package com.yuntan.common.exception;

/**
 * 账号不存在异常
 */
public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
