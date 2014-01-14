package com.flyn.sample;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flyn.ftp.FtpInfo;
import com.flyn.ftp.FtpRequest;
import com.flyn.ftp.FtpResponseListener;
import com.flyn.ftp.FtpStack;
import com.flyn.ftp4android.R;

public class MainActivity extends Activity
{

    private TextView    upload;
    private TextView    download;
    private ProgressBar uploadPb;
    private ProgressBar downPb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upload = (TextView) findViewById(R.id.textView1);
        download = (TextView) findViewById(R.id.textView2);
        uploadPb = (ProgressBar) findViewById(R.id.progressBar1);
        downPb = (ProgressBar) findViewById(R.id.progressBar2);

        findViewById(R.id.button1).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {

                        upload();
                    }
                }).start();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        download();
                    }
                }).start();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                    }
                }).start();
            }
        });

        findViewById(R.id.button4).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                    }
                }).start();
            }
        });
    }

    private void upload()
    {
        final long time = System.currentTimeMillis();
        FtpStack.ftp4jUpload(
                new FtpRequest(new FtpInfo("ftp.talkingoa.com", 21, "imuser", "imuser", null), Environment.getExternalStorageDirectory() + File.separator + "yyj" + File.separator + "aa.jpg",
                        "/var/ftp/imuser/android/image/2014_01_13/testFtp4j.jpg", false), new FtpResponseListener()
                {

                    @Override
                    public void onSuccess()
                    {
                        upload.setText("完成");
                        System.out.println("总耗时:" + (System.currentTimeMillis() - time));
                        uploadPb.setMax(2);
                        uploadPb.setProgress(2);
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        error.printStackTrace();

                    }

                    @Override
                    public void onProgress(int bytesWritten, int bytesTotal, int speed)
                    {
                        System.out.println("bytesWritten:" + bytesWritten);
                        System.out.println("bytesTotal:" + bytesTotal);
                        System.out.println("currentSpeed:" + speed);
                        upload.setText(String.valueOf(speed));
                        uploadPb.setMax(bytesTotal);
                        uploadPb.setProgress(bytesWritten);
                    }
                }).start(false);

    }

    private void download()
    {
        final long time = System.currentTimeMillis();
        try
        {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "yyj" + File.separator + "aa.jpg");
            file.delete();
        } catch (Exception e)
        {
        }

        FtpStack.ftp4jDownload(
                new FtpRequest(new FtpInfo("ftp.talkingoa.com", 21, "imuser", "imuser", null), Environment.getExternalStorageDirectory() + File.separator + "yyj" + File.separator + "aa.jpg",
                        "/var/ftp/imuser/android/image/2014_01_13/1389600666136_0113161137.jpg", false), new FtpResponseListener()
                {

                    @Override
                    public void onSuccess()
                    {
                        download.setText("完成");
                        System.out.println("总耗时:" + (System.currentTimeMillis() - time));
                        downPb.setMax(2);
                        downPb.setProgress(2);
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        error.printStackTrace();

                    }

                    @Override
                    public void onProgress(int bytesWritten, int bytesTotal, int speed)
                    {

                        download.setText(String.valueOf(speed));
                        downPb.setMax(bytesTotal);
                        downPb.setProgress(bytesWritten);
                    }
                }).start(false);

    }
}
