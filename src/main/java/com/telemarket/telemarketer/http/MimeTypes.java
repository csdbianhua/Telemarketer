package com.telemarket.telemarketer.http;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Hanson on 2016/11/29 22:50.
 */
public class MimeTypes {
    private final static Map<String, String> mimeMap = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(MimeTypes.class);

    static {
        try {
            Properties properties = new Properties();
            properties.load(MimeTypes.class.getResourceAsStream("mime.properties"));
            properties.entrySet().forEach(entry -> {
                Object key = entry.getKey();
                Object value = entry.getValue();
                mimeMap.put(StringUtils.lowerCase((String) key), (String) value);
            });
        } catch (IOException e) {
            LOGGER.error("加载mime.properties失败");
        }
    }

    public static String findContentType(String path) {
        int i = StringUtils.lastIndexOf(path, '.');
        if (i == -1 || i == path.length() - 1) {
            return "text/plain";
        }
        String suffix = path.substring(i + 1, path.length());
        return mimeMap.getOrDefault(suffix, "text/plain");
    }
}
