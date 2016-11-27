package com.telemarket.telemarketer;

import com.telemarket.telemarketer.io.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 服务器类
 * 负责等候和处理IO事件
 */
public class TelemarketerStartup {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        if (args.length < 1 || !args[0].equals("start")) {
            System.out.println("Usage: start [address:port]");
            System.exit(1);
        }
        InetAddress ip = null;
        int port = 0;
        try {
            if (args.length == 2 && args[1].matches(".+:\\d+")) {
                String[] address = args[1].split(":");
                ip = InetAddress.getByName(address[0]);
                port = Integer.valueOf(address[1]);
            } else {
                ip = InetAddress.getLocalHost();
                port = DEFAULT_PORT;
                System.out.println("未指定地址和端口,使用默认ip和端口..." + ip.getHostAddress() + ":" + port);
            }
        } catch (UnknownHostException e) {
            System.out.println("请输入正确的ip");
            System.exit(1);
        }

        Server server = new Server(ip, port);
        server.start();
    }


}
