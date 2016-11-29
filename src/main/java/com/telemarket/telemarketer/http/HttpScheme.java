package com.telemarket.telemarketer.http;

import org.apache.commons.lang3.StringUtils;

/**
 * Chen Yijie on 2016/11/29 11:12.
 */
public enum HttpScheme {

    HTTP("http"),
    HTTPS("https");

    private final String content;

    /* ------------------------------------------------------------ */
    HttpScheme(String s) {
        this.content = s;
    }

    public static HttpScheme parseScheme(String s) {
        String[] split = s.split("/");
        for (HttpScheme httpScheme : HttpScheme.values()) {
            if (StringUtils.equals(split[0].toLowerCase(), httpScheme.toString())) {
                return httpScheme;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return content;
    }


}
