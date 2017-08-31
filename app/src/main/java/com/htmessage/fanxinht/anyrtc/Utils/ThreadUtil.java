package com.htmessage.fanxinht.anyrtc.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongxuesong-pc on 2016/6/1.
 */
public class ThreadUtil {
    private static ThreadPool mThreadPool = getThreadPool();

    private ThreadUtil() {
    }

    /**
     * 在子线程执行任务
     *
     * @param task
     */
    public static void runInThread(Runnable task) {
        mThreadPool.execute(task);
    }

    /**
     * 清理线程中的问题;
     *
     * @param task
     */
    public static void cancelThread(Runnable task) {
        mThreadPool.cancel(task);
    }

    /**
     * 在UI线程执行任务
     *
     * @param task
     */
    public static void runInUIThread(Runnable task) {
        AppUtils.runOnUiThread(task);
    }


    /**
     * 在UI线程延时执行任务
     *
     * @param task
     * @param delayMillis 延时时间，单位毫秒
     */
    public static void runInUIThread(Runnable task, long delayMillis) {
        AppUtils.getHandler().postDelayed(task, delayMillis);
    }


    /**
     * 获取单例的线程池对象
     *
     * @return
     */
    public static ThreadPool getThreadPool() {
        if (mThreadPool == null) {
            synchronized (ThreadUtil.class) {
                if (mThreadPool == null) {
                    // cpu个数
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    // int count = cpuNum * 2 + 1;
                    int count = 15;
                    System.out.println("cpu个数:" + cpuNum);
                    mThreadPool = new ThreadPool(count, count, 0L);
                }
            }
        }

        return mThreadPool;
    }


    public static class ThreadPool {

        private int corePoolSize;// 核心线程数
        private int maximumPoolSize;// 最大线程数
        private long keepAliveTime;// 保持活跃时间(休息时间)

        private ThreadPoolExecutor executor;

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void execute(Runnable r) {

            if (executor == null) {
                executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(),
                        Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }
            executor.execute(r);// 将当前Runnable对象放在线程池中
        }

        // 移除任务
        public void cancel(Runnable r) {
            if (executor != null) {
                executor.getQueue().remove(r);// 从下载队列中移除下载任务
            }
        }
    }

}