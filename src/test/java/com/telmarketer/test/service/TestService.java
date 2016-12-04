package com.telmarketer.test.service;

import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/4
 */
@Service
public class TestService {

    @Path("/hello_world")
    public Response helloWorld(Request request) {
        return new Response(Status.SUCCESS_200);
    }
}
