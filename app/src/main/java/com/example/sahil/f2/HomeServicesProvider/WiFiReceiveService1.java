package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.superCache;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.OneFile;
import com.example.sahil.f2.Classes.WiFiSendData;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.CommonUtils;
import com.example.sahil.f2.Utilities.CreateNewUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Pattern;

public class WiFiReceiveService1 extends Service
{
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;
    public static Thread myOperationThread;
    private int oldTime=-1;
    private final int operationCode=501;
    final String TAG="501_SERVICE";
    private WiFiSendData wiFiSendData;
    private Socket socket;
    Context context;
    CreateNewUtils createNewUtils;
    CommonUtils commonUtils;
    private BufferedInputStream bufferedInputStream;


    @Override
    public int onStartCommand(Intent intent, int flags, final int startid)
    {
        Log.i(TAG,"service called");

        Intent myIntent;
        PendingIntent myPendingIntent;

        myIntent= new Intent(this, MainActivity.class);
        myIntent.putExtra("notification",5);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myPendingIntent=PendingIntent.getActivity(getApplicationContext(),operationCode,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        mNotifyManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.search_icon)
                .setContentTitle("Sending..")
                .setContentText("Calculating...")
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(myPendingIntent)
                .setProgress(100,0,true);
        mNotifyManager.notify(operationCode,mBuilder.build());




        myOperationThread=new Thread()
        {
            @Override
            public void run()
            {
                receiveTask();
            }
        };
        myOperationThread.start();


        return START_STICKY;
    }

    public void receiveTask()
    {
        context=getApplicationContext();
        createNewUtils=new CreateNewUtils(context);
        commonUtils=new CommonUtils();
        oldTime=0;
        boolean resumeAble;
        String toParentPath=wiFiSendData.toParentPath;
        //IF TO ROOT FOLDER DOES NOT EXIST CREATE IT
        OneFile toParentFile=new OneFile(toParentPath,context);
        if(!toParentFile.isExist())
        {
            createNewUtils.createInLocal(toParentPath,true,false);
        }
        try
        {
            bufferedInputStream=new BufferedInputStream(socket.getInputStream());
        }
        catch (Exception e)
        {
            notifyError(24,"failed to read input stream");
            stopSelf();
            return;
        }

        for(int i=0;i< wiFiSendData.serializablePacket.pathList.size();i++)
        {
            String toPath=slashAppender(toParentPath, wiFiSendData.serializablePacket.nameList.get(i));
            wiFiSendData.currentFileName= wiFiSendData.serializablePacket.nameList.get(i);

            File toFile=new File(toPath);
            if(toFile.exists())
            {
                keepBoth(i);
                wiFiSendData.currentFileName= wiFiSendData.serializablePacket.nameList.get(i);
            }


            if (wiFiSendData.serializablePacket.isFolderList.get(i))
            {
                folderCopier(toPath);
            }
            else
            {
                file_IO_Copier(toPath,wiFiSendData.serializablePacket.sizeLongList.get(i));
            }


            if(!wiFiSendData.isDownloadError && !wiFiSendData.cancelDownloadingPlease)
            {
                //no problem in downloading
                wiFiSendData.currentFileIndex++;
            }
            else
            {
                stopSelf();
                return;
            }

            Log.e(TAG,"Received file "+ wiFiSendData.currentFileIndex+ " of " + wiFiSendData.totalFiles);
        }

        wiFiSendData.progress=100;
        wiFiSendData.downloadedSize= wiFiSendData.serializablePacket.totalSizeToDownload;

        mBuilder.setContentTitle("Received Successfully .....")
                .setProgress(100,100,false)
                .setOngoing(false)
                .setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
        stopSelf();
    }


    public void file_IO_Copier(final String pathTo,long size)
    {
        Log.e(TAG,pathTo+"--"+pathTo+"--"+size+"--"+size);

        byte[] buf = new byte[61440];
        int len;

        OutputStream outputStream=commonUtils.getOutputStream(pathTo,false,context);
        if(outputStream==null)
        {
            notifyError(2,"'" + pathTo + "'" + " write access denied");
            stopSelf();
            return;
        }

        while (true)
        {
            if (wiFiSendData.cancelDownloadingPlease)
            {
                cancelProgress();
                return;
            }

            if(size>0)
            {
                try
                {
                    try
                    {
                        len=bufferedInputStream.read(buf,0,(int)Math.min(buf.length,size));
                        if(len==-1)
                        {
                            break;
                        }
                    }
                    catch (Exception e)
                    {
                        notifyError(24,"failed to read from input stream");
                        return;
                    }

                    try
                    {
                        outputStream.write(buf,0,len);
                    }
                    catch (Exception e)
                    {
                        notifyError(4,"'" + pathTo + "'" + "is missing Or write access denied");
                        return;
                    }

                    size-=len;
                    wiFiSendData.downloadedSize+=len;
                    setProgress();

                    if(oldTime< wiFiSendData.timeInSec )
                    {
                        notifyProgressPlease();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG,"error:"+e);
                    notifyError(24,"unknown error");
                    return;
                }
            }
            else
            {
                break;
            }
        }


        try
        {
            outputStream.close();
        }
        catch (Exception e) {}

    }


    @Override
    public void onCreate()
    {
        wiFiSendData = superCache.wiFiSendData3;
        socket=superCache.receiverSocket1;
        wiFiSendData.isServiceRunning=true;
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
        wiFiSendData.isServiceRunning=false;//very very important
        tasksCache.removeTask(operationCode+"");
        try
        {
            socket.close();
        }
        catch (Exception e)
        {

        }
        if(wiFiSendData.isDownloadError)
        {

        }
        else
        {
            mBuilder.setContentTitle("Received Successfully.....")
                    .setProgress(100,100,false)
                    .setOngoing(false)
                    .setAutoCancel(true);
            mNotifyManager.notify(operationCode,mBuilder.build());
        }
    }

    private void notifyProgressPlease()
    {
        oldTime= wiFiSendData.timeInSec;
        mBuilder.setProgress(100,(int) wiFiSendData.progress,false);
        mBuilder.setContentText(wiFiSendData.currentFileName);
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void setProgress()
    {
        if(wiFiSendData.serializablePacket.totalSizeToDownload==0)
        {
            wiFiSendData.progress=0;
        }
        else
        {
            wiFiSendData.progress= wiFiSendData.downloadedSize*100/wiFiSendData.serializablePacket.totalSizeToDownload;
        }

    }

    private void cancelProgress()
    {
        mBuilder.setContentTitle("Receiving Aborted....")
                .setOngoing(false).setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
        mNotifyManager.cancel(operationCode);
    }

    private void notifyError(int errorCode,String errorDetail)
    {
        wiFiSendData.isDownloadError=true;
        wiFiSendData.downloadErrorCode=errorCode;
        wiFiSendData.errorDetails=errorDetail;
        Log.e(TAG, ErrorHandler.getErrorName(errorCode)+"-->"+ wiFiSendData.errorDetails);
        notifyProgressWithMessage( "Error Receiving...", wiFiSendData.currentFileName,true);
    }

    private void notifyProgressWithMessage(String msg,String content,boolean vibrate)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int) wiFiSendData.progress,false)
                .setContentText(content)
                .setOngoing(false)
                .setAutoCancel(true);
        if(vibrate)
        {
            mBuilder.setVibrate(new long[]{1000,1000,1000});
        }
        mNotifyManager.notify(operationCode,mBuilder.build());

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

    public void keepBoth(int i)
    {
        int fileNumber=0;
        File toFile;
        if(wiFiSendData.serializablePacket.isFolderList.get(i))        //is a directory
        {
            String x1= wiFiSendData.serializablePacket.nameList.get(i)+"("+(++fileNumber)+")";
            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
             *
             *
             * like if a.txt and a(1).txt is present how to copy???
             *
             */
            while(true)
            {
                toFile=new File(slashAppender(wiFiSendData.toParentPath,x1));
                if(toFile.exists())
                {
                    x1= wiFiSendData.serializablePacket.nameList.get(i)+"("+(++fileNumber)+")";
                }
                else
                {
                    break;
                }
            }

            //first changing the inside files name then at last change the foldder name
            for(int j = i+1; j< wiFiSendData.serializablePacket.nameList.size(); j++)
            {
                if(wiFiSendData.serializablePacket.nameList.get(j).startsWith(wiFiSendData.serializablePacket.nameList.get(i)))
                {
                    String x2= wiFiSendData.serializablePacket.nameList.get(j).replaceFirst(Pattern.quote(wiFiSendData.serializablePacket.nameList.get(i)),x1);
                    wiFiSendData.serializablePacket.nameList.set(j,x2);
                }
                else//no need to check for more files in the list
                {
                    break;
                }
            }
            wiFiSendData.serializablePacket.nameList.set(i,x1);
        }
        else //is a file
        {
            String x= wiFiSendData.serializablePacket.nameList.get(i); //.....'vishwas.txt'------'mydiiiicccc'
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
                toFile=new File(slashAppender(wiFiSendData.toParentPath,x1+x2));
                if(toFile.exists())
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
            wiFiSendData.serializablePacket.nameList.set(i,x1+x2);
        }
        Log.i(TAG,"renaming file");
    }

    public void folderCopier(String toPath)
    {
        if(createNewUtils.createInLocal(toPath,true,false))
        {

        }
        else
        {
            notifyError(7,"'" + toPath + "'" + " failed to create");
        }
    }


}
