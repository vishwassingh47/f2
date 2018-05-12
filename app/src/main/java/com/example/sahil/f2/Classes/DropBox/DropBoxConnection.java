package com.example.sahil.f2.Classes.DropBox;

import android.content.SharedPreferences;


import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.security.PublicKey;

/**
 * Created by hit4man47 on 8/17/2017.
 */

public class DropBoxConnection
{
    public static int index=-1;
    public static DbxClientV2 mDbxClient;

    public static final String APP_KEY = "s0hacfpq5knp90o";
    public static final String APP_SECRET = "efz8cw4dgth2rz6";


    public static String userKey=null;
    public static String userName=null;
    public static long totalSize=0;
    public static long usedSize=0;
    public static boolean isDropboxAvailable=false;
    public static boolean isDropboxConnected=false;
    public static boolean isDropboxConnecting=false;
    public static boolean isErrorConnecting=false;
    public static int progress=0;
    public static String accountId=null;

    public static void clear()
    {
        accountId=null;
        mDbxClient=null;
        index=-1;
        userKey=null;
        userName=null;
        totalSize=0;
        usedSize=0;
        isDropboxAvailable=false;
        isDropboxConnected=false;
        isErrorConnecting=false;
        isDropboxConnecting=false;
        progress=0;

    }

}
