package com.yuntan.common.exception;

/**
 * 密码修改失败异常
 */
public class PasswordEditFailedException extends BusinessException{

    private static final long serialVersionUID = 1L;

    public PasswordEditFailedException() {
        super();
    }

    public PasswordEditFailedException(String msg){
        super(msg);
    }

    public PasswordEditFailedException(String msg, Throwable cause){
        super(msg, cause);
    }

    public PasswordEditFailedException(Throwable cause){
        super(cause);
    }

}
