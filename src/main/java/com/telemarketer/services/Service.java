package com.telemarketer.services;

import com.telemarketer.http.requests.Request;
import com.telemarketer.http.responses.Response;

/**
 * 服务接口
 */
public interface Service {
    Response service(Request request);
}
