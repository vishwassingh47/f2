package com.example.sahil.f2.Classes;

import android.app.Dialog;



public class WiFiSendData
{
    public SerializablePacket serializablePacket;
    public Dialog dialog=null;
    public Runnable runnable=null;
    public boolean isServiceRunning=false;
    public boolean cancelDownloadingPlease=false;

    public int timeInSec=0;

    public long progress=0;
    public long downloadedSize=0;
    public String currentFileName="calculating..";
    public int currentFileIndex=0;
    public int totalFiles=0;
    public boolean isDownloadError=false;
    public int downloadErrorCode=-1;
    public String errorDetails="";
    public int downloadCounter=0;
    public long oldDownloadedSize=0;
    public String toParentPath="/sdcard/f2/wifi";

}
