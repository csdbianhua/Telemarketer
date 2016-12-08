package com.telemarket.telemarketer.mvc;

import com.telemarket.telemarketer.http.HttpMethod;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.exceptions.ServerInternalException;
import com.telemarket.telemarketer.http.requests.MimeData;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.FormParam;
import com.telemarket.telemarketer.mvc.annotation.MultiPart;
import com.telemarket.telemarketer.mvc.annotation.QueryParam;
import com.telemarket.telemarketer.util.ReflectUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

/**
 * Hanson on 2016/11/28 17:07.
 */
public class ServiceMethodInfo {
    private Object object;
    private Method method;
    private HttpMethod[] httpMethod;

    public ServiceMethodInfo(Object object, Method method, HttpMethod[] httpMethod) {
        this.object = object;
        this.method = method;
        this.httpMethod = httpMethod;
    }

    public boolean containHttpMethod(String method) {
        for (HttpMethod httpMethod : httpMethod) {
            if (httpMethod.getName().equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object invoke(Request request) throws InvocationTargetException, IllegalAccessException {
        Parameter[] parameters = method.getParameters();
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            if (type == HttpServletRequest.class || type == Request.class) {
                objects[i] = request;
                continue;
            }
            Annotation[] annotations = parameter.getAnnotations();
            if (annotations.length == 0) {
                try {
                    objects[i] = processPojo(type, request);
                    continue;
                } catch (Exception e) {
                    throw new ServerInternalException("参数绑定java bean出错", e);
                }
            }
            Annotation an = annotations[0];
            Class<? extends Annotation> aType = an.annotationType();
            Object o = null;
            if (aType == MultiPart.class) {
                o = processMultiPart(an, request, type);
            } else if (aType == QueryParam.class) {
                o = processQueryParam(an, request, type);
            } else if (aType == FormParam.class) {
                o = processFormParam(an, request, type);
            } else {
                try {
                    o = processPojo(type, request);
                } catch (Exception e) {
                    throw new ServerInternalException("参数绑定java bean出错", e);
                }
            }
            if (o != null && o instanceof HttpServletResponse) {
                return o;
            }
            objects[i] = o;
        }
        return method.invoke(object, objects);
    }

    private Object processPojo(Class<?> type, Request request) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Method[] methods = type.getMethods();
        Object o = type.newInstance();
        for (Method method : methods) {
            String methodName = method.getName();
            Parameter[] parameters = method.getParameters();
            if (!methodName.startsWith("set") || methodName.length() == 3 || parameters.length != 1) {
                continue;
            }
            String paraName = methodName.substring(3);
            String val = request.getParameter(paraName);
            if (val == null) {
                continue;
            }
            Parameter para = parameters[0];
            Object result = ReflectUtil.parseObj(val, para.getType()); // 转换出错会抛出错误
            method.invoke(o, result);
        }
        return o;
    }

    private Object processFormParam(Annotation an, Request request, Class<?> type) {
        FormParam formParam = (FormParam) an;
        String value = formParam.value();
        Collection<String> strings = request.formValue(value);
        boolean empty = CollectionUtils.isEmpty(strings);
        if (formParam.require() && empty) {
            return new Response(Status.BAD_REQUEST_400);
        }
        return empty ? null : ReflectUtil.parseObj(strings.iterator().next(), type);
    }

    private Object processQueryParam(Annotation annotation, Request request, Class<?> type) {
        QueryParam queryParam = (QueryParam) annotation;
        String value = queryParam.value();
        Collection<String> strings = request.queryValue(value);
        boolean empty = CollectionUtils.isEmpty(strings);
        if (queryParam.require() && empty) {
            return new Response(Status.BAD_REQUEST_400);
        }
        return empty ? null : ReflectUtil.parseObj(strings.iterator().next(), type);
    }

    private Object processMultiPart(Annotation annotation, Request request, Class<?> type) {
        MultiPart part = (MultiPart) annotation;
        String value = part.value();
        MimeData mimeData = request.mimeValue(value);
        boolean empty = mimeData == null;
        if (part.require() && empty) {
            return new Response(Status.BAD_REQUEST_400);
        }
        return empty ? null : ReflectUtil.parseObj(mimeData, type);
    }
}
