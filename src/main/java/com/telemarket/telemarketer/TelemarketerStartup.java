package com.telemarket.telemarketer;

import com.telemarket.telemarketer.context.StartArgs;
import com.telemarket.telemarketer.io.Server;

/**
 * 开始类
 */
public class TelemarketerStartup {

    public static void main(String[] args) {
        StartArgs startArgs = new StartArgs(args, TelemarketerStartup.class);
        if (startArgs.isError()) {
            startArgs.printError();
            return;
        }
        Server server = new Server(startArgs);
        server.start();
    }


}
