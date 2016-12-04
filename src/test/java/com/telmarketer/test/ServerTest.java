package com.telmarketer.test;

import com.telemarket.telemarketer.TelemarketerStartup;
import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.io.Server;
import com.telemarket.telemarketer.mvc.ServiceRegistry;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Be careful!
 * Created by hason on 15/9/18.
 */
public class ServerTest {

    private static Server server;

    @BeforeClass
    public static void configServer() {
        Context.init(new String[]{"start", "localhost:8877"}, TelemarketerStartup.class);
        Assert.assertFalse(Context.isError());
        new Thread(() -> {
            server = new Server();
            server.start();
        }).start();
    }

    @Test
    public void baseTest() throws IOException {
        ServiceRegistry.registerClass("com.telmarketer.test.service.TestService");
        Socket socket = new Socket("localhost", 8877);
        String head = "GET /hello_world HTTP/1.1\r\n" +
                "Accept: */*\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n" +
                "Host: localhost:8877\r\n" +
                "User-Agent: HTTPie/0.9.2\r\n\r\n";
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            outputStream.write(head.getBytes("utf-8"));
            String line = br.readLine();
            Assert.assertThat(line, CoreMatchers.containsString("200"));
        }
        ServiceRegistry.unregister("/hello_world"); // TODO 目前只有反注册正则，需要反注册类
    }

    @Test
    public void postTest() throws IOException, URISyntaxException {
        ServiceRegistry.registerClass("com.telmarketer.test.service.PostService");
        Socket socket = new Socket("localhost", 8877);
        URL url = ClassLoader.getSystemResource("test_post.jpeg");
        Path pic = Paths.get(url.toURI());
        String msg = "POST /test_post HTTP/1.1\r\n" +
                "Host: localhost:8877\r\n" +
                "Connection: close\r\n" +
                "Content-Length: 25063\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "Origin: http://localhost:8877\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2939.0 Safari/537.36\r\n" +
                "Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryBBXcj9WdDs43Pkjt\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n" +
                "DNT: 1\r\n" +
                "Referer: http://localhost:8877/\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: zh-CN,zh;q=0.8,en;q=0.6\r\n" +
                "\r\n" +
                "------WebKitFormBoundaryBBXcj9WdDs43Pkjt\r\n" +
                "Content-Disposition: form-data; name=\"img\"; filename=\"k8vEBoCW.jpeg\"\r\n" +
                "Content-Type: image/jpeg\r\n\r\n";
        try (InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            os.write(msg.getBytes("utf-8"));
            os.write(Files.readAllBytes(pic));
            os.write("\r\n------WebKitFormBoundaryBBXcj9WdDs43Pkjt--\r\n".getBytes());
            os.flush();
            String line = br.readLine();
            Assert.assertThat(line, CoreMatchers.containsString("200"));
        }


    }

    @AfterClass
    public static void destroyContext() {
        server.destory();
    }
}
