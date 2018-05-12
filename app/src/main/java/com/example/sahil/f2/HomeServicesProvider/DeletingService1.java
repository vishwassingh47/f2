package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.DeleteData;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Ftp.MyFtpClient;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.DeleteUtils;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

/**
 * Created by hit4man47 on 12/25/2017.
 */

public class DeletingService1 extends Service
{
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;
    public static Thread myOperationThread;
    private int oldTime=-1;
    private TinyDB tinyDB;
    private FTPClient ftpClient;
    private DeleteUtils deleteUtils;
    private DeleteData deleteData;
    private final int operationCode=1;
    final String TAG="1_SERVICE";

    @Override
    public int onStartCommand(Intent intent, int flags, final int startid)
    {
        Log.i(TAG,"service called");

        Intent myIntent;
        PendingIntent myPendingIntent;

        tinyDB=new TinyDB(getApplicationContext());
        myIntent= new Intent(this, MainActivity.class);
        myIntent.putExtra("notification",5);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myPendingIntent=PendingIntent.getActivity(getApplicationContext(),operationCode,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        mNotifyManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.search_icon)
                .setContentTitle("Deleting")
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
                deleteTask();
            }
        };
        myOperationThread.start();



        return START_STICKY;
    }

    public void deleteTask()
    {
        ftpClient=null;
        
        if(deleteData.fromStorageId==6)
        {
            connectToFtp();
        }
        deleteUtils=new DeleteUtils(ftpClient,getApplicationContext());
        for(int i=0;i< deleteData.pathsList.size();i++)
        {
            deleteData.currentFileName=deleteData.namesList.get(i);
            if(deleteData.cancelDownloadingPlease)
            {
                cancelProgress();
                return;
            }

            if(oldTime<deleteData.timeInSec)
            {
                notifyProgress(deleteData.namesList.get(i));
            }

            
            if(deleteData.fromStorageId==6)
            {
                deleteData.isErrorList.set(i,!deleteUtils.deleteFromFtpServer(deleteData.pathsList.get(i),deleteData.folderList.get(i)?2:1));
                deleteData.currentFileIndex++;
                continue;
            }
            if(deleteData.fromStorageId==5)
            {
                deleteData.isErrorList.set(i,!deleteUtils.deleteFromDropBox(deleteData.pathsList.get(i)));
                deleteData.currentFileIndex++;
                continue;
            }
            if(deleteData.fromStorageId==4)
            {
                deleteData.isErrorList.set(i,!deleteUtils.deleteFromGoogleDrive(deleteData.pathsList.get(i),deleteData.shouldRecycle));
                deleteData.currentFileIndex++;
                continue;
            }
            if(deleteData.fromStorageId%10==0)
            {
                File file=new File(deleteData.pathsList.get(i));
                deleteData.isErrorList.set(i,!deleteUtils.deleteDocumentFile(file));
                deleteData.currentFileIndex++;
                continue;
            }
            if(deleteData.fromStorageId>=1 && deleteData.fromStorageId<=3)
            {
                if(deleteData.shouldRecycle)
                {
                    File file=new File(deleteData.pathsList.get(i));
                    deleteData.isErrorList.set(i,!deleteUtils.moveToRecycleBin(file));
                }
                else
                {
                    File file=new File(deleteData.pathsList.get(i));
                    deleteData.isErrorList.set(i,!deleteUtils.deleteFromInternal(file));
                }
                deleteData.currentFileIndex++;
                continue;
            }
            
        }


        mBuilder.setContentTitle("Deletion Complete.....")
                .setProgress(100,100,false)
                .setOngoing(false)
                .setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
        stopSelf();
    }

    @Override
    public void onCreate()
    {
        ftpClient=null;
        deleteData=null;
        deleteData= MyCacheData.getDeleteDataFromCode(operationCode);
        deleteData.isServiceRunning=true;
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
        deleteData.isServiceRunning=false;//very very important

        if(deleteData.isDownloadError)
        {
        }
        else
        {
            tasksCache.removeTask(operationCode+"");

            mBuilder.setContentTitle("Deletion Complete.....")
                    .setProgress(100,100,false)
                    .setOngoing(false)
                    .setAutoCancel(true);
            mNotifyManager.notify(operationCode,mBuilder.build());


        }

    }
    
    private void connectToFtp()
    {
        try
        {
            if(FtpCache.currentFullUrl==null)
            {
            }
            else
            {
                MyFtpClient myFtpClient=new MyFtpClient();
                ftpClient=myFtpClient.connect(FtpCache.currentFullUrl);
            }
        }
        catch (Exception e)
        {
            ftpClient=null;
        }
    }

    private void notifyProgress(String name)
    {
        oldTime=deleteData.timeInSec;
        mBuilder.setContentText(name);
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void cancelProgress()
    {
        mBuilder.setContentTitle("Deletion Aborted....")
        .setOngoing(false).setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
        mNotifyManager.cancel(operationCode);
    }

    

}
