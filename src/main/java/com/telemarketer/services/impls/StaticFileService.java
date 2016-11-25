package com.telemarketer.services.impls;

import com.telemarketer.http.requests.Request;
import com.telemarketer.http.responses.NotFoundResponse;
import com.telemarketer.http.responses.Response;
import com.telemarketer.util.PropertiesHelper;
import com.telemarketer.http.Status;
import com.telemarketer.http.responses.FileResponse;
import com.telemarketer.services.Service;
import com.telemarketer.services.InService;

import java.io.File;

/**
 * 静态文件服务
 */
@InService(urlPattern = "^" + StaticFileService.prefix + ".*$")
public class StaticFileService implements Service {

    public static final String prefix = "/s/";
    private static String staticPath;

    static {
        staticPath = PropertiesHelper.getProperty("static_path");
    }

    @Override
    public Response service(Request request) {
        String filePath = staticPath + File.separator + request.getURI().replaceAll(prefix, "");
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return new NotFoundResponse();
        }
        return new FileResponse(Status.SUCCESS_200, file);
    }
}
