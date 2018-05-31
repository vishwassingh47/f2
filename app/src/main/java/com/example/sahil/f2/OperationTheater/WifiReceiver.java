package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.superCache;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.SerializablePacket;
import com.example.sahil.f2.Classes.WiFiSendData;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.FunkyAdapters.CopyListAdapter;
import com.example.sahil.f2.HomeServicesProvider.WiFiReceiveService1;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import com.example.sahil.f2.UiClasses.Refresher;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WifiReceiver
{
    private ServerSocket serverSocketPhonePicker=null;
    private ServerSocket serverSocketHandShaker=null;
    private final String TAG="WiFiReceiver";
    private final MainActivity mainActivityObject;
    private Intent serviceIntent;

    public WifiReceiver(MainActivity mainActivityObject)
    {
        this.mainActivityObject=mainActivityObject;
        startPickingThePhone();
        listenForHandShake();
    }

    private void startPickingThePhone()
    {
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Socket socket=serverSocketPhonePicker.accept();
                        DataOutputStream outputStream=new DataOutputStream(socket.getOutputStream());
                        String deviceName=android.os.Build.MANUFACTURER +":"+ android.os.Build.MODEL;
                        outputStream.writeUTF(deviceName);
                        outputStream.flush();
                        outputStream.close();
                        socket.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG,"failed to accept serverSocketPhonePicker ->"+e);
                    }
                }

            }
        };

        if(serverSocketPhonePicker==null)
        {
            try
            {
                serverSocketPhonePicker=new ServerSocket(Constants.PHONE_PICKER_PORT);
                thread.start();
            }
            catch (Exception e)
            {
                serverSocketPhonePicker=null;
                Toast.makeText(mainActivityObject, "Failed to setup phone picker Server Socket", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listenForHandShake()
    {
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        final Socket socket=serverSocketHandShaker.accept();
                        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                        final SerializablePacket serializablePacket = (SerializablePacket) ois.readObject();
                        for(String x: serializablePacket.nameList)
                        {
                            Log.e("file :",x+"--");
                        }

                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                final Dialog dialog = new Dialog(mainActivityObject);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.wifi_receive_dialog);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);

                                final TextView senderDetails,totalSize;
                                final Button cancel,swipe,accept;
                                final ListView listView;
                                final LinearLayout page1,page2;

                                page1=(LinearLayout)dialog.findViewById(R.id.page1);
                                page2=(LinearLayout)dialog.findViewById(R.id.page2);

                                page1.setVisibility(View.VISIBLE);
                                page2.setVisibility(View.GONE);

                                cancel=(Button)dialog.findViewById(R.id.cancel);
                                swipe=(Button)dialog.findViewById(R.id.swipe);
                                accept=(Button) dialog.findViewById(R.id.accept);

                                senderDetails=(TextView)dialog.findViewById(R.id.sender_details);
                                totalSize=(TextView) dialog.findViewById(R.id.totalsize);


                                senderDetails.setText(serializablePacket.senderDeviceName+" ("+serializablePacket.senderIP+")");
                                HelpingBot helpingBot=new HelpingBot();
                                totalSize.setText(helpingBot.sizeinwords(serializablePacket.totalSizeToDownload));


                                accept.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Thread thread1=new Thread()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                try
                                                {
                                                    final WiFiSendData wiFiSendData=new WiFiSendData();
                                                    int taskId=0;
                                                    if(!tasksCache.tasksId.contains("501"))
                                                    {
                                                        tasksCache.addTask("501");
                                                        wiFiSendData.serializablePacket=serializablePacket;
                                                        superCache.wiFiSendData3 = wiFiSendData;
                                                        superCache.receiverSocket1=socket;
                                                        taskId=501;
                                                    }
                                                    else if (!tasksCache.tasksId.contains("502"))
                                                    {
                                                        tasksCache.addTask("502");
                                                        wiFiSendData.serializablePacket=serializablePacket;
                                                        superCache.wiFiSendData4 = wiFiSendData;
                                                        superCache.receiverSocket2=socket;
                                                        taskId=502;
                                                    }
                                                    if(taskId>0)
                                                    {
                                                        DataOutputStream  outputStream=new DataOutputStream(socket.getOutputStream());
                                                        outputStream.writeUTF(Constants.ACCEPT_WIFI_DATA);
                                                        outputStream.flush();
                                                        Log.e("response sent to sender","--");
                                                    }
                                                    else
                                                    {
                                                        Log.e(TAG,"SERVER TOO BUSY");
                                                        DataOutputStream  outputStream=new DataOutputStream(socket.getOutputStream());
                                                        outputStream.writeUTF(Constants.REJECT_WIFI_DATA);
                                                        outputStream.flush();
                                                        Log.e("response sent to sender","--");
                                                    }

                                                    final int taskId2=taskId;
                                                    Handler handler=new Handler(Looper.getMainLooper());
                                                    Runnable runnable=new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            dialog.cancel();
                                                            if(taskId2>0)
                                                            {
                                                                receiveFiles(wiFiSendData,taskId2);
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(mainActivityObject, "Server too busy", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    };
                                                    handler.post(runnable);

                                                }
                                                catch(Exception e)
                                                {
                                                    Log.e("--","Failed to send response to sender");
                                                }
                                            }
                                        };
                                        thread1.start();

                                    }
                                });


                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Thread thread1=new Thread()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                try
                                                {
                                                    DataOutputStream  outputStream=new DataOutputStream(socket.getOutputStream());
                                                    outputStream.writeUTF(Constants.REJECT_WIFI_DATA);
                                                    outputStream.flush();
                                                    Log.e("response sent to sender","--");
                                                }
                                                catch(Exception e)
                                                {
                                                    Log.e("--","Failed to send response to sender");
                                                }
                                            }
                                        };
                                        thread1.start();
                                        dialog.cancel();
                                    }
                                });


                                dialog.show();


                            }
                        };
                        mainHandler.post(myRunnable);

                    }
                    catch (Exception e)
                    {
                        Log.e("accccc","------"+e);
                    }
                }

            }
        };

        if(serverSocketHandShaker==null)
        {
            try
            {
                serverSocketHandShaker=new ServerSocket(Constants.HANDSHAKE_PORT);
                thread.start();
            }
            catch (Exception e)
            {
                serverSocketHandShaker=null;
                Toast.makeText(mainActivityObject, "Failed to setup handshake Server Socket", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void receiveFiles(final WiFiSendData wiFiSendData, final int operationId)
    {

        final TextView tv_taskName,tv_fileName,tv_fromPath,tv_toPath,tv_size,tv_itemProgress,tv_sizeProgress,tv_percent,tv_speed,tv_errorDetails;
        final ImageView iv_taskLogo,iv_toLogo,iv_fromLogo;
        final ProgressBar pb_progressBar;
        final Button btn_cancel,btn_open,btn_skip,btn_pause_resume,btn_swipe,btn_hide;
        final LinearLayout page1,page2;
        final ListView progressListView;

        final Dialog dialog = new Dialog(mainActivityObject);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_copy2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        tv_taskName=(TextView) dialog.findViewById(R.id.copy2_task_name);
        tv_fileName=(TextView) dialog.findViewById(R.id.copy2_name);
        tv_fromPath=(TextView) dialog.findViewById(R.id.copy2_from);
        tv_toPath=(TextView) dialog.findViewById(R.id.copy2_to);
        tv_size=(TextView) dialog.findViewById(R.id.copy2_size);
        tv_itemProgress=(TextView) dialog.findViewById(R.id.copy2_itemsProgress);
        tv_sizeProgress=(TextView) dialog.findViewById(R.id.copy2_sizeProgress);
        tv_percent=(TextView) dialog.findViewById(R.id.copy2_percent);
        tv_speed=(TextView) dialog.findViewById(R.id.copy2_speed);
        tv_errorDetails=(TextView) dialog.findViewById(R.id.copy2_errorDetails);

        iv_taskLogo=(ImageView)dialog.findViewById(R.id.copy2_logo);
        iv_fromLogo=(ImageView)dialog.findViewById(R.id.copy2_from_logo);
        iv_toLogo=(ImageView)dialog.findViewById(R.id.copy2_to_logo);

        pb_progressBar=(ProgressBar)dialog.findViewById(R.id.copy2_progressBar);

        btn_cancel=(Button)dialog.findViewById(R.id.copy2_cancel);
        btn_hide=(Button)dialog.findViewById(R.id.copy2_hide);
        btn_pause_resume=(Button)dialog.findViewById(R.id.copy2_pause_resume);
        btn_swipe=(Button)dialog.findViewById(R.id.copy2_swipe);
        btn_skip=(Button)dialog.findViewById(R.id.copy2_skip);
        btn_open=(Button)dialog.findViewById(R.id.copy2_open);

        page1=(LinearLayout)dialog.findViewById(R.id.copy2_page1);
        page2=(LinearLayout)dialog.findViewById(R.id.copy2_page2);

        progressListView=(ListView) dialog.findViewById(R.id.copy2_list);

        btn_cancel.setText("CANCEL");
        btn_hide.setText("HIDE");
        btn_pause_resume.setText("PAUSE");
        btn_pause_resume.setEnabled(false);
        btn_swipe.setText("DETAILS");
        btn_skip.setText("SKIP");
        btn_skip.setEnabled(false);
        btn_open.setText("OPEN");


        tv_errorDetails.setVisibility(View.GONE);
        btn_open.setVisibility(View.GONE);
        btn_skip.setVisibility(View.GONE);
        page1.setVisibility(View.VISIBLE);
        page2.setVisibility(View.GONE);



        final HelpingBot helpingBot=new HelpingBot();

        final String taskName=helpingBot.getTaskName(operationId);

        tv_taskName.setText(taskName);
        iv_taskLogo.setImageResource(helpingBot.getTaskLogo(operationId));

        iv_fromLogo.setImageResource(helpingBot.getPathLogo(1));
        tv_fromPath.setText(wiFiSendData.serializablePacket.senderIP+" ("+ wiFiSendData.serializablePacket.senderDeviceName+")");

        iv_toLogo.setImageResource(helpingBot.getPathLogo(6));
        tv_toPath.setText(wiFiSendData.serializablePacket.receiverIP+" ("+ wiFiSendData.serializablePacket.receiverDeviceName+")");

        tv_size.setText(helpingBot.sizeinwords(wiFiSendData.serializablePacket.totalSizeToDownload));


        wiFiSendData.totalFiles=wiFiSendData.serializablePacket.nameList.size();
        //temporary declaration
        tv_fileName.setText(wiFiSendData.currentFileName);
        tv_itemProgress.setText(wiFiSendData.currentFileIndex+"/"+ wiFiSendData.totalFiles +" items");
        tv_sizeProgress.setText(helpingBot.sizeinwords(wiFiSendData.downloadedSize)+"/"+helpingBot.sizeinwords(wiFiSendData.serializablePacket.totalSizeToDownload));
        tv_percent.setText(wiFiSendData.progress+" %");
        tv_speed.setText("calculating..");
        pb_progressBar.setProgress(0);


        btn_hide.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(btn_hide.getText().equals("HIDE"))
                {
                    dialog.hide();
                }
                if (btn_hide.getText().equals("CLOSE"))
                {
                    dialog.cancel();
                }
            }

        });

        btn_swipe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (page1.getVisibility()==View.GONE)
                {
                    btn_swipe.setText("DETAILS");
                    page1.setVisibility(View.VISIBLE);
                    page2.setVisibility(View.GONE);
                }
                else
                {
                    btn_swipe.setText("PROGRESS");
                    page1.setVisibility(View.GONE);
                    page2.setVisibility(View.VISIBLE);
                }
            }
        });



        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                wiFiSendData.cancelDownloadingPlease=true;
                tasksCache.removeTask(operationId+"");

                iv_toLogo.setVisibility(View.VISIBLE);
                tv_speed.setText(taskName+" Cancelled...");
                tv_speed.setTextColor(mainActivityObject.getResources().getColor(R.color.main_theme_red));
                btn_pause_resume.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_hide.setText("CLOSE");
            }

        });


        ArrayList<String> progressNameList;
        progressNameList=new ArrayList<>();
        progressNameList.addAll(wiFiSendData.serializablePacket.nameList);

        final CopyListAdapter copyListAdapter=new CopyListAdapter(mainActivityObject,1,progressNameList,operationId);

        switch (operationId)
        {
            case 501:
                serviceIntent= new Intent(mainActivityObject, WiFiReceiveService1.class);
                break;
            case 502:
                serviceIntent= new Intent(mainActivityObject, WiFiReceiveService1.class);
                break;
        }


        wiFiSendData.dialog=dialog;
        dialog.show();
        final Handler h=new Handler();
        progressListView.setAdapter(copyListAdapter);


        wiFiSendData.runnable=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e(operationId+"RUNNER", wiFiSendData.timeInSec+"......................");
                ++wiFiSendData.downloadCounter;
                if(wiFiSendData.downloadCounter%2==0)
                {
                    iv_taskLogo.setVisibility(View.INVISIBLE);
                }
                else
                {
                    iv_taskLogo.setVisibility(View.VISIBLE);
                }

                copyListAdapter.notifyDataSetChanged();
                progressListView.setSelection(wiFiSendData.currentFileIndex-3);

                tv_fileName.setText(wiFiSendData.currentFileName);
                tv_itemProgress.setText(wiFiSendData.currentFileIndex+"/"+ wiFiSendData.totalFiles +" items");
                tv_sizeProgress.setText(helpingBot.sizeinwords(wiFiSendData.downloadedSize)+"/"+helpingBot.sizeinwords(wiFiSendData.serializablePacket.totalSizeToDownload));
                tv_percent.setText(wiFiSendData.progress+" %");
                pb_progressBar.setProgress((int) wiFiSendData.progress);

                tv_speed.setText(helpingBot.sizeinwords(wiFiSendData.downloadedSize- wiFiSendData.oldDownloadedSize)+"/sec");

                wiFiSendData.oldDownloadedSize= wiFiSendData.downloadedSize;

                wiFiSendData.timeInSec++;

                if(!wiFiSendData.isServiceRunning) //when service has ended
                {
                    Refresher refresher =new Refresher(mainActivityObject);
                    refresher.refresh();

                    //SERVICE STOPPED BY USER
                    if(wiFiSendData.cancelDownloadingPlease)
                    {
                        h.removeCallbacks(wiFiSendData.runnable);
                    }
                    else
                    {
                        if(wiFiSendData.isDownloadError)
                        {
                            iv_taskLogo.setVisibility(View.VISIBLE);
                            tv_speed.setText(taskName+" Error");
                            tv_speed.setTextColor(mainActivityObject.getResources().getColor(R.color.main_theme_red));
                            Toast.makeText(mainActivityObject, "Error", Toast.LENGTH_SHORT).show();
                            btn_pause_resume.setText("RESUME");
                            tv_errorDetails.setText(ErrorHandler.getErrorName(wiFiSendData.downloadErrorCode)+"-->"+ wiFiSendData.errorDetails);
                            tv_errorDetails.setVisibility(View.VISIBLE);
                            btn_skip.setVisibility(View.VISIBLE);


                            h.removeCallbacks(wiFiSendData.runnable);
                        }
                        else
                        {
                            //SERVICE HAS COMPLETED DOWNLOADING
                            if(wiFiSendData.progress==100)
                            {
                                iv_taskLogo.setVisibility(View.VISIBLE);
                                tv_speed.setText(taskName+" Done...");
                                Toast.makeText(mainActivityObject, taskName+" Successful", Toast.LENGTH_SHORT).show();

                                btn_cancel.setVisibility(View.GONE);
                                btn_pause_resume.setVisibility(View.GONE);
                                btn_hide.setText("CLOSE");

                                tasksCache.removeTask(operationId+"");

                                h.removeCallbacks(wiFiSendData.runnable);
                            }
                        }
                    }
                }
                else
                {
                    h.postDelayed(wiFiSendData.runnable,1000);
                }
            }
        };

        wiFiSendData.isServiceRunning=true;
        mainActivityObject.startService(serviceIntent);
        h.postDelayed(wiFiSendData.runnable,1000);
    }


    public void destroy()
    {
        try
        {
            serverSocketPhonePicker.close();
        }
        catch (Exception e)
        {}
        try
        {
            serverSocketHandShaker.close();
        }
        catch (Exception e)
        {}
        serverSocketPhonePicker=null;
        serverSocketHandShaker=null;
    }
}
