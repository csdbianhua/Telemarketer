package com.telemarket.telemarketer.http.requests;

import com.telemarket.telemarketer.http.HttpScheme;
import org.apache.commons.collections4.MultiValuedMap;

import javax.servlet.http.Cookie;
import java.util.Collection;
import java.util.Map;

/**
 * Be careful!
 * Created by hason on 15/9/29.
 */
class RequestHeader {

    private String URI;
    private String method;
    private Map<String, String> head;
    private MultiValuedMap<String, String> queryMap;
    private String queryString;
    private Cookie[] cookies;
    private HttpScheme scheme;


    public Cookie[] getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
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



    public String getURI() {
        return URI;
    }

    public String getMethod() {
        return method;
    }

    public boolean containKey(String key) {
        return queryMap.containsKey(key);
    }

    public MultiValuedMap<String, String> getQueryMap() {
        return queryMap;
    }

    public Collection<String> queryValue(String key) {
        return queryMap.get(key);
    }

    public String getContentType() {
        return head.get("Content-Type");
    }

    public int getContentLength() {
        return Integer.valueOf(head.getOrDefault("Content-Length", "0"));
    }


    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryMap(MultiValuedMap<String, String> queryMap) {
        this.queryMap = queryMap;
    }

    public void setScheme(HttpScheme scheme) {
        this.scheme = scheme;
    }

    public HttpScheme getScheme() {
        return scheme;
    }
}
