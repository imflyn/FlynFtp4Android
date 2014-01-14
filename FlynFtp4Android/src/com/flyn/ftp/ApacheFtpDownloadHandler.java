package com.flyn.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

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
        FTPFile ftpFile = getRemoteFile(this.ftpRequest.getRemoteFilePath(), null);

        if (null == ftpFile || ftpFile.getSize() <= 0)
            throw new CustomFtpExcetion("RemoteFile not found.");

        File localFile = new File(this.ftpRequest.getLocalFilePath());
        File tempFile = new File(this.ftpRequest.getLocalFilePath().substring(0, this.ftpRequest.getLocalFilePath().lastIndexOf(".")) + ".tmp");

        if (localFile.exists())
            throw new CustomFtpExcetion("LocalFile already exists.");
        else if (tempFile.exists() && tempFile.length() >= ftpFile.getSize())
            throw new CustomFtpExcetion("TempFile already exists but it has error size.");
        else if (!localFile.exists() && !tempFile.exists())
            tempFile.getParentFile().mkdirs();

        boolean result = false;
        InputStream inputStream = null;
        RandomAccessFile outputStream = null;
        try
        {

            inputStream = new BufferedInputStream(this.ftpClient.retrieveFileStream(this.ftpRequest.getRemoteFilePath()));
            outputStream = new RandomAccessFile(tempFile, "rw");

            if (localFile.exists() && localFile.length() > 0)
            {
                this.bytesTotal = (int) ftpFile.getSize();
                this.bytesWritten = (int) tempFile.length();
                this.ftpClient.setRestartOffset(ftpFile.getSize());
                outputStream.seek(tempFile.length());
            } else
            {
                this.bytesTotal = (int) ftpFile.getSize();
                tempFile.createNewFile();
            }

            int count;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while ((count = inputStream.read(buffer)) != -1 && !isCancelled())
            {
                outputStream.write(buffer, 0, count);
                updateProgress(count);
            }

        } catch (FileNotFoundException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (NullPointerException e)
        {
            throw new CustomFtpExcetion(e);
        } finally
        {
            if (null != inputStream)
                try
                {
                    inputStream.close();
                } catch (IOException e)
                {
                    // ingore
                }
            if (null != outputStream)
                try
                {
                    outputStream.close();
                } catch (IOException e)
                {
                    // ingore
                }
        }
        try
        {
            result = this.ftpClient.completePendingCommand();

        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        }
        if (!result)
            throw new CustomFtpExcetion("Download file from ftp failed.");
        else
            tempFile.renameTo(localFile);
    }

}
