package com.telemarket.telemarketer.http.requests;

import com.telemarket.telemarketer.exceptions.NotSupportMethodException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Http请求
 */
public class Request implements HttpServletRequest {

    private final RequestHeader header;
    private final RequestBody body;
    private final AtomicReference<ConcurrentMap<String, Object>> attributes = new AtomicReference<>();
    private String characterEncoding;


    Request(RequestHeader header, RequestBody body) {
        this.header = header;
        this.body = body;
    }

    private ConcurrentMap<String, Object> getMap() {
        while (true) {
            ConcurrentMap<String, Object> map = attributes.get();
            if (map != null)
                return map;
            map = new ConcurrentHashMap<>();
            if (attributes.compareAndSet(null, map))
                return map;
        }
    }

    public MultiValuedMap<String, String> getQueryMap() {
        return header.getQueryMap();
    }

    public boolean queryContainKey(String key) {
        return header.containKey(key);
    }

    public Collection<String> queryValue(String key) {
        return header.queryValue(key);
    }

    public boolean formContainKey(String key) {
        return body.formContainKey(key);
    }

    public boolean mimeContainKey(String key) {
        return body.mimeContainKey(key);
    }


    public MultiValuedMap<String, String> getFormMap() {
        return body.getFormMap();
    }

    public Map<String, MimeData> getMimeMap() {
        return body.getMimeMap();
    }


    public MimeData mimeValue(String key) {
        return body.mimeValue(key);
    }


    @Override
    public String getAuthType() {
        throw new NotSupportMethodException();
    }

    @Override
    public Cookie[] getCookies() {
        return header.getCookies();
    }

    @Override
    public long getDateHeader(String s) {
        // Returns the value of the specified request header as a long value that represents a Date object.
        // 需要解析时间
        throw new NotSupportMethodException();
    }

    @Override
    public String getHeader(String s) {
        return header.getHead().get(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        throw new NotSupportMethodException();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(header.getHead().keySet());
    }

    @Override
    public int getIntHeader(String s) {
        return NumberUtils.toInt(header.getHead().get(s), -1);
    }

    @Override
    public String getMethod() {
        return header.getMethod().toUpperCase();
    }

    @Override
    public String getPathInfo() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getPathTranslated() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getContextPath() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getQueryString() {
        return header.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isUserInRole(String s) {
        throw new NotSupportMethodException();
    }

    @Override
    public Principal getUserPrincipal() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getRequestURI() {
        return header.getURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getServletPath() {
        throw new NotSupportMethodException();
    }

    @Override
    public HttpSession getSession(boolean b) {
        throw new NotSupportMethodException();
    }

    @Override
    public HttpSession getSession() {
        throw new NotSupportMethodException();
    }

    @Override
    public String changeSessionId() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        throw new NotSupportMethodException();
    }

    @Override
    public void login(String s, String s1) throws ServletException {
        throw new NotSupportMethodException();
    }

    @Override
    public void logout() throws ServletException {
        throw new NotSupportMethodException();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new NotSupportMethodException();
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        throw new NotSupportMethodException();
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        throw new NotSupportMethodException();
    }

    @Override
    public Object getAttribute(String s) {
        return getMap().get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(getMap().keySet());
    }

    @Override
    public String getCharacterEncoding() {
        if (StringUtils.isNotEmpty(characterEncoding)) {
            return characterEncoding;
        }
        String[] split = header.getContentType().split(";");
        if (split.length <= 1) {
            return null; // TODO 获取MIME中的charset
        }
        characterEncoding = split[split.length - 1].split("=")[1];
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        characterEncoding = encoding;
        if (!StringUtils.equalsIgnoreCase("UTF-8", encoding)) {
            Charset.forName(encoding);
        }
    }

    @Override
    public int getContentLength() {
        return header.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return getContentLength();
    }

    @Override
    public String getContentType() {
        return header.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public String getParameter(String s) {
        String method = getMethod();
        Collection<String> collection = method.equalsIgnoreCase("GET") ? header.getQueryMap().get(s) : body.getFormMap().get(s);
        return CollectionUtils.isEmpty(collection) ? null : collection.iterator().next();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        TreeSet<String> names = new TreeSet<>();
        names.addAll(header.getQueryMap().keySet());
        names.addAll(body.getFormMap().keySet());
        return Collections.enumeration(names);
    }

    @Override
    public String[] getParameterValues(String s) {
        Collection<String> values = CollectionUtils.union(header.queryValue(s), body.formValue(s));
        return CollectionUtils.isEmpty(values) ? new String[0] : (String[]) values.toArray();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getProtocol() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getScheme() {
        return header.getScheme().toString();
    }

    @Override
    public String getServerName() {
        throw new NotSupportMethodException();
    }

    @Override
    public int getServerPort() {
        throw new NotSupportMethodException();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new NotSupportMethodException();
    }

    @Override
    public String getRemoteAddr() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getRemoteHost() {
        throw new NotSupportMethodException();
    }

    @Override
    public void setAttribute(String s, Object o) {
        getMap().put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        getMap().remove(s);
    }

    @Override
    public Locale getLocale() {
        throw new NotSupportMethodException();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        throw new NotSupportMethodException();
    }

    @Override
    public String getRealPath(String s) {
        throw new NotSupportMethodException();
    }

    @Override
    public int getRemotePort() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getLocalName() {
        throw new NotSupportMethodException();
    }

    @Override
    public String getLocalAddr() {
        throw new NotSupportMethodException();
    }

    @Override
    public int getLocalPort() {
        throw new NotSupportMethodException();
    }

    @Override
    public ServletContext getServletContext() {
        throw new NotSupportMethodException();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new NotSupportMethodException();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isAsyncStarted() {
        throw new NotSupportMethodException();
    }

    @Override
    public boolean isAsyncSupported() {
        throw new NotSupportMethodException();
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new NotSupportMethodException();
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new NotSupportMethodException();
    }

}
