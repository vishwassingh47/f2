package com.example.sahil.f2;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by hit4man47 on 8/30/2017.
 */

public class WifiReceiveActivity extends AppCompatActivity
{
    static final int SocketServerPORT = 8080;

    public String serverIP=null;
    ServerSocket serverSocket;
    Socket socket2 = null;

    ServerSocketThread serverSocketThread;
    public Socket global=null;
    public int temp=0;

    ScrollView scroll;
    TextView logs;

    String log="";
    Runnable runnable=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutof_wifi_receiver_server);

        logs = (TextView) findViewById(R.id.receive_logs);
        scroll=(ScrollView)findViewById(R.id.receive_logs_scroll);
        final Handler handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {
                if(temp<0)
                {
                    handler.removeCallbacks(runnable);
                }
                else
                {
                    temp++;
                    logs.setText(log);
                    if(temp%2==0)
                    {
                        scroll.fullScroll(View.FOCUS_DOWN);
                    }
                    handler.postDelayed(runnable,1000);
                }
            }
        };
        handler.postDelayed(runnable,1000);


        getIpAddress();

        if(serverIP==null)
        {
            log+="******** ERROR : Wi-Fi Or HotSpot not turned On ********\n";
            log+="server failed to start ....\n";
            Toast.makeText(this, "ERROR :Wi-Fi Or HotSpot not turned On...\n", Toast.LENGTH_LONG).show();
            return;
        }
        log+="server starting \n";
        serverSocketThread = new ServerSocketThread();
        serverSocketThread.start();

    }


    public class ServerSocketThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                log+="server started with its ip"+ serverIP+" \n";
                serverSocket = new ServerSocket(SocketServerPORT);

                log+="listening for clients on Port 8080 .... \n";
                socket2 = serverSocket.accept();
                global=socket2;
                log+="********* Pinged with "+ socket2.getInetAddress() +"  *************\n";


                fileReceiver();
                serverSocket.close();
                socket2.close();

                log+="******** End Of Task********** \n";
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void fileReceiver()
    {
        try
        {

            BufferedOutputStream bos=null;
            InputStream is=null;

            int bytesRead = -1;
            long total=0;
            byte[] bytes = new byte[1024*5];


            try
            {
                log+="getting information of files to be received...\n";
                ObjectInputStream ois = new ObjectInputStream(global.getInputStream());
                ArrayList<ArrayList> superList = (ArrayList<ArrayList>) ois.readObject();

                ArrayList<String> nameList=new ArrayList<>();
                ArrayList<Boolean> isFolder=new ArrayList<>();
                ArrayList<Long> sizeLongList=new ArrayList<>();

                nameList=superList.get(0);
                isFolder=superList.get(1);
                sizeLongList=superList.get(2);


                //System.out.println("Message Received from client: " + message);
                //b(message);

                log+="file information received..\n";
                for(int i=0;i<nameList.size();i++)
                {
                    log+=" --FileName:"+ nameList.get(i)+" --IsFolder:"+isFolder.get(i)+" --Size:"+sizeLongList.get(i)+"\n";
                }

                ois.close();


                final File file=new File("/sdcard/f2");
                if(!file.exists())
                {
                    boolean b=file.mkdir();
                    if(b)
                    log+=" /sdcard/f2  directory created..\n";
                }
                log+="files will be received in directory < /sdcard/f2 >...\n";

                String root="/sdcard/f2";
                for(int i=0;i<nameList.size();i++)
                {
                    total=0;
                    log+="receiving file "+(i+1)+"\n";

                    if(isFolder.get(i))
                    {
                       File folder= new File("/sdcard/f2/"+nameList.get(i));
                        if(!folder.exists())
                        {
                            boolean b=folder.mkdirs();
                            log+="directory "+folder.getAbsolutePath() +" created.../n";
                        }
                    }
                    else
                    {
                        bos = new BufferedOutputStream(new FileOutputStream(root+"/"+nameList.get(i)));

                        global=serverSocket.accept();
                        is = global.getInputStream();


                        while((bytesRead=is.read(bytes))>0)
                        {
                            bos.write(bytes, 0, bytesRead);

                            total+=bytesRead;
                        }
                        log+="received file "+(i+1)+"having size "+total+" bytes"+"\n";

                        is.close();
                        bos.flush();
                        bos.close();

                    }

                    if(i==nameList.size()-1)
                    {
                        log+="********* all files received succussfully...******\n";
                        log+="############### Task Finished ##########################\n";
                    }

                }

            }
            catch(Exception E)
            {
                log+= "error......xxx...."+E.getMessage()+"\n";
            }

        }
        catch (Exception e)
        {
            log+="receiver error 2... "+e.getMessage()+"\n";

            e.printStackTrace();
        }

    }


    private void getIpAddress()
    {

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress())
                    {
                        String ip=inetAddress.getHostAddress();
                        if(ip.startsWith("192"))
                        {
                            serverIP=ip;
                        }
                        log += "SiteLocalAddress: "+ip+"\n";
                    }
                }

            }

        } catch (SocketException e)
        {

        }


    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        temp=-555;
        log="";
        try
        {
            serverSocket.close();
            socket2.close();
        }
        catch (Exception e)
        {

        }
    }

}
