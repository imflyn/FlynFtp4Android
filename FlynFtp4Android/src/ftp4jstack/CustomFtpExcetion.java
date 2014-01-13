package ftp4jstack;

public class CustomFtpExcetion extends Exception
{

    private static final long serialVersionUID = 1L;

    public CustomFtpExcetion()
    {
        super();
    }

    public CustomFtpExcetion(Throwable cause)
    {
        super(cause);
    }
    public CustomFtpExcetion(String exceptionMessage)
    {
        super(exceptionMessage);
    }

    public CustomFtpExcetion(String exceptionMessage, Throwable reason)
    {
        super(exceptionMessage, reason);
    }
}
