package com.telemarket.telemarketer.services;

import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.Service;
import com.telemarket.telemarketer.mvc.annotation.WebPath;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


@Service
@WebPath("/")
public class IndexService {

    @WebPath("/")
    public Response service(Request request) throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream("template/index.html");
        File index = Files.createTempFile("index", ".html").toFile();
        FileOutputStream os = new FileOutputStream(index);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        return new FileResponse(Status.SUCCESS_200, index);
    }
}
