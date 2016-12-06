package com.telemarket.telemarketer.http.requests;

import com.telemarket.telemarketer.http.HttpScheme;
import com.telemarket.telemarketer.http.exceptions.IllegalRequestException;
import com.telemarket.telemarketer.util.BytesUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Hanson on 2016/11/25 16:39.
 */
public class RequestParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestParser.class);

    public static Request parseRequest(SocketChannel channel) throws IllegalRequestException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);  // TODO ByteBuffer重用，ByteBuffer pool
        channel.read(buffer); //IOException
        buffer.flip();
        int remaining = buffer.remaining();
        if (remaining == 0) {
            return null;
        }
        byte[] bytes = new byte[remaining];
        buffer.get(bytes);
        int position = BytesUtil.indexOf(bytes, "\r\n\r\n");
        if (position == -1) {
            throw new IllegalRequestException("请求不合法");
        }
        byte[] head = Arrays.copyOf(bytes, position);
        RequestHeader requestHeader = parseHeader(head);
        int contentLength = requestHeader.getContentLength();
        buffer.position(position + 4);
        ByteBuffer bodyBuffer = ByteBuffer.allocate(contentLength);
        bodyBuffer.put(buffer);
        while (bodyBuffer.hasRemaining()) {
            channel.read(bodyBuffer); //IOException
        }
        byte[] body = bodyBuffer.array();
        RequestBody requestBody = parseBody(body, requestHeader);
        return new Request(requestHeader, requestBody);
    }

    private static RequestHeader parseHeader(byte[] head) throws IOException {
        RequestHeader header = new RequestHeader();
        try (BufferedReader reader = new BufferedReader(new StringReader(new String(head, "UTF-8")))) {
            Map<String, String> headMap = new HashMap<>();
            String line = reader.readLine();
            String[] lineOne = line.split("\\s");
            String path = URLDecoder.decode(lineOne[1], "utf-8");
            String method = lineOne[0];
            HttpScheme scheme = HttpScheme.parseScheme(lineOne[2]);
            while ((line = reader.readLine()) != null) {
                String[] keyValue = line.split(":", 2);
                headMap.put(keyValue[0].trim().toLowerCase(), keyValue[1].trim());
            }
            int index = path.indexOf('?');
            MultiValuedMap<String, String> queryMap = new ArrayListValuedHashMap<>();
            String queryString = StringUtils.EMPTY;
            if (index != -1) {
                queryString = path.substring(index + 1);
                RequestParser.parseParameters(queryString, queryMap);
                path = path.substring(0, index);
            }
            header.setURI(path); // 大小写敏感
            header.setMethod(method); // 大小写敏感
            header.setHead(headMap);
            header.setQueryString(queryString);
            header.setQueryMap(queryMap);
            header.setCookies(parseCookie(headMap));
            header.setScheme(scheme);
            return header;
        }
    }

    public static void parseParameters(String s, MultiValuedMap<String, String> requestParameters) {
        String[] paras = s.split("&");
        for (String para : paras) {
            String[] split = para.split("=");
            requestParameters.put(split[0], split[1]);
        }
    }

    public static RequestBody parseBody(byte[] body, RequestHeader header) {
        if (body.length == 0) {
            return new RequestBody();
        }
        String contentType = header.getContentType();
        Map<String, MimeData> mimeMap = Collections.emptyMap();
        MultiValuedMap<String, String> formMap = new ArrayListValuedHashMap<>();
        if (contentType.contains("application/x-www-form-urlencoded")) {
            try {
                String bodyMsg = new String(body, "utf-8");
                RequestParser.parseParameters(bodyMsg, formMap);
            } catch (UnsupportedEncodingException ignored) {
            }
        } else if (contentType.contains("multipart/form-data")) {
            int boundaryValueIndex = contentType.indexOf("boundary=");
            String bouStr = contentType.substring(boundaryValueIndex + 9); // 9是 `boundary=` 长度
            mimeMap = parseFormData(body, bouStr);
        }
        RequestBody requestBody = new RequestBody();
        requestBody.setFormMap(formMap);
        requestBody.setMimeMap(mimeMap);
        return requestBody;
    }

    /**
     * @param body   body
     * @param bouStr boundary 字符串
     * @return name和mime数据的map
     */
    private static Map<String, MimeData> parseFormData(byte[] body, String bouStr) {
        Map<String, MimeData> mimeData = new HashMap<>();
        int bouLength = bouStr.length();

        int lastIndex = BytesUtil.lastIndexOf(body, bouStr);
        int startIndex;
        int endIndex = BytesUtil.indexOf(body, bouStr);
        byte[] curBody;

        do {
            //  ------WebKitFormBoundaryIwVsTjLkjugAgonI
            //  Content-Disposition: form-data; name="photo"; filename="15-5.jpeg"
            //  Content-Type: image/jpeg
            //  \r\n
            //  .....
            //  ------WebKitFormBoundaryIwVsTjLkjugAgonI
            //  Content-Disposition: form-data; name="desc"
            //  some words
            //  ------WebKitFormBoundaryIwVsTjLkjugAgonI
            //
            //  请求就是这样,所以会用魔数比较快。response 的话会有别的disposition。
            //  然而这里默认了 name在前 filename在后,不知道会不会不符合规定,若是不能默认那还是用正则吧
            startIndex = endIndex + bouLength;
            endIndex = BytesUtil.indexOf(body, bouStr, startIndex + bouLength);

            curBody = Arrays.copyOfRange(body, startIndex + 2, endIndex); //去掉\r\n
            int lineEndIndex = BytesUtil.indexOf(curBody, "\r\n");
            byte[] lineOne = Arrays.copyOfRange(curBody, 0, lineEndIndex);
            int leftQuoIndex = BytesUtil.indexOf(lineOne, "\"");
            int rightQuoIndex = BytesUtil.indexOf(lineOne, "\"", leftQuoIndex + 1);
            String name;
            String fileName = null;
            String mimeType = null;
            byte[] data;
            name = new String(Arrays.copyOfRange(lineOne, leftQuoIndex + 1, rightQuoIndex));
            leftQuoIndex = BytesUtil.indexOf(lineOne, "\"", rightQuoIndex + 1);
            int curIndex;
            if (leftQuoIndex != -1) {
                rightQuoIndex = BytesUtil.indexOf(lineOne, "\"", leftQuoIndex + 1);
                fileName = new String(Arrays.copyOfRange(lineOne, leftQuoIndex + 1, rightQuoIndex));
                int headEndIndex = BytesUtil.indexOf(curBody, "\r\n\r\n", 13);
                mimeType = new String(Arrays.copyOfRange(curBody, lineEndIndex + 16, headEndIndex));
                curIndex = headEndIndex + 4;
            } else {
                curIndex = lineEndIndex + 2;
            }
            data = Arrays.copyOfRange(curBody, curIndex, curBody.length);
            mimeData.put(name, new MimeData(mimeType, data, fileName));
        } while (endIndex != lastIndex);
        return mimeData;
    }

    private static Cookie[] parseCookie(Map<String, String> headMap) {
        if (MapUtils.isEmpty(headMap)) {
            return new Cookie[0];
        }
        String cookies = headMap.get("cookie");
        if (StringUtils.isBlank(cookies)) {
            return new Cookie[0];
        }
        String[] split = cookies.split(";");
        Cookie[] cookieArray = new Cookie[split.length];
        for (int i = 0; i < split.length; i++) {
            String[] array;
            try {
                array = split[i].split("=", 2);
                cookieArray[i] = new Cookie(array[0], array[1]);
            } catch (RuntimeException e) {
                LOGGER.error("非法cookie", e);
                cookieArray[i] = new Cookie(StringUtils.EMPTY, StringUtils.EMPTY); // TODO 这里不能为空，如果出现了保留cookie怎么办
            }
        }
        return cookieArray;
    }
}
