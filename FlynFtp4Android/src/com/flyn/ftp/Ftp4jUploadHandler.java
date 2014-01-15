package com.flyn.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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

        File localFile = new File(this.ftpRequest.getLocalFilePath());
        if (!localFile.exists() || localFile.length() <= 0)
            throw new CustomFtpExcetion("LocalFile not found.");

        String remoteDirectory = this.ftpRequest.getRemoteFilePath().substring(0, this.ftpRequest.getRemoteFilePath().lastIndexOf("/"));
        createDirectory(remoteDirectory, null);
        changeWorkingDirectory(remoteDirectory, null);

        FTPFile ftpFile = getRemoteFile(this.ftpRequest.getRemoteFilePath(), null);
        if (null != ftpFile && ftpFile.getSize() >= localFile.length())
            throw new CustomFtpExcetion("RemoteFile exists.");
        
        InputStream inputStream= null;
        try
        {
            inputStream= new BufferedInputStream(new FileInputStream(localFile), 4096);
            this.bytesTotal = (int) localFile.length();
            if (null != ftpFile && ftpFile.getSize() < localFile.length())
            {
                this.bytesWritten = (int) ftpFile.getSize();
                this.ftpClient.append(this.ftpRequest.getRemoteFilePath(),inputStream, ftpFile.getSize(), this.ftpDataTransferListener);
            } else
            {
                this.ftpClient.upload(this.ftpRequest.getRemoteFilePath(), inputStream, 0, 0, this.ftpDataTransferListener);
            }

        } catch (Exception e)
        {
            throw new CustomFtpExcetion(e);
        }finally
        {
            IFtpHandler.closeQuickly(inputStream);
        }

    }

}
