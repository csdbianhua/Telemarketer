package com.telemarket.telemarketer.http;

/**
 * Hanson on 2016/11/29 13:33.
 */
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    HEAD("HEAD"), // TODO HEAD 方法
    OPTIONS("OPTIONS"); // TODO OPTIONS 方法

    private String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
