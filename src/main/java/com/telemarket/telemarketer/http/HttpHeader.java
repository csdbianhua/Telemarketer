package com.telemarket.telemarketer.http;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/3
 */
public enum HttpHeader {

    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    MIME_VERSION("MIME-Version"),
    IDENTITY("identity"),
    SET_COOKIE2("Set-Cookie2"),

    // 共有
    CACHE_CONTROL("Cache-Control"),
    DATE("Date"),
    PRAGMA("Pragma"),
    PROXY_CONNECTION("Proxy-Connection"),
    CONNECTION("Connection"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    UPGRADE("Upgrade"),
    WARNING("Warning"),
    TRAILER("Trailer"),
    NEGOTIATE("Negotiate"),
    VIA("Via"),
    ALLOW("Allow"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_LOCATION("Content-Location"),
    CONTENT_MD5("Content-MD5"),
    CONTENT_RANGE("Content-Range"),
    CONTENT_TYPE("Content-Type"),
    EXPIRES("Expires"),
    LAST_MODIFIED("Last-Modified"),


    // 请求
    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    AUTHORIZATION("Authorization"),
    EXPECT("Expect"),
    FORWARDED("Forwarded"),
    FROM("From"),
    HOST("Host"),
    IF_MATCH("If-Match"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    IF_NONE_MATCH("If-None-Match"),
    IF_RANGE("If-Range"),
    IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
    KEEP_ALIVE("Keep-Alive"),
    USER_AGENT("User-Agent"),
    X_FORWARDED_FOR("X-Forwarded-For"),
    X_FORWARDED_PROTO("X-Forwarded-Proto"),
    X_FORWARDED_SERVER("X-Forwarded-Server"),
    X_FORWARDED_HOST("X-Forwarded-Host"),
    MAX_FORWARDS("Max-Forwards"),
    PROXY_AUTHORIZATION("Proxy-Authorization"),
    RANGE("Range"),
    REQUEST_RANGE("Request-Range"),
    REFERER("Referer"),
    TE("TE"),
    // 响应
    ACCEPT_RANGES("Accept-Ranges"),
    AGE("Age"),
    ETAG("ETag"),
    LOCATION("Location"),
    PROXY_AUTHENTICATE("Proxy-Authenticate"),
    RETRY_AFTER("Retry-After"),
    SERVER("Server"),
    SERVLET_ENGINE("Servlet-Engine"),
    VARY("Vary"),
    WWW_AUTHENTICATE("WWW-Authenticate"),

    X_POWERED_BY("X-Powered-By"),
    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security");

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
