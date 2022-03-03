package com.yao2san.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void submit(Runnable task) {
        executorService.submit(task);
    }
}
