package com.telemarket.telemarketer.http.exceptions;

/**
 * 业务异常
 * <p>
 * Chen Yijie on 2016/11/25 16:37.
 */
public class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException(String msg,Throwable e) {
        super(msg,e);
    }

    public BaseRuntimeException(String msg) {
        super(msg);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
