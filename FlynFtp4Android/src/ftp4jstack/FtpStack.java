package ftp4jstack;

public class FtpStack
{
    public static FtpTask upload(FtpRequest ftpRequest)
    {
        return upload(ftpRequest, null);
    }

    public static FtpTask upload(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        FtpTask ftpFuture = new FtpTask(new FtpDispacther(new FtpUploadHandler(ftpRequest, ftpResponseHandler)));
        return ftpFuture;
    }

    public static FtpTask download(FtpRequest ftpRequest)
    {
        return download(ftpRequest, null);
    }

    public static FtpTask download(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        FtpTask ftpFuture = new FtpTask(new FtpDispacther(new FtpDownloadHandler(ftpRequest, ftpResponseHandler)));
        return ftpFuture;
    }

}
