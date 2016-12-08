package com.telemarket.telemarketer;

import com.telemarket.telemarketer.context.Context;
import com.telemarket.telemarketer.io.Server;

/**
 * 启动
 */
public class TelemarketerStartup {

    public static void main(String[] args) {
        Context.init(args, TelemarketerStartup.class);
        if (Context.isError()) {
            Context.printError();
            return;
        }
        Server server = new Server();
        server.start();
    }


}
