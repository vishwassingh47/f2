package com.example.sahil.f2.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.Classes.MyApp;
import com.example.sahil.f2.Classes.SimpleYesNoDialog;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.PagerXUtilities;
import com.example.sahil.f2.Rooted.SuOperations;
import com.example.sahil.f2.UiClasses.Refresher;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by hit4man47 on 2/15/2018.
 */

public class AppManagerUtils
{
    private final  Context context;
    private final CommonUtils commonUtils;

    public AppManagerUtils(Context context)
    {
        this.context=context;
        commonUtils=new CommonUtils();
    }

    public void openApp(String packageName)
    {
        try
        {
            PackageManager pm=context.getPackageManager();
            Intent intent=pm.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Failed to launch the application ", Toast.LENGTH_SHORT).show();
        }
    }

    public void openAppInStore(String packageName)
    {
        try
        {
            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packageName));
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Failed to launch the Play Store ", Toast.LENGTH_SHORT).show();
        }
    }

    public void openAppInSettings(String packageName)
    {
        try
        {
            Intent intent=new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri=Uri.parse("package:"+packageName);
            intent.setData(uri);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Failed to open the Settings", Toast.LENGTH_SHORT).show();
        }
    }


    public void installFromIntent(String apkPath)
    {
        try
        {
            Uri uri=Uri.fromFile(new File(apkPath));

            Intent intent=new Intent(Intent.ACTION_VIEW).setDataAndType(uri,"application/vnd.android.package-archive");

            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Failed to install", Toast.LENGTH_SHORT).show();
        }
    }

    public void unInstallFromIntent(String packageName)
    {
        try
        {
            Intent intent=new Intent(Intent.ACTION_DELETE);
            Uri uri=Uri.parse("package:"+packageName);
            intent.setData(uri);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Failed to uninstall", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean unInstallAsSuperUser(final String packageName, final String apkPath,boolean doInAsyncTask)
    {
        if(doInAsyncTask)
        {
            class MyAsyncTask  extends AsyncTask<Void,Void,String>
            {
                private ProgressDialog pd;

                protected void onPreExecute()
                {
                    super.onPreExecute();
                    pd=new ProgressDialog(context);
                    pd.setTitle("UnInstalling App.......");
                    pd.setMessage(packageName);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setIndeterminate(true);
                    pd.setCancelable(true);
                    pd.show();
                }

                protected String doInBackground(Void... arg0)
                {
                    return removeApk(apkPath,packageName);
                }

                @Override
                protected void onPostExecute(String toPath)
                {
                    pd.cancel();

                    if(toPath==null)
                    {
                        Toast.makeText(context, "operation failed", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context, "operation done", Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "Reboot to apply changes", Toast.LENGTH_LONG).show();
                        showRebootDialog();
                    }
                }

            }

            MyAsyncTask myAsyncTask=new MyAsyncTask();
            myAsyncTask.execute();
            return true;
        }
        else
        {
            String x=removeApk(apkPath,packageName);
            if(x==null)
                return false;
            else
            {
                MyCacheData.getUnInstallData(199).rebootRequired=true;
                return true;
            }

        }
    }

    /**
     *
     * @param packageName
     * @param apkPath
     * @param doInAsyncTask
     * @param pmOrUserOrSystem
     *                      1=pm install
     *                      2=install as user app or convert system->user
     *                      3=install as system app or convert user->system
     * @return
     */
    public boolean installAsSuperUser(final String packageName,final String apkPath, boolean doInAsyncTask, final int pmOrUserOrSystem)
    {
        if(doInAsyncTask)
        {
            class MyAsyncTask  extends AsyncTask<Void,Void,String>
            {
                private String apkPathx=apkPath;
                private int pmOrUserOrSystemx=pmOrUserOrSystem;
                private ProgressDialog pd;
                private boolean reboot=false;

                protected void onPreExecute()
                {
                    super.onPreExecute();
                    pd=new ProgressDialog(context);
                    pd.setTitle("Installing App.......");
                    pd.setMessage(packageName);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setIndeterminate(true);
                    pd.setCancelable(true);
                    pd.show();
                }

                protected String doInBackground(Void... arg0)
                {
                    reboot=false;
                    apkPathx=apkPath;
                    pmOrUserOrSystemx=pmOrUserOrSystem;

                    try
                    {
                        PackageManager packageManager=context.getPackageManager();
                        PackageInfo info=packageManager.getPackageInfo(packageName,PackageManager.GET_META_DATA);

                        //alreadyInstalled
                        apkPathx=info.applicationInfo.publicSourceDir;
                        if(pmOrUserOrSystemx==1)
                            pmOrUserOrSystemx=2;
                    }
                    catch (Exception e)
                    {

                    }



                    switch (pmOrUserOrSystemx)
                    {
                        case 1:
                            /*
                            TRY NOT TO CALL IT IN-------BECOZ IT MAY TAKE SOME TIME
                             */
                            String x=pmInstall(apkPathx,packageName);
                            if(x==null)
                            {
                                reboot=true;
                                x=installAsUserApp(apkPathx);
                            }
                            return x;
                        case 2:
                            reboot=true;
                            return installAsUserApp(apkPathx);
                        case 3:
                            reboot=true;
                            return installAsSystemApp(apkPathx);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String toPath)
                {
                    pd.cancel();

                    if(toPath==null)
                    {
                        Toast.makeText(context, "operation failed", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context, "operation done", Toast.LENGTH_LONG).show();
                        if(reboot)
                        {
                            Toast.makeText(context, "Reboot to apply changes", Toast.LENGTH_LONG).show();
                            showRebootDialog();
                        }
                    }
                }

            }

            MyAsyncTask myAsyncTask=new MyAsyncTask();
            myAsyncTask.execute();
            return true;
        }
        else
        {
            String apkPathx=apkPath;
            int pmOrUserOrSystemx=pmOrUserOrSystem;

            try
            {
                PackageManager packageManager=context.getPackageManager();
                PackageInfo info=packageManager.getPackageInfo(packageName,PackageManager.GET_META_DATA);

                //alreadyInstalled
                apkPathx=info.applicationInfo.publicSourceDir;
                if(pmOrUserOrSystemx==1)
                    pmOrUserOrSystemx=2;
            }
            catch (Exception e)
            {

            }

            //called from service

            String x=null;
            switch (pmOrUserOrSystemx)
            {
                case 1:
                    x= pmInstall(apkPathx,packageName);
                    if(x==null)
                    {
                        MyCacheData.getInstallData(99).pmFailed=true;
                        x=installAsUserApp(apkPathx);
                        if(x!=null)
                            MyCacheData.getInstallData(99).rebootRequired=true;
                    }
                    break;
                case 2:
                    x= installAsUserApp(apkPathx);
                    if(x!=null)
                        MyCacheData.getInstallData(99).rebootRequired=true;
                    break;
                case 3:
                    x= installAsSystemApp(apkPathx);
                    if(x!=null)
                        MyCacheData.getInstallData(99).rebootRequired=true;
                    break;
            }

            if(x==null)
            {
                return false;
            }
            else
            {
                return true;
            }

        }
    }


    public void backupApk(final ArrayList<MyApp> myAppArrayList)
    {
        class MyAsyncTask  extends AsyncTask<Void,Void,String>
        {
            private ProgressDialog pd;
            private int appsBackedUp=0;
            protected void onPreExecute()
            {
                super.onPreExecute();
                pd=new ProgressDialog(context);
                pd.setTitle("APK backup in progress");
                pd.setMessage(myAppArrayList.size()+" items");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(true);
                pd.show();
            }

            protected String doInBackground(Void... arg0)
            {
                appsBackedUp=0;

                for(MyApp myApp:myAppArrayList)
                {
                    File backupFolder=new File(Constants.APK_BACKUP_PATH);
                    if(!backupFolder.exists())
                    {
                        boolean x=backupFolder.mkdirs();
                    }

                    if(backupFolder.exists())
                    {
                        String appFullName=myApp.getAppName();
                        if(!appFullName.toLowerCase().endsWith(".apk"))
                        {
                            appFullName+=".apk";
                        }
                        String toPath=Constants.APK_BACKUP_PATH+"/"+appFullName;
                        boolean x=commonUtils.copyFile(myApp.getApkPath(),toPath);
                        if(x)
                        {
                            appsBackedUp++;
                        }
                    }
                }
                return "vvbvb";
            }

            @Override
            protected void onPostExecute(String toPath)
            {
                if(appsBackedUp==0)
                {
                    Toast.makeText(context, "Failed to Backup", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context, "Backup created at "+Constants.APK_BACKUP_PATH, Toast.LENGTH_LONG).show();
                }
                pd.cancel();
                //refresh
            }

        }

        MyAsyncTask myAsyncTask=new MyAsyncTask();
        myAsyncTask.execute();
    }

    public void shareOneApk(String apkFullName,String path )
    {
        try
        {
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"));
            intent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://"+path));
            //intent.putExtra(Intent.EXTRA_TEXT,apkFullName);
            context.startActivity(Intent.createChooser(intent,"Share "+apkFullName));
        }
        catch (Exception e)
        {
            Toast.makeText(context, "operation failed", Toast.LENGTH_SHORT).show();
        }
    }


    //install as user app
    private String pmInstall(String apkPath,String packageName)
    {
        try
        {
            SuOperations.runCommand("pm install -r -d '"+apkPath+"'");
            boolean success;
            PackageManager packageManager=context.getPackageManager();
            try
            {
                PackageInfo info=packageManager.getPackageInfo(packageName,PackageManager.GET_META_DATA);
                success=true;
            }
            catch (Exception e)
            {
                success=false;
            }
            if(!success)
            {
                return null;
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return "all well";
    }

    //install as system app or convert user->system
    private String installAsSystemApp(String apkPath)
    {
        //copy the apk to system
        String systemAppDirectory;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            //for kitkat and post kitkat
            systemAppDirectory="/system/priv-app";
        }
        else
        {
            //for pre kitkat
            systemAppDirectory="/system/app";
        }

        String fromPath;
        final String x="/data/app/";
        String newName;
        if(apkPath.startsWith(x))
        {
            int lastindexofSlash=apkPath.lastIndexOf('/');
            if(lastindexofSlash>x.length())
            {
                //  /data/app/com.sahil.F2/f2.apk
                fromPath=apkPath.substring(0,lastindexofSlash);//  /data/app/com.sahil.F2
            }
            else
            {
                //  /data/app/f2.apk
                fromPath=apkPath;   //  /data/app/f2.apk
            }
            newName=apkPath.substring(x.length());   //    com.sahil.F2/f2.apk  or f2.apk
        }
        else
        {
            fromPath=apkPath;
            int lastindexofSlash=apkPath.lastIndexOf('/');
            newName=apkPath.substring(lastindexofSlash+1);   //    or f2.apk
        }

        String toPath=systemAppDirectory;
        Log.e("FROM :",fromPath);
        Log.e("TO :",toPath+"/"+newName);
        if(fromPath.startsWith(toPath))
            return null;
        boolean copied=RootTools.getInternals().copyFileFolderRecursive(fromPath,toPath,true,true);
        SuOperations.doChmod(slashAppender(toPath,newName),644);//(rw  r   r)
        boolean deleted=false;
        if(copied)
        {
            if(PagerXUtilities.isRootPath(fromPath))
            {
                deleted=RootTools.getInternals().deleteFileOrDirectory(fromPath,true);
            }
            else
            {
                //dont actually delete the backup up apk
                deleted=true;
            }
        }

        if(copied && deleted)
            return "ALL is well";
        else
            return null;
    }

    //install as user app or convert system->user
    private String installAsUserApp(String apkPath)
    {
        //copy the apk to user
        String userAppDirectory="/data/app";

        String fromPath;
        String newName;
        String x="/system/priv-app/";
        if(apkPath.startsWith(x))
        {
            int lastindexofSlash=apkPath.lastIndexOf('/');
            if(lastindexofSlash>x.length())
            {
                //  //system/priv-app/com.sahil.F2/f2.apk
                fromPath=apkPath.substring(0,lastindexofSlash);//  /system/priv-app/com.sahil.F2
            }
            else
            {
                //  /system/priv-app/f2.apk
                fromPath=apkPath;   //  /system/priv-app/f2.apk
            }
            newName=apkPath.substring(x.length());   //    com.sahil.F2/f2.apk  or f2.apk

        }
        else
        {
            x="/system/app/";
            if(apkPath.startsWith(x))
            {
                int lastindexofSlash=apkPath.lastIndexOf('/');
                if(lastindexofSlash>x.length())
                {
                    //  /system/app/com.sahil.F2/f2.apk
                    fromPath=apkPath.substring(0,lastindexofSlash);//  /system/app/com.sahil.F2
                }
                else
                {
                    //  /system/app/f2.apk
                    fromPath=apkPath;   //  /system/app/f2.apk
                }
                newName=apkPath.substring(x.length());   //    com.sahil.F2/f2.apk  or f2.apk
            }
            else
            {
                fromPath=apkPath;
                int lastindexofSlash=apkPath.lastIndexOf('/');
                newName=apkPath.substring(lastindexofSlash+1);   //    or f2.apk
            }
        }

        String toPath=userAppDirectory;
        Log.e("FROM :",fromPath);
        Log.e("TO :",toPath+"/"+newName);
        if(fromPath.startsWith(toPath))
            return null;
        boolean copied=RootTools.getInternals().copyFileFolderRecursive(fromPath,toPath,true,true);
        SuOperations.doChmod(slashAppender(toPath,newName),644);//(rw  r   r)

        boolean deleted=false;

        if(copied)
        {
            if(PagerXUtilities.isRootPath(fromPath))
            {
                deleted=RootTools.getInternals().deleteFileOrDirectory(fromPath,true);
            }
            else
            {
                //dont actually delete the backup up apk
                deleted=true;
            }
        }


        if(copied && deleted)
            return "ALL is well";
        else
            return null;
    }

    private String removeApk(String apkPath,String packageName)
    {

        boolean deleted;
        deleted= RootTools.getInternals().deleteFileOrDirectory(apkPath,true);
        if(packageName!=null && packageName.length()>2)
        {
            String dataPath="/data/data/"+packageName;
            RootTools.getInternals().deleteFileOrDirectory(dataPath,true);
        }
        pmUninstall(packageName);
        if(deleted)
        {
            return "all well";
        }
        else
        {
            return null;
        }
    }

    private String pmUninstall(String packageName)
    {
        try
        {
            SuOperations.runCommand("pm uninstall '"+packageName+"'");
            boolean success;
            PackageManager packageManager=context.getPackageManager();
            try
            {
                PackageInfo info=packageManager.getPackageInfo(packageName,PackageManager.GET_META_DATA);
                success=false;
            }
            catch (Exception e)
            {
                success=true;
            }
            if(!success)
            {
                return null;
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return "all well";
    }



    public void makeUnMakeSystemApp(final String apkPath,final boolean makeSystem)
    {
        class MyAsyncTask  extends AsyncTask<Void,Void,String>
        {
            private ProgressDialog pd;

            protected void onPreExecute()
            {
                super.onPreExecute();
                pd=new ProgressDialog(context);
                pd.setTitle(makeSystem?"Making System App.......":"Making User App.......");
                pd.setMessage(apkPath);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(true);
                pd.show();
            }

            protected String doInBackground(Void... arg0)
            {
                if(makeSystem)
                    return makeMeSystemApp();
                else
                    return makeMeUserApp();

            }

            private String makeMeSystemApp()
            {
                //copy the apk to system
                String systemAppDirectory;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    //for kitkat and post kitkat
                    systemAppDirectory="/system/priv-app";
                }
                else
                {
                    //for pre kitkat
                    systemAppDirectory="/system/app";
                }

                String fromPath;
                final String x="/data/app/";
                String newName;
                if(apkPath.startsWith(x))
                {
                    int lastindexofSlash=apkPath.lastIndexOf('/');
                    if(lastindexofSlash>x.length())
                    {
                        //  /data/app/com.sahil.F2/f2.apk
                        fromPath=apkPath.substring(0,lastindexofSlash);//  /data/app/com.sahil.F2
                    }
                    else
                    {
                        //  /data/app/f2.apk
                        fromPath=apkPath;   //  /data/app/f2.apk
                    }
                    newName=apkPath.substring(x.length());   //    com.sahil.F2/f2.apk  or f2.apk
                }
                else
                {
                    fromPath=apkPath;
                    int lastindexofSlash=apkPath.lastIndexOf('/');
                    newName=apkPath.substring(lastindexofSlash+1);   //    or f2.apk
                }

                String toPath=systemAppDirectory;
                Log.e("FROM :",fromPath);
                Log.e("TO :",toPath+"/"+newName);
                if(fromPath.startsWith(toPath))
                    return null;
                boolean copied=RootTools.getInternals().copyFileFolderRecursive(fromPath,toPath,true,true);
                SuOperations.doChmod(slashAppender(toPath,newName),644);//(rw  r   r)
                boolean deleted=false;
                if(copied)
                {
                    if(PagerXUtilities.isRootPath(fromPath))
                    {
                        deleted=RootTools.getInternals().deleteFileOrDirectory(fromPath,true);
                    }
                    else
                    {
                        //dont actually delete the backup up apk
                        deleted=true;
                    }
                }

                if(copied && deleted)
                    return "ALL is well";
                else
                    return null;
            }

            private String makeMeUserApp()
            {
                //copy the apk to user
                String userAppDirectory="/data/app";

                String fromPath;
                String newName;
                String x="/system/priv-app/";
                if(apkPath.startsWith(x))
                {
                    int lastindexofSlash=apkPath.lastIndexOf('/');
                    if(lastindexofSlash>x.length())
                    {
                        //  //system/priv-app/com.sahil.F2/f2.apk
                        fromPath=apkPath.substring(0,lastindexofSlash);//  /system/priv-app/com.sahil.F2
                    }
                    else
                    {
                        //  /system/priv-app/f2.apk
                        fromPath=apkPath;   //  /system/priv-app/f2.apk
                    }
                    newName=apkPath.substring(x.length());   //    com.sahil.F2/f2.apk  or f2.apk

                }
                else
                {
                    x="/system/app/";
                    if(apkPath.startsWith(x))
                    {
                        int lastindexofSlash=apkPath.lastIndexOf('/');
                        if(lastindexofSlash>x.length())
                        {
                            //  /system/app/com.sahil.F2/f2.apk
                            fromPath=apkPath.substring(0,lastindexofSlash);//  /system/app/com.sahil.F2
                        }
                        else
                        {
                            //  /system/app/f2.apk
                            fromPath=apkPath;   //  /system/app/f2.apk
                        }
                        newName=apkPath.substring(x.length());   //    com.sahil.F2/f2.apk  or f2.apk
                    }
                    else
                    {
                        fromPath=apkPath;
                        int lastindexofSlash=apkPath.lastIndexOf('/');
                        newName=apkPath.substring(lastindexofSlash+1);   //    or f2.apk
                    }
                }

                String toPath=userAppDirectory;
                Log.e("FROM :",fromPath);
                Log.e("TO :",toPath+"/"+newName);
                if(fromPath.startsWith(toPath))
                    return null;
                boolean copied=RootTools.getInternals().copyFileFolderRecursive(fromPath,toPath,true,true);
                SuOperations.doChmod(slashAppender(toPath,newName),644);//(rw  r   r)

                boolean deleted=false;
                if(copied)
                {
                    deleted=RootTools.getInternals().deleteFileOrDirectory(fromPath,true);
                }

                if(copied && deleted)
                    return "ALL is well";
                else
                    return null;
            }

            @Override
            protected void onPostExecute(String toPath)
            {
                pd.cancel();

                if(toPath==null)
                {
                    Toast.makeText(context, "operation failed", Toast.LENGTH_LONG).show();
                }
                else
                {
                    showRebootDialog();
                }
            }

        }

        MyAsyncTask myAsyncTask=new MyAsyncTask();
        myAsyncTask.execute();

    }


    private void showRebootDialog()
    {
        SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
        {
            @Override
            public void yesClicked()
            {
                Thread thread=new Thread()
                {
                    @Override
                    public void run()
                    {
                        SuOperations.runCommand("reboot");
                    }
                };
                thread.start();
            }

            @Override
            public void noClicked()
            {

            }
        };
        simpleYesNoDialog.showDialog(context,"REBOOT","Please Reboot your device to apply the Changes.","Reboot","Cancel");

    }


    private String slashAppender(String a,String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }

}
