package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.superCache;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.SexyInputStream;
import com.example.sahil.f2.Classes.WiFiSendData;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WiFiSendService1 extends Service
{
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;
    public static Thread myOperationThread;
    private int oldTime=-1;
    private final int operationCode=401;
    final String TAG="401_SERVICE";
    private WiFiSendData wiFiSendData;
    private Socket socket;
    private BufferedOutputStream bufferedOutputStream;


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
                sendTask();
            }
        };
        myOperationThread.start();



        return START_STICKY;
    }

    public void sendTask()
    {
        oldTime=0;
        try
        {
           socket=new Socket(wiFiSendData.serializablePacket.receiverIP,Constants.HANDSHAKE_PORT);
        }
        catch (Exception e)
        {
            notifyError(23,"failed to create object");
            stopSelf();
            return;
        }


        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            oos.writeObject(wiFiSendData.serializablePacket);
            oos.flush();
            Log.e(TAG,"packet sent");
        }
        catch (Exception e)
        {
            Log.e(TAG,"packet send error"+e);
            notifyError(23,"failed to send initial data to server");
            stopSelf();
            return;
        }


        try
        {
            Log.e(TAG,"waiting for response from receiver");
            DataInputStream dis=new DataInputStream(socket.getInputStream());
            String response=dis.readUTF();
            Log.e(TAG,"response from receiver:"+response);

            if(response.equals(Constants.ACCEPT_WIFI_DATA))
            {
                Log.e(TAG,"response OK");
            }
            else
            {
                if(response.equals(Constants.REJECT_WIFI_DATA))
                {
                    notifyError(25,"");
                    stopSelf();
                    return;
                }
                else
                {
                    notifyError(23,"Unknown Response");
                    stopSelf();
                    return;
                }
            }
        }
        catch(Exception e)
        {
            Log.e("ex",e+"--");
            notifyError(23,"failed to read response");
            stopSelf();
            return;
        }

        try
        {
            bufferedOutputStream=new BufferedOutputStream(socket.getOutputStream());
        }
        catch (Exception e)
        {
            Log.e(TAG,"OUTPUT STREAM:"+e+"--");
            notifyError(23,"failed to open output stream");
            stopSelf();
            return;
        }

        for(int i=0;i< wiFiSendData.serializablePacket.pathList.size();i++)
        {
            String path=wiFiSendData.serializablePacket.pathList.get(i);
            wiFiSendData.currentFileName= wiFiSendData.serializablePacket.nameList.get(i);

            if(wiFiSendData.serializablePacket.isFolderList.get(i))
            {
                //ignore it is a folder
            }
            else
            {
                uploadFile(path);
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

            Log.e(TAG,"Uploaded file "+ wiFiSendData.currentFileIndex+ " of " + wiFiSendData.totalFiles);
        }

        wiFiSendData.progress=100;
        wiFiSendData.downloadedSize= wiFiSendData.serializablePacket.totalSizeToDownload;

        mBuilder.setContentTitle("Transfer Complete .....")
            .setProgress(100,100,false)
            .setOngoing(false)
            .setAutoCancel(true);
        mNotifyManager.notify(operationCode,mBuilder.build());
        stopSelf();
    }


    private void uploadFile(String path)
    {
        SexyInputStream sexyInputStream=new SexyInputStream(path);
        if(sexyInputStream.isOk())
        {
            byte[] buf = new byte[61440];
            int len;

            while (true)
            {
                len = sexyInputStream.read(buf);
                if(len== Constants.SEXY_INPUT_STREAM_READ_FAILED)
                {
                    notifyError(1,"'" + path + "'" + "read access denied OR doesn't exist");
                    return;
                }
                if(len<=0)
                {
                    break;
                }

                if (wiFiSendData.cancelDownloadingPlease)
                {
                    cancelProgress();
                    return;
                }

                //writing to output stream
                try
                {
                    bufferedOutputStream.write(buf, 0, len);
                }
                catch (Exception e)
                {
                    try
                    {
                        sexyInputStream.close();
                    }
                    catch (Exception ex) {}

                    notifyError(23,"failed to write to output stream");
                    return;
                }

                wiFiSendData.downloadedSize+=len;
                setProgress();

                if(oldTime< wiFiSendData.timeInSec )
                {
                    notifyProgressPlease();
                }
            }

            try
            {
                sexyInputStream.close();
            }
            catch (Exception ex) {}


        }
        else
        {
            notifyError(1,"'" + path + "'" + "read access denied OR doesn't exist");
            stopSelf();
            return;
        }
    }

    @Override
    public void onCreate()
    {
        wiFiSendData = superCache.wiFiSendData1;
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
            mBuilder.setContentTitle("Transfer Complete.....")
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
        mBuilder.setContentTitle("Transfer Aborted....")
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
        notifyProgressWithMessage( "Error Transferring...", wiFiSendData.currentFileName,true);
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


}
