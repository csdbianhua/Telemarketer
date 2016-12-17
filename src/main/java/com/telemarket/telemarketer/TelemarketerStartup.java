package com.telemarket.telemarketer;

import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.io.Server;
import com.telemarket.telemarketer.services.IndexService;

/**
 * 启动类
 */
public class TelemarketerStartup {

    /**
     * 启动方法
     *
     * @param args  参数
     * @param clazz 扫描指定类所在的包及其子包
     */
    public static void run(String[] args, Class<?>... clazz) {
        Context.init(args, clazz);
        if (Context.isError()) {
            Context.printError();
            return;
        }
        Server server = new Server();
        server.start();
    }

    public static void main(String[] args) {
        TelemarketerStartup.run(args, IndexService.class);
    }

}
