package com.telemarket.telemarketer.context;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Chen Yijie on 2016/11/27 21:08.
 */
public class Context {
    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);
    public static final int DEFAULT_PORT = 8877;
    private static String bashPath;
    private static String packageName;
    private static InetAddress ip;
    private static int port;
    private static String errorMsg;

    static void setBashPath(String bashPath) {
        Context.bashPath = bashPath;
    }

    static void setPackageName(String packageName) {
        Context.packageName = packageName;
    }

    static void setIp(InetAddress inetAddress) {
        Context.ip = inetAddress;
    }

    static void setPort(int port) {
        Context.port = port;
    }

    public static String getBashPath() {
        return bashPath;
    }


    public static String getPackageName() {
        return packageName;
    }

    public static InetAddress getIp() {
        return ip;
    }

    public static int getPort() {
        return port;
    }

    public static void init(String[] args, Class rootClazz) {
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
            return;
        }
        loadLogConfiguration();
        String homePath = rootClazz.getResource("/").getPath();
        String packageName = rootClazz.getPackage().getName();
        Context.setBashPath(homePath);
        Context.setPackageName(packageName);
        Context.setIp(ip);
        Context.setPort(port);
    }

    public static boolean isError() {
        return errorMsg != null;
    }

    public static void printError() {
        System.err.println(errorMsg);
    }

    private static void loadLogConfiguration() {
        String logConfigurationPath = System.getProperty("logback.configurationFile");
        if (StringUtils.isBlank(logConfigurationPath)) {
            logConfigurationPath = "conf/logback-tele.xml";
        }
        URL configPath = ClassLoader.getSystemResource(logConfigurationPath);
        if (configPath == null) {
            System.err.println("无可用日志配置，将使用缺省配置");
            return;
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            configurator.doConfigure(configPath);
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        } catch (JoranException e) {
            System.err.println("配置日志配置出错");
            e.printStackTrace(System.err);
        }
    }
}
