package com.telemarket.telemarketer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * 负责获取配置
 */
public class PropertiesHelper {

    private static Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);
    private static Properties properties;
    private static String resourcePath;

    private PropertiesHelper() {
    }

    static {
        properties = new Properties();
        String setting = ClassLoader.getSystemResource("setting.properties").getPath();
        try (FileInputStream fileStream = new FileInputStream(setting)) {
            properties.load(fileStream);
        } catch (IOException e) {
            logger.error("配置文件无法读取 : " + setting);
            System.exit(1);
        }
        URL resource = PropertiesHelper.class.getClassLoader().getResource("");
        if (resource == null) {
            logger.error("resource路径读取失败 ");
            System.exit(1);
        }
        resourcePath = resource.getFile();

    }

    public static File getTemplateFile(String htmlPath) {
        return new File(PropertiesHelper.getResourcePath("template" + File.separator + htmlPath));
    }


    public static String getResourcePath(String relativePath) {
        return resourcePath + relativePath;
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

    public static String getProperty(String name, String defaultValue) {
        String result = properties.getProperty(name);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }
}
