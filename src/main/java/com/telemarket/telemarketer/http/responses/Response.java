package com.telemarket.telemarketer.http.responses;

import com.telemarket.telemarketer.exceptions.NotSupportMethodException;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.util.PropertiesHelper;
import com.telemarket.telemarketer.util.TimeUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Http响应
 */
public class Response implements HttpServletResponse {

    protected static final String HTTP_VERSION = "HTTP/1.1";
    protected static final String DEFAULT_CHARSET = "utf-8";
    protected Locale locale; // TODO 时区设置
    protected Status status;
    protected Map<String, String> heads;
    protected byte[] content;

    public Response(Status status) {
        this.status = status;
        heads = new HashMap<>();
        content = new byte[0];
        heads.put("Date", TimeUtils.toRFC822(ZonedDateTime.now()));
        heads.put("Server", PropertiesHelper.getProperty("server_name", "Telemarketer"));
        heads.put("Connection", "Close"); // TODO keep-alive
    }

    public void setHead(String key, String value) {
        heads.put(key, value);
    }

    public String getField(String key) {
        return heads.get(key);
    }


    @Override
    public void addCookie(Cookie cookie) {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean containsHeader(String name) {
        throw new NotSupportMethodException();
    }

    @Override
    public String encodeURL(String url) {
        throw new NotSupportMethodException();
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new NotSupportMethodException();
    }

    @Override
    @Deprecated
    public String encodeUrl(String url) {
        throw new NotSupportMethodException();
    }

    @Override
    @Deprecated
    public String encodeRedirectUrl(String url) {
        throw new NotSupportMethodException();
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public void sendError(int sc) throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public void setDateHeader(String name, long date) {
        throw new NotSupportMethodException();
    }

    @Override
    public void addDateHeader(String name, long date) {
        throw new NotSupportMethodException();
    }

    @Override
    public void setHeader(String name, String value) {
        throw new NotSupportMethodException();
    }

    @Override
    public void addHeader(String name, String value) {
        throw new NotSupportMethodException();
    }

    @Override
    public void setIntHeader(String name, int value) {
        throw new NotSupportMethodException();
    }

    @Override
    public void addIntHeader(String name, int value) {
        throw new NotSupportMethodException();
    }

    @Override
    public void setStatus(int sc) {
        throw new NotSupportMethodException();
    }

    @Override
    @Deprecated
    public void setStatus(int sc, String sm) {
        throw new NotSupportMethodException();
    }

    public int getStatus() {
        return status.getCode();
    }

    @Override
    public String getHeader(String name) {
        throw new NotSupportMethodException();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        throw new NotSupportMethodException();
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new NotSupportMethodException();
    }

    private ByteBuffer finalData = null;


    // TODO 解耦HTTP生成
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
                head = sb.toString().getBytes(DEFAULT_CHARSET);
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

    @Override
    public String getCharacterEncoding() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getContentType() {
        throw new NotSupportMethodException();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new NotSupportMethodException();
    }

    @Override
    public void setContentLength(int len) {
        throw new NotSupportMethodException();
    }

    @Override
    public void setContentLengthLong(long len) {
        throw new NotSupportMethodException();
    }

    @Override
    public void setContentType(String type) {
        throw new NotSupportMethodException();
    }

    @Override
    public void setBufferSize(int size) {
        throw new NotSupportMethodException();
    }

    @Override
    public int getBufferSize() {
        throw new NotSupportMethodException();
    }

    @Override
    public void flushBuffer() throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public void resetBuffer() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isCommitted() {
        throw new NotSupportMethodException();
    }

    @Override
    public void reset() {
        throw new NotSupportMethodException();
    }

    @Override
    public void setLocale(Locale loc) {
        locale = loc;
    }

    @Override
    public Locale getLocale() {
        return locale == null ? Locale.getDefault() : locale;
    }
}
