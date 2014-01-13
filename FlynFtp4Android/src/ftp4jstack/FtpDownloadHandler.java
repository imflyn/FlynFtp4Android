package ftp4jstack;

import java.io.File;

import ftp4j.FTPFile;

public class FtpDownloadHandler extends FtpHandler
{

    protected FtpDownloadHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        super(ftpRequest, ftpResponseHandler);
    }

    @Override
    protected void doTask() throws CustomFtpExcetion
    {

        try
        {
            if (connect())
                if (login())
                {
                    download();
                }
        } catch (CustomFtpExcetion e)
        {
            new CustomFtpExcetion(e);
        } finally
        {
            disconnect();
        }
    }

    private void download() throws CustomFtpExcetion
    {
        FTPFile ftpFile = getRemoteFile(this.ftpRequest.getRemoteFilePath(), null);
        if (null == ftpFile || ftpFile.getSize() <= 0)
        {
            throw new CustomFtpExcetion("Remote File not exists.");
        }
        File localFile = new File(this.ftpRequest.getLocalFilePath());
        if (localFile.exists() && localFile.length() >= ftpFile.getSize())
            throw new CustomFtpExcetion("Local file exists.");
        try
        {
            if (localFile.exists() && localFile.length() > 0)
            {
                this.ftpClient.download(this.ftpRequest.getRemoteFilePath(), localFile, localFile.length(), this.ftpDataTransferListener);
            } else
            {
                this.ftpClient.download(this.ftpRequest.getRemoteFilePath(), localFile, this.ftpDataTransferListener);
            }
        } catch (Exception e)
        {
            new CustomFtpExcetion(e);
        }
    }

}
