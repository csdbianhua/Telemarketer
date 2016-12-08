package com.telemarket.telemarketer.util;

import com.telemarket.telemarketer.http.requests.MimeData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/6
 */
public class ReflectUtil {

    private static final Map<Class, Function<Object, Object>> TYPE_FUNCTION_MAP; // 类型转换函数
    private static final Function<Object, Object> MISS_FUNCTION = s -> null;

    static {
        TYPE_FUNCTION_MAP = new HashMap<>();
        Function<Object, Object> boolFunction = s -> Boolean.parseBoolean((String) s);
        Function<Object, Object> longFunction = s -> Long.parseLong((String) s);
        Function<Object, Object> intFunction = s -> Integer.parseInt((String) s);
        Function<Object, Object> doubleFunction = s -> Double.parseDouble((String) s);
        Function<Object, Object> floatFunction = s -> Float.parseFloat((String) s);
        TYPE_FUNCTION_MAP.put(Boolean.class, s -> boolFunction);
        TYPE_FUNCTION_MAP.put(boolean.class, s -> boolFunction);
        TYPE_FUNCTION_MAP.put(String.class, String::valueOf);
        TYPE_FUNCTION_MAP.put(Long.class, longFunction);
        TYPE_FUNCTION_MAP.put(long.class, longFunction);
        TYPE_FUNCTION_MAP.put(Integer.class, intFunction);
        TYPE_FUNCTION_MAP.put(int.class, intFunction);
        TYPE_FUNCTION_MAP.put(Double.class, doubleFunction);
        TYPE_FUNCTION_MAP.put(double.class, doubleFunction);
        TYPE_FUNCTION_MAP.put(Float.class, floatFunction);
        TYPE_FUNCTION_MAP.put(float.class, floatFunction);
        TYPE_FUNCTION_MAP.put(byte[].class, o -> {
            if (o instanceof MimeData) {
                return ((MimeData) o).getData();
            } else {
                return null;
            }
        });
        TYPE_FUNCTION_MAP.put(MimeData.class, o -> o);

    }

    public static Object parseObj(Object val, Class<?> type) {
        return TYPE_FUNCTION_MAP.getOrDefault(type, MISS_FUNCTION).apply(val);
    }
}
