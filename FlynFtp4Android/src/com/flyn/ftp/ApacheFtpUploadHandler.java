package com.flyn.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPFile;

public class ApacheFtpUploadHandler extends ApacheFtpHandler
{

    protected ApacheFtpUploadHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        super(ftpRequest, ftpResponseHandler);
    }

    @Override
    protected void doTask() throws CustomFtpExcetion
    {
        File localFile = new File(this.ftpRequest.getLocalFilePath());
        if (!localFile.exists() || localFile.length() <= 0)
            throw new CustomFtpExcetion("LocalFile not found.");

        String remoteDirectory = this.ftpRequest.getRemoteFilePath().substring(0, this.ftpRequest.getRemoteFilePath().lastIndexOf("/"));
        createDirectory(remoteDirectory, null);
        changeWorkingDirectory(remoteDirectory, null);

        FTPFile ftpFile = getRemoteFile(this.ftpRequest.getRemoteFilePath(), null);
        if (null != ftpFile && ftpFile.getSize() >= localFile.length())
            throw new CustomFtpExcetion("RemoteFile exists.");

        boolean result = false;
        try
        {

            if (null != ftpFile && ftpFile.getSize() < localFile.length())
            {
                this.bytesTotal = (int) (localFile.length() - ftpFile.getSize());
                this.ftpClient.setRestartOffset(ftpFile.getSize());

            } else
            {
                this.bytesTotal = (int) localFile.length();
                this.ftpClient.setRestartOffset(0);
            }
            result = this.ftpClient.appendFile(this.ftpRequest.getRemoteFilePath(), new BufferedInputStream(new FileInputStream(localFile), DEFAULT_BUFFER_SIZE));

        } catch (Exception e)
        {
            throw new CustomFtpExcetion(e);
        }

        if (!result)
            throw new CustomFtpExcetion("Upload file to ftp failed");
    }

}
