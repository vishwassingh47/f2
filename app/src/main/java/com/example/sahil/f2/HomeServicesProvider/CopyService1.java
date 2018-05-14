package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.MyCacheData;

import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.OneFile;
import com.example.sahil.f2.Classes.SexyInputStream;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuOperations;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.Utilities.CommonUtils;
import com.example.sahil.f2.Utilities.CreateNewUtils;
import com.example.sahil.f2.Utilities.DeleteUtils;
import com.stericson.RootTools.RootTools;

import java.io.BufferedInputStream;
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
 * Created by Acer on 01-08-2017.
 */

public class CopyService1 extends Service
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

    CopyData copyData;
    int operationCode=101;
    
    final String TAG=operationCode+"_SERVICE";
    int oldTime=-1;

    public static Thread myOperationThread;
    public ArrayList<String> deleteFolderList;
    private DeleteUtils deleteUtils;
    private boolean cpCommand=false;
    Context context;
    TinyDB tinyDB;
    CreateNewUtils createNewUtils;

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

        deleteFolderList=new ArrayList<>();



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
        context=getApplicationContext();
        createNewUtils=new CreateNewUtils(context);
        oldTime=-1;
        updateNameList=false;
        boolean resumeAble;
        final int startIndex= copyData.currentFileIndex;


        String toParentPath=copyData.toRootPath;

        //IF TO ROOT FOLDER DOES NOT EXIST CREATE IT
        OneFile toParentFile=new OneFile(toParentPath,context);
        if(!toParentFile.isExist())
        {
            createNewUtils.createInLocal(toParentPath,true,false);
        }

        Log.e(TAG,copyData.totalFiles+"--"+startIndex+"--"+toParentPath);

        for(int i = startIndex; i< copyData.totalFiles; i++)
        {
            String toPath=slashAppender(toParentPath, copyData.namesList.get(i));
            copyData.currentFileName= copyData.namesList.get(i);

            if(checkIsError17(copyData.pathsList.get(i)))
            {
                //toRootPath=from+"something"
                notifyError(17, copyData.toRootPath);
                stopSelf();
                return;
            }


            OneFile toOneFile=new OneFile(toPath,context);
            resumeAble=false;

            if(toOneFile.isExist())
            {
                if(toOneFile.isHavingAccess() && toOneFile.isJavaFile() && toOneFile.isFile() &&  CurrentDestinationPath.equals(toPath) )
                {
                    resumeAble=true;
                }
                else
                {
                    //rename
                    keepBoth(i);
                    copyData.currentFileName= copyData.namesList.get(i);
                    updateNameList=true;
                }
            }

            CurrentDestinationPath=slashAppender(copyData.toRootPath, copyData.currentFileName);

            if(copyData.isMoving)
            {
                if(tryingFastMoved(i,toPath))
                {
                    i= copyData.currentFileIndex-1;
                    continue;
                }
                else
                {
                    Log.e("FAST MOVE FAILED:","--"+i+"'--"+ copyData.pathsList.get(i)+"----ACTUALLY MOVING ==> COPY + DELETE");
                }
            }



            if (copyData.folderList.get(i))
            {
                folderCopier(toPath);
            }
            else
            {
                copyFileManager(copyData.pathsList.get(i),toPath,resumeAble,copyData.sizeLongList.get(i));
            }


            if(!copyData.isDownloadError && !copyData.cancelDownloadingPlease && !copyData.pauseDownloadingPlease )
            {
                //no problem in downloading

                copyData.currentFileIndex++;
                CurrentDestinationPath="";
                LastDestinationPath="";


                if(copyData.isMoving)
                {
                    String []breaker= copyData.currentFileName.split("\\/");//-----[0]=Whatsapp,[1]=media,[2]=file
                    if(breaker.length==1)
                    {
                        if(copyData.folderList.get(i)) //is a folder
                        {
                            deleteFolderList.add(copyData.pathsList.get(i));
                        }
                        else
                        {
                            deleteUtils.deleteFromLocal(copyData.pathsList.get(i),false);
                        }
                    }
                }
            }
            else
            {
                if(copyData.cancelDownloadingPlease)
                {
                    mNotifyManager.cancel(operationCode);
                }
                stopSelf();
                return;
            }

            Log.i(TAG,"Copied file "+ copyData.currentFileIndex+ " of " + copyData.totalFiles);
        }


        if(copyData.isMoving)
        {
            for(String path:deleteFolderList)
            {
                deleteUtils.deleteFromLocal(path,true);
            }
        }
        
        copyData.progress=100;
        copyData.downloadedSize= copyData.totalSizeToDownload;
        notifyMessage("Successful...",false);
        stopSelf();

    }

    private void copyFileManager(String pathFrom,String pathTo,boolean resumeAble,long size)
    {
        Log.e(TAG,pathFrom+"--"+pathTo+"--"+resumeAble+"--"+size);
        if(CurrentDestinationPath.equals(LastDestinationPath))
        {
            return;
        }
        Log.e(TAG,pathFrom+"##"+pathTo+"--"+resumeAble+"--"+size);
        //GETTING THE INPUT STREAM
        SexyInputStream sexyInputStream;
        long copied=0;
        if(resumeAble)
        {
            File toFile=new File(pathTo);
            copied=toFile.length();
        }
        sexyInputStream=new SexyInputStream(pathFrom);
        if(!sexyInputStream.isOk())
        {
            notifyError(1,"'" + pathFrom + "'" + "read access denied OR doesn't exist");
            stopSelf();
            return;
        }
        Log.e(TAG,pathFrom+"@@"+pathTo+"--"+resumeAble+"--"+size);

        //GETTING THE OUTPUT STREAM
        CommonUtils commonUtils=new CommonUtils();
        OutputStream outputStream=commonUtils.getOutputStream(pathTo,resumeAble,context);
        if(outputStream==null)
        {
            sexyInputStream.close();

            if(SuperUser.hasUserEnabledSU)
            {
                //cp command
                cpCommand=true;
                if(RootTools.getInternals().copyFile(pathFrom,pathTo,true,false))
                {
                    copyData.downloadedSize+=size;
                    setProgress();

                    if(oldTime< copyData.timeInSec )
                    {
                        notifyProgressPlease();
                    }

                    if(copyData.pauseDownloadingPlease)
                    {
                        tinyDB.putString(operationCode+"CurrentDestinationPath",slashAppender(copyData.toRootPath, copyData.currentFileName));
                        tinyDB.putString(operationCode+"currentFileName", copyData.currentFileName);
                        tinyDB.putInt(operationCode+"currentFileIndex", copyData.currentFileIndex);

                        notifyMessage("Paused",false);
                        return;
                    }
                    if (copyData.cancelDownloadingPlease)
                    {
                        tinyDB.remove(operationCode+"IsRunning");
                        tinyDB.remove(operationCode+"CurrentDestinationPath");
                        tinyDB.remove(operationCode+"LastDestinationPath");

                        notifyMessage("Cancelled",false);
                        return;
                    }
                }
                else
                {
                    notifyError(15,"cp/cat command betrayed");
                    return;
                }
            }
            else
            {
                notifyError(2,"'" + pathTo + "'" + " write access denied");
                stopSelf();
            }
            return;
        }
        if(!sexyInputStream.skipBytes(copied))
        {
            notifyError(22,"'" + pathFrom + "'" + "skip bytes failed");
            stopSelf();
            return;
        }

        cpCommand=false;

        file_IO_Copier(sexyInputStream,outputStream,pathTo,pathFrom);
        //start copying ..........
    }

    public void file_IO_Copier(final SexyInputStream sexyInputStream,final OutputStream outputStream,final String pathTo,final String pathFrom)
    {
        Log.e(TAG,pathFrom+"--"+pathTo+"--"+sexyInputStream+"--"+outputStream);

        byte[] buf = new byte[61440];
        int len;


        while (true)
        {

            len = sexyInputStream.read(buf);
            if(len== Constants.SEXY_INPUT_STREAM_READ_FAILED)
            {
                notifyError(1,"'" + pathFrom + "'" + "read access denied OR doesn't exist");
                return;
            }
            if(len<=0)
            {
                break;
            }

            if(copyData.pauseDownloadingPlease)
            {
                tinyDB.putString(operationCode+"CurrentDestinationPath",slashAppender(copyData.toRootPath, copyData.currentFileName));
                tinyDB.putString(operationCode+"currentFileName", copyData.currentFileName);
                tinyDB.putInt(operationCode+"currentFileIndex", copyData.currentFileIndex);

                notifyMessage("Paused",false);
                return;
            }
            if (copyData.cancelDownloadingPlease)
            {
                tinyDB.remove(operationCode+"IsRunning");
                tinyDB.remove(operationCode+"CurrentDestinationPath");
                tinyDB.remove(operationCode+"LastDestinationPath");

                notifyMessage("Cancelled",false);
                return;
            }


            //writing to output stream
            try
            {
                outputStream.write(buf, 0, len);
            }
            catch (IOException e)
            {
                try
                {
                    sexyInputStream.close();
                    outputStream.close();
                }
                catch (Exception ex) {}

                notifyError(4,"'" + pathTo + "'" + "is missing Or write access denied");
                return;
            }
            catch (NullPointerException e)
            {
                try
                {
                    sexyInputStream.close();
                    outputStream.close();
                }
                catch (Exception ex) {}

                notifyError(5,"'" + pathTo + "'");
                return;

            }
            catch (IndexOutOfBoundsException e)
            {
                try
                {
                    sexyInputStream.close();
                    outputStream.close();
                }
                catch (Exception ex) {}

                notifyError(6,"'" + pathTo + "'");
                return;
            }

            copyData.downloadedSize+=len;
            setProgress();

            if(oldTime< copyData.timeInSec )
            {
                notifyProgressPlease();
            }
        }


        LastDestinationPath=pathTo;


        try
        {
            sexyInputStream.close();
            outputStream.close();
        }
        catch (Exception e) {}

    }

    public void folderCopier(String toPath)
    {
        if(CurrentDestinationPath.equals(LastDestinationPath))
        {
            return;
        }
        if(createNewUtils.createInLocal(toPath,true,false))
        {
            LastDestinationPath=toPath;
        }
        else
        {
            notifyError(7,"'" + toPath + "'" + " failed to create");
        }
    }

    @Override
    public void onCreate()
    {
        operationCode=101;
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
        OneFile toOneFile;
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
                toOneFile=new OneFile(slashAppender(copyData.toRootPath,x1),context);
                if(toOneFile.isExist())
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
                toOneFile=new OneFile(slashAppender(copyData.toRootPath,x1+x2),context);
                if(toOneFile.isExist())
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

    private void setProgress()
    {
        if(copyData.totalSizeToDownload==0)
        {
            copyData.progress=0;
        }
        else
        {
            copyData.progress= copyData.downloadedSize*100/copyData.totalSizeToDownload;
        }

    }

    private String slashAppender(String a,String b)
    {
        if(a.endsWith("/") && b.startsWith("/"))
        {
            a=a.substring(0,a.length()-1);
            return a+b;
        }
        if(a.endsWith("/") || b.startsWith("/"))
            return a+b;
        else
           return a+"/"+b;
    }

    private boolean checkIsError17(String fromPath)
    {
        if(copyData.isMoving && copyData.toRootPath.startsWith(fromPath))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean tryingFastMoved(int index,String toPath)
    {
        Log.e("TRYING FAST:","--"+index+"'--"+ copyData.pathsList.get(index));
        File toJavaFile=new File(toPath);
        File fromJavaFile=new File(copyData.pathsList.get(index));

        if(fromJavaFile.renameTo(toJavaFile))
        {
            copyData.downloadedSize+= copyData.sizeLongList.get(index);
            setProgress();
            copyData.currentFileIndex++;
            if(oldTime< copyData.timeInSec )
            {
                notifyProgressPlease();
            }
            String folderName= copyData.namesList.get(index)+"/";
            for(int j = index+1; j< copyData.totalFiles; j++)
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
            return true;
        }
        else
        {
            return false;
        }
    }

    private void notifyMessage(String message,boolean vibrate)
    {
        notifyProgressWithMessage(taskName+" "+message,copyData.currentFileName,vibrate);
    }

    private void notifyError(int errorCode,String errorDetail)
    {
        copyData.isDownloadError=true;
        copyData.downloadErrorCode=errorCode;
        copyData.errorDetails=errorDetail;
        Log.e(TAG, ErrorHandler.getErrorName(errorCode)+"-->"+ copyData.errorDetails);
        notifyProgressWithMessage(taskName+" Error...",copyData.currentFileName,true);

    }

    private void notifyProgressPlease()
    {
        oldTime= copyData.timeInSec;
        mBuilder.setProgress(100,(int) copyData.progress,false);
        mBuilder.setContentText(copyData.currentFileName);
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void notifyProgressWithMessage(String msg,String content,boolean vibrate)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int) copyData.progress,false)
                .setContentText(content)
                .setOngoing(false)
                .setAutoCancel(true);
        if(vibrate)
        {
            mBuilder.setVibrate(new long[]{1000,1000,1000});
        }

        mNotifyManager.notify(operationCode,mBuilder.build());
    }


/*
    101LastDestinationPath:
    it is used to deal with cases when downloading of that file is completed but is asked again to download next time when service starts
    case 1: when file is downloaded and service stops while doing isMoving OPERATION(which may be time taking)
    case 2: when file is downloaded but user asks it to pause

    101CurrentDestinationPath:
    it holds the value,from the time when downloading starts till that file is downloaded(_and OR isMoving)


    resume:
    if ( 201CurrentDestinationPath is not empty  && file(201CurrentDestinationPath) exists )

    uploadToTinyDB1() deals with values of 201 cache
    uploadToTinyDB2()  deals with values of 201 cache and is run only when file is downloaded

 */


}