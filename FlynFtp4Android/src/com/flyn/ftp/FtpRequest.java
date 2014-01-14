package com.flyn.ftp;

public class FtpRequest
{
    private FtpInfo ftpInfo;
    private String  localFilePath;
    private String  remoteFilePath;
    private boolean ifRetry;

    public FtpRequest(FtpInfo ftpInfo, String localFilePath, String remoteFilePath, boolean ifRetry)
    {
        super();
        this.ftpInfo = ftpInfo;
        this.localFilePath = localFilePath;
        this.remoteFilePath = remoteFilePath;
        this.ifRetry = ifRetry;
    }

    public final FtpInfo getFtpInfo()
    {
        return ftpInfo;
    }

    public final void setFtpInfo(FtpInfo ftpInfo)
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
