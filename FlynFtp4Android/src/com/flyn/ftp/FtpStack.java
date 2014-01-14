package com.flyn.ftp;

public class FtpStack
{
    public static FtpTask ftp4jUpload(FtpRequest ftpRequest)
    {
        return ftp4jUpload(ftpRequest, null);
    }

    public static FtpTask ftp4jUpload(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        FtpTask ftpFuture = new FtpTask(new FtpDispacther(new Ftp4jUploadHandler(ftpRequest, ftpResponseHandler)));
        return ftpFuture;
    }

    public static FtpTask ftp4jDownload(FtpRequest ftpRequest)
    {
        return ftp4jDownload(ftpRequest, null);
    }

    public static FtpTask ftp4jDownload(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        FtpTask ftpFuture = new FtpTask(new FtpDispacther(new Ftp4jDownloadHandler(ftpRequest, ftpResponseHandler)));
        return ftpFuture;
    }

    public static FtpTask apacheUpload(FtpRequest ftpRequest)
    {
        return apacheUpload(ftpRequest, null);
    }

    public static FtpTask apacheUpload(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        FtpTask ftpFuture = new FtpTask(new FtpDispacther(new ApacheFtpUploadHandler(ftpRequest, ftpResponseHandler)));
        return ftpFuture;
    }

    public static FtpTask apacheDownload(FtpRequest ftpRequest)
    {
        return apacheDownload(ftpRequest, null);
    }

    public static FtpTask apacheDownload(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        FtpTask ftpFuture = new FtpTask(new FtpDispacther(new ApacheFtpDownloadHandler(ftpRequest, ftpResponseHandler)));
        return ftpFuture;
    }

}
