package com.telemarket.telemarketer.http.requests;

import java.util.Collections;
import java.util.Map;

/**
 * Be careful!
 * Created by hason on 15/9/29.
 */
class RequestHeader {

    private String URI;
    private String method;
    private Map<String, String> head;
    private Map<String, String> queryMap;

    RequestHeader() {
        URI = "";
        method = "";
        head = Collections.emptyMap();
        queryMap = Collections.emptyMap();
    }


    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHead() {
        return head;
    }

    public void setHead(Map<String, String> head) {
        this.head = head;
    }

    public void setQueryMap(Map<String, String> queryMap) {
        this.queryMap = queryMap;
    }

    public String getURI() {
        return URI;
    }

    public String getMethod() {
        return method;
    }

    public boolean containKey(String key) {
        return queryMap.containsKey(key);
    }

    public Map<String, String> getQueryMap() {
        return Collections.unmodifiableMap(queryMap);
    }

    public String queryValue(String key) {
        return queryMap.get(key);
    }

    public String getContentType() {
        return head.get("Content-Type");
    }

    public int getContentLength() {
        return Integer.valueOf(head.getOrDefault("Content-Length", "0"));
    }
}
