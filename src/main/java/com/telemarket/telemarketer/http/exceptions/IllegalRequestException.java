package com.telemarket.telemarketer.http.exceptions;

/**
 * 请求解析发生错误时抛出此异常
 */
public class IllegalRequestException extends BaseRuntimeException {
    public IllegalRequestException(String msg) {
        super(msg);
    }

    public IllegalRequestException(String msg,Throwable e) {
        super(msg,e);
    }

}
