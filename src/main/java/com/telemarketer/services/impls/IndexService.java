package com.telemarketer.services.impls;

import com.telemarketer.http.requests.Request;
import com.telemarketer.http.responses.Response;
import com.telemarketer.util.PropertiesHelper;
import com.telemarketer.http.responses.FileResponse;
import com.telemarketer.http.Status;
import com.telemarketer.services.Service;
import com.telemarketer.services.InService;


@InService(urlPattern = "^/$")
public class IndexService implements Service {
    @Override
    public Response service(Request request) {

        return new FileResponse(Status.SUCCESS_200, PropertiesHelper.getTemplateFile("index.html"));
    }
}
