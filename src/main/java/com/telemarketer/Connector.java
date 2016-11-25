package com.telemarketer;

import com.telemarketer.http.exceptions.IllegalRequestException;
import com.telemarketer.http.requests.Request;
import com.telemarketer.http.responses.Response;
import com.telemarketer.http.exceptions.ServerInternalException;
import com.telemarketer.http.responses.NotFoundResponse;
import com.telemarketer.http.responses.ServerInternalResponse;
import com.telemarketer.services.ServiceRegistry;
import com.telemarketer.services.Service;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 控制器
 */
public class Connector implements Runnable {

    private static Logger logger = Logger.getLogger("Connector");
    private final SocketChannel channel;
    private final Selector selector;


    public Connector(SocketChannel client, Selector selector) {
        this.channel = client;
        this.selector = selector;
    }

    @Override
    public void run() {
        Request request = null;
        Response response;
        try {
            request = Request.parseRequest(channel);
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
            logger.log(Level.SEVERE, e, () -> "服务器内部错误");
            System.exit(1);
            response = new ServerInternalResponse();
        } catch (IllegalRequestException e) {
            logger.log(Level.WARNING, e, () -> "请求有错误");
            response = new ServerInternalResponse();
        }
        attachResponse(response);

        assert request != null;
        logger.info(request.getMethod() + " \"" + request.getURI() + "\" " + response.getStatus().getCode());


    }

    private void attachResponse(Response response) {
        try {
            channel.register(selector, SelectionKey.OP_WRITE, response);
            selector.wakeup();
        } catch (ClosedChannelException e) {
            logger.log(Level.WARNING, e, () -> "通道已关闭");
        }
    }


}
