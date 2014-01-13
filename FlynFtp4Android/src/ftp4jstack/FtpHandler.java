package ftp4jstack;

import java.io.IOException;

import android.util.Log;
import ftp4j.FTPClient;
import ftp4j.FTPCommunicationListener;
import ftp4j.FTPDataTransferListener;
import ftp4j.FTPException;
import ftp4j.FTPFile;
import ftp4j.FTPIllegalReplyException;

public abstract class FtpHandler
{
    private static final String   TAG                 = FtpHandler.class.getName();

    private static final int      DEFAULT_RETRY_COUNT = 3;
    private static final String   DEFAULT_CHARSET     = "utf-8";

    protected FtpResponseListener ftpResponseListener;
    protected FtpRequest          ftpRequest;
    protected final FTPClient     ftpClient;

    private int                   executionCount      = 0;
    private boolean               isCancelled         = false;
    private boolean               cancelIsNotified    = false;
    private boolean               isFinished          = false;

    protected FtpHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseListener)
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
                String[] replyCode = this.ftpClient.connect(this.ftpRequest.getFtpInfo().getHost(), this.ftpRequest.getFtpInfo().getPort());
                for (int i = 0; i < replyCode.length; i++)
                {
                    Log.i(TAG, "connect replyCode:".concat(replyCode[0]));
                }
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

                if (this.ftpClient.isCompressionEnabled())
                    this.ftpClient.setCompressionEnabled(true);// 支持压缩传输

                return true;
            } catch (IllegalStateException e)
            {
                // should not happen
                Log.e(TAG, "Client not connected.");
            } catch (IOException e)
            {
                new CustomFtpExcetion("login error IOException", e);
            } catch (FTPIllegalReplyException e)
            {
                new CustomFtpExcetion("login error FTPIllegalReplyException", e);
            } catch (FTPException e)
            {
                new CustomFtpExcetion("login error FTPException", e);
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
            new CustomFtpExcetion("remoteFileExists error Exception", e);
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
            new CustomFtpExcetion("getRemoteFile error Exception", e);
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
            new CustomFtpExcetion("createDirectory error Exception", e);
        }
    }

    protected void changeWorkingDirectory(String remoteDirectory, String charset) throws CustomFtpExcetion
    {
        try
        {
            this.ftpClient.changeDirectory(new String(remoteDirectory.getBytes(), charset == null ? DEFAULT_CHARSET : charset));
        } catch (Exception e)
        {
            new CustomFtpExcetion("createDirectory error Exception", e);
        }
    }

    protected void disconnect()
    {
        if (null != this.ftpClient && this.ftpClient.isConnected() && this.ftpClient.isAuthenticated())
        {
            try
            {
                this.ftpClient.logout();
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
                try
                {
                    if (null != this.ftpClient && this.ftpClient.isConnected())
                        this.ftpClient.disconnect(true);
                } catch (Exception e)
                {
                    Log.e(TAG, "disconnect error Exception", e);
                    // ingore
                }
            }
        }
    }

    protected abstract void doTask() throws CustomFtpExcetion;

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

    protected final boolean isFinished()
    {
        return isCancelled() || this.isFinished;
    }

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

    protected final boolean cancel()
    {
        this.isCancelled = true;
        return isCancelled();
    }

    protected void setUseSynchronousMode(boolean value)
    {
        if (null != this.ftpResponseListener)
            this.ftpResponseListener.setUseSynchronousMode(value);
    }

    protected void updateProgress()
    {

    }

    protected FTPDataTransferListener ftpDataTransferListener = new FTPDataTransferListener()
                                                              {

                                                                  @Override
                                                                  public void transferred(int length)
                                                                  {

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
