package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Cache.superCache;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.SerializablePacket;
import com.example.sahil.f2.Classes.WiFiDevice;
import com.example.sahil.f2.Classes.WiFiSendData;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.FunkyAdapters.ConnectedDeviceAdapter;
import com.example.sahil.f2.FunkyAdapters.CopyListAdapter;
import com.example.sahil.f2.HomeServicesProvider.WiFiSendService1;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import com.example.sahil.f2.UiClasses.Refresher;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

public class WifiSender
{
    Runnable runnable;
    Integer counter=0;
    ArrayList<WiFiDevice> wifiList;
    String deviceIP,prefix;
    private MainActivity mainActivity;
    private LinearLayout wifi_off;
    private ProgressBar progressBar;
    private ListView listView;
    private boolean searchComplete=false;
    private Button rescan;
    ConnectedDeviceAdapter connectedDeviceAdapter;
    private Intent serviceIntent;

    public WifiSender(MainActivity mainActivity)
    {
       this.mainActivity=mainActivity;
    }

    public void showWiFiDialog()
    {
        final Dialog dialog=new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wifi_devices);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        wifi_off=(LinearLayout) dialog.findViewById(R.id.wifi_off);
        progressBar=(ProgressBar) dialog.findViewById(R.id.progress);
        Button cancel=(Button)dialog.findViewById(R.id.cancel);
        rescan= (Button) dialog.findViewById(R.id.rescan);
        listView=(ListView)dialog.findViewById(R.id.wifi_list_view);

        progressBar.setVisibility(View.GONE);
        wifi_off.setVisibility(View.GONE);
        wifiList=new ArrayList<>();

        connectedDeviceAdapter=new ConnectedDeviceAdapter(mainActivity,0,wifiList);
        listView.setAdapter(connectedDeviceAdapter);
        listViewListener();


        rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                searchDevices();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.cancel();
            }
        });

        searchDevices();
        dialog.show();

    }

    private void getDeviceIp()
    {
        try
        {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress())
                    {
                        //getting only the required IPV4
                        String ip=inetAddress.getHostAddress();
                        if(ip.startsWith("192"))
                        {
                            deviceIP=ip;
                            prefix=deviceIP.substring(0, deviceIP.lastIndexOf(".") + 1);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            Log.e("error:","cant find devie iP");
        }

    }

    private void searchDevices()
    {
        deviceIP=null;
        prefix=null;
        getDeviceIp();
        wifiList.clear();
        connectedDeviceAdapter.notifyDataSetChanged();
        final Handler handler=new Handler();
        searchComplete=false;
        rescan.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        runnable=new Runnable()
        {
            @Override
            public void run()
            {
                if(searchComplete)
                {
                    rescan.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    handler.removeCallbacks(runnable);
                }
                else
                {
                    handler.postDelayed(runnable,1000);
                }

            }
        };
        handler.postDelayed(runnable,1000);

        if(deviceIP==null)
        {
            searchComplete=true;
            wifi_off.setVisibility(View.VISIBLE);
            Toast.makeText(mainActivity, "ERROR :Wi-Fi Or HotSpot not turned On", Toast.LENGTH_LONG).show();
            Log.e("ERROR","Wi-Fi Or HotSpot not turned On");
            return;
        }

        wifi_off.setVisibility(View.GONE);


        counter=26;
        Thread [] thread=new Thread[26];
        for(int i=0;i<26;i++)
        {
            thread[i]=new Thread(new WorkingTask((i*10)+1));
            thread[i].start();
            Log.e("started",counter+"--");
        }

    }

    private class WorkingTask implements Runnable
    {
        private int x;

        public WorkingTask(int x)
        {
            this.x=x;
        }

        public void run()
        {
            for(int i=x;i<=x+9;i++)
            {
                if(i>=255)
                {
                    continue;
                }
                try
                {
                    String testIp = prefix + String.valueOf(i);
                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(500);
                    String hostName = address.getCanonicalHostName();

                    if (reachable)
                    {
                        if(!String.valueOf(testIp).equals(deviceIP))
                        {
                            Log.e("mathced","****************************");

                             final String ip=String.valueOf(testIp);
                            Socket socket=new Socket(ip,9234);
                            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
                            final String deviceName=dataInputStream.readUTF();

                            // Get a handler that can be used to post to the main thread
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    WiFiDevice wiFiDevice=new WiFiDevice();
                                    wiFiDevice.setDeviceName(deviceName);
                                    wiFiDevice.setIp(ip);
                                    wifiList.add(wiFiDevice);
                                    connectedDeviceAdapter.notifyDataSetChanged();
                                }
                            };
                            mainHandler.post(myRunnable);

                            dataInputStream.close();
                            socket.close();





                            Log.e("TAG", "Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is connnected");
                        }
                    }

                }
                catch (Exception e)
                {
                    Log.e("error:","thread executing failed: "+e);
                }
            }

            synchronized (counter)
            {
                counter--;
                if(counter==0)
                {
                    searchComplete=true;
                }
                Log.e("finished",counter+"--");
            }
        }

    }

    public void listViewListener()
    {
        final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView <?> listv,View v,int pos,long id)
            {
                WiFiDevice wiFiDevice=wifiList.get(pos);
                WiFiSendData wiFiSendData =new WiFiSendData();
                SerializablePacket serializablePacket=new SerializablePacket();
                wiFiSendData.serializablePacket=serializablePacket;
                wiFiSendData.serializablePacket.receiverDeviceName=(wiFiDevice.getDeviceName());
                wiFiSendData.serializablePacket.receiverIP=(wiFiDevice.getIp());

                String deviceName=android.os.Build.MANUFACTURER +":"+ android.os.Build.MODEL;
                wiFiSendData.serializablePacket.senderDeviceName=(deviceName);
                wiFiSendData.serializablePacket.senderIP=(deviceIP);

                wiFiSendData.serializablePacket.pathList=(PasteClipBoard.pathList);
                wiFiSendData.serializablePacket.nameList=(PasteClipBoard.nameList);
                wiFiSendData.serializablePacket.isFolderList=(PasteClipBoard.isFolderList);
                long tSize=0;
                for(long x:PasteClipBoard.sizeLongList)
                {
                    tSize+=x;
                }
                wiFiSendData.serializablePacket.totalSizeToDownload=(tSize);
                wiFiSendData.serializablePacket.sizeLongList=(PasteClipBoard.sizeLongList);
                wiFiSendData.totalFiles=PasteClipBoard.nameList.size();

                //PasteClipBoard.clear();
                sendHandShakePacket(wiFiSendData);
                //startService(wifiList.get(pos));
                Toast.makeText(mainActivity, "WAITING FOR RESPONSE", Toast.LENGTH_SHORT).show();

            }
        };

        listView.setOnItemClickListener(listener);
    }

    private void sendHandShakePacket(final WiFiSendData wiFiSendData)
    {

        int tId=0;
        if(!tasksCache.tasksId.contains("401"))
        {
            superCache.wiFiSendData1 = wiFiSendData;
            tId=401;
        }
        else if (!tasksCache.tasksId.contains("402"))
        {
            superCache.wiFiSendData2 = wiFiSendData;
            tId=402;
        }
        final int operationId=tId;
        if(operationId==0)
        {
            Toast.makeText(mainActivity, "Server too busy,Please WAIT", Toast.LENGTH_SHORT).show();
            return;
        }


        final TextView tv_taskName,tv_fileName,tv_fromPath,tv_toPath,tv_size,tv_itemProgress,tv_sizeProgress,tv_percent,tv_speed,tv_errorDetails;
        final ImageView iv_taskLogo,iv_toLogo,iv_fromLogo;
        final ProgressBar pb_progressBar;
        final Button btn_cancel,btn_open,btn_skip,btn_pause_resume,btn_swipe,btn_hide;
        final LinearLayout page1,page2;
        final ListView progressListView;

        final Dialog dialog = new Dialog(mainActivity);
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
                mainActivity.showHideButtons(-1);
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
                tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                btn_pause_resume.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_hide.setText("CLOSE");
            }

        });


        ArrayList<String> progressNameList;
        progressNameList=new ArrayList<>();
        progressNameList.addAll(wiFiSendData.serializablePacket.nameList);

        final CopyListAdapter copyListAdapter=new CopyListAdapter(mainActivity,1,progressNameList,operationId);

        switch (operationId)
        {
            case 401:
                serviceIntent= new Intent(mainActivity, WiFiSendService1.class);
                break;
            case 402:
                serviceIntent= new Intent(mainActivity, WiFiSendService1.class);
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
                    Refresher refresher =new Refresher(mainActivity);
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
                            tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                            Toast.makeText(mainActivity, "Error", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(mainActivity, taskName+" Successful", Toast.LENGTH_SHORT).show();

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
        mainActivity.startService(serviceIntent);
        h.postDelayed(wiFiSendData.runnable,1000);
    }

}
