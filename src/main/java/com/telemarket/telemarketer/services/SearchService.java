package com.telemarket.telemarketer.services;

import com.telemarket.telemarketer.http.HttpMethod;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.MimeData;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.MultiPart;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import com.telemarket.telemarketer.util.PropertiesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Be careful!
 * Created by hason on 15/9/30.
 */

@Service
public class SearchService {

    @Path("/test")
    public Response test(Request request) {
        return new FileResponse(Status.SUCCESS_200, PropertiesHelper.getTemplateFile("search.html"));
    }

    @Path(value = "/search", method = HttpMethod.POST)
    public Response service(Request request, @MultiPart("photo") MimeData mimeData) {
        if (request.mimeContainKey("photo")) {
            MimeData photo = request.mimeValue("photo");
            byte[] data = photo.getData();
            String fileName = photo.getFileName();
            int i = fileName.lastIndexOf('.');
            try {
                File file = File.createTempFile("test", i == -1 ? ".jpeg" : fileName.substring(i, fileName.length()));
                FileOutputStream os = new FileOutputStream(file);
                os.write(data);
                os.close();
                return new FileResponse(Status.SUCCESS_200, file);
            } catch (IOException e) {
                return new Response(Status.INTERNAL_SERVER_ERROR_500);
            }
        }
        return new Response(Status.BAD_REQUEST_400);
    }
}
