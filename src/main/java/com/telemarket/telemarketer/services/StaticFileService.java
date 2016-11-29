package com.telemarket.telemarketer.services;

import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.NotFoundResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import com.telemarket.telemarketer.util.PropertiesHelper;

import java.io.File;

/**
 * 静态文件服务
 */
@Service
public class StaticFileService {

    static final String prefix = "/s/";
    private static String staticPath;
    private static String root;

    static {
        staticPath = PropertiesHelper.getProperty("static_path");
        root = StaticFileService.class.getClassLoader().getResource("").getPath();
    }

    @Path(StaticFileService.prefix + ".*")
    public Response service(Request request) {
        String filePath = staticPath + File.separator + request.getRequestURI().replaceAll(prefix, "");
        File file = new File(root, filePath);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return new NotFoundResponse();
        }
        return new FileResponse(Status.SUCCESS_200, file);
    }
}
