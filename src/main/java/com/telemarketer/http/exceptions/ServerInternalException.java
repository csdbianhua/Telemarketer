package com.telemarketer.http.exceptions;

/**
 * 内部错误时抛出此异常
 */
public class ServerInternalException extends RuntimeException {
    public ServerInternalException(String msg,Throwable e) {
        super(msg,e);
    }

    public ServerInternalException(String msg) {
        super(msg);
    }
}
