package com.example.sahil.f2.Cache;

import android.app.Dialog;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 9/2/2017.
 */

public class wifiSendCache
{


    public static long totalsizetodownload=0;
    public static long progress=0;
    public static long downloadedsize=0;
    public static String currentFileName="calculating..";
    public static int currentFileNumber=0;
    public static int totalfiles=0;
    public static boolean isDownloadError=false;
    public static int downloadErrorCode=0;


    public static boolean isServiceRunning=false;
    public static boolean stopDownloadingPlease=false;
    public static int timeinsec=1;



    public static int download_counter=0;
    public static long olddownloadedsize=0;

    public static Runnable runnable=null;




    public static String toRootpath="";
    public static String fromRootpath="";
    public static int tostorageId=0;
    public static int fromStorageId=0;

    public static ArrayList<String> pathsList;
    public static ArrayList<String> namesList;
    public static ArrayList<Long> sizeLongList;
    public static ArrayList<Boolean> folderList;


    public static String log="";





    public static void clear()
    {
        //IMPORTANTS.......
        totalsizetodownload=0;
        progress=0;
        downloadedsize=0;
        currentFileName="calculating...";
        totalfiles=0;
        currentFileNumber=0;
        toRootpath="";
        fromRootpath="";
        tostorageId=0;
        fromStorageId=0;

        namesList= new ArrayList<>();
        sizeLongList=new ArrayList<>();
        pathsList=new ArrayList<>();
        folderList=new ArrayList<>();


        //NOT IMPORTANTS...SETTED AUTOMATICALLY BY THE DECIDER FUNCTION
        isDownloadError=false;
        downloadErrorCode=0;
        stopDownloadingPlease=false;
        isServiceRunning=false;
        timeinsec=1;
        download_counter=0;
        olddownloadedsize=0;
        runnable=null;

        log="";
    }

    public static void ClearTheShit()
    {

        isDownloadError=false;
        downloadErrorCode=0;
        stopDownloadingPlease=false;
        isServiceRunning=false;
        timeinsec=1;
        download_counter=0;
        olddownloadedsize=0;
        runnable=null;

    }


}

