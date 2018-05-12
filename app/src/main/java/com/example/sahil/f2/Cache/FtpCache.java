package com.example.sahil.f2.Cache;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.ftpserver.FtpServer;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 1/3/2018.
 */

public class FtpCache
{
    public static ArrayList<String> ftpList=new ArrayList<>();
    public static FTPClient mFTPClient=null;
    public static FtpServer mFTPServer=null;
    public static String currentFullUrl=null;
    public static String serverUserName=null,serverPassword=null;
    public static int serverPortNumber=4000;
    public static boolean readOnly=true;
    public static String scannedQR=null;

    public static void clear()
    {
        ftpList.clear();
        mFTPClient=null;
        currentFullUrl=null;
        mFTPServer=null;
        serverUserName=null;
        serverPassword=null;
        serverPortNumber=4000;
        readOnly=true;
        scannedQR=null;
    }
}
