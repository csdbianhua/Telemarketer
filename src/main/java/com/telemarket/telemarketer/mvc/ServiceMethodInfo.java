package com.telemarket.telemarketer.mvc;

import com.telemarket.telemarketer.http.HttpMethod;
import com.telemarket.telemarketer.http.requests.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Chen Yijie on 2016/11/28 17:07.
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
        return method.invoke(object, request);
    }
}
