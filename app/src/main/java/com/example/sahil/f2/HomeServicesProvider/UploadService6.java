package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.DownloadData;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.Ftp.MyFtpClient;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.Utilities.DeleteUtils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by hit4man47 on 1/3/2018.
 */

public class UploadService6 extends Service
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

    int operationCode=306;
    DownloadData downloadData;
    private DeleteUtils deleteUtils;

    final String TAG=operationCode+"_SERVICE";
    int oldTime=-1;

    public static Thread myOperationThread;
    public ArrayList<String> deleteList;

    private FTPClient ftpClient=null;
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
                .setContentTitle("Uploading to FTP Server")
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
        boolean resumable;
        final int startIndex= downloadData.currentFileIndex;

        for(int i=startIndex;i<downloadData.totalFiles;i++)
        {
            try
            {
                if(ftpClient==null)
                {
                    if(FtpCache.currentFullUrl==null)
                    {
                        downloadData.isDownloadError=true;
                        downloadData.downloadErrorCode=18;
                        downloadData.errorDetails="Check your Internet Connection";
                        Log.e(TAG, ErrorHandler.getErrorName(18)+"-->"+downloadData.errorDetails);
                        stopSelf();
                        return;
                    }
                    else
                    {
                        MyFtpClient myFtpClient=new MyFtpClient();
                        ftpClient=myFtpClient.connect(FtpCache.currentFullUrl);
                        if(ftpClient==null)
                        {
                            throw new Exception("My exception");
                        }

                        //making sure that toRoot directory existed
                        boolean exist=isDirectoryExistOnFtpServer(downloadData.toRootPath);
                        if(!exist)
                        {
                            Log.e(TAG,"TO ROOT PATH DOES NOT EXIST");
                            makeDirs(downloadData.toRootPath);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                downloadData.isDownloadError=true;
                downloadData.downloadErrorCode=19;
                downloadData.errorDetails="Check your Internet Connection";
                Log.e(TAG, "CONNECTING-->"+ErrorHandler.getErrorName(19)+"-->"+downloadData.errorDetails+e.getLocalizedMessage()+e.getMessage());
                stopSelf();
                return;
            }

            resumable=false;
            if(CurrentDestinationPath.equals(slashAppender(downloadData.toRootPath,downloadData.namesList.get(i))))
            {
                resumable=true;
            }
            else
            {
                //always check if this file already exist or not,if it does create a new name for that file
                try
                {
                    keepBoth(i);
                }
                catch (Exception e)
                {
                    downloadData.isDownloadError=true;
                    downloadData.downloadErrorCode=19;
                    downloadData.errorDetails="Check your Internet Connection";
                    Log.e(TAG,"keepBoth--> "+ ErrorHandler.getErrorName(19)+"-->"+downloadData.errorDetails+e.getLocalizedMessage()+e.getMessage());
                    stopSelf();
                    return;
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
                //no problem in uploading
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
                            File file=new File(downloadData.pathsList.get(i));
                            if(downloadData.fromStorageId%10==0 || downloadData.fromStorageId%11==0)
                            {
                                if(downloadData.fromStorageId%10==0)
                                {
                                    deleteUtils.deleteDocumentFile(file);
                                }
                                else
                                {
                                    //media store hack
                                }
                            }
                            else
                            {
                                deleteUtils.deleteFromInternal(file);
                            }
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
                    notifyProgressWithMessage(i,"Uploading Error...",true);
                    stopSelf();
                    return;
                }
                if(downloadData.cancelDownloadingPlease)
                {
                    notifyProgressWithMessage(i,"Uploading Cancelled...",false);
                    mNotifyManager.cancel(operationCode);
                    stopSelf();
                    return;
                }
                if(downloadData.pauseDownloadingPlease)
                {
                    notifyProgressWithMessage(i,"Uploading Paused...",false);
                    stopSelf();
                    return;
                }
            }
            Log.i(TAG,"Uploading file "+(downloadData.currentFileIndex)+ " of " +downloadData.totalFiles);
        }




        if(downloadData.isMoving)
        {
            for(int j=0;j<deleteList.size();j++)
            {
                File file=new File(deleteList.get(j));
                if(downloadData.fromStorageId%10==0 || downloadData.fromStorageId%11==0)
                {
                    if(downloadData.fromStorageId%10==0)
                    {
                        deleteUtils.deleteDocumentFile(file);
                    }
                    else
                    {
                        //media store hack
                    }
                }
                else
                {
                    deleteUtils.deleteFromInternal(file);
                }
            }
        }

        downloadData.progress=100;
        downloadData.downloadedSize=downloadData.totalSizeToDownload;
        notifyProgressWithMessage(downloadData.namesList.size()-1,"Uploading Successful...",false);
        stopSelf();

    }


    public void fileCopier(final int index,final boolean resumable)
    {
        if(CurrentDestinationPath.equals(LastDestinationPath))
        {
            return;
        }

        String fromPath= downloadData.pathsList.get(index);
        String toPath=slashAppender(downloadData.toRootPath,downloadData.namesList.get(index));

        long chunkStart=0;

        if(resumable)
        {
            Log.e(TAG,"resuming...");
            chunkStart=getFileOnFtpSize(toPath);
        }

        Log.e(TAG,"CHUNK start IS:"+chunkStart);
        //OPENING INPUT STREAM
        File fromFile = new File(fromPath);
        FileInputStream fileInputStream;
        try
        {
            fileInputStream= new FileInputStream(fromFile);
            fileInputStream.getChannel().position(chunkStart);
        }
        catch (Exception e)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=1;
            downloadData.errorDetails="'" + fromPath + "'" + " does't exist OR read access denied";
            Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ downloadData.errorDetails);
            stopSelf();
            return;
        }


        OutputStream outputStream;
        try
        {
            if(chunkStart==0)
            {
                outputStream=ftpClient.storeFileStream(toPath);
            }
            else
            {
                ftpClient.setRestartOffset(chunkStart);
                outputStream=ftpClient.appendFileStream(toPath);
            }
        }
        catch (CopyStreamException e)
        {

            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=20;
            Log.e("CopyStreamException",e.getLocalizedMessage()+"--"+e.getMessage());
            stopSelf();
            return;
        }
        catch (Exception e)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=4;
            downloadData.errorDetails="'" + toPath + "'" + " cannot be created";
            Log.e(TAG, "opening OS-->"+ErrorHandler.getErrorName(4)+"-->"+downloadData.errorDetails+e.getLocalizedMessage());
            stopSelf();
            return;
        }

        if(outputStream==null)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=21;
            downloadData.errorDetails="'" + toPath + "'" + " cannot be created";
            Log.e(TAG, "null OS-->"+ErrorHandler.getErrorName(21));
            stopSelf();
            return;
        }

        //Uplaoding
        byte[] buf = new byte[61440];
        int len;
        try
        {
            while ((len = fileInputStream.read(buf)) > 0)
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
                    downloadData.errorDetails = "'" + toPath + "'" + " write access denied or Check internet connection";
                    Log.e(TAG, ErrorHandler.getErrorName(4) + "-->" + downloadData.errorDetails);
                    try
                    {
                        fileInputStream.close();
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
                    downloadData.errorDetails = "'" + toPath + "'";
                    Log.e(TAG, ErrorHandler.getErrorName(5) + "-->" + downloadData.errorDetails+e.getLocalizedMessage()+e.getMessage());
                    try
                    {
                        fileInputStream.close();
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
                    downloadData.errorDetails = "'" + toPath + "'";
                    Log.e(TAG, ErrorHandler.getErrorName(6) + "-->" + downloadData.errorDetails);
                    try
                    {
                        fileInputStream.close();
                        outputStream.close();
                    }
                    catch (Exception ex)
                    {}
                    stopSelf();
                    return;
                }


                if (!fromFile.exists())
                {
                    downloadData.isDownloadError = true;
                    downloadData.downloadErrorCode = 1;
                    downloadData.errorDetails = "'" + fromPath + "'" + "is missing Or read access denied";
                    Log.e(TAG, ErrorHandler.getErrorName(1) + "-->" + downloadData.errorDetails);
                    try
                    {
                        fileInputStream.close();
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
            downloadData.errorDetails="'" + downloadData.pathsList.get(index) + "' " + "is missing Or Check your internet connection";

            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+downloadData.errorDetails+e.getLocalizedMessage()+e.getMessage());
            try
            {
                fileInputStream.close();
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
            downloadData.errorDetails="'" + downloadData.pathsList.get(index) + "'";
            Log.e(TAG, ErrorHandler.getErrorName(5)+"-->"+downloadData.errorDetails+e.getLocalizedMessage()+e.getMessage());
            try
            {
                fileInputStream.close();
                outputStream.close();
            }
            catch (Exception ex)
            {}
            stopSelf();
            return;
        }

        try
        {
            //boolean x=ftpClient.completePendingCommand();
            //Log.e(TAG,"IS SUCCUESFULL??"+x);
        }
        catch (Exception e)
        {

        }

        LastDestinationPath=toPath;

        try
        {
            fileInputStream.close();
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
        try
        {
            makeDirs(toPath);
        }
        catch (Exception e)
        {
            downloadData.isDownloadError=true;
            downloadData.downloadErrorCode=7;
            downloadData.errorDetails="'" + toPath + "'" + " cannot be created";
            Log.e(TAG, ErrorHandler.getErrorName(7)+"-->"+downloadData.errorDetails);
            stopSelf();
            return;
        }

        LastDestinationPath=toPath;

    }


    @Override
    public void onCreate()
    {
        ftpClient=null;
        operationCode=306;
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
        Log.e(TAG,"destroyed");
        downloadData.isServiceRunning=false;//very very importanr

        if(downloadData.cancelDownloadingPlease || downloadData.totalFiles==downloadData.currentFileIndex)
        {
            tinyDB.remove(operationCode+"IsRunning");
            tinyDB.remove(operationCode+"CurrentDestinationPath");
            tinyDB.remove(operationCode+"LastDestinationPath");
            tasksCache.removeTask(operationCode+"");
        }
    }


    private long getFileOnFtpSize(String toPath)
    {
        String parent="/";
        String name;
        long size=0;
        int lastIndex=toPath.lastIndexOf('/');
        if(lastIndex>0)
        {
            parent=toPath.substring(0,lastIndex);
        }
        name=toPath.substring(lastIndex+1);

        FTPFile[] filesFtp=null;
        try
        {
            filesFtp= ftpClient.listFiles(parent);
        }
        catch (Exception e)
        {
            filesFtp=null;
        }
        if(filesFtp==null || filesFtp.length==0)
        {
            return 0;
        }
        for(FTPFile f:filesFtp)
        {
            if(f.getName().equals(name))
            {
                size=f.getSize();
                return size;
            }
        }
        return size;
    }


    public void keepBoth(int i) throws Exception
    {
        int fileNumber=0;
        String toPath=slashAppender(downloadData.toRootPath,downloadData.namesList.get(i));
        if(downloadData.folderList.get(i))        //is a directory
        {

            boolean exist=isDirectoryExistOnFtpServer(toPath);
            if(!exist)
            {
                return;
            }
            String x1=downloadData.namesList.get(i)+"("+(++fileNumber)+")";
            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
            *
            *
            * like if a.txt and a(1).txt is present how to copy???
            *
            */
            while(true)
            {
                if(isDirectoryExistOnFtpServer(slashAppender(downloadData.toRootPath,x1)))
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
            updateNameList=true;

        }

        else //is a file
        {
            boolean exist=isFileExistOnFtpServer(toPath);
            if(!exist)
            {
                return;
            }
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
                if(isFileExistOnFtpServer(slashAppender(downloadData.toRootPath,x1+x2)))
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
            updateNameList=true;
        }
        Log.e(TAG,"renaming file");
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


    private boolean isDirectoryExistOnFtpServer(String path) throws Exception
    {
        ftpClient.changeWorkingDirectory(path);
        int returnCode=ftpClient.getReplyCode();
        if(returnCode==550)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean isFileExistOnFtpServer(String path) throws Exception
    {
        Log.e(TAG,"CHECKING IF FILE EXIST ON SERVER:");
        InputStream inputStream=ftpClient.retrieveFileStream(path);
        int returnCode=ftpClient.getReplyCode();
        if(inputStream==null || returnCode==550)
        {
            Log.e(TAG,"FILE EXIST ON SERVER:NO");
            return false;
        }
        try
        {
            inputStream.close();
        }
        catch (Exception e)
        {}

        ftpClient.completePendingCommand();
        Log.e(TAG,"FILE EXIST ON SERVER:YES");
        return true;
    }

    private boolean makeDirs(String dirToMake) throws Exception
    {
        String [] pathElements=dirToMake.split("\\/");
        if(pathElements.length>0)
        {
            for(String singleDir:pathElements)
            {
                boolean existed=ftpClient.changeWorkingDirectory(singleDir);
                if(!existed)
                {
                    boolean created=ftpClient.makeDirectory(singleDir);
                    if(created)
                    {
                        ftpClient.changeWorkingDirectory(singleDir);
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}

/*
    204LastDestinationPath:
    it is used to deal with cases when downloading of that file is completed but is asked again to download next time when service starts
    case 1: when file is downloaded and service stops while doing isMoving OPERATION(which may be time taking)
    case 2: when file is downloaded but user asks it to pause

    204CurrentDestinationPath:
    it holds the value,from the time when downloading starts till that file is downloaded(_and OR isMoving)


    resume:
    if ( 204CurrentDestinationPath is not empty  && file(204CurrentDestinationPath) exists )

    uploadToTinyDB1() deals with values of 204 cache
    uploadToTinyDB2()  deals with values of 204 cache and is run only when file is downloaded

 */



