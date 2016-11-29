package com.telemarket.telemarketer.mvc;

import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

/**
 * 服务注册中心
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private static Map<String, ServiceMethodInfo> services = Collections.synchronizedMap(new TreeMap<String, ServiceMethodInfo>());

    public static void register(String pattern, ServiceMethodInfo service) {
        services.put(pattern, service);
    }

    public static boolean containPattern(String pattern) {
        return services.containsKey(pattern);
    }

    /**
     * 根据路径查找对应服务
     *
     * @param path 请求路径
     * @return 对应服务
     */
    public static ServiceMethodInfo findService(String path) {
        for (Map.Entry<String, ServiceMethodInfo> entry : services.entrySet()) {
            if (path.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 注册服务
     */
    public static void registerServices() {
        String bashPath = Context.getBashPath();
        String name = Context.getPackageName();
        registerFromPackage(name, bashPath + name.replaceAll("\\.", Matcher.quoteReplacement(File.separator)), file -> file.isDirectory() || file.getName().endsWith(".class"));
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
                    Service annotation = aClass.getAnnotation(Service.class);
                    if (annotation != null) {
                        Path classAnnotation = aClass.getAnnotation(Path.class);
                        String classPath = StringUtils.EMPTY;
                        if (classAnnotation != null) {
                            classPath = classAnnotation.value();
                        }
                        Method[] methods = aClass.getMethods();
                        Object controller = aClass.newInstance();
                        for (Method method : methods) {
                            Path methodAnnotation = method.getAnnotation(Path.class);
                            if (methodAnnotation == null) {
                                continue;
                            }
                            String methodPath = methodAnnotation.value();
                            ServiceMethodInfo info = new ServiceMethodInfo(controller, method);
                            String path = combinePath(classPath, methodPath);
                            if (containPattern(path)) {
                                LOGGER.warn("request map存在重复,映射路径为'{}',将被覆盖!", path);
                            }
                            register(path, info);
                            LOGGER.info("成功注册服务,映射{}到{}.{}", path, className, method.getName());
                        }
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    LOGGER.warn("注册服务出错", e);
                }
            }
        }
    }

    /**
     * 复制于FileSystem的resolve
     *
     * @param parent 父路径
     * @param child  子路径
     * @return 结果
     */
    private static String combinePath(String parent, String child) {
        if (StringUtils.isEmpty(parent)) return child;
        if (StringUtils.isEmpty(child)) return parent;
        int pn = parent.length();
        int cn = child.length();
        String c = child;
        int childStart = 0;
        int parentEnd = pn;

        if ((cn > 1) && (c.charAt(0) == File.pathSeparatorChar)) {
            if (c.charAt(1) == File.pathSeparatorChar) {
                childStart = 2;
            } else {
                childStart = 1;

            }
            if (cn == childStart) {
                if (parent.charAt(pn - 1) == File.pathSeparatorChar)
                    return parent.substring(0, pn - 1);
                return parent;
            }
        }

        if (parent.charAt(pn - 1) == File.pathSeparatorChar)
            parentEnd--;

        int strlen = parentEnd + cn - childStart;
        char[] theChars = null;
        if (child.charAt(childStart) == File.pathSeparatorChar) {
            theChars = new char[strlen];
            parent.getChars(0, parentEnd, theChars, 0);
            child.getChars(childStart, cn, theChars, parentEnd);
        } else {
            theChars = new char[strlen + 1];
            parent.getChars(0, parentEnd, theChars, 0);
            theChars[parentEnd] = File.pathSeparatorChar;
            child.getChars(childStart, cn, theChars, parentEnd + 1);
        }
        return new String(theChars);
    }
}
