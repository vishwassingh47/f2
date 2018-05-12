package com.example.sahil.f2.GokuFrags;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Classes.CustomZXingScannerView;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.Ftp.MyFtpClient;
import com.example.sahil.f2.FunkyAdapters.FtpAdapter;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;

import java.util.ArrayList;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by hit4man47 on 1/3/2018.
 */

public class ftpLoginPager extends Fragment
{
    private MainActivity mainActivityObject;
    private String TAG="FTP LOGIN PAGE";
    private GridView gridView;
    private FtpAdapter ftpAdapter;

    ZXingScannerView mScannerView=null;
    private int rootFrameId;

    private Handler QRHandler;
    private Runnable QRRunnable;

    View view;
    private TinyDB tinyDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contaimer, Bundle saved)
    {
        Log.e(TAG,"created");
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_ftp_pager,contaimer,false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        view=getView();
        rootFrameId=((ViewGroup)getView().getParent()).getId();
        tinyDB=new TinyDB(mainActivityObject);
        try
        {
            setUpGridView();
        }
        catch (Exception e)
        {}
    }

    class MyAsyncTask extends AsyncTask<String, Integer, String>
    {

        private ProgressDialog pd;
        private MyFtpClient myFtpClient;
        private String fullUrl;
        private int connectOrLogin;

        public MyAsyncTask(String fullUrl,int connectOrLogin)
        {
            this.fullUrl=fullUrl;
            this.connectOrLogin=connectOrLogin;
        }

        protected void onPreExecute()
        {
            super.onPreExecute();
            String taskName;
            pd=new ProgressDialog(mainActivityObject);
            taskName="Connecting...";
            pd.setTitle(taskName);
            pd.setMessage(fullUrl);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... arg0)
        {
            myFtpClient=new MyFtpClient();
            FtpCache.mFTPClient=myFtpClient.connect(fullUrl);

            if(FtpCache.mFTPClient==null)
            {
                FtpCache.currentFullUrl=null;
                return null;
            }
            else
            {
                FtpCache.currentFullUrl=fullUrl;
                return "all done";
            }
        }

        @Override
        protected void onPostExecute(String toPath)
        {
            if(toPath==null)
            {
                Toast.makeText(mainActivityObject, "FAILED to connect", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(connectOrLogin==1)
                {
                    Toast.makeText(mainActivityObject, "Connected", Toast.LENGTH_SHORT).show();
                    storagePager newFragment=new storagePager();
                    newFragment.newStarted("/");
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    ft.replace(rootFrameId,newFragment);
                    ft.commit();
                    //connected
                }
                if(connectOrLogin==2)
                {
                    Toast.makeText(mainActivityObject, "Connected and data saved", Toast.LENGTH_SHORT).show();
                    FtpCache.ftpList.add(fullUrl);
                    tinyDB.putListString("ftpList",FtpCache.ftpList);
                    //refresh
                    refresh();
                }
            }
            pd.cancel();
        }

    }


    private void gridViewClickListener()
    {

        final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView <?> listv, View v, final int position, long id)
            {
                if(position==FtpCache.ftpList.size())
                {
                    //show login dialog
                    showLoginDialog();
                }
                else
                {
                    Log.e(TAG,FtpCache.mFTPClient+"---"+FtpCache.currentFullUrl+"---"+FtpCache.ftpList.get(position));
                    if(FtpCache.mFTPClient==null || !FtpCache.currentFullUrl.equals(FtpCache.ftpList.get(position)))
                    {
                        MyAsyncTask myAsyncTask=new MyAsyncTask(FtpCache.ftpList.get(position),1);
                        myAsyncTask.execute();
                    }
                    else
                    {
                        storagePager newFragment=new storagePager();
                        newFragment.newStarted("/");
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        ft.replace(rootFrameId,newFragment);
                        ft.commit();
                    }
                }
            }

        };
        gridView.setOnItemClickListener(listener);
    }


    private void showLoginDialog()
    {
        final Dialog dialog = new Dialog(mainActivityObject);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_ftp_login_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        final EditText et_host=(EditText) dialog.findViewById(R.id.edtHostName);
        final EditText et_userName=(EditText) dialog.findViewById(R.id.edtUserName);
        final EditText et_password=(EditText) dialog.findViewById(R.id.edtPassword);
        final EditText et_port=(EditText) dialog.findViewById(R.id.edtPort);

        final Button login=(Button) dialog.findViewById(R.id.btnLoginFtp);
        Button cancel=(Button) dialog.findViewById(R.id.btnCancelFtp);

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
            }
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String host = et_host.getText().toString().trim();
                final String port = et_port.getText().toString().trim();
                final String username = et_userName.getText().toString().trim();
                final String password = et_password.getText().toString().trim();

                if (host.length() < 1)
                {
                    Toast.makeText(mainActivityObject, "Please Enter Host Address!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (port.length() < 1)
                {
                    Toast.makeText(mainActivityObject, "Please Enter port !", Toast.LENGTH_LONG).show();
                    return;
                }
                String fullUrl;
                if(username.length()==0 || password.length()==0)
                {
                    fullUrl="ftp://"+host+":"+port;
                }
                else
                {
                    fullUrl="ftp://"+username+":"+password+"@"+host+":"+port;
                }
                Log.e("Written url:",fullUrl);

                dialog.cancel();
                MyAsyncTask myAsyncTask=new MyAsyncTask(fullUrl,2);
                myAsyncTask.execute();

            }
        });
        FtpCache.scannedQR=null;
        QRHandler=new Handler();
        QRRunnable=new Runnable()
        {
            @Override
            public void run()
            {
                if(dialog==null || !dialog.isShowing() || FtpCache.scannedQR!=null)
                {
                    if(FtpCache.scannedQR!=null)
                    {
                        ArrayList<String> details=MyFtpClient.extractFullUrl(FtpCache.scannedQR);
                        et_host.setText(details.get(0));
                        et_port.setText(details.get(1));
                        et_userName.setText(details.get(2));
                        et_password.setText(details.get(3));
                        login.performClick();
                    }
                    QRHandler.removeCallbacks(QRRunnable);
                }
                else
                {
                    QRHandler.postDelayed(QRRunnable,500);
                }
            }
        };
        QRHandler.post(QRRunnable);


        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                stopCamera();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                stopCamera();
            }
        });

        mScannerView=new ZXingScannerView(mainActivityObject)
        {
            @Override
            protected IViewFinder createViewFinderView(Context context)
            {
                CustomZXingScannerView customZXingScannerView=new CustomZXingScannerView(context);
                //customZXingScannerView.setBorderColor(context.getResources().getColor(R.color.main_theme_red));
                //customZXingScannerView.setMaskColor(context.getResources().getColor(R.color.black));

                return customZXingScannerView;
            }
        };

        LinearLayout qr_layout=(LinearLayout)dialog.findViewById(R.id.ftp_qr_layout);
        qr_layout.addView(mScannerView);
        showCamera();

    }

    public void refresh()
    {
        ftpLoginPager fragment=new ftpLoginPager();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,fragment);
        ft.commit();
    }


    public void backPressed(Page page)
    {
        mainActivityObject.removePage(page);
        mainActivityObject.setUpViewPager();
    }

    @Override
    public void onPause()
    {
        Log.e("onPause","--");
        super.onPause();
        stopCamera();
    }

    @Override
    public void onResume()
    {
        Log.e("onResume","--");
        super.onResume();
        showCamera();
    }

    @Override
    public void onDestroy()
    {
        if(QRHandler!=null && QRRunnable!=null)
        {
            QRHandler.removeCallbacks(QRRunnable);
        }
        super.onDestroy();
    }

    private void setUpGridView() throws Exception
    {
        gridView=(GridView)view.findViewById(R.id.ftp_grid);
        ftpAdapter=new FtpAdapter(mainActivityObject, FtpCache.ftpList,getFragmentManager().findFragmentById(rootFrameId));
        gridView.setAdapter(ftpAdapter);
        gridViewClickListener();
    }

    private void stopCamera()
    {
        try
        {
            mScannerView.stopCamera();
        }
        catch (Exception e)
        {}
    }

    private void showCamera()
    {
        try
        {
            mScannerView.setResultHandler(mainActivityObject);
            mScannerView.startCamera();
        }
        catch (Exception e)
        {}
    }

}
