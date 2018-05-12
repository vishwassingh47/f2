package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.InstallData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.MyApp;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.AppManagerUtils;

/**
 * Created by hit4man47 on 2/16/2018.
 */

public class InstallService extends Service
{
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;
    public static Thread myOperationThread;
    private int oldTime=-1;
    private final int operationCode=99;
    private InstallData installData;
    final String TAG="99_SERVICE";


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
                .setContentTitle("Installing")
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
                installTask();
            }
        };
        myOperationThread.start();


        return START_STICKY;

    }


    private void installTask()
    {
        AppManagerUtils appManagerUtils=new AppManagerUtils(getApplicationContext());
        for(int i=0;i<installData.myFilesList.size();i++)
        {
            MyApp myApp=installData.myFilesList.get(i).getMyApp();
            installData.progress=(i*100)/installData.totalFiles;
            if(installData.cancelPlease)
            {
                cancelProgress();
                return;
            }

            if(oldTime<installData.timeInSec)
            {
                notifyProgress();
            }

            int pmOrUserOrSystem=installData.pmOrUserOrSystem;

            if(pmOrUserOrSystem==1 && installData.pmFailed)
            {
                pmOrUserOrSystem=2;
            }

            boolean x=appManagerUtils.installAsSuperUser(myApp.getPackageName(),myApp.getApkPath(),false,pmOrUserOrSystem);
            if(x)
            {
                installData.successCount++;
            }
            else
            {
                installData.failedCount++;
            }
            installData.isErrorList.set(i,!x);

            installData.currentFileIndex++;

        }

        installData.progress=100;

        notifyProgressWithMessage("Installation Complete.....");

        stopSelf();

    }


    @Override
    public void onCreate()
    {
        installData=null;
        installData= MyCacheData.getInstallData(operationCode);
        installData.isServiceRunning=true;
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
        installData.isServiceRunning=false;//very very important

        if(installData.cancelPlease || installData.currentFileIndex==installData.totalFiles)
        {
            tasksCache.removeTask(operationCode+"");
        }
        else
        {
            notifyProgressWithMessage("Installation Failed.....");
        }
    }



    private void cancelProgress()
    {
        mBuilder.setContentTitle("Installation Aborted....")
                .setOngoing(false).setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
        mNotifyManager.cancel(operationCode);
    }

    private void notifyProgress()
    {
        oldTime=installData.timeInSec;
        mBuilder.setContentText("Success:"+installData.successCount+",Failed:"+installData.failedCount);
        mBuilder.setProgress(100,(int)installData.progress,false);
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void notifyProgressWithMessage(String msg)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int)installData.progress,false)
                .setContentText("Success:"+installData.successCount+",Failed:"+installData.failedCount)
                .setOngoing(false)
                .setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
    }




}
