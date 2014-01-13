package com.flyn.sample;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.flyn.ftp4android.R;

import ftp4jstack.FtpRequest;
import ftp4jstack.FtpStack;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    private void upload()
    {

    }

    private void download()
    {
        try
        {
            File file=new File( Environment.getExternalStorageDirectory() + File.separator+"yyj" + File.separator+"aa.jpg");
            file.delete();
        } catch (Exception e)
        {
        }
        
        long time=System.currentTimeMillis();
        FtpStack.download(
                new FtpRequest(new FtpRequest.FtpInfo("ftp.talkingoa.com", 21, "imuser", "imuser", null), Environment.getExternalStorageDirectory() + File.separator+"yyj" + File.separator+"aa.jpg",
                        "/var/ftp/imuser/android/image/2014_01_13/1389600666136_0113161137.jpg", false), null).start(true);
        System.out.println("总耗时:"+(System.currentTimeMillis()-time));
    
    }
}
