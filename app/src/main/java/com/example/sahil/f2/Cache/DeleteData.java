package com.example.sahil.f2.Cache;

import android.app.Dialog;

import com.example.sahil.f2.Classes.MyContainer;
import com.example.sahil.f2.Classes.MyFile;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/25/2017.
 */

public class DeleteData
{
    public  int timeInSec=0;
    public  boolean shouldRecycle=false;
    public  long totalSizeToDownload=0;
    public  long progress=0;
    public  long downloadedSize=0;
    public  String currentFileName="calculating..";
    public  int currentFileIndex=0;
    public  int totalFiles=0;
    public  boolean isDownloadError=false;
    public  int downloadErrorCode=-1;
    public  String errorDetails="";
    public  int fromStorageId=0;

    public  Dialog dialog=null;
    public  Runnable runnable=null;
    public  boolean isServiceRunning=false;
    public  boolean cancelDownloadingPlease=false;

    public  ArrayList<Boolean> isErrorList;
    public  ArrayList<String> pathsList;
    public  ArrayList<String> namesList;
    public  ArrayList<Long> sizeLongList;
    public  ArrayList<Boolean> folderList;


    public ArrayList<MyFile> myFilesToDeleteList;
    public  ArrayList<MyContainer> deleteFromWhichContainer;

    public DeleteData()
    {
        clear();
    }

    public void clear()
    {
        //IMPORTANTS.......
        shouldRecycle=false;
        totalSizeToDownload=0;
        timeInSec=0;
        progress=0;
        downloadedSize=0;
        currentFileName="calculating...";
        totalFiles=0;
        currentFileIndex=0;
        fromStorageId=0;

        myFilesToDeleteList=new ArrayList<>();
        deleteFromWhichContainer=new ArrayList<>();

        isErrorList =new ArrayList<>();
        namesList= new ArrayList<>();
        sizeLongList=new ArrayList<>();
        pathsList=new ArrayList<>();
        folderList=new ArrayList<>();



        //NOT IMPORTANTS...SETTED AUTOMATICALLY BY THE DECIDER FUNCTION
        dialog=null;
        runnable=null;
        isServiceRunning=false;
        cancelDownloadingPlease=false;
        isDownloadError=false;
        errorDetails="";
        downloadErrorCode=-1;
    }

}
