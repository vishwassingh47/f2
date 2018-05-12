package com.example.sahil.f2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Cache.WifiUtil;
import com.example.sahil.f2.Cache.wifiSendCache;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.FunkyAdapters.ConnectedDeviceAdapter;
import com.example.sahil.f2.HomeServicesProvider.WifiSendService;
import com.example.sahil.f2.OperationTheater.HelpingBot;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by hit4man47 on 8/31/2017.
 */

public class WifiSendActivity extends AppCompatActivity
{


    ConnectedDeviceAdapter connectedDeviceAdapter;
    String deviceIP=null;
    String prefix = null;

    ListView listView;
    HelpingBot helpingbot;

    WiFiSend wiFiSend;
    LinearLayout progress_layout;

    ScrollView scroll;
    TextView logs;
    Button search;

    public static String log="";

    Runnable runnable=null;

    int temp=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutof_wifi_sender_client);

        wiFiSend=new WiFiSend();
        helpingbot=new HelpingBot();

        Intent intent=getIntent();
        final boolean isStartUp= intent.getBooleanExtra("start",true);


        wifiSendCache.log="";
        log="";

        WifiUtil.connectedIps=new ArrayList<>();

        listView=(ListView)findViewById(R.id.wifi_connected);
        connectedDeviceAdapter=new ConnectedDeviceAdapter(WifiSendActivity.this,0,WifiUtil.connectedIps);
        listView.setAdapter(connectedDeviceAdapter);
        listViewListener();

        search=(Button) findViewById(R.id.searchfor_connected_devices);
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchforDevies();
                search.setVisibility(View.GONE);
            }
        });
        search.setVisibility(View.GONE);


        progress_layout=(LinearLayout)findViewById(R.id.send_progress_layout);
        logs = (TextView) findViewById(R.id.send_logs);
        scroll=(ScrollView)findViewById(R.id.send_logs_scroll);

        final Handler handler=new Handler();

        temp=0;
        runnable=new Runnable()
        {
            @Override
            public void run()
            {

                temp++;
                if(temp%10==0 && WifiUtil.connectedIps.size()==0)
                {
                    search.setVisibility(View.VISIBLE);
                }

                log+=wifiSendCache.log;
                wifiSendCache.log="";
                logs.setText(log);
                if(temp%30==0)
                {
                    scroll.fullScroll(View.FOCUS_DOWN);
                }

                handler.postDelayed(runnable,1000);
            }
        };
        handler.postDelayed(runnable,1000);


        if(isStartUp)
        {
            progress_layout.setVisibility(View.GONE);
            log+="waiting for the user to select the IP address and connect...\n";
        }
        else
        {
            /*
            progress_layout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);

            wiFiSend.showProgress();
            if(wifiSendCache.isServiceRunning)
            {
                wiFiSend.showRunnable();
            }
            */
        }

        searchforDevies();
    }


    public class WiFiSend
    {

        final TextView send_name=(TextView) findViewById(R.id.send_name);
        final TextView send_from=(TextView) findViewById(R.id.send_from);
        final TextView send_to=(TextView) findViewById(R.id.send_to);
        final TextView send_size=(TextView) findViewById(R.id.send_size);
        final TextView send_itemsprogress=(TextView) findViewById(R.id.send_itemsProgress);
        final TextView send_sizeprogress=(TextView) findViewById(R.id.send_sizeProgress);
        final TextView send_percent=(TextView) findViewById(R.id.send_percent);
        final TextView send_speed=(TextView) findViewById(R.id.send_speed);


        final ImageView send_fromlogo=(ImageView)findViewById(R.id.send_from_logo);
        final ImageView send_tologo=(ImageView)findViewById(R.id.send_to_logo);

        final ProgressBar send_progressBar=(ProgressBar)findViewById(R.id.send_progressBar);

        final  Button send_cancel=(Button)findViewById(R.id.send_cancel);


        public void showProgress()
        {
            log+="showing the progress...\n";

            send_from.setText(wifiSendCache.fromRootpath);
            send_to.setText(wifiSendCache.toRootpath);
            send_size.setText(helpingbot.sizeinwords(wifiSendCache.totalsizetodownload));
            send_name.setText(wifiSendCache.currentFileName);


            send_itemsprogress.setText(wifiSendCache.currentFileNumber+"/"+wifiSendCache.totalfiles +" items");
            send_sizeprogress.setText(helpingbot.sizeinwords(wifiSendCache.downloadedsize)+"/"+helpingbot.sizeinwords(wifiSendCache.totalsizetodownload));

            send_percent.setText(wifiSendCache.progress+" %");

            send_progressBar.setProgress((int)wifiSendCache.progress);
            send_speed.setText("calculating..");


            if(wifiSendCache.isDownloadError)
            {
                log+="error ...\n";
                send_speed.setText("ERROR");
            }


            send_cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e("stopped service ","stopping--------------------");

                    if(send_cancel.getText().equals("CANCEL"))
                    {
                        log+="cancelling the service...\n";
                        if(wifiSendCache.isServiceRunning)
                        {
                            wifiSendCache.stopDownloadingPlease=true;
                            //runner will change it to "CLOSE"
                        }
                        else
                        {
                            //WHEN RUNNER IS NOT RUNNING AND WE WISH TO SHOW PROGRESS ONLY
                            send_cancel.setText("CLOSE");
                        }

                    }
                    else if(send_cancel.getText().equals("CLOSE"))
                    {
                       finish();
                    }
                }
            });

        }


        public void showRunnable()
        {

            log+="starting the runner...\n";
            final Handler h=new Handler();
            wifiSendCache.runnable=new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e("RUNNER",wifiSendCache.timeinsec+"......................");
                    ++wifiSendCache.download_counter;

                    send_name.setText(wifiSendCache.currentFileName);
                    send_itemsprogress.setText(wifiSendCache.currentFileNumber+"/"+wifiSendCache.totalfiles +" items");
                    send_sizeprogress.setText(helpingbot.sizeinwords(wifiSendCache.downloadedsize)+"/"+helpingbot.sizeinwords(wifiSendCache.totalsizetodownload));
                    send_percent.setText(wifiSendCache.progress+" %");
                    send_progressBar.setProgress((int)wifiSendCache.progress);
                    send_speed.setText(helpingbot.sizeinwords(wifiSendCache.downloadedsize-wifiSendCache.olddownloadedsize)+"/sec");

                    wifiSendCache.olddownloadedsize=wifiSendCache.downloadedsize;

                    wifiSendCache.timeinsec++;


                    if(!wifiSendCache.isServiceRunning) //when service has ended
                    {
                        //SERVICE STOPPED BY USER
                        if(wifiSendCache.stopDownloadingPlease)
                        {

                            log+="stopping the service...\n";

                            send_speed.setText("Downloading Stopped...");
                            send_speed.setTextColor(Color.parseColor("#f44336"));
                            send_cancel.setText("CLOSE");

                            h.removeCallbacks(wifiSendCache.runnable);
                        }

                        //SERVICE HAS COMPLETED DOWNLOADING
                        if(wifiSendCache.progress==100)
                        {

                            log+="service has finished its task...\n";
                            log+="100% done...\n";

                            send_speed.setText("Downloading Done.....");
                            send_speed.setTextColor(Color.parseColor("#4caf50"));
                            Toast.makeText(WifiSendActivity.this, "Copying Successful", Toast.LENGTH_SHORT).show();
                            send_cancel.setText("CLOSE");

                            h.removeCallbacks(wifiSendCache.runnable);
                        }

                        //SERVICE HAS FACED SOME ERRORS AND MAY BE STARTED AGAIN
                        if(wifiSendCache.isDownloadError)
                        {
                            log+="Error while processing....\n";

                            send_speed.setText("ERROR Downloading");
                            send_speed.setTextColor(Color.parseColor("#f44336"));
                            Toast.makeText(WifiSendActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            send_cancel.setText("CLOSE");

                            log+="service TERMINATED...\n";
                            h.removeCallbacks(wifiSendCache.runnable);
                        }
                    }
                    else
                    {
                        h.postDelayed(wifiSendCache.runnable,1000);
                    }

                }
            };
            h.postDelayed(wifiSendCache.runnable,1000);

        }


    }


    public void startService(String IP)
    {

        progress_layout.setVisibility(View.VISIBLE);

        final Intent ix= new Intent(WifiSendActivity.this, WifiSendService.class);
        ix.putExtra("ip",IP);

        log+="assigning the service...\n";
        startService(ix);

        log+="Trying to Bang Bang!!! the Port 8080 with" + IP +"...\n";


        Log.e("Service to send","..started");

    }


    public void setUpCache(String IP)
    {
        if(!wifiSendCache.isServiceRunning && !tasksCache.tasksId.contains("501"))
        {
            //second condrion for case when some previous task has failed with error and thus not to assign that service again to someone else

            log+="clearing Wi-Fi Cache...\n";
            log+="ReFilling Wi-Fi Cache...\n";
            MainActivity.paster.toParentPath=IP;
            MainActivity.paster.toStorageCode=5;


            wifiSendCache.clear();
            wifiSendCache.fromRootpath= MainActivity.paster.fromParentPath;
            wifiSendCache.fromStorageId= MainActivity.paster.fromStorageCode;
            wifiSendCache.totalfiles= MainActivity.paster.pastelistname.size();


            for(int i=0;i<wifiSendCache.totalfiles;i++)
            {
                wifiSendCache.totalsizetodownload+= MainActivity.paster.pastesizelistLong.get(i);

                wifiSendCache.namesList.add(i,MainActivity.paster.pastelistname.get(i));
                wifiSendCache.sizeLongList.add(i,MainActivity.paster.pastesizelistLong.get(i));
                wifiSendCache.pathsList.add(i,MainActivity.paster.pastelistpath.get(i));
                wifiSendCache.folderList.add(i,MainActivity.paster.pasteIsFolder.get(i));
            }


            wifiSendCache.toRootpath=MainActivity.paster.toParentPath;
            wifiSendCache.tostorageId=MainActivity.paster.toStorageCode;

            log+="ClipBoard Cleared...\n";
            MainActivity.paster.pastelistname.clear();
            MainActivity.paster.pastesizelistLong.clear();
            MainActivity.paster.pastelistpath.clear();
            MainActivity.paster.pasteIsFolder.clear();
        }
    }

    
    public void getConnectedIp()
    {
        if(deviceIP==null)
        {
            search.setVisibility(View.VISIBLE);
            log+="ERROR :Wi-Fi Or HotSpot not turned On...\n";
            Toast.makeText(this, "ERROR :Wi-Fi Or HotSpot not turned On", Toast.LENGTH_LONG).show();
            Log.e("ERROR","Wi-Fi Or HotSpot not turned On");
            return;
        }


        Thread [] thread=new Thread[26];
        log+="searching for networks...\n";
        for(int i=0;i<26;i++)
        {
            thread[i]=new Thread(new WorkingTask((i*10)+1));
            thread[i].start();
        }

    }


    public class WorkingTask implements Runnable
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

                            final String ip=String.valueOf(testIp);
                            // Get a handler that can be used to post to the main thread
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    log+="network found with IP: "+ip+"\n";
                                    WifiUtil.connectedIps.add(ip);
                                    connectedDeviceAdapter.notifyDataSetChanged();
                                }
                            };
                            mainHandler.post(myRunnable);



                            Log.e("TAG", "Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is connnected");
                        }
                    }

                }
                catch (Exception e)
                {
                    Log.e("error:","thread executing failed"+x);
                }
            }
        }

    }


    public void getDeviceIp()
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
                            log+="Your IP address is:"+ip+"\n";
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


    public void searchforDevies()
    {

        deviceIP=null;
        prefix=null;
        getDeviceIp();
        getConnectedIp();
    }


    public void listViewListener()
    {
        final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView <?> listv,View v,int pos,long id)
            {

                log+="connecting...\n";
                setUpCache(WifiUtil.connectedIps.get(pos));
                startService(WifiUtil.connectedIps.get(pos));
                wiFiSend.showProgress();
                wiFiSend.showRunnable();
                listView.setVisibility(View.GONE);
            }
        };

        listView.setOnItemClickListener(listener);
    }


}
