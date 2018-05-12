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

import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.MyCacheData;

import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.Utilities.DeleteUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * Created by Acer on 01-08-1017.
 */

public class CopyService2 extends Service
{

    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;

    private Handler handler;
    private Runnable runnable;
    private String CurrentDestinationPath,LastDestinationPath;

    boolean updateNameList=false;

    private String taskName;

    private String TDBCurrentDestinationPath,TDBLastDestinationPath,TDBcurrentFileName;
    private int TDBcurrentFileIndex;
    private long TDBprogress,TDBdownloadedSize;

    HelpingBot helpingBot;
    boolean isSpace=true;

    CopyData copyData;
    int operationCode=102;

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

        taskName= copyData.isMoving?"Moving":"Copying";

        mNotifyManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.search_icon)
                .setContentTitle(taskName)
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
                copyTask();
            }
        };
        myOperationThread.start();


        handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {

                if(copyData.currentFileIndex!=TDBcurrentFileIndex)
                {
                    TDBcurrentFileIndex= copyData.currentFileIndex;
                    tinyDB.putInt(operationCode+"currentFileIndex",TDBcurrentFileIndex);
                }
                if(!copyData.currentFileName.equals(TDBcurrentFileName))
                {
                    TDBcurrentFileName= copyData.currentFileName;
                    tinyDB.putString(operationCode+"currentFileName",TDBcurrentFileName);
                }
                if(copyData.progress!=TDBprogress)
                {
                    TDBprogress=copyData.progress;
                    tinyDB.putLong(operationCode+"progress",TDBprogress);
                }
                if(copyData.downloadedSize!=TDBdownloadedSize)
                {
                    TDBdownloadedSize=copyData.downloadedSize;
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
                    tinyDB.putListString(operationCode+"namesList", copyData.namesList);
                }


                if(copyData.isServiceRunning )
                {
                    if(!copyData.cancelDownloadingPlease && !copyData.pauseDownloadingPlease && !copyData.isDownloadError)
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


    public void copyTask()
    {

        oldTime=-1;
        updateNameList=false;
        isSpace=true;
        boolean resumable;
        boolean fastMove;
        final int startIndex= copyData.currentFileIndex;

        for(int i = startIndex; i< copyData.totalFiles; i++)
        {
            File toRootFile=new File(copyData.toRootPath);
            if(!toRootFile.exists())
            {
                boolean x=toRootFile.mkdirs();
            }

            if(copyData.isMoving && copyData.toRootPath.startsWith(copyData.pathsList.get(i)))
            {
                //toRootPath=from+"something"

                copyData.isDownloadError=true;
                copyData.downloadErrorCode=17;
                copyData.errorDetails= copyData.toRootPath;
                Log.e(TAG, ErrorHandler.getErrorName(17)+"-->"+ copyData.errorDetails);
                notifyProgressWithMessage(i,taskName+" Error...",true);
                stopSelf();
                return;
            }

            File to=new File(slashAppender(copyData.toRootPath, copyData.namesList.get(i)));


            Log.e("TRYING FAST:","--"+i+"'--"+ copyData.pathsList.get(i));

            if(copyData.isFastMove)
            {
                fastMove=isFastMoved(new File(copyData.pathsList.get(i)),to);
                if(fastMove)
                {
                    copyData.currentFileName= copyData.namesList.get(i);
                    copyData.downloadedSize+= copyData.sizeLongList.get(i);
                    if(copyData.totalSizeToDownload==0)
                    {
                        copyData.progress=0;
                    }
                    else
                    {
                        copyData.progress= copyData.downloadedSize*100/ copyData.totalSizeToDownload;
                    }
                    copyData.currentFileIndex++;
                    if(oldTime< copyData.timeInSec )
                    {
                        notifyProgressPlease(i);
                    }
                    String folderName= copyData.namesList.get(i)+"/";
                    for(int j = i+1; j< copyData.totalFiles; j++)
                    {
                        if(copyData.namesList.get(j).startsWith(folderName))
                        {
                            copyData.currentFileName= copyData.namesList.get(j);
                            copyData.downloadedSize+= copyData.sizeLongList.get(j);
                            copyData.currentFileIndex++;
                        }
                        else
                        {
                            break;
                        }
                    }
                    i= copyData.currentFileIndex-1;
                    continue;
                }
            }


            if(copyData.isMoving)
            {
                Log.e("FAST FAILED:","--"+i+"'--"+ copyData.pathsList.get(i));
                Log.e(TAG,"ACTUALLY MOVING ==> COPY + DELETE");
            }



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

            copyData.currentFileName= copyData.namesList.get(i);

            /*
            THIS is done HERE also to deal with case when space is low ,,so that user can resume it later
             */
            //MAKING ALL THE VALUES SYNCRONIZE

            //tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
            CurrentDestinationPath=slashAppender(copyData.toRootPath, copyData.currentFileName);
            //tinyDB.putString(operationCode+"currentFileName",copyData.currentFileName);
            //tinyDB.putInt(operationCode+"currentFileIndex",copyData.currentFileIndex);
            //tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");


            //CHECLING FOR SPACE ERRORS
            long partialDownloaded=0;
            if(resumable)
            {
                partialDownloaded=to.length();
            }
            if((new File(slashAppender(copyData.toRootPath,"")).getUsableSpace())< (copyData.sizeLongList.get(i)-partialDownloaded))
            {
                isSpace=false;
            }



            //SPACE IS THERE For THIS PARTICULAR FILE
            if(isSpace)
            {
                if (copyData.folderList.get(i))
                {
                    if(!resumable)
                        folderCopier(i);
                }
                else
                {
                    fileCopier(i,resumable);
                }

                if(!copyData.isDownloadError && !copyData.cancelDownloadingPlease && !copyData.pauseDownloadingPlease )
                {
                    //no problem in downloading
                    if(copyData.isMoving)
                    {
                        String []breaker= copyData.currentFileName.split("\\/");//-----[0]=Whatsapp,[1]=media,[2]=file
                        if(copyData.folderList.get(i)) //is a folder
                        {
                            if(breaker.length==1)
                            {
                                deleteList.add(copyData.pathsList.get(i));
                            }
                        }
                        else
                        {
                            if(breaker.length==1)
                            {
                                File file=new File(copyData.pathsList.get(i));
                                if(copyData.fromStorageId%10==0 || copyData.fromStorageId%11==0)
                                {
                                    if(copyData.fromStorageId%10==0)
                                    {
                                        deleteUtils.deleteDocumentFile(file);
                                    }
                                    // mediaStoreHack

                                }
                                else
                                {
                                    deleteUtils.deleteFromInternal(file);
                                }
                            }
                        }
                    }


                    copyData.currentFileIndex++;
                    //MAKING ALL THE VALUES SYNCRONIZE

                    //tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
                    //tinyDB.putString(operationCode+"currentFileName",copyData.currentFileName);
                    //tinyDB.putInt(operationCode+"currentFileIndex",copyData.currentFileIndex);
                    CurrentDestinationPath="";
                    LastDestinationPath="";
                    //tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");

                }
                else
                {
                    if(copyData.isDownloadError)
                    {
                        notifyProgressWithMessage(i,taskName+" Error...",true);
                        stopSelf();
                        return;
                    }
                    if(copyData.cancelDownloadingPlease)
                    {
                        notifyProgressWithMessage(i,taskName+" Cancelled...",false);
                        mNotifyManager.cancel(operationCode);
                        stopSelf();
                        return;
                    }
                    if(copyData.pauseDownloadingPlease)
                    {
                        notifyProgressWithMessage(i,taskName+" Paused...",false);
                        stopSelf();
                        return;
                    }
                }
            }
            else    //NO SPACE IS THERE FOR THIS PARTICULAR FILE
            {
                copyData.isDownloadError=true;
                copyData.downloadErrorCode=0;
                copyData.errorDetails= copyData.toStoragePath;
                notifyProgressWithMessage(i,"LOW SPACE ERROR...",true);
                Log.e(TAG, ErrorHandler.getErrorName(0)+"-->"+ copyData.errorDetails);
                stopSelf();
                return;
            }

            Log.i(TAG,"Copied file "+ copyData.currentFileIndex+ " of " + copyData.totalFiles);

        }

        if(copyData.isMoving)
        {
            for(int j=0;j<deleteList.size();j++)
            {
                File file=new File(deleteList.get(j));
                if(copyData.fromStorageId%10==0 || copyData.fromStorageId%11==0)
                {
                    if(copyData.fromStorageId%10==0)
                    {
                        deleteUtils.deleteDocumentFile(file);
                    }
                    // mediaStoreHack

                }
                else
                {
                    deleteUtils.deleteFromInternal(file);
                }
            }
        }


        copyData.progress=100;
        copyData.downloadedSize= copyData.totalSizeToDownload;
        notifyProgressWithMessage(copyData.namesList.size()-1,taskName+" Successful...",false);
        stopSelf();

    }


    public void fileCopier(final int index,final boolean resumable)
    {

        if(CurrentDestinationPath.equals(LastDestinationPath))
        {
            return;
        }


        String pathFrom= copyData.pathsList.get(index);
        String pathTo=slashAppender(copyData.toRootPath, copyData.namesList.get(index));

        File fromFile=new File(pathFrom);
        File toFile=new File(pathTo);

        FileInputStream fileInputStream;

        long copied=0;

        //GETTING THE INPUT STREAM
        if(resumable && toFile.exists())
        {
            copied=toFile.length();
        }

        try
        {
            fileInputStream= new FileInputStream(fromFile);
            fileInputStream.getChannel().position(copied);
        }
        catch (SecurityException e2)
        {
            copyData.isDownloadError=true;
            copyData.downloadErrorCode=2;
            copyData.errorDetails="'" + pathFrom + "'" + " read access denied";
            Log.e(TAG, ErrorHandler.getErrorName(2)+"-->"+ copyData.errorDetails);
            stopSelf();
            return;
        }
        catch (Exception e1)
        {
            copyData.isDownloadError=true;
            copyData.downloadErrorCode=1;
            copyData.errorDetails="'" + pathFrom + "'" + " doesn't exist OR is invalid File";
            Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ copyData.errorDetails);
            stopSelf();
            return;
        }


        OutputStream outputStream=null;

        try
        {
            if(copyData.toStorageId%10==0 || copyData.toStorageId%11==0)
            {
                if(copyData.toStorageId%10==0)
                {
                    //SAF
                    DocumentFile df = StorageAccessFramework.fileToDocumentFileConverter(pathTo,false,getApplicationContext());
                    if(df==null)
                    {
                        copyData.isDownloadError=true;
                        copyData.downloadErrorCode=3;
                        copyData.errorDetails="Goto Settings and Update SD Card Permission";
                        Log.e(TAG, ErrorHandler.getErrorName(3)+"-->"+ copyData.errorDetails);
                        stopSelf();
                        try
                        {
                            fileInputStream.close();
                        }
                        catch (IOException e)
                        {}

                        return;
                    /*---RETURN ----BECAUSE STOPPING THE SERVICE TAKING SOME TIME ,
                    THE NEXT STATEMENTS WILL RUN INCLUIDNG closing the null output stream
                     */
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
            }
            else
            {
                if(resumable)
                {
                    Log.i(TAG,"resuming...");
                    outputStream= new BufferedOutputStream(new FileOutputStream(pathTo,true) );
                }
                else
                {
                    outputStream= new BufferedOutputStream(new FileOutputStream(pathTo) );
                }
            }
        }
        catch (FileNotFoundException e1)
        {
            copyData.isDownloadError=true;
            copyData.downloadErrorCode=1;
            copyData.errorDetails="'" + pathTo + "'" + " cannot be created";
            Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ copyData.errorDetails);
            stopSelf();
            return;
        }
        catch (SecurityException e2)
        {
            copyData.isDownloadError=true;
            copyData.downloadErrorCode=2;
            copyData.errorDetails="'" + pathTo + "'" + " write access denied";
            Log.e(TAG, ErrorHandler.getErrorName(2)+"-->"+ copyData.errorDetails);
            stopSelf();
            return;
        }

        byte[] buf = new byte[61440];
        int len;

        try
        {
            while ((len = fileInputStream.read(buf)) > 0)
            {
                if(copyData.pauseDownloadingPlease)
                {
                    //tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
                    tinyDB.putString(operationCode+"CurrentDestinationPath",slashAppender(copyData.toRootPath, copyData.namesList.get(index)));
                    tinyDB.putString(operationCode+"currentFileName", copyData.currentFileName);
                    tinyDB.putInt(operationCode+"currentFileIndex", copyData.currentFileIndex);
                    //tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");
                    return;
                }
                if (copyData.cancelDownloadingPlease)
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
                    copyData.isDownloadError=true;
                    copyData.downloadErrorCode=4;
                    copyData.errorDetails="'" + pathTo + "'" + "is missing Or write access denied";
                    Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ copyData.errorDetails);

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
                    copyData.isDownloadError=true;
                    copyData.downloadErrorCode=5;
                    copyData.errorDetails="'" + pathTo + "'";
                    Log.e(TAG, ErrorHandler.getErrorName(5)+"-->"+ copyData.errorDetails);

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
                    copyData.isDownloadError=true;
                    copyData.downloadErrorCode=6;
                    copyData.errorDetails="'" + pathTo + "'";
                    Log.e(TAG, ErrorHandler.getErrorName(6)+"-->"+ copyData.errorDetails);
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


                if(!fromFile.exists())
                {
                    copyData.isDownloadError=true;
                    copyData.downloadErrorCode=1;
                    copyData.errorDetails="'" + pathFrom + "'" + "is missing Or read access denied";
                    Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ copyData.errorDetails);
                    try
                    {
                        fileInputStream.close();
                        outputStream.close();
                    }
                    catch (Exception ex)
                    {

                    }
                    stopSelf();
                    return;
                }
                if(!toFile.exists())
                {
                    copyData.isDownloadError=true;
                    copyData.downloadErrorCode=1;
                    copyData.errorDetails="'" + pathTo + "'" + "is missing Or write access denied";
                    Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ copyData.errorDetails);
                    try
                    {
                        fileInputStream.close();
                        outputStream.close();
                    }
                    catch (Exception ex)
                    {

                    }
                    stopSelf();
                    return;
                }


                copyData.downloadedSize+=len;
                if(copyData.totalSizeToDownload==0)
                {
                    copyData.progress=0;
                }
                else
                {
                    copyData.progress= copyData.downloadedSize*100/ copyData.totalSizeToDownload;
                }

                if(oldTime< copyData.timeInSec )
                {
                    notifyProgressPlease(index);
                }
            }
        }
        catch (IOException e)
        {
            copyData.isDownloadError=true;
            copyData.downloadErrorCode=4;
            copyData.errorDetails="'" + pathFrom + "'" + "is missing Or read access denied";
            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ copyData.errorDetails);
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
            copyData.isDownloadError=true;
            copyData.downloadErrorCode=5;
            copyData.errorDetails="'" + pathFrom + "'";
            Log.e(TAG, ErrorHandler.getErrorName(5)+"-->"+ copyData.errorDetails);
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


        LastDestinationPath=pathTo;

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

        String toPath=slashAppender(copyData.toRootPath, copyData.namesList.get(index));
        File to=new File(toPath);

        if(copyData.toStorageId%10==0 || copyData.toStorageId%11==0)
        {
            if(copyData.toStorageId%10==0)
            {
                DocumentFile fx = StorageAccessFramework.fileToDocumentFileConverter(toPath,true,getApplicationContext());
                //folder has been created but still checking the error if any

                if(fx==null)
                {
                    copyData.isDownloadError=true;
                    copyData.downloadErrorCode=3;
                    copyData.errorDetails="Goto Settings and Update SD Card Permission";
                    Log.e(TAG, ErrorHandler.getErrorName(3)+"-->"+ copyData.errorDetails);
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
                copyData.isDownloadError=true;
                copyData.downloadErrorCode=7;
                copyData.errorDetails="'" + toPath + "'" + " cannot be created";
                Log.e(TAG, ErrorHandler.getErrorName(7)+"-->"+ copyData.errorDetails);
                stopSelf();
                return;
            }
        }

        LastDestinationPath=toPath;
    }


    @Override
    public void onCreate()
    {
        operationCode=102;
        copyData= MyCacheData.getCopyDataFromCode(operationCode);
        tinyDB=new TinyDB(getApplicationContext());
        copyData.isServiceRunning=true;
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
        copyData.isServiceRunning=false;//very very important

        if(copyData.cancelDownloadingPlease || copyData.totalFiles== copyData.currentFileIndex)
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

        if(copyData.folderList.get(i))        //is a directory
        {

            String x1= copyData.namesList.get(i)+"("+(++fileNumber)+")";
            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
            *
            *
            * like if a.txt and a(1).txt is present how to copy???
            *
            */
            while(true)
            {
                if(new File(slashAppender(copyData.toRootPath,x1)).exists())
                {
                    x1= copyData.namesList.get(i)+"("+(++fileNumber)+")";
                }
                else
                {
                    break;
                }
            }

            //first changing the inside files name then at last change the foldder name
            for(int j = i+1; j< copyData.namesList.size(); j++)
            {

                if(copyData.namesList.get(j).startsWith(copyData.namesList.get(i)))
                {

                    String x2= copyData.namesList.get(j).replaceFirst(Pattern.quote(copyData.namesList.get(i)),x1);
                    copyData.namesList.set(j,x2);
                }
                else//no need to check for more files in the list
                {
                    break;
                }
            }

            copyData.namesList.set(i,x1);
        }

        else //is a file
        {
            String x= copyData.namesList.get(i); //.....'vishwas.txt'------'mydiiiicccc'
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
                if(new File(slashAppender(copyData.toRootPath,x1+x2)).exists())
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
            copyData.namesList.set(i,x1+x2);
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


    private boolean isFastMoved(File fromFile,File toFile)
    {
        if(toFile.exists())
        {
            toFile=keepBoth(toFile);
        }
        return fromFile.renameTo(toFile);
    }


    public File keepBoth(final File file)
    {

        int fileNumber=0;
        String path=file.getAbsolutePath();
        int lastIndex=path.lastIndexOf('/');
        String rootPath=path.substring(0,lastIndex);
        Log.i(TAG,"renaming file");
        if(file.isDirectory())        //is a directory
        {
            String x1=file.getName()+"("+(++fileNumber)+")";

            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
            *
            *
            * like if a.txt and a(1).txt is present how to copy???
            *
            */
            while(true)
            {
                if(new File(slashAppender(rootPath,x1)).exists())
                {
                    x1=file.getName()+"("+(++fileNumber)+")";
                }
                else
                {
                    break;
                }
            }
            return new File(slashAppender(rootPath,x1));
        }

        else //is a file
        {
            String x=file.getName(); //.....'vishwas.txt'------'mydiiiicccc'
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
                if(new File(slashAppender(rootPath,x1+x2)).exists())
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
            return new File(slashAppender(rootPath,x1+x2));
        }
    }

    private void notifyProgressPlease(int index)
    {
        oldTime= copyData.timeInSec;
        mBuilder.setProgress(100,(int) copyData.progress,false);
        mBuilder.setContentText(copyData.namesList.get(index));
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void notifyProgressWithMessage(int index,String msg,boolean vibrate)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int) copyData.progress,false)
                .setContentText(copyData.namesList.get(index))
                .setOngoing(false)
                .setAutoCancel(true);
        if(vibrate)
        {
            mBuilder.setVibrate(new long[]{1000,1000,1000});
        }

        mNotifyManager.notify(operationCode,mBuilder.build());
    }

}