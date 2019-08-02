package com.example.dunzi.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolManager {
    private static ThreadPoolExecutor executor;
    private static ThreadPool mThreadPool;

    public static synchronized ThreadPool getThreadPoolInstance() {
        if (mThreadPool == null) {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            int threadCount = cpuCount * 2 + 1;
            mThreadPool = new ThreadPool(threadCount, threadCount, 1L);
        }
        return mThreadPool;
    }


    public static class ThreadPool {
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void execute(Runnable r) {
            if (executor == null) {
                executor = new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }
            executor.execute(r);
        }
    }

    //取消任务
    public static void cancel(Runnable r) {
        if (executor != null) {
            executor.getQueue().remove(r);
        }
    }


}
