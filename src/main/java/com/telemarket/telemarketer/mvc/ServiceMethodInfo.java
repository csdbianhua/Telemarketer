package com.telemarket.telemarketer.mvc;

import java.lang.reflect.Method;

/**
 * Chen Yijie on 2016/11/28 17:07.
 */
public class ServiceMethodInfo {
    private Object object;
    private Method method;

    public ServiceMethodInfo(Object object, Method method) {
        this.object = object;
        this.method = method;
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
}
