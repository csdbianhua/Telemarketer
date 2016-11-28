package com.telemarket.telemarketer.io;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Chen Yijie on 2016/11/28 13:18.
 */
public class ThreadPool {

    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(20,
                200,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new ThreadPoolExecutor.DiscardPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                threadPoolExecutor.shutdown();
            }
        });
    }

    public static void execute(Runnable task) {
        threadPoolExecutor.execute(task);
    }
}
