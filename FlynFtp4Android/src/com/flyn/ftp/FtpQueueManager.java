package com.flyn.ftp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FtpQueueManager
{
    private static FtpQueueManager ftpManager;
    private final static Object    LOCK = new Object();
    private ExecutorService        executorService;

    protected static FtpQueueManager getInstance()
    {
        if (null == ftpManager)
        {
            synchronized (LOCK)
            {
                if (null == ftpManager)
                {
                    ftpManager = new FtpQueueManager();
                    ftpManager.init();
                }
            }
        }
        return ftpManager;
    }

    private void init()
    {
        if (null == this.executorService)
            this.executorService = Executors.newCachedThreadPool(new ThreadFactory()
            {
                AtomicInteger index = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r)
                {
                    Thread thread = new Thread(r, "FtpTask #" + index);
                    thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    return thread;
                }
            });

    }

    protected Future<?> exectue(Runnable runnable)
    {

        Future<?> future = this.executorService.submit(runnable);

        return future;
    }

}
