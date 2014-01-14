package ftp4jstack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import ftp4j.FTPFile;

public class Ftp4jUploadHandler extends Ftp4jHandler
{

    protected Ftp4jUploadHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
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
        if (null != ftpFile && ftpFile.getSize() >= localFile.length())
            throw new CustomFtpExcetion("RemoteFile exists.");

        try
        {
            // if (null != ftpFile && ftpFile.getSize() < localFile.length())
            // {
            // this.bytesTotal = (int) (localFile.length() - ftpFile.getSize());
            // this.ftpClient.upload(localFile, ftpFile.getSize(),
            // this.ftpDataTransferListener);
            // } else
            // {
            // this.bytesTotal = (int) localFile.length();
            // this.ftpClient.upload(localFile, this.ftpDataTransferListener);
            //
            // int index = this.ftpRequest.getRemoteFilePath().lastIndexOf("/");
            // String newname = remoteDirectory +
            // this.ftpRequest.getRemoteFilePath().substring(index,
            // this.ftpRequest.getRemoteFilePath().length());
            // String oldname = remoteDirectory + File.separator +
            // localFile.getName();
            //
            // this.ftpClient.rename(oldname, newname);
            // }

            if (null != ftpFile && ftpFile.getSize() < localFile.length())
            {
                this.bytesTotal = (int) (localFile.length() - ftpFile.getSize());
                this.ftpClient.upload(this.ftpRequest.getRemoteFilePath(), new FileInputStream(localFile), ftpFile.getSize(), ftpFile.getSize(), this.ftpDataTransferListener);

            } else
            {
                this.bytesTotal = (int) localFile.length();
                this.ftpClient.upload(this.ftpRequest.getRemoteFilePath(), new BufferedInputStream(new FileInputStream(localFile), 4096), 0, 0, this.ftpDataTransferListener);
            }

        } catch (Exception e)
        {
            throw new CustomFtpExcetion(e);
        }

    }

}
