package com.telmarketer.test.service;

import com.telemarket.telemarketer.http.HttpMethod;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.MimeData;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/4
 */
@Service
public class PostService {

    @Path(value = "/test_post", method = HttpMethod.POST)
    public Response testPost(Request request) {
        if (request.mimeContainKey("img")) {
            MimeData photo = request.mimeValue("img");
            byte[] data = photo.getData();
            try {
                File file = File.createTempFile("the", ".jpeg");
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
