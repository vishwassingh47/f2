package com.example.sahil.f2.Cache;

import android.app.Dialog;

import com.example.sahil.f2.Classes.MyFile;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 2/16/2018.
 */

public class InstallData
{
    public  int timeInSec=0;
    public  int currentFileIndex=0;
    public  int totalFiles=0;
    public int successCount=0;
    public int failedCount=0;
    public  long progress=0;
    public boolean isServiceRunning=false;
    public int pmOrUserOrSystem=0;
    public boolean pmFailed=false;
    public boolean rebootRequired=false;

    public Dialog dialog=null;
    public  Runnable runnable=null;
    public Boolean cancelPlease=false;

    public ArrayList<Boolean> isErrorList;
    public  ArrayList<MyFile> myFilesList;


    public void clear()
    {
        //IMPORTANTS.......
        timeInSec=0;
        progress=0;
        totalFiles=0;
        currentFileIndex=0;
        successCount=0;
        failedCount=0;
        cancelPlease=false;
        isServiceRunning=false;
        pmOrUserOrSystem=0;
        pmFailed=false;
        rebootRequired=false;

        isErrorList =new ArrayList<>();
        myFilesList= new ArrayList<>();

        dialog=null;
        runnable=null;

    }



}
