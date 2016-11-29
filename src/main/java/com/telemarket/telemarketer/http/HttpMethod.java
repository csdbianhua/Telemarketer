package com.telemarket.telemarketer.http;

/**
 * Chen Yijie on 2016/11/29 13:33.
 */
public enum HttpMethod {
    GET("get"), POST("post");

    private String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
