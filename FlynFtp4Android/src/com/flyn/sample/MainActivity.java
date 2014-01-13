package com.flyn.sample;

import com.yyj.ftp4jdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

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

            }
        });

        findViewById(R.id.button2).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

            }
        });
    }

}
