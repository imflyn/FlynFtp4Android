package com.flyn.ftp;

public abstract class IFtpHandler
{
    abstract void doTaskWithRetries();

    abstract boolean isFinished();

    abstract boolean isCancelled();

    abstract boolean cancel();

    abstract void setUseSynchronousMode(boolean value);
}
  