package ftp4jstack;

public interface FtpStatus
{
    int CONNECT_SUCCESS       = 0;
    int CONNECT_FAILURE       = 1;
    int LOGIN_SUCCESS         = 2;
    int LOGIN_FAILURE         = 3;
    int REMOTE_FILE_NOTEXISTS = 4;
    int REMOTE_FILE_EXISTS    = 5;
    int LOCAL_FILE_EXISTS     = 6;
    int LOCAL_FILE_NOTEXISTS  = 7;
    int DOWNLOAD_SUCCESS      = 8;
    int DOWNLOAD_FAILURE      = 9;
    int UPLOAD_SUCCESS      = 8;
    int UPLOAD_FAILURE      = 9;
}
