package com.telemarket.telemarketer;

import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.io.Server;

/**
 * 启动
 */
public class TelemarketerStartup {

    public static void run(String[] args, Class<?> clazz) {
        Context.init(args, clazz);
        if (Context.isError()) {
            Context.printError();
            return;
        }
        Server server = new Server();
        server.start();
    }

    public static void main(String[] args) {
        TelemarketerStartup.run(args, TelemarketerStartup.class);
    }

}
