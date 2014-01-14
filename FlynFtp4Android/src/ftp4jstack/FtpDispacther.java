package ftp4jstack;

public class FtpDispacther implements Runnable
{

    private IFtpHandler ftpHandler;

    protected FtpDispacther(IFtpHandler ftpHandler)
    {
        this.ftpHandler = ftpHandler;
    }

    @Override
    public void run()
    {
        if (isCancelled())
        {
            return;
        }
        this.ftpHandler.doTaskWithRetries();

    }

    protected boolean isFinished()
    {
        return isCancelled() || this.ftpHandler.isFinished();
    }

    protected boolean isCancelled()
    {
        return this.ftpHandler.isCancelled();
    }

    protected boolean cancel()
    {
        return this.ftpHandler.cancel();
    }

    protected void setUseSynchronousMode(boolean value)
    {
        this.ftpHandler.setUseSynchronousMode(value);
    }
}
