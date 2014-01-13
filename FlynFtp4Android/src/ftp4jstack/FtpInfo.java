package ftp4jstack;

public  class FtpInfo
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
