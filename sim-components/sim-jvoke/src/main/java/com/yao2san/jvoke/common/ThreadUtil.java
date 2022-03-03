package com.yao2san.jvoke.common;

import java.util.concurrent.*;

public class ThreadUtil {
    public static ThreadPoolExecutor FACTORY;

    static {
        FACTORY = new ThreadPoolExecutor(5, 50, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

}
