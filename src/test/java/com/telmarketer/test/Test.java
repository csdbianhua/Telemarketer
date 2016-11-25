package com.telmarketer.test;

import java.io.*;
import java.net.Socket;

/**
 * Be careful!
 * Created by hason on 15/9/18.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("49.140.166.27", 8080);
        InputStream inputStream = socket.getInputStream();
        String head = "GET / HTTP/1.1\r\n" +
                "Accept: */*\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n" +
                "Host: 49.140.166.27:8080\r\n" +
                "User-Agent: HTTPie/0.9.2\r\n\r\n";
        OutputStream outputStream = socket.getOutputStream();

        outputStream.write(head.getBytes("utf-8"));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

    }
}
