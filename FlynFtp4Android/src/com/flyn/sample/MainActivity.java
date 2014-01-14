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

    private TextView    tv_4jupload;
    private TextView    tv_4jdownload;
    private ProgressBar pb_4jupload;
    private ProgressBar pb_4jdownload;
    private TextView    tv_apacheupload;
    private TextView    tv_apachedownload;
    private ProgressBar pb_apacheupload;
    private ProgressBar pb_apachedownload;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_4jupload = (TextView) findViewById(R.id.tv_ftp4jupload);
        tv_4jdownload = (TextView) findViewById(R.id.tv_ftp4jdownload);
        pb_4jupload = (ProgressBar) findViewById(R.id.pb_ftp4jupload);
        pb_4jdownload = (ProgressBar) findViewById(R.id.pb_ftp4jdownload);
        tv_apacheupload = (TextView) findViewById(R.id.tv_apacheupload);
        tv_apachedownload = (TextView) findViewById(R.id.tv_apachedownload);
        pb_apacheupload = (ProgressBar) findViewById(R.id.pb_apacheupload);
        pb_apachedownload = (ProgressBar) findViewById(R.id.pb_apachedownload);

        findViewById(R.id.btn_ftp4jupload).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {

                        jupload();
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_ftp4jdownload).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        jdownload();
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_apacheupload).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        aupload();
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_apachedownload).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        adownload();
                    }
                }).start();
            }
        });

    }

    private void jupload()
    {
        final long time = System.currentTimeMillis();
        FtpStack.ftp4jUpload(
                new FtpRequest(new FtpInfo("ftp.talkingoa.com", 21, "imuser", "imuser", null), Environment.getExternalStorageDirectory() + File.separator + "yyj" + File.separator + "aa.jpg",
                        "/var/ftp/imuser/android/image/2014_01_13/testFtp4j.jpg", false), new FtpResponseListener()
                {

                    @Override
                    public void onSuccess()
                    {
                        tv_4jupload.setText("完成");
                        System.out.println("总耗时:" + (System.currentTimeMillis() - time));
                        pb_4jupload.setMax(2);
                        pb_4jupload.setProgress(2);
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
                        tv_4jupload.setText(String.valueOf(speed));
                        pb_4jupload.setMax(bytesTotal);
                        pb_4jupload.setProgress(bytesWritten);
                    }
                }).start(false);

    }

    private void jdownload()
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
                        tv_4jdownload.setText("完成");
                        System.out.println("总耗时:" + (System.currentTimeMillis() - time));
                        pb_4jdownload.setMax(2);
                        pb_4jdownload.setProgress(2);
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        error.printStackTrace();

                    }

                    @Override
                    public void onProgress(int bytesWritten, int bytesTotal, int speed)
                    {

                        tv_4jdownload.setText(String.valueOf(speed));
                        pb_4jdownload.setMax(bytesTotal);
                        pb_4jdownload.setProgress(bytesWritten);
                    }
                }).start(false);

    }

    private void aupload()
    {
        final long time = System.currentTimeMillis();
        FtpStack.apacheUpload(
                new FtpRequest(new FtpInfo("ftp.talkingoa.com", 21, "imuser", "imuser", null), Environment.getExternalStorageDirectory() + File.separator + "yyj" + File.separator + "aa.jpg",
                        "/var/ftp/imuser/android/image/2014_01_13/testFtp4j.jpg", false), new FtpResponseListener()
                {

                    @Override
                    public void onSuccess()
                    {
                        tv_apacheupload.setText("完成==" + "总耗时:" + (System.currentTimeMillis() - time));
                        pb_apacheupload.setMax(2);
                        pb_apacheupload.setProgress(2);
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
                        tv_apacheupload.setText(String.valueOf(speed));
                        pb_apacheupload.setMax(bytesTotal);
                        pb_apacheupload.setProgress(bytesWritten);
                    }
                }).start(false);

    }

    private void adownload()
    {
        final long time = System.currentTimeMillis();
        try
        {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "yyj" + File.separator + "aa.jpg");
            file.delete();
        } catch (Exception e)
        {
        }

        FtpStack.apacheDownload(
                new FtpRequest(new FtpInfo("ftp.talkingoa.com", 21, "imuser", "imuser", null), Environment.getExternalStorageDirectory() + File.separator + "yyj" + File.separator + "aa.jpg",
                        "/var/ftp/imuser/android/image/2014_01_13/1389600666136_0113161137.jpg", false), new FtpResponseListener()
                {

                    @Override
                    public void onSuccess()
                    {
                        tv_apachedownload.setText("完成==" + "总耗时:" + (System.currentTimeMillis() - time));
                        System.out.println("总耗时:" + (System.currentTimeMillis() - time));
                        pb_apachedownload.setMax(2);
                        pb_apachedownload.setProgress(2);
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        error.printStackTrace();

                    }

                    @Override
                    public void onProgress(int bytesWritten, int bytesTotal, int speed)
                    {

                        tv_apachedownload.setText(String.valueOf(speed));
                        pb_apachedownload.setMax(bytesTotal);
                        pb_apachedownload.setProgress(bytesWritten);
                    }
                }).start(false);

    }
}
