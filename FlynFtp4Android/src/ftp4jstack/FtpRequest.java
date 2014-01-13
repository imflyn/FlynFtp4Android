package ftp4jstack;

public class FtpRequest
{
    private FtpInfo ftpInfo;
    private String  localFilePath;
    private String  remoteFilePath;
    private boolean ifRetry;

    public FtpRequest(FtpInfo ftpInfo, String localFilePath, String remoteFilePath, boolean ifRetry)
    {
        super();
        this.ftpInfo = ftpInfo;
        this.localFilePath = localFilePath;
        this.remoteFilePath = remoteFilePath;
        this.ifRetry = ifRetry;
    }

    public final FtpInfo getFtpInfo()
    {
        return ftpInfo;
    }

    public final void setFtpInfo(FtpInfo ftpInfo)
    {
        this.ftpInfo = ftpInfo;
    }

    public final String getLocalFilePath()
    {
        return localFilePath;
    }

    public final void setLocalFilePath(String localFilePath)
    {
        this.localFilePath = localFilePath;
    }

    public final String getRemoteFilePath()
    {
        return remoteFilePath;
    }

    public final void setRemoteFilePath(String remoteFilePath)
    {
        this.remoteFilePath = remoteFilePath;
    }

    public final boolean isIfRetry()
    {
        return ifRetry;
    }

    public final void setIfRetry(boolean ifRetry)
    {
        this.ifRetry = ifRetry;
    }

    public static class FtpInfo
    {
        private String host;
        private int    port;
        private String username;
        private String password;
        private String account;

        public FtpInfo(String host, int port, String username, String password, String account)
        {
            super();
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.account = account;
        }

        public final String getHost()
        {
            return host;
        }

        public final void setHost(String host)
        {
            this.host = host;
        }

        public final int getPort()
        {
            return port;
        }

        public final void setPort(int port)
        {
            this.port = port;
        }

        public final String getUsername()
        {
            return username;
        }

        public final void setUsername(String username)
        {
            this.username = username;
        }

        public final String getPassword()
        {
            return password;
        }

        public final void setPassword(String password)
        {
            this.password = password;
        }

        public final String getAccount()
        {
            return account;
        }

        public final void setAccount(String account)
        {
            this.account = account;
        }

    }
}
