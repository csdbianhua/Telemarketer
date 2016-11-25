package com.telemarketer.http.responses;

import com.google.gson.Gson;
import com.telemarketer.http.Status;
import com.telemarketer.http.exceptions.ServerInternalException;

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
        heads.put("Content-Type", "application/json; charset=" + CHARSET);
        try {
            super.content = new Gson().toJson(obj).getBytes(CHARSET);
        } catch (UnsupportedEncodingException ignored) {
        }

    }
}
