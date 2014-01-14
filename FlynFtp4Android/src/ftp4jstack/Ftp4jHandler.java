package ftp4jstack;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import ftp4j.FTPClient;
import ftp4j.FTPCommunicationListener;
import ftp4j.FTPDataTransferListener;
import ftp4j.FTPException;
import ftp4j.FTPFile;
import ftp4j.FTPIllegalReplyException;

public abstract class Ftp4jHandler extends IFtpHandler
{
    private static final String   TAG                 = Ftp4jHandler.class.getName();

    private static final int      DEFAULT_RETRY_COUNT = 3;
    private static final String   DEFAULT_CHARSET     = "utf-8";

    protected FtpResponseListener ftpResponseListener;
    protected FtpRequest          ftpRequest;
    protected FTPClient           ftpClient;

    private int                   executionCount      = 0;
    private boolean               isCancelled         = false;
    private boolean               cancelIsNotified    = false;
    private boolean               isFinished          = false;

    private Timer                 timer;
    private boolean               isScheduleing       = true;
    private long                  timeStamp           = System.currentTimeMillis();
    private long                  sizeStamp           = 0;
    private int                   currentSpeed        = 0;
    protected int                 bytesTotal          = 0;
    private int                   bytesWritten        = 0;

    protected Ftp4jHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseListener)
    {
        this.ftpResponseListener = ftpResponseListener;
        this.ftpRequest = ftpRequest;
        this.ftpClient = new FTPClient();
        init();
    }

    protected void init()
    {
        this.ftpClient.setCharset(DEFAULT_CHARSET);
        this.ftpClient.setSecurity(FTPClient.SECURITY_FTP);
        this.ftpClient.setPassive(true);// 设置Ftp被动模式
        this.ftpClient.setType(FTPClient.TYPE_BINARY);// 设置二进制传输
        this.ftpClient.addCommunicationListener(new FTPCommunicationListener()
        {
            @Override
            public void sent(String statement)
            {
                Log.i(TAG, "sent:" + statement);
            }

            @Override
            public void received(String statement)
            {
                Log.i(TAG, "received:" + statement);
            }
        });
    }

    protected boolean connect() throws CustomFtpExcetion
    {
        if (this.ftpClient != null && !this.ftpClient.isConnected())
        {
            try
            {
                this.ftpClient.connect(this.ftpRequest.getFtpInfo().getHost(), this.ftpRequest.getFtpInfo().getPort());
                // unuseful
                // for (int i = 0; i < replyCode.length; i++)
                // {
                // Log.i(TAG, "Connect replyCode:".concat(replyCode[0]));
                // }
                return true;
            } catch (IllegalStateException e)
            {
                // should not happen
                Log.e(TAG, "Client already connected to " + this.ftpRequest.getFtpInfo().getHost() + " on port " + this.ftpRequest.getFtpInfo().getPort());
                return true;
            } catch (IOException e)
            {
                new CustomFtpExcetion("Connect error IOException", e);
            } catch (FTPIllegalReplyException e)
            {
                new CustomFtpExcetion("Connect error FTPIllegalReplyException", e);
            } catch (FTPException e)
            {
                new CustomFtpExcetion("Connect error FTPException", e);
            }
        }
        return false;
    }

    protected boolean login() throws CustomFtpExcetion
    {
        if (null != this.ftpClient && this.ftpClient.isConnected() && !this.ftpClient.isAuthenticated())
        {
            try
            {
                this.ftpClient.login(this.ftpRequest.getFtpInfo().getUsername(), this.ftpRequest.getFtpInfo().getPassword(), this.ftpRequest.getFtpInfo().getAccount());

                // if (this.ftpClient.isCompressionEnabled())
                // this.ftpClient.setCompressionEnabled(true);// 支持压缩传输

                return true;
            } catch (IllegalStateException e)
            {
                // should not happen
                Log.e(TAG, "Client not connected.");
            } catch (IOException e)
            {
                new CustomFtpExcetion("Login error IOException", e);
            } catch (FTPIllegalReplyException e)
            {
                new CustomFtpExcetion("Login error FTPIllegalReplyException", e);
            } catch (FTPException e)
            {
                new CustomFtpExcetion("Login error FTPException", e);
            }
        }
        return false;
    }

    protected boolean remoteFileExists(String remotePath, String charset) throws CustomFtpExcetion
    {
        try
        {
            FTPFile[] ftpFileName = this.ftpClient.list(new String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET : charset));
            if (null != ftpFileName && ftpFileName.length > 0)
                return true;
            else
                return false;
        } catch (Exception e)
        {
            new CustomFtpExcetion("remoteFileExists  Exception", e);
        }
        return false;
    }

    protected FTPFile getRemoteFile(String remotePath, String charset) throws CustomFtpExcetion
    {
        FTPFile ftpFile = null;
        try
        {
            FTPFile[] ftpFileName = this.ftpClient.list(new String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET : charset));

            if (null != ftpFileName && ftpFileName.length > 0)
                ftpFile = ftpFileName[0];
        } catch (Exception e)
        {
            new CustomFtpExcetion("GetRemoteFile error Exception", e);
        }
        return ftpFile;
    }

    protected void createDirectory(String remoteDirectory, String charset) throws CustomFtpExcetion
    {
        try
        {
            if (!remoteFileExists(remoteDirectory, charset))
                this.ftpClient.createDirectory(new String(remoteDirectory.getBytes(), charset == null ? DEFAULT_CHARSET : charset));
        } catch (Exception e)
        {
            new CustomFtpExcetion("CreateDirectory error Exception", e);
        }
    }

    protected void changeWorkingDirectory(String remoteDirectory, String charset) throws CustomFtpExcetion
    {
        try
        {
            this.ftpClient.changeDirectory(new String(remoteDirectory.getBytes(), charset == null ? DEFAULT_CHARSET : charset));
        } catch (Exception e)
        {
            new CustomFtpExcetion("CreateDirectory error Exception", e);
        }
    }

    protected void disconnect()
    {
        if (null != this.ftpClient && this.ftpClient.isConnected())
        {
            try
            {
                this.ftpClient.disconnect(true);
            } catch (IllegalStateException e)
            {
                // should not happen
                Log.e(TAG, "Client not connected.");
            } catch (IOException e)
            {
                Log.e(TAG, "disconnect error IOException", e);
            } catch (FTPIllegalReplyException e)
            {
                Log.e(TAG, "disconnect error FTPIllegalReplyException", e);
            } catch (FTPException e)
            {
                Log.e(TAG, "disconnect error FTPException", e);
            } finally
            {
                this.ftpClient = null;
            }
        }
    }

    protected abstract void doTask() throws CustomFtpExcetion;

    @Override
    protected void doTaskWithRetries()
    {
        int retryCount = ifRetry() ? DEFAULT_RETRY_COUNT : 1;
        Throwable cause = null;
        try
        {
            while (retryCount-- > 0)
            {
                try
                {
                    if (isCancelled())
                        return;
                    startTimer();
                    doTask();
                    if (null != this.ftpResponseListener)
                        this.ftpResponseListener.sendSuccessMessage();
                    return;
                }
                // catch (InterruptedException e)
                // {
                // // Task should has be cancelled.
                // cancel();
                // }
                catch (CustomFtpExcetion e)
                {
                    if (isCancelled())
                        return;
                    cause = e;
                }
                if (retryCount > 0 && (this.ftpResponseListener != null))
                {
                    this.ftpResponseListener.sendRetryMessage(this.executionCount);
                }
            }
        } catch (Exception e)
        {
            Log.e(TAG, "Unhandled exception origin cause", e);
            cause = e;
        } finally
        {
            stopTimer();
            this.isFinished = true;
            if (null != this.ftpResponseListener)
                this.ftpResponseListener.sendFinishMessage();
        }
        if (null != this.ftpResponseListener)
            this.ftpResponseListener.sendFailuerMessage(cause);
    }

    protected final boolean ifRetry()
    {
        return this.ftpRequest != null ? this.ftpRequest.isIfRetry() : false;
    }

    @Override
    protected final boolean isFinished()
    {
        return isCancelled() || this.isFinished;
    }

    @Override
    protected final boolean isCancelled()
    {
        if (this.isCancelled)
            sendCancelNotification();

        return this.isCancelled;
    }

    private synchronized void sendCancelNotification()
    {
        if (!this.isFinished && this.isCancelled && !this.cancelIsNotified)
        {
            this.cancelIsNotified = true;
            if (this.ftpResponseListener != null)
                this.ftpResponseListener.sendCancelMessage();
        }
    }

    @Override
    protected final boolean cancel()
    {
        this.isCancelled = true;
        return isCancelled();
    }

    @Override
    protected void setUseSynchronousMode(boolean value)
    {
        if (null != this.ftpResponseListener)
            this.ftpResponseListener.setUseSynchronousMode(value);
    }

    private void updateProgress(int count)
    {
        this.bytesWritten += count;
    }

    private void startTimer()
    {
        if (null == this.timer)
            this.timer = new Timer();

        final TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (isScheduleing && !Thread.currentThread().isInterrupted())
                {
                    long nowTime = System.currentTimeMillis();
                    long spendTime = nowTime - timeStamp;
                    timeStamp = nowTime;

                    long getSize = bytesWritten - sizeStamp;
                    sizeStamp = bytesWritten;
                    if (spendTime > 0)
                        currentSpeed = (int) ((getSize / spendTime) / 1.024);

                    if (null != ftpResponseListener)
                    {
                        ftpResponseListener.sendProgressMessage(bytesWritten, bytesTotal, currentSpeed);
                    }
                } else
                {
                    stopTimer();
                }
            }
        };
        this.timer.schedule(task, 100, 1000);
    }

    private void stopTimer()
    {
        this.isScheduleing = false;
        if (this.timer != null)
        {
            this.timer.cancel();
            this.timer = null;
        }
    }

    protected FTPDataTransferListener ftpDataTransferListener = new FTPDataTransferListener()
                                                              {

                                                                  @Override
                                                                  public void transferred(int length)
                                                                  {
                                                                      updateProgress(length);
                                                                  }

                                                                  @Override
                                                                  public void started()
                                                                  {

                                                                  }

                                                                  @Override
                                                                  public void failed()
                                                                  {

                                                                  }

                                                                  @Override
                                                                  public void completed()
                                                                  {

                                                                  }

                                                                  @Override
                                                                  public void aborted()
                                                                  {

                                                                  }
                                                              };

}
