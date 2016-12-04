package com.telmarketer.test;

import com.telemarket.telemarketer.TelemarketerStartup;
import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.io.Server;
import com.telemarket.telemarketer.mvc.ServiceRegistry;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

/**
 * Be careful!
 * Created by hason on 15/9/18.
 */
public class ServerTest {

    private Server server;

    @Before
    public void configServer() {
        Context.init(new String[]{"start", "localhost:8877"}, TelemarketerStartup.class);
        Assert.assertFalse(Context.isError());
        new Thread(() -> {
            server = new Server();
            server.start();
        }).start();
    }

    @Test
    public void baseTest() throws IOException {
        ServiceRegistry.register("com.telmarketer.test.service.TestService");
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
    }

    @After
    public void destroyContext() {
        server.destory();
    }
}
