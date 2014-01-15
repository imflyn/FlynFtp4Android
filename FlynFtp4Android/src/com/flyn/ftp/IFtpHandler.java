package com.flyn.ftp;

import java.io.Closeable;
import java.io.IOException;

public abstract class IFtpHandler
{
    abstract void doTaskWithRetries();

    abstract boolean isFinished();

    abstract boolean isCancelled();

    abstract boolean cancel();

    abstract void setUseSynchronousMode(boolean value);

    static void closeQuickly(Closeable stream)
    {
        if (null != stream)
            try
            {
                stream.close();
            } catch (IOException e)
            {
            }
    }
}
