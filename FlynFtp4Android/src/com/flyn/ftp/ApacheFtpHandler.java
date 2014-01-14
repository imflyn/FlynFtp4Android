package com.flyn.ftp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

public abstract class ApacheFtpHandler extends IFtpHandler
{
    private static final String   TAG                              = ApacheFtpHandler.class.getName();

    protected static final int    DEFAULT_BUFFER_SIZE              = 8 * 1024;
    private static final int      DEFAULT_RETRY_COUNT              = 3;
    private static final int      KEEP_ALIVE_TIMEOUT               = 30;
    private static final int      CONTROL_KEEP_ALIVE_REPLY_TIMEOUT = 30;
    private static final String   DEFAULT_CHARSET                  = "utf-8";
    private static final boolean  LIST_HIDDEN                      = false;
    private static final int      TRANSFER_TYPE                    = FTP.BINARY_FILE_TYPE;
    private static final int      FILE_TRANSFER_MODE               = FTP.BINARY_FILE_TYPE;

    protected FtpResponseListener ftpResponseListener;
    protected FtpRequest          ftpRequest;
    protected FTPClient           ftpClient;

    private int                   executionCount                   = 0;
    private boolean               isCancelled                      = false;
    private boolean               cancelIsNotified                 = false;
    private boolean               isFinished                       = false;

    private Timer                 timer;
    private boolean               isScheduleing                    = true;
    private long                  timeStamp                        = System.currentTimeMillis();
    private long                  sizeStamp                        = 0;
    private int                   currentSpeed                     = 0;
    protected int                 bytesTotal                       = 0;
    protected int                 bytesWritten                     = 0;

    protected ApacheFtpHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseListener)
    {
        this.ftpResponseListener = ftpResponseListener;
        this.ftpRequest = ftpRequest;
        this.ftpClient = new FTPClient();
        init();
    }

    protected void init()
    {
        this.ftpClient.setControlEncoding(DEFAULT_CHARSET);
        this.ftpClient.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT);
        this.ftpClient.setControlKeepAliveReplyTimeout(CONTROL_KEEP_ALIVE_REPLY_TIMEOUT);
        this.ftpClient.setListHiddenFiles(LIST_HIDDEN);
        // suppress login details
        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

    }

    protected boolean connect() throws CustomFtpExcetion
    {
        if (this.ftpClient != null && !this.ftpClient.isConnected())
        {
            try
            {
                this.ftpClient.connect(this.ftpRequest.getFtpInfo().getHost(), this.ftpRequest.getFtpInfo().getPort());
                int reply = this.ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply))
                {
                    // disconnect();
                    throw new CustomFtpExcetion("FTP server refused connection.");
                } else
                    return true;
            } catch (SocketException e)
            {
                throw new CustomFtpExcetion("SocketException", e);
            } catch (IOException e)
            {
                throw new CustomFtpExcetion("IOException", e);
            } catch (NullPointerException e)
            {
                throw new CustomFtpExcetion("NullPointerException", e);
            }
        }
        return false;
    }

    protected boolean login() throws CustomFtpExcetion
    {
        __login: try
        {
            if (null != this.ftpClient && this.ftpClient.isConnected())
            {
                if (!this.ftpClient.login(this.ftpRequest.getFtpInfo().getUsername(), this.ftpRequest.getFtpInfo().getPassword()))
                {
                    this.ftpClient.logout();
                    break __login;
                }

                // settings
                this.ftpClient.setFileType(TRANSFER_TYPE);
                this.ftpClient.setFileTransferMode(FILE_TRANSFER_MODE);
                this.ftpClient.enterLocalPassiveMode();
                this.ftpClient.setBufferSize(DEFAULT_BUFFER_SIZE);
                this.ftpClient.setReceiveBufferSize(DEFAULT_BUFFER_SIZE);

                FTPClientConfig config = new FTPClientConfig();
                config.setLenientFutureDates(true);
                this.ftpClient.configure(config);

            }
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (NullPointerException e)
        {
            throw new CustomFtpExcetion(e);
        }
        return true;
    }

    protected boolean remoteFileExists(String remotePath, String charset) throws CustomFtpExcetion
    {
        FTPFile[] ftpFileName = null;
        try
        {
            FTPListParseEngine engine = this.ftpClient.initiateListParsing(new String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET : charset));

            if (engine.hasNext())
            {
                ftpFileName = engine.getNext(1);
            }

            // ftpFileName = this.ftpClient.listFiles(new
            // String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET :
            // charset));
        } catch (UnsupportedEncodingException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        }
        if (null != ftpFileName && ftpFileName.length > 0)
            return true;
        else
            return false;
    }

    protected boolean remoteDirectoryExists(String remotePath, String charset) throws CustomFtpExcetion
    {
        FTPFile[] ftpFileName = null;
        try
        {
            FTPListParseEngine engine = this.ftpClient.initiateListParsing(new String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET : charset));

            if (engine.hasNext())
            {
                ftpFileName = engine.getNext(1);
            }
            // ftpFileName = this.ftpClient.listDirectories(new
            // String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET :
            // charset));
        } catch (UnsupportedEncodingException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        }
        if (null != ftpFileName && ftpFileName.length > 0)
            return true;
        else
            return false;
    }

    protected FTPFile getRemoteFile(String remotePath, String charset) throws CustomFtpExcetion
    {
        FTPFile ftpFile = null;
        FTPFile[] ftpFileName = null;
        try
        {
            FTPListParseEngine engine = this.ftpClient.initiateListParsing(new String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET : charset));

            if (engine.hasNext())
            {
                ftpFileName = engine.getNext(1);
            }

            // ftpFileName = this.ftpClient.listFiles(new
            // String(remotePath.getBytes(), charset == null ? DEFAULT_CHARSET :
            // charset));
        } catch (UnsupportedEncodingException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        }
        if (null != ftpFileName && ftpFileName.length > 0)
            ftpFile = ftpFileName[0];

        return ftpFile;
    }

    protected void createDirectory(String remoteDirectory, String charset) throws CustomFtpExcetion
    {
        if (!remoteDirectoryExists(remoteDirectory, charset))
            try
            {
                this.ftpClient.makeDirectory(new String(remoteDirectory.getBytes(), charset == null ? DEFAULT_CHARSET : charset));
            } catch (UnsupportedEncodingException e)
            {
                throw new CustomFtpExcetion(e);
            } catch (IOException e)
            {
                throw new CustomFtpExcetion(e);
            }
    }

    protected void changeWorkingDirectory(String remoteDirectory, String charset) throws CustomFtpExcetion
    {
        try
        {
            this.ftpClient.changeWorkingDirectory(new String(remoteDirectory.getBytes(), charset == null ? DEFAULT_CHARSET : charset));
        } catch (UnsupportedEncodingException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        }
    }

    protected void disconnect()
    {
        if (null != this.ftpClient)
        {
            try
            {
                this.ftpClient.logout();
            } catch (IOException e)
            {
                // ingore
            } finally
            {
                try
                {
                    if (null != this.ftpClient)
                        this.ftpClient.disconnect();
                } catch (IOException e)
                {
                    // ingore
                } finally
                {
                    this.ftpClient = null;
                }
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

                    if (connect())
                        if (login())
                        {
                            if (isCancelled())
                                return;
                            startTimer();
                            doTask();
                        }
                    stopTimer();
                    if (null != this.ftpResponseListener && !isCancelled())
                        this.ftpResponseListener.sendSuccessMessage();
                    return;
                } catch (CustomFtpExcetion e)
                {
                    if (isCancelled())
                        return;
                    cause = e;
                } finally
                {
                    stopTimer();
                    disconnect();
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

    protected void updateProgress(int count)
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
        this.timer.schedule(task, 300, 1000);
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

}
