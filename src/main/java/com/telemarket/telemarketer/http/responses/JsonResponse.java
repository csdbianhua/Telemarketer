package com.telemarket.telemarketer.http.responses;

import com.alibaba.fastjson.JSONObject;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.exceptions.ServerInternalException;

import java.io.UnsupportedEncodingException;

/**
 * Json响应
 */
public class JsonResponse extends Response {
    public JsonResponse(Status status, Object obj) {
        super(status);
        if (obj == null) {
            throw new ServerInternalException("Json响应对象为空");
        }
        heads.put("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
        try {
            super.content = JSONObject.toJSONString(obj).getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException ignored) {
        }

    }
}
