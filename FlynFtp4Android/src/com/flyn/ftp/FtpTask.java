package com.flyn.ftp;

import java.lang.ref.WeakReference;
import java.util.concurrent.Future;

public class FtpTask
{
    private WeakReference<FtpDispacther> ftpDispacther;
    private Future<?>                    future;
    private boolean                      useSynchronousMode;

    protected FtpTask(FtpDispacther ftpDispacther)
    {
        this.ftpDispacther = new WeakReference<FtpDispacther>(ftpDispacther);
    }

    public void start(boolean useSynchronousMode)
    {
        FtpDispacther ftpDispacther = this.ftpDispacther.get();
        if (null != ftpDispacther)
        {
            if (!useSynchronousMode)
                this.future = FtpQueueManager.getInstance().exectue(ftpDispacther);
            else
            {
                ftpDispacther.setUseSynchronousMode(true);
                ftpDispacther.run();
            }
        }

    }

    public boolean isFinished()
    {
        FtpDispacther _request = this.ftpDispacther.get();
        return _request == null || _request.isFinished();
    }

    public boolean isCancelled()
    {
        FtpDispacther _request = this.ftpDispacther.get();
        return _request == null || _request.isCancelled();
    }

    public boolean cancel(boolean mayInterruptIfRunning)
    {
        FtpDispacther _request = this.ftpDispacther.get();
        boolean bol = _request.cancel();
        if (!this.useSynchronousMode)
            this.future.cancel(mayInterruptIfRunning);
        return bol;
    }

    public boolean shouldBeGarbageCollected()
    {
        // nothing
        return false;
    }

}
