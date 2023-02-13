package com.powernode.utils;


import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ManagerThreadPool {

    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            4,
            Runtime.getRuntime().availableProcessors(),
            20,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(30),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );
}
