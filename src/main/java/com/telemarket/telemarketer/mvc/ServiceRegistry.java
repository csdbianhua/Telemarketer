package com.telemarket.telemarketer.mvc;

import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.http.HttpMethod;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.io.ThreadPool;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import com.telemarket.telemarketer.util.FileUtil;
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
        ServiceMethodInfo remove = services.remove(path);
        if (remove != null) {
            LOGGER.info("取消注册服务,path:{},method:{}.{}",
                    path,
                    remove.getObject().getClass().getName(),
                    remove.getMethod().getName());
        }
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
     * 动态注册服务
     *
     * @param className 服务类名
     * @return 注册成功返回true
     */
    public static boolean registerClass(String className) {
        if (StringUtils.isBlank(className)) {
            return false;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Service annotation = clazz.getAnnotation(Service.class);
            boolean flag = false;
            if (annotation != null) {
                Path classAnnotation = clazz.getAnnotation(Path.class);
                String[] classPath = new String[]{"/"};
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
                    String[] methodPath = methodAnnotation.value();
                    HttpMethod[] httpMethod = methodAnnotation.method();
                    if (ArrayUtils.isEmpty(httpMethod)) {
                        if (ArrayUtils.isEmpty(classHttpMethod)) {
                            httpMethod = new HttpMethod[]{HttpMethod.GET};
                        } else {
                            httpMethod = classHttpMethod;
                        }
                    }
                    ServiceMethodInfo info = new ServiceMethodInfo(controller, method, httpMethod);
                    for (String cP : classPath) {
                        for (String s : methodPath) {
                            String path = FileUtil.combinePath(cP, s);
                            if (containPattern(path)) {
                                LOGGER.warn("request map存在重复,映射路径为'{}',将被覆盖!", path);
                            }
                            register(path, info);
                            flag = true;
                            LOGGER.info("成功注册服务,映射[{}]到[{}.{}]", path, clazz.getName(), method.getName());
                        }
                    }
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

    /**
     * 扫描注册服务
     */
    public static void registerServices() {
        String bashPath = Context.getBashPath();
        String name = Context.getPackageName();
        ThreadPool.execute(
                new RegistryThread(name,
                        bashPath + name.replaceAll("\\.", Matcher.quoteReplacement(File.separator)),
                        file -> file.isDirectory() || file.getName().endsWith(".class")));
    }

    private static class RegistryThread extends Thread {
        private String packageName;
        private String path;
        private FileFilter fileFilter;

        RegistryThread(String packageName, String path, FileFilter fileFilter) {
            this.packageName = packageName;
            this.path = path;
            this.fileFilter = fileFilter;
        }

        @Override
        public void run() {
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                return;
            }
            File[] dirFiles = dir.listFiles(fileFilter);
            if (dirFiles == null) {
                return;
            }
            for (File file : dirFiles) {
                if (file.isDirectory()) {
                    ThreadPool.execute(new RegistryThread(packageName + "." + file.getName(), file.getAbsolutePath(), fileFilter));
                } else {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    registerClass(packageName + "." + className);
                }
            }
        }
    }
}
