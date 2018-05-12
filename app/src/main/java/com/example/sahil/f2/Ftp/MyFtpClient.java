package com.example.sahil.f2.Ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by hit4man47 on 1/3/2018.
 */

public class MyFtpClient
{

    private String TAG="FTP CLIENT";

    public FTPClient connect(String fullUrl)
    {
        FTPClient ftpClient=null;
        boolean status=false;
        String host,username,password;
        int port;
        ArrayList<String> details=extractFullUrl(fullUrl);
        host=details.get(0);
        port=Integer.valueOf(details.get(1));
        username=details.get(2);
        password=details.get(3);

        if(username.length()<1)
        {
            username = "anonymous";
            password = "";
        }
        Log.e(TAG,"Connecting......................................");
        Log.e(TAG,host+"    "+port+"    "+username+"    "+password);
        try
        {
            ftpClient = new FTPClient();
            // connecting to the host

            ftpClient.connect(host, port);

            showServerReply(ftpClient);
            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
            {
                // login using username & password
                //ftpClient.enterLocalPassiveMode();
                status = ftpClient.login(username, password);
                showServerReply(ftpClient);
				/*
				 * Set File Transfer Mode
				 *
				 * To avoid corruption issue you must specified a correct
				 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
				 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
				 * transferring text, image, and compressed files.
				 */
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error: could not connect to host " + host);
            status=false;
        }
        if(status)
        {
            return ftpClient;
        }
        else
        {
            return null;
        }
    }

    private void showServerReply(FTPClient ftpClient)
    {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0)
        {
            for (String aReply : replies)
            {
                Log.e(TAG, "SERVER REPLY: "+aReply);
            }
        }
    }


    public static ArrayList<String> extractFullUrl(String fullUrl)
    {
        String portNumber,host,username,password;
        int lastIndex;
        if(fullUrl.contains("@"))
        {
            lastIndex=fullUrl.lastIndexOf('@');
            String hostPort=fullUrl.substring(lastIndex+1);
            String userPass=fullUrl.substring(0,lastIndex);
            lastIndex=hostPort.lastIndexOf(':');
            host=hostPort.substring(0,lastIndex);
            portNumber=hostPort.substring(lastIndex+1);
            lastIndex=userPass.lastIndexOf(':');
            username=userPass.substring(6,lastIndex);
            password=userPass.substring(lastIndex+1);
        }
        else
        {
            lastIndex=fullUrl.lastIndexOf(':');
            host=fullUrl.substring(6,lastIndex);
            portNumber=fullUrl.substring(lastIndex+1);
            username="";
            password="";
        }

        ArrayList<String> details=new ArrayList<>();
        details.add(host);
        details.add(portNumber);
        details.add(username);
        details.add(password);

        return details;

    }


}
