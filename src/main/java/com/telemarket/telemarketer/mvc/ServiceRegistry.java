package com.telemarket.telemarketer.mvc;

import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.http.HttpMethod;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import org.apache.commons.lang3.ArrayUtils;
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

    private static final char SEPARATOR_CHAR = '/';
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private static Map<String, ServiceMethodInfo> services = Collections.synchronizedMap(new TreeMap<String, ServiceMethodInfo>());

    /**
     * 动态注册服务
     *
     * @param path    路径
     * @param service 服务
     */
    public static void register(String path, ServiceMethodInfo service) {
        services.put(path, service);
    }

    /**
     * 动态注销服务
     *
     * @param path 路径
     */
    public static void unregister(String path) {
        services.remove(path);
    }

    public static boolean containPattern(String pattern) {
        return services.containsKey(pattern);
    }

    /**
     * 根据路径查找对应服务 TODO 提升搜寻服务速度,使用Trie一类的，同时需要实现最长匹配原则 另外应取消正则，用*匹配
     *
     * @param request 请求
     * @return 对应服务
     */
    public static ServiceMethodInfo findService(Request request) {
        String requestURI = request.getRequestURI();
        for (Map.Entry<String, ServiceMethodInfo> entry : services.entrySet()) {
            if (requestURI.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 注册服务 TODO 可以使用多线程
     */
    public static void registerServices() {
        String bashPath = Context.getBashPath();
        String name = Context.getPackageName();
        registerFromPackage(name, bashPath + name.replaceAll("\\.", Matcher.quoteReplacement(File.separator)), file -> file.isDirectory() || file.getName().endsWith(".class"));
    }

    /**
     * 动态注册服务
     *
     * @param className 服务类名
     * @return 注册成功返回true
     */
    public static boolean register(String className) {
        if (StringUtils.isBlank(className)) {
            return false;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Service annotation = clazz.getAnnotation(Service.class);
            boolean flag = false;
            if (annotation != null) {
                Path classAnnotation = clazz.getAnnotation(Path.class);
                String classPath = "/";
                HttpMethod[] classHttpMethod = null;
                if (classAnnotation != null) {
                    classPath = classAnnotation.value();
                    classHttpMethod = classAnnotation.method();
                }
                Method[] methods = clazz.getMethods();
                Object controller = clazz.newInstance();
                for (Method method : methods) {
                    Path methodAnnotation = method.getAnnotation(Path.class);
                    if (methodAnnotation == null) {
                        continue;
                    }
                    String methodPath = methodAnnotation.value();
                    HttpMethod[] httpMethod = methodAnnotation.method();
                    if (ArrayUtils.isEmpty(httpMethod)) {
                        if (ArrayUtils.isEmpty(classHttpMethod)) {
                            httpMethod = new HttpMethod[]{HttpMethod.GET};
                        } else {
                            httpMethod = classHttpMethod;
                        }
                    }
                    ServiceMethodInfo info = new ServiceMethodInfo(controller, method, httpMethod);
                    String path = combinePath(classPath, methodPath);
                    if (containPattern(path)) {
                        LOGGER.warn("request map存在重复,映射路径为'{}',将被覆盖!", path);
                    }
                    register(path, info);
                    flag = true;
                    LOGGER.info("成功注册服务,映射[{}]到[{}.{}]", path, clazz.getName(), method.getName());
                }
            }
            return flag;
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("无法访问服务构造器,{}", className, e);
            return false;
        } catch (ClassNotFoundException e) {
            LOGGER.error("无法找到服务类,{}", className, e);
            return false;
        }
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
                register(packageName + "." + className);
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

        if ((cn > 1) && (c.charAt(0) == SEPARATOR_CHAR)) {
            if (c.charAt(1) == SEPARATOR_CHAR) {
                childStart = 2;
            } else {
                childStart = 1;

            }
            if (cn == childStart) {
                if (parent.charAt(pn - 1) == SEPARATOR_CHAR)
                    return parent.substring(0, pn - 1);
                return parent;
            }
        }

        if (parent.charAt(pn - 1) == SEPARATOR_CHAR)
            parentEnd--;

        int strlen = parentEnd + cn - childStart;
        char[] theChars = null;
        if (child.charAt(childStart) == SEPARATOR_CHAR) {
            theChars = new char[strlen];
            parent.getChars(0, parentEnd, theChars, 0);
            child.getChars(childStart, cn, theChars, parentEnd);
        } else {
            theChars = new char[strlen + 1];
            parent.getChars(0, parentEnd, theChars, 0);
            theChars[parentEnd] = SEPARATOR_CHAR;
            child.getChars(childStart, cn, theChars, parentEnd + 1);
        }
        return new String(theChars);
    }
}
