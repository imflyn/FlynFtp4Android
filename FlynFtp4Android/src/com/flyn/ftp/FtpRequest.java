package com.flyn.ftp;

public class FtpRequest
{
    private FtpInfoBean ftpInfo;
    private String  localFilePath;
    private String  remoteFilePath;
    private boolean ifRetry=false;

    public FtpRequest(FtpInfoBean ftpInfo, String localFilePath, String remoteFilePath, boolean ifRetry)
    {
        this(ftpInfo, localFilePath, remoteFilePath);
        this.ifRetry = ifRetry;
    }
    
    public FtpRequest(FtpInfoBean ftpInfo, String localFilePath, String remoteFilePath)
    {
        this.ftpInfo = ftpInfo;
        this.localFilePath = localFilePath;
        this.remoteFilePath = remoteFilePath;
    }

    public final FtpInfoBean getFtpInfo()
    {
        return ftpInfo;
    }

    public final void setFtpInfo(FtpInfoBean ftpInfo)
    {
        this.ftpInfo = ftpInfo;
    }

    public final String getLocalFilePath()
    {
        return localFilePath;
    }

    public final void setLocalFilePath(String localFilePath)
    {
        this.localFilePath = localFilePath;
    }

    public final String getRemoteFilePath()
    {
        return remoteFilePath;
    }

    public final void setRemoteFilePath(String remoteFilePath)
    {
        this.remoteFilePath = remoteFilePath;
    }

    public final boolean isIfRetry()
    {
        return ifRetry;
    }

    public final void setIfRetry(boolean ifRetry)
    {
        this.ifRetry = ifRetry;
    }

}
