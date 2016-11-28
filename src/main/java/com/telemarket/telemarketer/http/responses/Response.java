package com.telemarket.telemarketer.http.responses;

import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.util.PropertiesHelper;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Http响应
 */
public class Response {

    private static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CHARSET = "utf-8";
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

    static {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    protected Status status;
    protected Map<String, String> heads;
    protected byte[] content;

    public Response(Status status) {
        this.status = status;
        heads = new HashMap<>();
        content = new byte[0];
        heads.put("Date", simpleDateFormat.format(new Date()));
        heads.put("Server", PropertiesHelper.getProperty("server_name", "Telemarketer"));
        heads.put("Connection", "Close");
    }

    public void setHead(String key, String value) {
        heads.put(key, value);
    }

    public String getField(String key) {
        return heads.get(key);
    }


    public Status getStatus() {
        return status;
    }

    private ByteBuffer finalData = null;

    public ByteBuffer getByteBuffer() {
        if (finalData == null) {
            heads.put("Content-Length", String.valueOf(content.length));
            StringBuilder sb = new StringBuilder();
            sb.append(HTTP_VERSION).append(" ").append(status.getCode()).append(" ").append(status.getMessage()).append("\r\n");
            for (Map.Entry<String, String> entry : heads.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
            }
            sb.append("\r\n");
            byte[] head = new byte[0];
            try {
                head = sb.toString().getBytes(CHARSET);
            } catch (UnsupportedEncodingException ignored) {
            }
            finalData = ByteBuffer.allocate(head.length + content.length + 2);
            finalData.put(head);
            finalData.put(content);
            finalData.put((byte) '\r');
            finalData.put((byte) '\n');
            finalData.flip();
        }
        return finalData;
    }
}
