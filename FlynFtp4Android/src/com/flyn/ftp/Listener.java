package com.flyn.ftp;

public interface Listener
{

    void onStart();

    void onFinish();

    void onProgress(int bytesWritten, int bytesTotal, int speed);

    void onRetry(int retryNo);

    void onCancel();

    void onSuccess();

    void onFailure(Throwable error);

}