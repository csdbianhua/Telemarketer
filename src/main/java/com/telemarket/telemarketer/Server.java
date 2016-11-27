package com.telemarket.telemarketer;

import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.services.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 服务器类
 * 负责等候和处理IO事件
 */
public class Server {
    private static final int DEFAULT_PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private InetAddress ip;
    private int port;
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Selector selector;

    public Server(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length < 1 || !args[0].equals("start")) {
            System.out.println("Usage: start [address:port]");
            System.exit(1);
        }
        InetAddress ip = null;
        int port = 0;
        try {
            if (args.length == 2 && args[1].matches(".+:\\d+")) {
                String[] address = args[1].split(":");
                ip = InetAddress.getByName(address[0]);
                port = Integer.valueOf(address[1]);
            } else {
                ip = InetAddress.getLocalHost();
                port = DEFAULT_PORT;
                System.out.println("未指定地址和端口,使用默认ip和端口..." + ip.getHostAddress() + ":" + port);
            }
        } catch (UnknownHostException e) {
            System.out.println("请输入正确的ip");
            System.exit(1);
        }

        Server server = new Server(ip, port);
        server.start();
    }

    public void start() {
        init();
        while (true) {
            try {
                if (selector.select() == 0) {
                    continue;
                }
            } catch (IOException e) {
                LOGGER.error("selector错误", e);
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                try {
                    iterator.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
                        SocketChannel client = serverSocket.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        Response response = (Response) key.attachment();
                        ByteBuffer byteBuffer = response.getByteBuffer();
                        if (byteBuffer.hasRemaining()) {
                            client.write(byteBuffer);
                        }
                        if (!byteBuffer.hasRemaining()) {
                            key.cancel();
                            client.close();
                        }
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        executor.execute(new Connector(client, selector));
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);// 取消对读取事件的兴趣 本来写的是cancel 以为是取消对读的感兴趣，
                        // 结果导致register 写事件的时候非常慢,等待了几秒。 不知道为什么
                    }
                } catch (Exception e) {
                    LOGGER.error("socket channel 出错了", e);
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    private void init() {
        long start = System.currentTimeMillis();
        System.setProperty("logback.configurationFile", "conf/logback-tele.xml");
        ServerSocketChannel serverChannel;
        try {
            ServiceRegistry.registerServices();
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(this.ip, this.port));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            LOGGER.error("初始化错误", e);
            System.exit(1);
        }
        LOGGER.info("服务器启动 http://{}:{}/ ,耗时{}ms", ip.getHostAddress(), port, System.currentTimeMillis() - start);
    }

}
