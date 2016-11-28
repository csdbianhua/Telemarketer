package com.telemarket.telemarketer.context;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Chen Yijie on 2016/11/27 21:09.
 */
public class StartArgs {
    public static final int DEFAULT_PORT = 8877;
    private InetAddress ip;
    private int port;
    private String errorMsg;

    public StartArgs(String[] args, Class rootClazz) {
        if (args.length < 1 || !args[0].equals("start")) {
            errorMsg = "Usage: start [address:port]";
            return;
        }
        try {
            if (args.length == 2 && args[1].matches(".+:\\d+")) {
                String[] address = args[1].split(":");
                ip = InetAddress.getByName(address[0]);
                port = Integer.valueOf(address[1]);
            } else {
                ip = InetAddress.getByName("localhost");
                port = DEFAULT_PORT;
                System.out.println("未指定地址和端口,使用默认ip和端口..." + ip.getHostAddress() + ":" + port);
            }
        } catch (UnknownHostException e) {
            errorMsg = "请输入正确的ip";
        }
        String homePath = rootClazz.getResource("/").getPath();
        String packageName = rootClazz.getPackage().getName();
        Context.setBashPath(homePath);
        Context.setPackageName(packageName);
    }


    public boolean isError() {
        return errorMsg != null;
    }

    public void printError() {
        System.err.println(errorMsg);
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
