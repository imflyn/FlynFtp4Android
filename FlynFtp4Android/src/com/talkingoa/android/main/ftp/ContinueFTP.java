package com.talkingoa.android.main.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

/** */
/**
 * 支持断点续传的FTP实用类
 * 
 * @version 0.1 实现基本断点上传下载
 * @version 0.2 实现上传下载进度汇报
 * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
public class ContinueFTP
{
    public FTPClient ftpClient = new FTPClient();

    public ContinueFTP()
    {

        // 设置将过程中使用到的命令输出到控制台
        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

    }

    /**
     * /** 连接到FTP服务器
     * 
     * @param hostname
     *            主机名
     * @param port
     *            端口
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return 是否连接成功
     * @throws IOException
     */
    public boolean connect(String hostname, int port, String username, String password) throws IOException
    {
        ftpClient.connect(hostname, port);
        ftpClient.setControlEncoding("UTF-8");
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
        {
            if (ftpClient.login(username, password))
            {
                return true;
            }
        }
        disconnect();
        return false;
    }

    /** */
    /**
     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
     * 
     * @param remote
     *            远程文件路径
     * @param local
     *            本地文件路径
     * @return 上传的状态
     * @throws IOException
     */
    public DownloadStatus download(String remote, String local) throws IOException
    {
        // 设置被动模式
        ftpClient.enterLocalPassiveMode();
        // 设置以二进制方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        DownloadStatus result;

        // 检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
        if (files.length != 1)
        {
            System.out.println("远程文件不存在");
            return DownloadStatus.Remote_File_Noexist;
        }

        long lRemoteSize = files[0].getSize();
        File f = new File(local);
        // 本地存在文件，进行断点下载
        if (f.exists())
        {
            long localSize = f.length();
            // 判断本地文件大小是否大于远程文件大小
            if (localSize >= lRemoteSize)
            {
                System.out.println("本地文件大于远程文件，下载中止");
                return DownloadStatus.Local_Bigger_Remote;
            }

            // 进行断点续传，并记录状态
            FileOutputStream out = new FileOutputStream(f, true);
            ftpClient.setRestartOffset(localSize);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            long process = localSize / step;
            int c;
            try
            {
                while ((c = in.read(bytes)) != -1)
                {
                    out.write(bytes, 0, c);
                    localSize += c;
                    long nowProcess = localSize / step;
                    if (nowProcess > process)
                    {
                        process = nowProcess;
                        if (process % 10 == 0)
                            System.out.println("下载进度：" + process);
                        // TODO 更新文件下载进度,值存放在process变量中
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                return DownloadStatus.Download_From_Break_Failed;
            } finally
            {
                if (null != in)
                    in.close();
                if (null != out)
                    out.close();
            }
            boolean isDo = ftpClient.completePendingCommand();
            if (isDo)
            {
                result = DownloadStatus.Download_From_Break_Success;
            } else
            {
                result = DownloadStatus.Download_From_Break_Failed;
            }
        } else
        {
            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
            byte[] bytes = new byte[424];// 1024 /512一次读写字节数xudong 20131113
            long step = lRemoteSize / 100;
            long process = 0;
            long localSize = 0L;
            int c;
            try
            {
                while ((c = in.read(bytes)) != -1)
                {
                    out.write(bytes, 0, c);
                    localSize += c;
                    long nowProcess = localSize / step;
                    if (nowProcess > process)
                    {
                        process = nowProcess;
                        if (process % 10 == 0)
                            // System.out.println("下载进度：" + process);
                            Log.i("ContinueFTP", "下载进度:" + process);
                        // TODO 更新文件下载进度,值存放在process变量中
                    }
                }

            } catch (Exception e)
            {
                e.printStackTrace();
                return DownloadStatus.Download_New_Failed;
            } finally
            {
                if (null != in)
                    in.close();
                if (null != out)
                    out.close();
            }

            boolean upNewStatus = ftpClient.completePendingCommand();
            if (upNewStatus)
            {
                result = DownloadStatus.Download_New_Success;
            } else
            {
                result = DownloadStatus.Download_New_Failed;
            }
        }
        return result;
    }

    /** */
    /**
     * 上传文件到FTP服务器，支持断点续传
     * 
     * @param local
     *            本地文件名称，绝对路径
     * @param remote
     *            远程文件路径，使用/home/directory1/subdirectory/file.ext或是
     *            http://www.guihua.org /subdirectory/file.ext
     *            按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
     * @return 上传结果
     * @throws IOException
     */
    public UploadStatus upload(String local, String remote, FtpListener listener) throws IOException
    {
        // 设置PassiveMode传输
        ftpClient.enterLocalPassiveMode();
        // 设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setTcpNoDelay(true);
        ftpClient.setBufferSize(1048576);
        UploadStatus result;
        // 对远程目录的处理
        String remoteFileName = remote;
        if (remote.contains("/"))
        {
            remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
            // 创建服务器远程目录结构，创建失败直接返回
            if (CreateDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail)
            {

                Log.i("ContinueFTP", "创建服务器远程目录结构，创建失败直接返回:");
                return UploadStatus.Create_Directory_Fail;
            }
        }

        // 检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("UTF-8"), "iso-8859-1"));
        if (files.length == 1)
        {
            long remoteSize = files[0].getSize();
            File f = new File(local);
            long localSize = f.length();
            if (remoteSize == localSize)
            {
                Log.i("ContinueFTP", "UploadStatus.File_Exits:");
                return UploadStatus.File_Exits;
            } else if (remoteSize > localSize)
            {
                Log.i("ContinueFTP", "UploadStatus.Remote_Bigger_Local:");
                return UploadStatus.Remote_Bigger_Local;
            }

            // 尝试移动文件内读取指针,实现断点续传
            result = uploadFile(remoteFileName, f, ftpClient, remoteSize, listener);

            // 如果断点续传没有成功，则删除服务器上文件，重新上传
            if (result == UploadStatus.Upload_From_Break_Failed)
            {
                if (!ftpClient.deleteFile(remoteFileName))
                {
                    Log.i("ContinueFTP", "UploadStatus.Delete_Remote_Faild:");
                    return UploadStatus.Delete_Remote_Faild;
                }
                Log.i("ContinueFTP", "断点续传:");
                result = uploadFile(remoteFileName, f, ftpClient, 0, listener);
            }
        } else
        {
            Log.i("ContinueFTP", "没有断点续传:");
            result = uploadFile(remoteFileName, new File(local), ftpClient, 0, listener);
        }
        
        return result;
    }

    /** */
    /**
     * 断开与远程服务器的连接
     * 
     * @throws IOException
     */
    public void disconnect() throws IOException
    {
        if (ftpClient.isConnected())
        {
            ftpClient.disconnect();
        }
    }

    /** */
    /**
     * 递归创建远程服务器目录
     * 
     * @param remote
     *            远程服务器文件绝对路径
     * @param ftpClient
     *            FTPClient对象
     * @return 目录创建是否成功
     * @throws IOException
     */
    public UploadStatus CreateDirecroty(String remote, FTPClient ftpClient) throws IOException
    {
        UploadStatus status = UploadStatus.Create_Directory_Success;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(new String(directory.getBytes("UTF-8"), "iso-8859-1")))
        {
            // 如果远程目录不存在，则递归创建远程服务器目录
            int start = 0;
            int end = 0;
            if (directory.startsWith("/"))
            {
                start = 1;
            } else
            {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true)
            {
                String subDirectory = new String(remote.substring(start, end).getBytes("UTF-8"), "iso-8859-1");
                if (!ftpClient.changeWorkingDirectory(subDirectory))
                {
                    if (ftpClient.makeDirectory(subDirectory))
                    {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else
                    {
                        System.out.println("创建目录失败");
                        return UploadStatus.Create_Directory_Fail;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/", start);

                // 检查所有目录是否创建完毕
                if (end <= start)
                {
                    break;
                }
            }
        }
        return status;
    }

    /** */
    /**
     * 上传文件到服务器,新上传和断点续传
     * 
     * @param remoteFile
     *            远程文件名，在上传之前已经将服务器工作目录做了改变
     * @param localFile
     *            本地文件File句柄，绝对路径
     * @param processStep
     *            需要显示的处理进度步进值
     * @param ftpClient
     *            FTPClient引用
     * @return
     * @throws IOException
     */
    public UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize, FtpListener listener) throws IOException
    {
        UploadStatus status;
        // 显示进度的上传
        long step = localFile.length() / 100;
        long process = 0;
        long localreadbytes = 0L;
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("UTF-8"), "iso-8859-1"));
        Log.i("ContinueFTP", "上传进度OutputStream:");
        // 断点续传
        if (remoteSize > 0)
        {

            Log.i("ContinueFTP", "remoteSize > 0:");
            ftpClient.setRestartOffset(remoteSize);
            process = remoteSize / step;
            raf.seek(remoteSize);
            localreadbytes = remoteSize;
        }

        try
        {
            byte[] bytes = new byte[424];// 1024 /512一次读写字节数xudong 20131111
            int c;
            Log.i("ContinueFTP", "开始进度:");

            while ((c = raf.read(bytes)) != -1)
            {
                out.write(bytes, 0, c);
                localreadbytes += c;
                if (localreadbytes / step != process)
                {
                    process = localreadbytes / step;

                    int precent = (int) process;
                    if (null != listener && process % 10 == 0 && precent < 100)
                    {
                        listener.onRunning(precent);
                    }
                    Log.i("ContinueFTP", "上传进度:" + process);
                    // TODO 汇报上传状态
                    // //--xudong 20131018-超时返回------
                    // if(ChatActivity.isAlbumSendOverTime ||synchronized
                    // ChatActivity.isCameraOverTime
                    // || ChatActivity.isVoiceSendOverTime)
                    // {
                    // break;
                    // //return UploadStatus.Upload_New_File_Failed;
                    // }

                    // if(process >= 100){
                    //
                    // mContext.sendBroadcast(new Intent("FTPReflash"));
                    //
                    // }
                    // ----------------------

                }
            }
            out.flush();

        } catch (FileNotFoundException e)
        {

            e.printStackTrace();
            Log.e("ContinueFTP", "读写SD卡文件出错");
            return UploadStatus.Upload_New_File_Failed;

        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e("ContinueFTP", "读写SD卡文件出错");
            return UploadStatus.Upload_New_File_Failed;
        } finally
        {
            if (null != raf)
                raf.close();
            if (null != out)
                out.close();
        }

        Log.i("ContinueFTP", "下载完成:");
        boolean result = ftpClient.completePendingCommand();
        if (remoteSize > 0)
        {
            status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
        } else
        {
            status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
        }
        return status;
    }

    // public static void main(String[] args) {
    // ContinueFTP myFtp = new ContinueFTP();
    // try {
    // myFtp.connect("172.16.201.151", 7021, "acs", "acs@fonsview");
    // myFtp.ftpClient.makeDirectory(new String("视频文件".getBytes("UTF-8"),
    // "iso-8859-1"));
    // myFtp.ftpClient.changeWorkingDirectory(new String("视频文件"
    // .getBytes("UTF-8"), "iso-8859-1"));
    //
    // myFtp.upload("F:\\少女时代_Star.mp4",
    // "/视频/少女时代_Star.mp4");
    //
    //
    // /** 下载文件 **/
    // System.out.println(myFtp.download("/aaa/Blue hills.jpg",
    // "E:\\Blue hills.jpg"));
    // myFtp.disconnect();
    // } catch (IOException e) {
    // System.out.println("连接FTP出错：" + e.getMessage());
    // }
    // }

}
