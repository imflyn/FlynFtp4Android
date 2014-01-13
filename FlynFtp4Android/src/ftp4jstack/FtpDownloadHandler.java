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
        File tempFile = new File(this.ftpRequest.getLocalFilePath().substring(0, this.ftpRequest.getLocalFilePath().lastIndexOf(".")) + ".tmp");
        if (localFile.exists())
            throw new CustomFtpExcetion("LocalFile already exists.");
        else if (tempFile.exists() && tempFile.length() >= ftpFile.getSize())
            throw new CustomFtpExcetion("TempFile has been created and is wrong.");

        try
        {
            if (localFile.exists() && localFile.length() > 0)
            {
                this.ftpClient.download(this.ftpRequest.getRemoteFilePath(), localFile, tempFile.length(), this.ftpDataTransferListener);
            } else
            {
                tempFile.createNewFile();
                this.ftpClient.download(this.ftpRequest.getRemoteFilePath(), tempFile, this.ftpDataTransferListener);
                tempFile.renameTo(localFile);
            }
        } catch (Exception e)
        {
            new CustomFtpExcetion(e);
        }
    }

}
