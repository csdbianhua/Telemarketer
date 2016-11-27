package com.telemarket.telemarketer;

import com.telemarket.telemarketer.http.exceptions.IllegalRequestException;
import com.telemarket.telemarketer.http.exceptions.ServerInternalException;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.requests.RequestParser;
import com.telemarket.telemarketer.http.responses.NotFoundResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.http.responses.ServerInternalResponse;
import com.telemarket.telemarketer.services.Service;
import com.telemarket.telemarketer.services.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 控制器
 */
public class Connector implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);
    private final SocketChannel channel;
    private final Selector selector;


    public Connector(SocketChannel client, Selector selector) {
        this.channel = client;
        this.selector = selector;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        Request request = null;
        Response response;
        try {
            request = RequestParser.parseRequest(channel);
            if (request == null) {
                return;
            }
            Service service = ServiceRegistry.findService(request.getURI());
            if (service == null) {
                response = new NotFoundResponse();
            } else {
                response = service.service(request);
                if (response == null) {
                    throw new ServerInternalException("service返回了一个null");
                }
            }

        } catch (ServerInternalException | IOException e) { // 这个IOException都是parseRequest里出来的
            LOGGER.error("服务器内部错误", e);
            response = new ServerInternalResponse();
        } catch (IllegalRequestException e) {
            LOGGER.error("请求有错误", e);
            response = new ServerInternalResponse();
        }
        attachResponse(response);

        assert request != null;
        LOGGER.info("{} \"{}\" {} {}ms", request.getMethod(), request.getURI(), response.getStatus().getCode(), System.currentTimeMillis() - start);


    }

    private void attachResponse(Response response) {
        try {
            channel.register(selector, SelectionKey.OP_WRITE, response);
            selector.wakeup();
        } catch (ClosedChannelException e) {
            LOGGER.error("通道已关闭", e);
        }
    }


}
