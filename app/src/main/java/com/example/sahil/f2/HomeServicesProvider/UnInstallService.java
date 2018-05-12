package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.UnInstallData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.MyApp;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.AppManagerUtils;

/**
 * Created by hit4man47 on 2/16/2018.
 */

public class UnInstallService extends Service
{
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;
    public static Thread myOperationThread;
    private int oldTime=-1;
    private final int operationCode=199;
    private UnInstallData unInstallData;
    final String TAG="199_SERVICE";


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
                .setContentTitle("UnInstalling")
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
                uninstallTask();
            }
        };
        myOperationThread.start();



        return START_STICKY;

    }


    private void uninstallTask()
    {
        AppManagerUtils appManagerUtils=new AppManagerUtils(getApplicationContext());
        for(int i=0;i<unInstallData.myFilesList.size();i++)
        {
            MyApp myApp=unInstallData.myFilesList.get(i).getMyApp();
            unInstallData.progress=(i*100)/unInstallData.totalFiles;
            if(unInstallData.cancelPlease)
            {
                cancelProgress();
                return;
            }

            if(oldTime<unInstallData.timeInSec)
            {
                notifyProgress();
            }

            boolean x=appManagerUtils.unInstallAsSuperUser(myApp.getPackageName(),myApp.getApkPath(),false);
            if(x)
            {
                unInstallData.successCount++;
            }
            else
            {
                unInstallData.failedCount++;
            }
            unInstallData.isErrorList.set(i,!x);

            unInstallData.currentFileIndex++;

        }

        unInstallData.progress=100;

        notifyProgressWithMessage("UnInstallation Complete.....");

        stopSelf();

    }


    @Override
    public void onCreate()
    {
        unInstallData=null;
        unInstallData= MyCacheData.getUnInstallData(operationCode);
        unInstallData.isServiceRunning=true;
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
        unInstallData.isServiceRunning=false;//very very important

        if(unInstallData.cancelPlease || unInstallData.currentFileIndex==unInstallData.totalFiles)
        {
            tasksCache.removeTask(operationCode+"");
        }
        else
        {
            notifyProgressWithMessage("UnInstallation Failed.....");
        }
    }



    private void cancelProgress()
    {
        mBuilder.setContentTitle("UnInstallation Aborted....")
                .setOngoing(false).setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
        mNotifyManager.cancel(operationCode);
    }

    private void notifyProgress()
    {
        oldTime=unInstallData.timeInSec;
        mBuilder.setContentText("Success:"+unInstallData.successCount+",Failed:"+unInstallData.failedCount);
        mBuilder.setProgress(100,(int)unInstallData.progress,false);
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void notifyProgressWithMessage(String msg)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int)unInstallData.progress,false)
                .setContentText("Success:"+unInstallData.successCount+",Failed:"+unInstallData.failedCount)
                .setOngoing(false)
                .setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
    }




}
