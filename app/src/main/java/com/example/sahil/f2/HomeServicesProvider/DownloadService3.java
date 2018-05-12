package com.example.sahil.f2.HomeServicesProvider;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.NotificationCompat;
import android.util.Log;


import com.example.sahil.f2.Cache.DownloadData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;

import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.Utilities.DeleteUtils;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Acer on 14-08-2017.
 */

public class DownloadService3 extends Service
{
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;

    private Handler handler;
    private Runnable runnable;
    private String CurrentDestinationPath,LastDestinationPath;

    boolean updateNameList=false;

    private String TDBCurrentDestinationPath,TDBLastDestinationPath,TDBcurrentFileName;
    private int TDBcurrentFileIndex;
    private long TDBprogress,TDBdownloadedSize;

    HelpingBot helpingBot;
    boolean isSpace=true;

    int operationCode=203;
    DownloadData downloadData;
    
    final String TAG=operationCode+"_SERVICE";
    int oldTime=-1;

    public static Thread myOperationThread;
    public ArrayList<String> deleteList;
    private DeleteUtils deleteUtils;

    TinyDB tinyDB;


    @Override
    public int onStartCommand(Intent intent, int flags, final int startid)
    {

        Log.i(TAG,"service called");

        helpingBot=new HelpingBot();
        Intent myIntent;
        PendingIntent myPendingIntent;
        deleteUtils=new DeleteUtils(null,getApplicationContext());

        myIntent= new Intent(this, MainActivity.class);
        myIntent.putExtra("notification",5);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myPendingIntent=PendingIntent.getActivity(getApplicationContext(),operationCode,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        mNotifyManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.search_icon)
                .setContentTitle("Downloading from Google Drive")
                .setContentText("Calculating...")
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(myPendingIntent)
                .setProgress(100,0,true);
        mNotifyManager.notify(operationCode,mBuilder.build());

        deleteList=new ArrayList<>();


        TDBCurrentDestinationPath=tinyDB.getString(operationCode+"CurrentDestinationPath");
        TDBLastDestinationPath=tinyDB.getString(operationCode+"LastDestinationPath");
        TDBcurrentFileName=tinyDB.getString(operationCode+"currentFileName");
        TDBcurrentFileIndex=tinyDB.getInt(operationCode+"currentFileIndex");
        TDBprogress=tinyDB.getLong(operationCode+"progress",0);
        TDBdownloadedSize=tinyDB.getLong(operationCode+"downloadedSize",0);


        CurrentDestinationPath=TDBCurrentDestinationPath;
        LastDestinationPath=TDBLastDestinationPath;

        myOperationThread=new Thread()
        {
            @Override
            public void run()
            {
                downloadTask();
            }
        };
        myOperationThread.start();


        handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {

                if(downloadData.currentFileIndex!=TDBcurrentFileIndex)
                {
                    TDBcurrentFileIndex=downloadData.currentFileIndex;
                    tinyDB.putInt(operationCode+"currentFileIndex",TDBcurrentFileIndex);
                }
                if(!downloadData.currentFileName.equals(TDBcurrentFileName))
                {
                    TDBcurrentFileName=downloadData.currentFileName;
                    tinyDB.putString(operationCode+"currentFileName",TDBcurrentFileName);
                }
                if(downloadData.progress!=TDBprogress)
                {
                    TDBprogress=downloadData.progress;
                    tinyDB.putLong(operationCode+"progress",TDBprogress);
                }
                if(downloadData.downloadedSize!=TDBdownloadedSize)
                {
                    TDBdownloadedSize=downloadData.downloadedSize;
                    tinyDB.putLong(operationCode+"downloadedSize",TDBdownloadedSize);
                }
                if(!CurrentDestinationPath.equals(TDBCurrentDestinationPath))
                {
                    TDBCurrentDestinationPath=CurrentDestinationPath;
                    tinyDB.putString(operationCode+"CurrentDestinationPath",TDBCurrentDestinationPath);
                }
                if(!LastDestinationPath.equals(TDBLastDestinationPath))
                {
                    TDBLastDestinationPath=LastDestinationPath;
                    tinyDB.putString(operationCode+"LastDestinationPath",TDBLastDestinationPath);
                }

                if(updateNameList)
                {
                    updateNameList=false;
                    tinyDB.putListString(operationCode+"namesList",downloadData.namesList);
                }


                if(downloadData.isServiceRunning )
                {
                    if(!downloadData.cancelDownloadingPlease && !downloadData.pauseDownloadingPlease && !downloadData.isDownloadError)
                    {
                        handler.postDelayed(runnable,1000);
                    }
                    else
                    {
                        handler.removeCallbacks(runnable);
                    }
                }
                else
                {
                    handler.removeCallbacks(runnable);
                }
            }
        };
        handler.postDelayed(runnable,200);


        return START_STICKY;
    }


    public void downloadTask()
    {

        oldTime=-1;
        updateNameList=false;
        isSpace=true;
        boolean resumable;
        final int startIndex= downloadData.currentFileIndex;

        for(int i=startIndex;i<downloadData.totalFiles;i++)
        {
            File rootDir=new File(downloadData.toRootPath);
            if(!rootDir.exists())
            {
                boolean x=rootDir.mkdirs();
            }
            File to=new File(slashAppender(downloadData.toRootPath,downloadData.namesList.get(i)));
            resumable=false;
            if(to.exists())
            {
                if(CurrentDestinationPath.equals(to.getAbsolutePath()))
                {
                    resumable=true;
                }
                else
                {
                    keepBoth(i);
                    updateNameList=true;
                }
            }

            downloadData.currentFileName=downloadData.namesList.get(i);


            /*
            THIS is done HERE also to deal with case when space is low ,,so that user can resume it later
             */
            //MAKING ALL THE VALUES SYNCRONIZE

            //tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
            CurrentDestinationPath=slashAppender(downloadData.toRootPath,downloadData.currentFileName);
            //tinyDB.putString(operationCode+"currentFileName",downloadData.currentFileName);
            //tinyDB.putInt(operationCode+"currentFileIndex",downloadData.currentFileIndex);
            //tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");


            //CHECLING FOR SPACE ERRORS
            long partialDownloaded=0;
            if(resumable)
            {
                partialDownloaded=to.length();
            }
            if((new File(slashAppender(downloadData.toRootPath,"")).getUsableSpace())< (downloadData.sizeLongList.get(i)-partialDownloaded))
            {
                isSpace=false;
            }

            //SPACE IS THERE For THIS PARTICULAR FILE
            if(isSpace)
            {
                if (downloadData.folderList.get(i))
                {
                    if(!resumable)
                        folderCopier(i);
                }
                else
                {
                    fileCopier(i,resumable);
                }


                if(!downloadData.isDownloadError && !downloadData.cancelDownloadingPlease && !downloadData.pauseDownloadingPlease )
                {
                    //no problem in downloading
                    if(downloadData.isMoving)
                    {
                        String []breaker= downloadData.currentFileName.split("\\/");//-----[0]=Whatsapp,[1]=media,[2]=file
                        if(downloadData.folderList.get(i)) //is a folder
                        {
                            if(breaker.length==1)
                            {
                                deleteList.add(downloadData.pathsList.get(i));
                            }
                        }
                        else
                        {
                            if(breaker.length==1)
                            {
                                deleteUtils.deleteFromGoogleDrive(downloadData.pathsList.get(i),true);
                            }
                        }
                    }

                    downloadData.currentFileIndex++;
                    //MAKING ALL THE VALUES SYNCRONIZE

                    //tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
                    //tinyDB.putString(operationCode+"currentFileName",downloadData.currentFileName);
                    //tinyDB.putInt(operationCode+"currentFileIndex",downloadData.currentFileIndex);
                    CurrentDestinationPath="";
                    LastDestinationPath="";
                    //tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");
                }
                else
                {
                    if(downloadData.isDownloadError)
                    {
                        notifyProgressWithMessage(i,"Downloading Error...",true);
                        stopSelf();
                        return;
                    }
                    if(downloadData.cancelDownloadingPlease)
                    {
                        notifyProgressWithMessage(i,"Downloading Cancelled...",false);
                        mNotifyManager.cancel(operationCode);
                        stopSelf();
                        return;
                    }
                    if(downloadData.pauseDownloadingPlease)
                    {
                        notifyProgressWithMessage(i,"Downloading Paused...",false);
                        stopSelf();
                        return;
                    }
                }
            }
            else    //NO SPACE IS THERE FOR THIS PARTICULAR FILE
            {

                downloadData.isDownloadError=true;
                downloadData.downloadErrorCode=0;
                downloadData.errorDetails=downloadData.toStoragePath;
                notifyProgressWithMessage(i,"LOW SPACE ERROR...",true);
                Log.e(TAG, ErrorHandler.getErrorName(0)+"-->"+downloadData.errorDetails);
                stopSelf();
                return;

            }

            Log.i(TAG,"Downloading file "+(downloadData.currentFileIndex)+ " of " +downloadData.totalFiles);
        }


        if(downloadData.isFastDownload)
        {
            tinyDB.putString(downloadData.pathsList.get(0),slashAppender(downloadData.toRootPath , downloadData.namesList.get(0)));
        }

        if(downloadData.isMoving)
        {
            for(int j=0;j<deleteList.size();j++)
            {
                deleteUtils.deleteFromGoogleDrive(deleteList.get(j),true);
            }
        }

        downloadData.progress=100;
        downloadData.downloadedSize=downloadData.totalSizeToDownload;
        notifyProgressWithMessage(downloadData.namesList.size()-1,"Downloading Successful...",false);
        stopSelf();

    }


    public void fileCopier(final int index,final boolean resumable)
    {
        if(CurrentDestinationPath.equals(LastDestinationPath))
        {
            return;
        }

        String pathfromId= downloadData.pathsList.get(index);
        String pathto=slashAppender(downloadData.toRootPath,downloadData.namesList.get(index));

        File toFile = new File(pathto);


        //if file size to download is of zero bytes ,create it
        if(downloadData.sizeLongList.get(index)==0)
        {
            if(downloadData.toStorageId%10==0 || downloadData.toStorageId%11==0)
            {
                if(downloadData.toStorageId%10==0)
                {
                    try
                    {
                        DocumentFile df = StorageAccessFramework.fileToDocumentFileConverter(toFile.getAbsolutePath(),false,getApplicationContext());
                        if(df==null)
                        {
                            throw new Exception();
                        }
                        else
                        {
                            LastDestinationPath=pathto;
                            return;
                        }
                    }
                    catch (Exception e)
                    {
                        downloadData.isDownloadError=true;
                        downloadData.downloadErrorCode=3;
                        downloadData.errorDetails="Goto Settings and Update SD Card Permission";
                        Log.e(TAG, ErrorHandler.getErrorName(3)+"-->"+downloadData.errorDetails);
                        stopSelf();
                        return;
                    }
                }

                //media store hack
            }
            else
            {
                try
                {
                    if(toFile.createNewFile())
                    {
                        LastDestinationPath=pathto;
                        return;
                    }
                    else
                    {
                        throw new Exception();
                    }
                }
                catch (Exception e1)
                {
                    downloadData.isDownloadError=true;
                    downloadData.downloadErrorCode=1;
                    downloadData.errorDetails="'" + pathto + "'" + " cannot be created";
                    Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+downloadData.errorDetails);
                    stopSelf();
                    return;
                }
            }
        }






        //OPENING THE OUTPUT STREAM
        OutputStream outputStream=null;
        try
        {
            if(downloadData.toStorageId%10==0 || downloadData.toStorageId%11==0)
            {
                if(downloadData.toStorageId%10==0)
                {
                    DocumentFile df = StorageAccessFramework.fileToDocumentFileConverter(pathto, false,getApplicationContext());
                    if (df == null)
                    {
                        downloadData.isDownloadError=true;
                        downloadData.downloadErrorCode=3;
                        downloadData.errorDetails="Goto Settings and Update SD Card Permission";
                        Log.e(TAG, ErrorHandler.getErrorName(3)+"-->"+downloadData.errorDetails);
                        stopSelf();
                        return;
                    }
                    else
                    {
                        if(resumable)
                        {
                            Log.i(TAG,"resuming...");
                            outputStream = new BufferedOutputStream(getApplicationContext().getContentResolver().openOutputStream(df.getUri(),"wa"));
                        }
                        else
                        {
                            outputStream = new BufferedOutputStream(getApplicationContext().getContentResolver().openOutputStream(df.getUri()));
                        }

                    }
                }

                //media store hack
            }
            else
            {
                if(resumable)
                {
                    Log.i(TAG,"resuming...");
                    outputStream= new BufferedOutputStream(new FileOutputStream(pathto,true) );
                }
                else
                {
                    outputStream= new BufferedOutputStream(new FileOutputStream(pathto) );
                }
            }
        }
        catch (FileNotFoundException e1)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=1;
            downloadData.errorDetails="'" + pathto + "'" + " cannot be created";
            Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+downloadData.errorDetails);
            stopSelf();
            return;
        }
        catch (SecurityException e2)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=2;
            downloadData.errorDetails="'" + pathto + "'" + " write access denied";
            Log.e(TAG, ErrorHandler.getErrorName(2)+"-->"+downloadData.errorDetails);
            stopSelf();
            return;
        }




        //GETTING THE INPUT STREAM
        InputStream inputStream=null;
        long downloaded=0;

        if(resumable && toFile.exists())
        {
            downloaded=toFile.length();
        }
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setRange("bytes=" +downloaded+ "-" +downloadData.sizeLongList.get(index));


        try
        {
            Drive.Files.Get get= GoogleDriveConnection.m_service_client.files().get(pathfromId);
            get.setRequestHeaders(httpHeaders);

            try
            {
                inputStream=get.executeMediaAsInputStream();
            }
            catch (IOException e)
            {
                get.setAcknowledgeAbuse(true);
                inputStream=get.executeMediaAsInputStream();
            }
        }
        catch (Exception e)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=4;
            if(e.getMessage()!=null && e.getMessage().toLowerCase().contains("not found"))
            {
                downloadData.errorDetails="'" + downloadData.namesList.get(index) + "' " + "is missing from GoogleDrive";
            }
            else
            {
                downloadData.errorDetails="Check your Internet Connection";
            }

            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+downloadData.errorDetails);
            stopSelf();
            return;
        }



        //DOWNLOADING
        byte[] buf = new byte[61440];
        int len;
        try
        {
            while ((len = inputStream.read(buf)) > 0)
            {
                if(downloadData.pauseDownloadingPlease)
                {
                    //tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
                    tinyDB.putString(operationCode+"CurrentDestinationPath",slashAppender(downloadData.toRootPath,downloadData.namesList.get(index)));
                    tinyDB.putString(operationCode+"currentFileName",downloadData.currentFileName);
                    tinyDB.putInt(operationCode+"currentFileIndex",downloadData.currentFileIndex);
                    //tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");
                    return;
                }
                if (downloadData.cancelDownloadingPlease)
                {
                    tinyDB.remove(operationCode+"IsRunning");
                    tinyDB.remove(operationCode+"CurrentDestinationPath");
                    tinyDB.remove(operationCode+"LastDestinationPath");
                    return;
                }

                try
                {
                    outputStream.write(buf, 0, len);
                }
                catch (IOException e)
                {
                    downloadData.isDownloadError = true;
                    downloadData.downloadErrorCode = 4;
                    downloadData.errorDetails = "'" + pathto + "'" + "is missing Or write access denied";
                    Log.e(TAG, ErrorHandler.getErrorName(4) + "-->" + downloadData.errorDetails);
                    try
                    {
                        inputStream.close();
                        outputStream.close();
                    }
                    catch (Exception ex)
                    {}
                    stopSelf();
                    return;
                }
                catch (NullPointerException e)
                {
                    downloadData.isDownloadError = true;
                    downloadData.downloadErrorCode = 5;
                    downloadData.errorDetails = "'" + pathto + "'";
                    Log.e(TAG, ErrorHandler.getErrorName(5) + "-->" + downloadData.errorDetails);
                    try
                    {
                        inputStream.close();
                        outputStream.close();
                    }
                    catch (Exception ex)
                    {}
                    stopSelf();
                    return;
                }
                catch (IndexOutOfBoundsException e)
                {
                    downloadData.isDownloadError = true;
                    downloadData.downloadErrorCode = 6;
                    downloadData.errorDetails = "'" + pathto + "'";
                    Log.e(TAG, ErrorHandler.getErrorName(6) + "-->" + downloadData.errorDetails);
                    try
                    {
                        inputStream.close();
                        outputStream.close();
                    }
                    catch (Exception ex)
                    {}
                    stopSelf();
                    return;
                }


                if (!toFile.exists())
                {
                    downloadData.isDownloadError = true;
                    downloadData.downloadErrorCode = 1;
                    downloadData.errorDetails = "'" + pathto + "'" + "is missing Or write access denied";
                    Log.e(TAG, ErrorHandler.getErrorName(1) + "-->" + downloadData.errorDetails);
                    try
                    {
                        inputStream.close();
                        outputStream.close();
                    }
                    catch (Exception ex)
                    {}
                    stopSelf();
                    return;
                }


                downloadData.downloadedSize += len;
                if (downloadData.totalSizeToDownload == 0)
                {
                    downloadData.progress = 0;
                }
                else
                {
                    downloadData.progress = downloadData.downloadedSize * 100 / downloadData.totalSizeToDownload;
                }


                if (oldTime < downloadData.timeInSec)
                {
                    notifyProgressPlease(index);
                }

            }
        }
        catch (IOException e)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=4;

            if(e.getMessage()!=null && e.getMessage().toLowerCase().contains("not found"))
            {
                downloadData.errorDetails="'" + downloadData.namesList.get(index) + "' " + "is missing from GoogleDrive";
            }
            else
            {
                downloadData.errorDetails="'" + downloadData.namesList.get(index) + "' " + "is missing from GoogleDrive Or Check your internet connection";
            }

            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+downloadData.errorDetails);
            try
            {
                inputStream.close();
                outputStream.close();
            }
            catch (Exception ex)
            {}
            stopSelf();
            return;
        }
        catch (NullPointerException e)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=5;
            downloadData.errorDetails="'" + downloadData.namesList.get(index) + "'";
            Log.e(TAG, ErrorHandler.getErrorName(5)+"-->"+downloadData.errorDetails);
            try
            {
                inputStream.close();
                outputStream.close();
            }
            catch (Exception ex)
            {}
            stopSelf();
            return;
        }

        LastDestinationPath=pathto;

        try
        {
            inputStream.close();
            outputStream.close();
        }
        catch (Exception e)
        {}
    }


    public void folderCopier(int index)
    {

        if(CurrentDestinationPath.equals(LastDestinationPath))
        {
            return;
        }

        String toPath=downloadData.toRootPath+"/"+ downloadData.namesList.get(index);
        File to=new File(toPath);

        if(downloadData.toStorageId%10==0 || downloadData.toStorageId%11==0)
        {
            if(downloadData.toStorageId%10==0)
            {
                DocumentFile fx = StorageAccessFramework.fileToDocumentFileConverter(toPath,true,getApplicationContext());
                //folder has been created but still checking the error if any

                if(fx==null)
                {
                    downloadData.isDownloadError=true;
                    downloadData.downloadErrorCode=3;
                    downloadData.errorDetails="Goto Settings and Update SD Card Permission";
                    Log.e(TAG, ErrorHandler.getErrorName(3)+"-->"+downloadData.errorDetails);
                    stopSelf();
                    return;
                }
            }
        }
        else
        {
            boolean isCreated=to.mkdirs();
            if(!isCreated)
            {
                downloadData.isDownloadError=true;
                downloadData.downloadErrorCode=7;
                downloadData.errorDetails="'" + toPath + "'" + " cannot be created";
                Log.e(TAG, ErrorHandler.getErrorName(7)+"-->"+downloadData.errorDetails);
                stopSelf();
                return;
            }
        }

        LastDestinationPath=toPath;

    }


    @Override
    public void onCreate()
    {
        operationCode=203;
        downloadData= MyCacheData.getDownloadDataFromCode(operationCode);
        tinyDB=new TinyDB(getApplicationContext());
        downloadData.isServiceRunning=true;
        tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");
        tasksCache.addTask(operationCode+"");
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return  null;
    }


    @Override
    public void onDestroy()
    {
        Log.i(TAG,"destroyed");
        downloadData.isServiceRunning=false;//very very importanr

        if(downloadData.pauseDownloadingPlease)
        {
            notifyProgressWithMessage(downloadData.currentFileIndex,"Downloading Paused...",false);
        }
        if(downloadData.cancelDownloadingPlease || downloadData.totalFiles==downloadData.currentFileIndex)
        {
            tinyDB.remove(operationCode+"IsRunning");
            tinyDB.remove(operationCode+"CurrentDestinationPath");
            tinyDB.remove(operationCode+"LastDestinationPath");
            tasksCache.removeTask(operationCode+"");
        }
    }


    public void keepBoth(int i)
    {
        int fileNumber=0;

        if(downloadData.folderList.get(i))        //is a directory
        {

            String x1=downloadData.namesList.get(i)+"("+(++fileNumber)+")";
            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
            *
            *
            * like if a.txt and a(1).txt is present how to copy???
            *
            */
            while(true)
            {
                if(new File(slashAppender(downloadData.toRootPath,x1)).exists())
                {
                    x1=downloadData.namesList.get(i)+"("+(++fileNumber)+")";
                }
                else
                {
                    break;
                }
            }

            //first changing the inside files name then at last change the foldder name
            for(int j=i+1;j<downloadData.namesList.size();j++)
            {

                if(downloadData.namesList.get(j).startsWith(downloadData.namesList.get(i)))
                {

                    String x2=downloadData.namesList.get(j).replaceFirst(Pattern.quote(downloadData.namesList.get(i)),x1);
                    downloadData.namesList.set(j,x2);
                }
                else//no need to check for more files in the list
                {
                    break;
                }
            }

            downloadData.namesList.set(i,x1);
        }

        else //is a file
        {
            String x=downloadData.namesList.get(i); //.....'vishwas.txt'------'mydiiiicccc'
            String x2="";
            if(x.lastIndexOf('.')>=0)
            {
                x2=x.substring(x.lastIndexOf('.'),x.length());//.....'.txt'------''
            }

            String x1="";

            if(x.lastIndexOf('.')>=0)
            {
                x1=x.substring(0,x.lastIndexOf('.'))+"("+(++fileNumber)+")";//....'vishwas(1)'
            }
            else
            {
                x1=x.substring(0,x.length())+"("+(++fileNumber)+")";//------'mydiiiicccc(1'
            }


            while (true)
            {
                if(new File(slashAppender(downloadData.toRootPath,x1+x2)).exists())
                {
                    if(x.lastIndexOf('.')>=0)
                    {
                        x1=x.substring(0,x.lastIndexOf('.'))+"("+(++fileNumber)+")";//....'vishwas(1)'
                    }
                    else
                    {
                        x1=x.substring(0,x.length())+"("+(++fileNumber)+")";//------'mydiiiicccc(1'
                    }
                }
                else
                {
                    break;
                }
            }
            downloadData.namesList.set(i,x1+x2);
        }
        Log.i(TAG,"renaming file");
    }



    private String slashAppender(String a,String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }


    private void notifyProgressPlease(int index)
    {
        oldTime= downloadData.timeInSec;
        mBuilder.setProgress(100,(int)downloadData.progress,false);
        mBuilder.setContentText(downloadData.namesList.get(index));
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void notifyProgressWithMessage(int index,String msg,boolean vibrate)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int)downloadData.progress,false)
                .setContentText(downloadData.namesList.get(index))
                .setOngoing(false)
                .setAutoCancel(true);
        if(vibrate)
        {
            mBuilder.setVibrate(new long[]{1000,1000,1000});
        }

        mNotifyManager.notify(operationCode,mBuilder.build());
    }


}

/*
    203LastDestinationPath:
    it is used to deal with cases when downloading of that file is completed but is asked again to download next time when service starts
    case 1: when file is downloaded and service stops while doing isMoving OPERATION(which may be time taking)
    case 2: when file is downloaded but user asks it to pause

    203CurrentDestinationPath:
    it holds the value,from the time when downloading starts till that file is downloaded(_and OR isMoving)


    resume:
    if ( 203CurrentDestinationPath is not empty  && file(203CurrentDestinationPath) exists )

    uploadToTinyDB1() deals with values of 203 cache
    uploadToTinyDB2()  deals with values of 203 cache and is run only when file is downloaded

 */

