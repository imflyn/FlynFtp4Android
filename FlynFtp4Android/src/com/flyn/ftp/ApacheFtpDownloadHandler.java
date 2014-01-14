package com.flyn.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;

public class ApacheFtpDownloadHandler extends ApacheFtpHandler
{

    protected ApacheFtpDownloadHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
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

        File localFile = new File(this.ftpRequest.getLocalFilePath());
        File tempFile = new File(this.ftpRequest.getLocalFilePath().substring(0, this.ftpRequest.getLocalFilePath().lastIndexOf(".")) + ".tmp");

        if (localFile.exists())
            throw new CustomFtpExcetion("LocalFile already exists.");
        else if (tempFile.exists() && tempFile.length() >= ftpFile.getSize())
            throw new CustomFtpExcetion("TempFile already exists but it has error size.");
        else if (!localFile.exists() && !tempFile.exists())
            tempFile.getParentFile().mkdirs();

        boolean result = false;
        try
        {

            if (localFile.exists() && localFile.length() > 0)
            {
                this.bytesTotal = (int) (ftpFile.getSize() - tempFile.length());
                this.ftpClient.setRestartOffset(tempFile.length());

            } else
            {
                this.ftpClient.setRestartOffset(0);
                this.bytesTotal = (int) ftpFile.getSize();
            }

            result = this.ftpClient.retrieveFile(this.ftpRequest.getRemoteFilePath(), new BufferedOutputStream(new FileOutputStream(tempFile), DEFAULT_BUFFER_SIZE));

        } catch (FileNotFoundException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (NullPointerException e)
        {
            throw new CustomFtpExcetion(e);
        }
        if (!result)
            throw new CustomFtpExcetion("Download file from ftp failed.");
    }

}
