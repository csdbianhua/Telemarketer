package com.telemarket.telemarketer.util;

import com.telemarket.telemarketer.exceptions.TransformTypeException;
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
        Function<Object, Object> charFunction = s -> ((String) s).charAt(0);
        TYPE_FUNCTION_MAP.put(Boolean.class, boolFunction);
        TYPE_FUNCTION_MAP.put(String.class, String::valueOf);
        TYPE_FUNCTION_MAP.put(Long.class, longFunction);
        TYPE_FUNCTION_MAP.put(Integer.class, intFunction);
        TYPE_FUNCTION_MAP.put(Double.class, doubleFunction);
        TYPE_FUNCTION_MAP.put(Float.class, floatFunction);
        TYPE_FUNCTION_MAP.put(Character.class, charFunction);
        TYPE_FUNCTION_MAP.put(byte[].class, o -> ((MimeData) o).getData());
        TYPE_FUNCTION_MAP.put(MimeData.class, o -> o);

    }

    /**
     * 转换对象到指定类型,可能抛出ClassCastException
     *
     * @param val  对象
     * @param type 指定类型
     * @return 转换结果
     */
    public static Object parseObj(Object val, Class<?> type) {
        if (val == null) {
            return null;
        }
        Object result;
        try {
            result = TYPE_FUNCTION_MAP.getOrDefault(type, MISS_FUNCTION).apply(val);
            valid(result, type);
        } catch (RuntimeException e) {
            throw new TransformTypeException("parseObj error,target type:" + type.getName(), e);
        }
        return result;
    }

    private static void valid(Object val, Class<?> type) {
        if (val != null && val.getClass() != type) {
            throw new ClassCastException(val.getClass().getName() + " cannot be cast to " + type.getName());
        }
    }
}
