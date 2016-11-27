package com.telemarket.telemarketer.services;

import com.telemarket.telemarketer.context.StartArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

/**
 * 服务注册中心
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private static Map<String, Service> services = Collections.synchronizedMap(new TreeMap<String, Service>()); //TODO 一直可写的话注意同步问题

    public static void register(String pattern, Service service) {
        services.put(pattern, service);
    }

    /**
     * 根据路径查找对应服务
     *
     * @param path 请求路径
     * @return 对应服务
     */
    public static Service findService(String path) {
        for (Map.Entry<String, Service> entry : services.entrySet()) {
            if (path.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 注册服务
     *
     * @param startArgs
     */
    public static void registerServices(StartArgs startArgs) {
        Class rootClazz = startArgs.getRootClazz();
        URL packageUrl = rootClazz.getResource("/");
        if (packageUrl == null) {
            return;
        }
        String name = rootClazz.getPackage().getName();
        registerFromPackage(name, packageUrl.getFile() + name.replaceAll("\\.", Matcher.quoteReplacement(File.separator)), file -> file.isDirectory() || file.getName().endsWith(".class"));
    }

    private static void registerFromPackage(String packageName, String packagePath, FileFilter fileFilter) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(fileFilter);
        assert dirfiles != null;
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                registerFromPackage(packageName + "." + file.getName(), file.getAbsolutePath(), fileFilter);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> aClass = Class.forName(packageName + "." + className);
                    InService annotation = aClass.getAnnotation(InService.class);
                    if (annotation != null && Service.class.isAssignableFrom(aClass)) {
                        register(annotation.urlPattern(), aClass.asSubclass(Service.class).newInstance());
                        LOGGER.info("成功注册服务,映射{}到{}", annotation.urlPattern(), className);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    LOGGER.warn("注册服务出错", e);
                }
            }
        }
    }
}
