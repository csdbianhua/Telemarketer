package com.telemarketer.http.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Be careful!
 * Created by hason on 15/9/29.
 */
class RequestHeader {
    private static Logger logger = Logger.getLogger("RequestHeader");

    private String URI;
    private String method;
    private Map<String, String> head;
    private Map<String, String> queryMap;

    public RequestHeader() {
        URI = "";
        method = "";
        head = Collections.emptyMap();
        queryMap = Collections.emptyMap();
    }

    public void parseHeader(byte[] head) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(new String(head, "UTF-8")))) {
            Map<String, String> headMap = new HashMap<>();
            String path;
            String method;
            try {
                String line = reader.readLine();
                String[] lineOne = line.split("\\s");
                path = URLDecoder.decode(lineOne[1], "utf-8");
                method = lineOne[0];
                while ((line = reader.readLine()) != null) {
                    String[] keyValue = line.split(":");
                    headMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, e, () -> "请求解析有错误");
                throw new RuntimeException(e);
            }
            Map<String, String> queryMap = Collections.emptyMap();
            int index = path.indexOf('?');
            if (index != -1) {
                queryMap = new HashMap<>();
                Request.parseParameters(path.substring(index + 1), queryMap);
                path = path.substring(0, index);
            }

            this.URI = path;
            this.method = method;
            this.head = headMap;
            this.queryMap = queryMap;
        }

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
