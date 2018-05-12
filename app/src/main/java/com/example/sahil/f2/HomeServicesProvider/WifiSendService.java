package com.example.sahil.f2.HomeServicesProvider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Cache.wifiSendCache;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;


/**
 * Created by hit4man47 on 9/2/2017.
 */

public class WifiSendService extends Service
{

    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;
    HelpingBot helpingBot;

    int oldTime=0;

    Socket socket = null;

    String ip="";
    public Thread threadToSend;


    public void sendTask()
    {
        oldTime=0;

        try
        {


            wifiSendCache.log+="connecting to server ("+ip+") on 8080...\n";
            socket = new Socket(ip, 8080);
            wifiSendCache.log+="connected to server ("+ip+") on 8080...\n";

            wifiSendCache.log+="sending file information to server...\n";
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<ArrayList> superList = new ArrayList<>();

            superList.add(wifiSendCache.namesList);
            superList.add(wifiSendCache.folderList);
            superList.add(wifiSendCache.sizeLongList);

            oos.writeObject(superList);
            oos.close();
            socket.close();
            wifiSendCache.log+="file details to server sent...\n";

            if(!MainActivity.paster.ipConnectedList.contains(ip))
            {
                MainActivity.paster.ipConnectedList.add(ip);
            }

        }
        catch(Exception e)
        {
            wifiSendCache.isDownloadError=true;
            wifiSendCache.downloadErrorCode=3;

            wifiSendCache.log+="failed connecting to the server...\n";
            wifiSendCache.log+=e.getMessage()+"...\n";
        }


            if(!wifiSendCache.isDownloadError)
            while (wifiSendCache.namesList.size() > 0)
            {

                wifiSendCache.currentFileName = wifiSendCache.namesList.get(0);
                wifiSendCache.currentFileNumber = wifiSendCache.totalfiles - wifiSendCache.namesList.size() + 1;
                wifiSendCache.log+="sending item "+wifiSendCache.namesList.get(0)+"--"+wifiSendCache.currentFileNumber+"/"+wifiSendCache.totalfiles+ "...\n";


                if (wifiSendCache.folderList.get(0))
                {
                   //no need to send folder
                }
                else
                {
                    while (true)
                    {
                        try
                        {
                            wifiSendCache.log+="pinging with server ...\n";
                            socket = new Socket(ip, 8080);
                            break;
                        }
                        catch (Exception e)
                        {
                            wifiSendCache.log+="Pinging with server again...\n";
                            Log.e("trying to reconnect to ", "--" + ip);
                        }
                    }


                    filecopier(0);
                }


                if (wifiSendCache.isDownloadError||wifiSendCache.stopDownloadingPlease)
                {
                   break;
                }




                wifiSendCache.namesList.remove(0);
                wifiSendCache.pathsList.remove(0);
                wifiSendCache.folderList.remove(0);
                wifiSendCache.sizeLongList.remove(0);


            }

            if(wifiSendCache.isDownloadError||wifiSendCache.stopDownloadingPlease)
            {
                if (wifiSendCache.isDownloadError)
                {
                    mBuilder.setContentTitle("ERROR while transferring...");
                    mBuilder.setVibrate(new long[]{1000, 1000, 1000});
                    mBuilder.setContentText(wifiSendCache.namesList.get(0));
                    mNotifyManager.notify(6, mBuilder.build());
                    stopSelf();
                    return;
                }
                if (wifiSendCache.stopDownloadingPlease)
                {
                    mBuilder.setContentTitle("Transfer stopped...");
                    mBuilder.setVibrate(new long[]{1000, 1000, 1000});
                    mBuilder.setContentText(wifiSendCache.namesList.get(0));
                    mNotifyManager.notify(6, mBuilder.build());
                    stopSelf();
                    return;
                }

            }
            else
            {
                wifiSendCache.log+="*********** Sent all the files successfully*************\n";

                Log.e("all done","downloading is completed.........................");

                mBuilder.setContentTitle("Transfer Successfull...");
                mBuilder.setVibrate(new long[]{1000,1000,1000});
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mBuilder.setProgress(100,100,false);
                mBuilder.setContentText("Done....");//there is some error
                mNotifyManager.notify(6,mBuilder.build());
                stopSelf();
            }



    }


    public void filecopier(int i)
    {

        String pathform= wifiSendCache.pathsList.get(i);
        File file = new File(pathform);

        byte[] buffer = new byte[61440];

        try
        {

            OutputStream os = socket.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));


            int length = 0;
            long total = 0;
            while ((length = bis.read(buffer)) > 0)
            {
                Log.e(i + " th file", length + "..");
                os.write(buffer, 0, length);
                wifiSendCache.downloadedsize+=length;
                if(wifiSendCache.totalsizetodownload==0)
                {
                    wifiSendCache.progress=0;
                }
                else
                {
                    wifiSendCache.progress=wifiSendCache.downloadedsize*100/wifiSendCache.totalsizetodownload;
                }

                if(oldTime< wifiSendCache.timeinsec )
                {
                    oldTime= wifiSendCache.timeinsec;
                    mBuilder.setProgress(100,(int)wifiSendCache.progress,false);
                    mBuilder.setContentText(wifiSendCache.namesList.get(i));
                    mNotifyManager.notify(6,mBuilder.build());
                }


                if(wifiSendCache.stopDownloadingPlease)
                {
                    break;
                }

            }

            wifiSendCache.log+=total+ "bytes sent...\n";
            Log.e(i + " done", (total / 1048576) + "mb");

            bis.close();
            os.flush();
            os.close();
            socket.close();
        }
        catch (Exception e)
        {
            wifiSendCache.log+="failed in between..."+e.getMessage()+"\n";
            wifiSendCache.isDownloadError=true;
        }

    }


    public int onStartCommand(Intent intent, int flags, final int startid)
    {

        wifiSendCache.log+="service started....\n";


        ip=intent.getStringExtra("ip");


        helpingBot=new HelpingBot();

        mNotifyManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.search_icon)
                .setContentTitle("Sending...")
                .setContentText("Calculating...")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setProgress(100,0,false);
        mNotifyManager.notify(6,mBuilder.build());



        threadToSend=new Thread()
        {
            @Override
            public void run()
            {
                wifiSendCache.log+="new thread assigned...\n";
                sendTask();
            }
        };
        threadToSend.start();
        return START_STICKY;
    }


    @Override
    public void onCreate()
    {
        wifiSendCache.log+="service created...\n";
        wifiSendCache.isServiceRunning=true;
        tasksCache.tasksId.add("501");

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return  null;
    }

    @Override
    public void onDestroy()
    {
        wifiSendCache.isServiceRunning=false;
        wifiSendCache.log+="service destroyed...\n";

        if(wifiSendCache.isDownloadError)
        {
            wifiSendCache.log+="Service not cleaned from TaskManger...\n";
        }
        else
        {
            wifiSendCache.log+="Service cleaned from TaskManger...\n";
            tasksCache.removeTask("501");
        }
    }


}
