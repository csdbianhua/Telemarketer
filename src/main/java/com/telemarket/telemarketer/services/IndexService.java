package com.telemarket.telemarketer.services;

import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.JsonResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import com.telemarket.telemarketer.util.PropertiesHelper;

import java.io.File;
import java.util.HashMap;


@Service
@Path("/")
public class IndexService {

    @Path("/")
    public Response service(Request request) {
        File templateFile = PropertiesHelper.getTemplateFile("index.html");
        return new FileResponse(Status.SUCCESS_200, templateFile);
    }

    @Path("hello_world")
    public Response helloWorld(Request request) {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "哈哈哈");
        return new JsonResponse(Status.SUCCESS_200, map);
    }
}
