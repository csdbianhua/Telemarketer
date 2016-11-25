package com.telemarket.telemarketer.services.impls;

import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.services.InService;
import com.telemarket.telemarketer.services.Service;
import com.telemarket.telemarketer.util.PropertiesHelper;

import java.io.File;


@InService(urlPattern = "^/$")
public class IndexService implements Service {
    @Override
    public Response service(Request request) {

        File templateFile = PropertiesHelper.getTemplateFile("index.html");
        return new FileResponse(Status.SUCCESS_200, templateFile);
    }
}
