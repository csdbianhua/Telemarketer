package com.telemarket.telemarketer.services;

import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.NotFoundResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import com.telemarket.telemarketer.util.PropertiesHelper;
import com.telemarket.telemarketer.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 静态文件服务 TODO 考虑从普通服务中抽离
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
        String ifModifiedSinceStr = request.getHeader("if-modified-since");
        if (StringUtils.isNotEmpty(ifModifiedSinceStr)) {
            long isModifiedSince = TimeUtils.parseRFC822(ifModifiedSinceStr).toInstant().toEpochMilli();
            if (file.lastModified() <= isModifiedSince) {
                return new Response(Status.NOT_MODIFIED_304);
            }
        }
        return new FileResponse(Status.SUCCESS_200, file);
    }
}
