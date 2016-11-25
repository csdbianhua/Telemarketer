package com.telemarket.telemarketer.http.requests;

import com.telemarket.telemarketer.util.BytesUtil;
import com.telemarket.telemarketer.http.exceptions.IllegalRequestException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;

/**
 * Http请求
 */
public class Request {

    private final RequestHeader header;
    private final RequestBody body;


    Request(RequestHeader header, RequestBody body) {
        this.header = header;
        this.body = body;
    }


    public Map<String, String> getQueryMap() {
        return header.getQueryMap();
    }

    public boolean queryContainKey(String key) {
        return header.containKey(key);
    }

    public String queryValue(String key) {
        return header.queryValue(key);
    }

    public boolean formContainKey(String key) {
        return body.formContainKey(key);
    }

    public boolean mimeContainKey(String key) {
        return body.mimeContainKey(key);
    }


    public Map<String, String> getFormMap() {
        return body.getFormMap();
    }

    public Map<String, MIMEData> getMimeMap() {
        return body.getMimeMap();
    }

    public String formValue(String key) {
        return body.formValue(key);
    }

    public MIMEData mimeValue(String key) {
        return body.mimeValue(key);
    }

    public String getURI() {
        return header.getURI();
    }


    public String getMethod() {
        return header.getMethod();
    }
}
