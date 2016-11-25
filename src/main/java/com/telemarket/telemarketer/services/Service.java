package com.telemarket.telemarketer.services;

import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.Response;

/**
 * 服务接口
 */
public interface Service {
    Response service(Request request);
}
