package com.telemarket.telemarketer.context;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.telemarket.telemarketer.util.PropertiesHelper;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Chen Yijie on 2016/11/27 21:08.
 */
public class Context {
    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);
    public static final int DEFAULT_PORT = 8877;
    private static InetAddress ip;
    private static int port;
    private static String errorMsg;
    private static Map<Class, ScanConfig> scanConfig;

    static void setIp(InetAddress inetAddress) {
        Context.ip = inetAddress;
    }

    static void setPort(int port) {
        Context.port = port;
    }

    public static InetAddress getIp() {
        return ip;
    }

    public static int getPort() {
        return port;
    }

    public static void init(String[] args, Class... scanClazz) {
        if (args == null || args.length < 1 || !args[0].equals("start")) {
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
        HashMap<Class, ScanConfig> scanConfigs = new HashMap<>();
        for (Class aClass : scanClazz) {
            String homePath = aClass.getResource("/").getPath();
            String packageName = aClass.getPackage().getName();
            scanConfigs.put(aClass, new ScanConfig(homePath, packageName));
        }
        Context.setScanConfig(scanConfigs);
        Context.setIp(ip);
        Context.setPort(port);

        if (!loadLogConfiguration()) {
            return;
        }
//        if (!loadViewConfiguration()) {
//            return;
//        }
    }

    public static boolean isError() {
        return errorMsg != null;
    }

    public static void printError() {
        System.err.println(errorMsg);
    }

    private static boolean loadLogConfiguration() { // TODO 抽离加载配置的方法 使系统统一扫描加载
        String logConfigurationPath = System.getProperty("logback.configurationFile");
        if (StringUtils.isBlank(logConfigurationPath)) {
            logConfigurationPath = "conf/logback-tele.xml";
        }
        URL configPath = ClassLoader.getSystemResource(logConfigurationPath);
        if (configPath == null) {
            System.err.println("无可用日志配置，将使用缺省配置");
            return true;
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
            return false;
        }
        return true;
    }

    private static boolean loadViewConfiguration() { // TODO 配置使用ViewResolver
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        try {
            cfg.setDirectoryForTemplateLoading(new File(PropertiesHelper.getResourcePath("template")));
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return false;
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(true);
        return true;
    }

    public static void setScanConfig(Map<Class, ScanConfig> scanConfig) {
        Context.scanConfig = Collections.unmodifiableMap(scanConfig);
    }

    public static Map<Class, ScanConfig> getScanConfig() {
        return scanConfig;
    }
}
