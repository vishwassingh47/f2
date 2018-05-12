package com.example.sahil.f2.GokuFrags;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.Ftp.MyFtpClient;
import com.example.sahil.f2.FunkyAdapters.FtpAdapter;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.apache.commons.net.ftp.FTP;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by hit4man47 on 1/5/2018.
 */

public class ftpServerPager extends Fragment
{
    private MainActivity mainActivityObject;
    private String TAG="FTP SERVER PAGE";

    private LinearLayout details1,details2;
    private Button start_stop,refresh;
    private TextView details1_url,details1_read_write,details1_host,details1_port,details1_username,details1_password,details2_url,details2_read_write,details2_host,details2_port,details2_username,details2_password,sharedPath;
    private ImageView details1_QR,details2_QR;
    private int rootFrameId;

    View view;
    private TinyDB tinyDB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contaimer, Bundle saved)
    {
        Log.e(TAG,"created");
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_ftp_server,contaimer,false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        view=getView();
        rootFrameId=((ViewGroup)getView().getParent()).getId();
        tinyDB=new TinyDB(mainActivityObject);
        initaliseUi();
        btnClick();
    }


    private void initaliseUi()
    {
        start_stop=(Button) view.findViewById(R.id.ftp_server_start_stop);
        refresh=(Button) view.findViewById(R.id.ftp_server_refresh);

        details1=(LinearLayout) view.findViewById(R.id.ftp_server_details1);
        details1_url=(TextView) view.findViewById(R.id.ftp_server_details1_url);
        details1_read_write=(TextView) view.findViewById(R.id.ftp_server_details1_read_write);
        details1_host=(TextView) view.findViewById(R.id.ftp_server_details1_host);
        details1_port=(TextView) view.findViewById(R.id.ftp_server_details1_port);
        details1_username=(TextView) view.findViewById(R.id.ftp_server_details1_username);
        details1_password=(TextView) view.findViewById(R.id.ftp_server_details1_password);
        details1_QR=(ImageView) view.findViewById(R.id.ftp_server_details1_QR);

        details2=(LinearLayout) view.findViewById(R.id.ftp_server_details2);
        details2_url=(TextView) view.findViewById(R.id.ftp_server_details2_url);
        details2_read_write=(TextView) view.findViewById(R.id.ftp_server_details2_read_write);
        details2_host=(TextView) view.findViewById(R.id.ftp_server_details2_host);
        details2_port=(TextView) view.findViewById(R.id.ftp_server_details2_port);
        details2_username=(TextView) view.findViewById(R.id.ftp_server_details2_username);
        details2_password=(TextView) view.findViewById(R.id.ftp_server_details2_password);
        details2_QR=(ImageView) view.findViewById(R.id.ftp_server_details2_QR);

        sharedPath=(TextView) view.findViewById(R.id.ftp_server_sharedPath);


        if(FtpCache.mFTPServer==null || FtpCache.mFTPServer.isStopped() || FtpCache.mFTPServer.isSuspended())
        {
            FtpCache.mFTPServer=null;
            hideDetails();
        }
        else
        {
            showDetails();
        }
    }

    private void btnClick()
    {
        start_stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(FtpCache.mFTPServer==null || FtpCache.mFTPServer.isStopped() || FtpCache.mFTPServer.isSuspended())
                {
                    showDialog();
                }
                else
                {
                    start_stop.setText("STOPPING");
                    stopServer();
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    refresh();
                }
                catch (Exception e)
                {}
            }
        });

    }


    private void hideDetails()
    {
        start_stop.setText("START SERVER");
        details1.setVisibility(View.GONE);
        details2.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
        sharedPath.setVisibility(View.GONE);
    }

    private void showDetails()
    {
        start_stop.setText("STOP SERVER");
        refresh.setVisibility(View.VISIBLE);
        sharedPath.setText(Constants.FTP_SERVER_PATH);
        sharedPath.setVisibility(View.VISIBLE);

        ArrayList<String> hostList=getIpAddressHostList();
        String wifiHost=hostList.get(0);
        String internetHost=hostList.get(1);


        if(wifiHost==null && internetHost==null)
        {
            Toast.makeText(mainActivityObject, "Turn On Wifi or Internet", Toast.LENGTH_SHORT).show();
        }

        if(wifiHost!=null)
        {
            details1.setVisibility(View.VISIBLE);
            details1_password.setText(FtpCache.serverPassword);
            details1_username.setText(FtpCache.serverUserName);
            details1_read_write.setText(FtpCache.readOnly?"Read Only":"Read-Write");
            details1_host.setText(wifiHost);
            details1_port.setText(FtpCache.serverPortNumber+"");

            String fullUrl=createFullUrl(wifiHost);
            details1_url.setText(fullUrl);
            details1_QR.setVisibility(View.INVISIBLE);
            setQR(fullUrl,details1_QR);
        }
        else
        {
            details1.setVisibility(View.GONE);
        }
        if(internetHost!=null)
        {
            details2.setVisibility(View.VISIBLE);
            details2_password.setText(FtpCache.serverPassword);
            details2_username.setText(FtpCache.serverUserName);
            details2_read_write.setText(FtpCache.readOnly?"Read Only":"Read-Write");
            details2_host.setText(internetHost);
            details2_port.setText(FtpCache.serverPortNumber+"");

            String fullUrl=createFullUrl(internetHost);
            details2_url.setText(fullUrl);
            details2_QR.setVisibility(View.INVISIBLE);
            setQR(fullUrl,details2_QR);
        }
        else
        {
            details2.setVisibility(View.GONE);
        }
    }

    private String createFullUrl(String host)
    {
        String url="ftp://";
        if(FtpCache.serverUserName.length()>0)
        {
            url+=FtpCache.serverUserName+":"+FtpCache.serverPassword+"@";
        }
        url+=host+":"+FtpCache.serverPortNumber;
        return url;
    }


    private void showDialog()
    {

        final Dialog dialog = new Dialog(mainActivityObject);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_ftp_server_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        final EditText et_username=(EditText) dialog.findViewById(R.id.dialog_ftps_username);
        final EditText et_password=(EditText) dialog.findViewById(R.id.dialog_ftps_password);
        final Button cancel=(Button) dialog.findViewById(R.id.dialog_ftps_cancel);
        final Button ok=(Button) dialog.findViewById(R.id.dialog_ftps_ok);
        final RadioGroup radioGroup=(RadioGroup) dialog.findViewById(R.id.dialog_ftps_radio_group);
        final RadioGroup radioGroup2=(RadioGroup) dialog.findViewById(R.id.dialog_ftps_radio_group2);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                View radioButton=radioGroup.findViewById(checkedId);
                int index=radioGroup.indexOfChild(radioButton);
                switch (index)
                {
                    case 0:
                        et_password.setEnabled(false);
                        et_username.setEnabled(false);
                        break;
                    case 1:
                        et_password.setEnabled(true);
                        et_username.setEnabled(true);
                        break;
                }
            }
        });
        et_password.setText(tinyDB.getString("FtpServerPassword"));
        et_username.setText(tinyDB.getString("FtpServerUserName"));
        et_password.setEnabled(false);
        et_username.setEnabled(false);

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
            }
        });

        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int radioButtonId=radioGroup.getCheckedRadioButtonId();
                View radioButton=radioGroup.findViewById(radioButtonId);
                int index=radioGroup.indexOfChild(radioButton);
                String userName="";
                String password="";
                if(index==1)
                {
                    userName=et_username.getText().toString();
                    password=et_password.getText().toString();
                    if(userName.length()==0 || password.length()==0)
                    {
                        Toast.makeText(mainActivityObject, "Username or password is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tinyDB.putString("FtpServerPassword",password);
                    tinyDB.putString("FtpServerUserName",userName);
                }
                radioButtonId=radioGroup2.getCheckedRadioButtonId();
                radioButton=radioGroup2.findViewById(radioButtonId);
                index=radioGroup2.indexOfChild(radioButton);

                FtpCache.readOnly=index==0;
                FtpCache.serverUserName=userName;
                FtpCache.serverPassword=password;
                dialog.cancel();
                startServerThread();
            }
        });

        dialog.show();

    }

    private void stopServer()
    {
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                Log.e(TAG,"SUSPENDING..");
                FtpCache.mFTPServer.suspend();
                Log.e(TAG,"STOPPING..");
                FtpCache.mFTPServer.stop();
                Log.e(TAG,"STOPPED??.."+ FtpCache.mFTPServer.isStopped());

                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(mainActivityObject, "server stopped", Toast.LENGTH_SHORT).show();
                        try
                        {
                            refresh();
                        }
                        catch (Exception e)
                        {}

                    }
                };

                mainHandler.post(myRunnable);
            }
        };
        if(FtpCache.mFTPServer==null || FtpCache.mFTPServer.isStopped() || FtpCache.mFTPServer.isSuspended())
        {
        }
        else
        {
            thread.start();
        }

    }

    private ArrayList<String> getIpAddressHostList()
    {
        ArrayList<String> hostsList=new ArrayList<>();

        hostsList.add(0,null);//    wifi
        hostsList.add(1,null);//    internet

        try
        {
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
                            hostsList.set(0,ip);//wifi
                        }
                        else
                        {
                            hostsList.set(1,ip);//internet
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            Log.e(TAG,"ERROR GETTING IP ADDRESSES");
        }

        return hostsList;
    }

    private void startServerThread()
    {
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                startServer();

                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(FtpCache.mFTPServer==null || FtpCache.mFTPServer.isStopped() || FtpCache.mFTPServer.isSuspended())
                        {
                            Toast.makeText(mainActivityObject, "Server failed to start", Toast.LENGTH_SHORT).show();
                            FtpCache.mFTPServer=null;
                            start_stop.setText("START SERVER");
                            details1.setVisibility(View.GONE);
                            details2.setVisibility(View.GONE);
                        }
                        else
                        {
                            Toast.makeText(mainActivityObject, "Server Started", Toast.LENGTH_SHORT).show();
                            start_stop.setText("STOP SERVER");
                            showDetails();
                        }
                    }
                };
                mainHandler.post(myRunnable);

            }
        };
        thread.start();
    }

    private void startServer()
    {
        FtpCache.serverPortNumber=4000;
        for(int retry=1;retry<=3;retry++)
        {
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory factory = new ListenerFactory();
            factory.setPort(FtpCache.serverPortNumber);//
            serverFactory.addListener("default", factory.createListener());
            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            final String PATH=MainActivity.Physical_Storage_PATHS.get(0)+"/myusers.properties";
            File file;
            file=new File(PATH);

            boolean x=file.delete();

            file=new File(PATH);
            if(!file.exists())
            {
                try
                {
                    boolean y=file.createNewFile();
                }
                catch (Exception e)
                {
                  Log.e(TAG,"ERROR CREATING myusers.properties");
                }
            }
            file=new File(PATH);
            userManagerFactory.setFile(file);//choose any. We're telling the FTP-server where to read it's user list
            userManagerFactory.setPasswordEncryptor(new PasswordEncryptor()
            {//We store clear-text passwords in this example

                @Override
                public String encrypt(String password)
                {
                    return password;
                }

                @Override
                public boolean matches(String passwordToCheck, String storedPassword)
                {
                    return passwordToCheck.equals(storedPassword);
                }
            });

            ConnectionConfigFactory connectionConfigFactory=new ConnectionConfigFactory();
            //Let's add a user, since myusers.properties files is empty on our first test run
            BaseUser user = new BaseUser();

            if(FtpCache.serverUserName.length()>0 && FtpCache.serverPassword.length()>0)
            {
                connectionConfigFactory.setAnonymousLoginEnabled(false);
                serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
                user.setName(FtpCache.serverUserName);
                user.setPassword(FtpCache.serverPassword);
            }
            else
            {
                FtpCache.serverUserName="";
                FtpCache.serverPassword="";
                //anonymous user
                connectionConfigFactory.setAnonymousLoginEnabled(true);
                serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
                user.setName("anonymous");
            }

            user.setHomeDirectory(Constants.FTP_SERVER_PATH);
            if(FtpCache.readOnly)
            {
                // add no write permission
            }
            else
            {
                List<Authority> authorities = new ArrayList<Authority>();
                authorities.add(new WritePermission());
                user.setAuthorities(authorities);
            }

            UserManager um = userManagerFactory.createUserManager();

            try
            {
                um.save(user);//Save the user to the user list on the filesystem
            }
            catch (FtpException e1)
            {
                Log.e("error creatinhg user","---");
                //Deal with exception as you need
            }
            serverFactory.setUserManager(um);
            Map<String, Ftplet> m = new HashMap<String, Ftplet>();
            m.put("miaFtplet", new Ftplet()
            {

                @Override
                public void init(FtpletContext ftpletContext) throws FtpException
                {
                    Log.e(TAG,"INIT--"+"admin name:"+ftpletContext.getUserManager().getAdminName()+"--users total--"+ftpletContext.getUserManager().getAllUserNames().length);
                    //System.out.println("init");
                    //System.out.println("Thread #" + Thread.currentThread().getId());
                }

                @Override
                public void destroy()
                {
                    Log.e(TAG,"DESTROYED--");
                    //System.out.println("destroy");
                    //System.out.println("Thread #" + Thread.currentThread().getId());
                }

                @Override
                public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException
                {
                    //System.out.println("beforeCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine());
                    //System.out.println("Thread #" + Thread.currentThread().getId());

                    //do something
                    return FtpletResult.DEFAULT;//...or return accordingly
                }

                @Override
                public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) throws FtpException, IOException
                {
                    //System.out.println("afterCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine() + " | " + reply.getMessage() + " : " + reply.toString());
                    //System.out.println("Thread #" + Thread.currentThread().getId());

                    //do something
                    return FtpletResult.DEFAULT;//...or return accordingly
                }

                @Override
                public FtpletResult onConnect(FtpSession session) throws FtpException, IOException
                {
                    //Log.e("--",session.getUser()+"--"+session+"--"+session.getUserArgument());
                    //Log.e(TAG,"--CONNECTED---userName:"+session.getUser().getName());
                    //Log.e(TAG,"--CONNECTED---userip:"+"--"+session.getClientAddress()+"--server ip:"+session.getServerAddress());
                    //System.out.println("onConnect " + session.getUserArgument() + " : " + session.toString());
                    //System.out.println("Thread #" + Thread.currentThread().getId());

                    //do something
                    return FtpletResult.DEFAULT;//...or return accordingly
                }

                @Override
                public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException
                {
                    //Log.e("--",session.getUser()+"--"+session.getSessionId());
                    //Log.e(TAG,"--CONNECTED---userName:"+session.getUser().getName());
                    //Log.e(TAG,"--DISCONNECTED---userip:"+"--"+session.getClientAddress()+"--server ip:"+session.getServerAddress());
                    //System.out.println("onConnect " + session.getUserArgument() + " : " + session.toString());
                    //System.out.println("Thread #" + Thread.currentThread().getId());
                    return FtpletResult.DEFAULT;//...or return accordingly
                }
            });
            serverFactory.setFtplets(m);
            //Map<String, Ftplet> mappa = serverFactory.getFtplets();
            //System.out.println(mappa.size());
            //System.out.println("Thread #" + Thread.currentThread().getId());
            //System.out.println(mappa.toString());


            FtpCache.mFTPServer = serverFactory.createServer();
            try
            {
                FtpCache.mFTPServer.start();//Your FTP server starts listening for incoming FTP-connections, using the configuration options previously set
                Log.e(TAG,"connected......................................******************************************");
            }
            catch (FtpException ex)
            {
                FtpCache.mFTPServer=null;
                Log.e("FTPEXCEPTION:",ex.getLocalizedMessage()+"--"+ex.getMessage());
                Log.e(TAG,"failed......................................******************************************");

                //Deal with exception as you need
            }
            catch (FtpServerConfigurationException e)
            {
                Log.e(TAG,"failed......................................******************************************");
                FtpCache.mFTPServer=null;
                FtpCache.serverPortNumber+=100;
                Log.e("FtpServerConfiguration",e.getMessage());
                continue;
            }
            break;
        }
    }


    private void refresh() throws Exception
    {
        ftpServerPager fragment=new ftpServerPager();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,fragment);
        ft.commit();
    }

    public void backPressed(Page page)
    {
        stopServer();
        mainActivityObject.removePage(page);
        mainActivityObject.setUpViewPager();
    }


    private void setQR(String url,ImageView imageView)
    {
        class MyAsyncTaskQR extends AsyncTask<String, Integer, Bitmap>
        {
            final private ImageView imageView;
            final private String url;

            public MyAsyncTaskQR(ImageView imageView,String url)
            {
                this.url=url;
                this.imageView=imageView;
            }

            protected void onPreExecute()
            {
                super.onPreExecute();
            }

            protected Bitmap doInBackground(String... arg0)
            {
                Bitmap bitmap=null;
                try
                {
                    Map<EncodeHintType, ErrorCorrectionLevel> hintMap =new HashMap<EncodeHintType, ErrorCorrectionLevel>();
                    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 125, 125, hintMap);
                    //converting bitmatrix to bitmap
                    int width = matrix.getWidth();
                    int height = matrix.getHeight();
                    int[] pixels = new int[width * height];
                    // All are 0, or black, by default
                    for (int y = 0; y < height; y++)
                    {
                        int offset = y * width;
                        for (int x = 0; x < width; x++)
                        {
                            pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                        }
                    }

                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                    //setting bitmap to image view
                }
                catch (Exception e)
                {
                    return null;
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                if(bitmap==null)
                {

                }
                else
                {
                    try
                    {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                    }
                    catch (Exception e)
                    {
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }
                //refresh
            }

        }

        MyAsyncTaskQR myAsyncTask=new MyAsyncTaskQR(imageView,url);
        myAsyncTask.execute();
    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


}
