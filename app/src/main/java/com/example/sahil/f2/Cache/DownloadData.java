package com.example.sahil.f2.Cache;

        import android.app.Dialog;
        import android.util.Log;

        import com.example.sahil.f2.Maintenance.TinyDB;
        import com.example.sahil.f2.OperationTheater.PasteClipBoard;

        import java.io.File;
        import java.util.ArrayList;

/**
 * Created by Acer on 14-08-2017.
 */

public class DownloadData
{
    private final String code;

    public  boolean isMoving=false;
    public  long totalSizeToDownload=0;
    public  long progress=0;
    public  long downloadedSize=0;
    public  String currentFileName="calculating..";
    public  int currentFileIndex=0;
    public  int totalFiles=0;
    public  boolean isDownloadError=false;
    public  int downloadErrorCode=-1;
    public  String errorDetails="";
    public  String toStoragePath="";
    public  String toRootPath="";
    public  String fromRootPath="";
    public  int toStorageId=0;
    public  int fromStorageId=0;

    public  Dialog dialog=null;
    public  Runnable runnable=null;
    public  boolean isServiceRunning=false;
    public  boolean cancelDownloadingPlease=false;
    public  boolean pauseDownloadingPlease=false;
    public  int timeInSec=1;
    public  int downloadCounter=0;
    public  long oldDownloadedSize=0;
    public  boolean isFastDownload=false;

    public  ArrayList<String> pathsList;
    public  ArrayList<String> namesList;
    public  ArrayList<Long> sizeLongList;
    public  ArrayList<Boolean> folderList;


    public DownloadData(String code)
    {
        this.code=code;
        clear();
    }


    public  void clear()
    {
        //IMPORTANTS.......
        isFastDownload=false;
        isMoving=false;
        totalSizeToDownload=0;
        progress=0;
        downloadedSize=0;
        currentFileName="calculating...";
        totalFiles=0;
        currentFileIndex=0;
        toRootPath="";
        fromRootPath="";
        toStorageId=0;
        fromStorageId=0;
        toStoragePath="";


        namesList= new ArrayList<>();
        sizeLongList=new ArrayList<>();
        pathsList=new ArrayList<>();
        folderList=new ArrayList<>();


        //NOT IMPORTANTS...SETTED AUTOMATICALLY BY THE DECIDER FUNCTION
        dialog=null;
        runnable=null;
        isServiceRunning=false;
        cancelDownloadingPlease=false;
        pauseDownloadingPlease=false;
        isDownloadError=false;
        errorDetails="";
        downloadErrorCode=-1;
        timeInSec=1;
        downloadCounter=0;
        oldDownloadedSize=0;


    }


    public  boolean getTinyDbData(TinyDB tinyDB)
    {
        clear();
        String currentPath=tinyDB.getString(code+"CurrentDestinationPath");
        boolean isDataAvailable=tinyDB.getString(code+"IsRunning").length()>0;
        if(isDataAvailable)
        {
            isFastDownload=tinyDB.getBoolean(code+"isFastDownload");
            isMoving=tinyDB.getBoolean(code+"isMoving");
            totalSizeToDownload=tinyDB.getLong(code+"totalSizeToDownload",0);
            progress=tinyDB.getLong(code+"progress",0);
            downloadedSize=tinyDB.getLong(code+"downloadedSize",0);
            currentFileName=tinyDB.getString(code+"currentFileName");
            currentFileIndex=tinyDB.getInt(code+"currentFileIndex");
            totalFiles=tinyDB.getInt(code+"totalFiles");
            toRootPath=tinyDB.getString(code+"toRootPath");
            fromRootPath=tinyDB.getString(code+"fromRootPath");
            toStorageId=tinyDB.getInt(code+"toStorageId");
            fromStorageId=tinyDB.getInt(code+"fromStorageId");
            toStoragePath=tinyDB.getString(code+"toStoragePath");


            pathsList=tinyDB.getListString(code+"pathsList");
            namesList=tinyDB.getListString(code+"namesList");
            sizeLongList=tinyDB.getListLong(code+"sizeLongList");
            folderList=tinyDB.getListBoolean(code+"folderList");


            Log.e(code+"index",currentFileIndex+"--"+currentPath+"--"+currentFileName);
        }
        return isDataAvailable;

    }

    public  void getPasterDataAndResetPasterAndTinyDB(TinyDB tinyDB)
    {
        clear();

        fromRootPath= PasteClipBoard.fromParentPath;
        fromStorageId= PasteClipBoard.fromStorageCode;

        toRootPath=PasteClipBoard.toRootPath;
        toStorageId=PasteClipBoard.toStorageCode;

        totalFiles= PasteClipBoard.nameList.size();
        if(PasteClipBoard.cutOrCopy==1)
        {
            isMoving=true;
        }
        isFastDownload=PasteClipBoard.isFastDownload;

        for(int i=0;i<totalFiles;i++)
        {
            totalSizeToDownload+= PasteClipBoard.sizeLongList.get(i);

            namesList.add(i,PasteClipBoard.nameList.get(i));
            sizeLongList.add(i,PasteClipBoard.sizeLongList.get(i));
            pathsList.add(i,PasteClipBoard.pathList.get(i));
            folderList.add(i,PasteClipBoard.isFolderList.get(i));
        }


        //resetting paster list
        PasteClipBoard.clear();



        //setting 1st time tinyDb data
        tinyDB.putBoolean(code+"isFastDownload",isFastDownload);
        tinyDB.putBoolean(code+"isMoving",isMoving);
        tinyDB.putLong(code+"totalSizeToDownload",totalSizeToDownload);
        tinyDB.putLong(code+"progress",progress);
        tinyDB.putLong(code+"downloadedSize",downloadedSize);
        tinyDB.putString(code+"currentFileName",currentFileName);
        tinyDB.putInt(code+"currentFileIndex",currentFileIndex);
        tinyDB.putInt(code+"totalFiles",totalFiles);
        tinyDB.putString(code+"toRootPath",toRootPath);
        tinyDB.putInt(code+"toStorageId",toStorageId);
        tinyDB.putString(code+"toStoragePath",toStoragePath);
        tinyDB.putString(code+"fromRootPath",fromRootPath);
        tinyDB.putInt(code+"fromStorageId",fromStorageId);


        tinyDB.putListString(code+"pathsList",pathsList);
        tinyDB.putListString(code+"namesList",namesList);
        tinyDB.putListLong(code+"sizeLongList",sizeLongList);
        tinyDB.putListBoolean(code+"folderList",folderList);



        tinyDB.remove(code+"IsRunning");
        tinyDB.remove(code+"CurrentDestinationPath");
        tinyDB.remove(code+"LastDestinationPath");



    }

}
