package ftp4jstack;

import java.io.File;

import ftp4j.FTPFile;

public class FtpUploadHandler extends FtpHandler
{

    protected FtpUploadHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
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
                    upload();
                }
        } catch (CustomFtpExcetion e)
        {
            throw new CustomFtpExcetion(e);
        } finally
        {
            disconnect();
        }
    }

    private void upload() throws CustomFtpExcetion
    {
        String remoteDirectory = this.ftpRequest.getRemoteFilePath().substring(0, this.ftpRequest.getRemoteFilePath().lastIndexOf("/"));
        createDirectory(remoteDirectory, null);
        changeWorkingDirectory(remoteDirectory, null);

        File localFile = new File(this.ftpRequest.getLocalFilePath());
        if (!localFile.exists() || localFile.length() <= 0)
            throw new CustomFtpExcetion("LocalFile not found.");

        FTPFile ftpFile = getRemoteFile(this.ftpRequest.getRemoteFilePath(), null);
        if (null != ftpFile && ftpFile.getSize() > localFile.length())
            throw new CustomFtpExcetion("RemoteFile not found.");

        try
        {
            if (ftpFile.getSize() > 0 && ftpFile.getSize() < localFile.length())
            {
                this.ftpClient.upload(localFile, ftpFile.getSize(), this.ftpDataTransferListener);
            } else
            {
                this.ftpClient.upload(localFile, this.ftpDataTransferListener);
            }
        } catch (Exception e)
        {
            throw new CustomFtpExcetion(e);
        }

    }

}
