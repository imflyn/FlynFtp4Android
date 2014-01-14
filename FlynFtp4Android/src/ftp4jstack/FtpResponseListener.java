package ftp4jstack;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public abstract class FtpResponseListener implements Listener
{

    private static final String TAG              = FtpResponseListener.class.getName();

    private static final int    SUCCESS_MESSAGE  = 0;
    private static final int    FAILURE_MESSAGE  = 1;
    private static final int    START_MESSAGE    = 2;
    private static final int    FINISH_MESSAGE   = 3;
    private static final int    PROGRESS_MESSAGE = 4;
    private static final int    RETRY_MESSAGE    = 5;
    private static final int    CANCEL_MESSAGE   = 6;

    private Handler             handler;

    private boolean             useSynchronousMode;

    public FtpResponseListener()
    {
        postRunnable(null);
    }

    /**
     * Avoid leaks
     * 
     * @author V
     * 
     */
    static class InternalHandler extends Handler
    {
        private WeakReference<FtpResponseListener> mHandler;

        protected InternalHandler(FtpResponseListener ftpResponseHandler)
        {
            super(Looper.getMainLooper());
            this.mHandler = new WeakReference<FtpResponseListener>(ftpResponseHandler);
        }

        @Override
        public void handleMessage(Message msg)
        {
            FtpResponseListener ftpResponseHandler = this.mHandler.get();
            if (null != ftpResponseHandler)
            {
                ftpResponseHandler.handleMessage(msg);
            }

        }

    }

    protected void postRunnable(Runnable runnable)
    {
        boolean missingLooper = null == Looper.getMainLooper();
        if (missingLooper)
        {
            Looper.prepare();
        }
        if (null == this.handler)
        {
            this.handler = new InternalHandler(this);
        }
        if (null != runnable)
        {
            this.handler.post(runnable);
        }
        if (missingLooper)
        {
            Looper.loop();
        }
    }

    private void sendMessage(Message msg)
    {
        if (getUseSynchronousMode() || this.handler == null)
        {
            handleMessage(msg);
        } else if (!Thread.currentThread().isInterrupted())
        {
            this.handler.sendMessage(msg);
        }
    }

    private Message obtainMessage(int messageId, Object messageData)
    {
        Message msg;
        if (this.handler != null)
        {
            msg = this.handler.obtainMessage(messageId, messageData);
        } else
        {
            msg = Message.obtain();
            if (null != msg)
            {
                msg.what = messageId;
                if (null != messageData)
                    msg.obj = messageData;
            }
        }
        return msg;
    }

    private void handleMessage(Message msg)
    {
        Object[] messageData;
        switch (msg.what)
        {
            case START_MESSAGE:
                onStart();
                break;
            case SUCCESS_MESSAGE:
                onSuccess();
                break;
            case RETRY_MESSAGE:
                messageData = (Object[]) msg.obj;
                if (null != messageData && messageData.length == 1)
                    onRetry((Integer) messageData[0]);
                else
                    Log.i(TAG, "RETRY_MESSAGE didn't get enough params.");
                break;
            case PROGRESS_MESSAGE:
                messageData = (Object[]) msg.obj;
                if (null != messageData && messageData.length >= 3)
                {
                    try
                    {
                        onProgress((Integer) messageData[0], (Integer) messageData[1], (Integer) messageData[2]);
                    } catch (Throwable t)
                    {
                        Log.e(TAG, "Custom onProgress contains an error", t);
                    }
                } else
                    Log.e(TAG, "PROGRESS_MESSAGE didn't got enough params.");
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
            case FAILURE_MESSAGE:
                messageData = (Object[]) msg.obj;
                if (messageData != null && messageData.length == 1)
                    onFailure((Throwable) messageData[0]);
                else
                    Log.e(TAG, "FAILURE_MESSAGE didn't get enough params.");
                break;
            case CANCEL_MESSAGE:
                onCancel();
                break;
        }
    }

    protected void sendStartMessage()
    {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    protected void sendFailuerMessage(Throwable error)
    {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { error }));
    }

    protected void sendSuccessMessage()
    {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, null));
    }

    protected void sendCancelMessage()
    {
        sendMessage(obtainMessage(CANCEL_MESSAGE, null));
    }

    protected void sendRetryMessage(int retryNo)
    {
        sendMessage(obtainMessage(retryNo, new Object[] { retryNo }));
    }

    protected void sendFinishMessage()
    {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }

    protected void sendProgressMessage(int bytesWritten, int bytesTotal, int speed)
    {
        sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[] { bytesWritten, bytesTotal, speed }));
    }

    public boolean getUseSynchronousMode()
    {
        return this.useSynchronousMode;
    }

    public void setUseSynchronousMode(boolean value)
    {
        this.useSynchronousMode = value;
    }

    @Override
    public abstract void onSuccess();

    @Override
    public abstract void onFailure(Throwable error);

    @Override
    public void onStart()
    {

    }

    @Override
    public void onFinish()
    {

    }

    @Override
    public void onProgress(int bytesWritten, int bytesTotal, int speed)
    {

    }

    @Override
    public void onRetry(int retryNo)
    {

    }

    @Override
    public void onCancel()
    {

    }

}
