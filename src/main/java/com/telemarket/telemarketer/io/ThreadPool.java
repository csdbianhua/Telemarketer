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
        threadPoolExecutor = new ThreadPoolExecutor(10,
                200,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
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
