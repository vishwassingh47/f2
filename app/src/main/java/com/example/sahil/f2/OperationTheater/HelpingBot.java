package com.example.sahil.f2.OperationTheater;

import android.app.Activity;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;

import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by sahil on 23-07-2017.
 */

public class HelpingBot
{

    public String sizeinwords(long sizeinBytes )
    {
        boolean isNegative=sizeinBytes<0;
        if(isNegative)
        {
            sizeinBytes=sizeinBytes*(-1);
        }
        String suffix="";
        float varSize=0;
        if(sizeinBytes<1024)
        {
            varSize=(float)sizeinBytes;
            suffix="Byte";
        }
        if(sizeinBytes>=1024 && sizeinBytes<1048576)
        {
            varSize=(float)sizeinBytes/1024;
            suffix="KB";
        }
        if(sizeinBytes>=1048576 && sizeinBytes<1073741824)
        {
            varSize=(float)sizeinBytes/1048576;
            suffix="MB";
        }
        if(sizeinBytes>=1073741824)
        {
            varSize=(float)sizeinBytes/1073741824;
            suffix="GB";
        }

        if(isNegative)
        {
            varSize=varSize*(-1);
        }
        return String.format("%.2f",varSize)+" "+suffix;
    }

    public String getCustomFileExtension(File file) //file should not be a folder
    {
        String path=file.getAbsolutePath();
        int lastIndexOfSlash=path.lastIndexOf('/');
        String name=path.substring(lastIndexOfSlash+1);
        int lastIndexOfDot=name.lastIndexOf('.');

        /*
        name can be of these order:
        1   me.txt
        2   .txt
        3   me.
        4   me
         */
        if(lastIndexOfDot!=name.length()-1 && lastIndexOfDot>=0)//if not case 3 and not 4
        {
            String extension=name.substring(lastIndexOfDot+1);
            return extension;
        }
        else
        {
            return null;
        }

    }


    public int getPathLogo(int pathId)
    {
        if(pathId==1 || pathId==2 || pathId==3 || pathId%10==0 || pathId%11==0)
        {
            return R.drawable.sd_card;
        }
        if(pathId==4)
        {
            return R.mipmap.google_drive;
        }
        if(pathId==5)
        {
            return R.mipmap.dropbox;
        }
        if(pathId==6)
        {
            return R.mipmap.server;
        }

        return R.mipmap.unknown;

    }

    public String getPathRelativeToLogo(int pathId,String path)
    {
        String result="";

        if(pathId==1 || pathId==2 || pathId==3 || pathId%10==0 || pathId%11==0)
        {
            String storagePath="";
            for(String x:MainActivity.Physical_Storage_PATHS)
            {
                if(path.startsWith(x))
                {
                    storagePath=x;
                }
            }
            result=path.replaceFirst(Pattern.quote(storagePath),"");
            return result;
        }
        if(pathId==4)
        {
            return "/..."+path;
        }
        if(pathId==5 || pathId==6)
        {
            return path;
        }


        return "";
    }

    public int getTaskLogo(String taskId)
    {
        switch (taskId)
        {
            case "1":
                return R.mipmap.delete1;
            case "2":
                return R.mipmap.delete1;
            case "99":
                return R.mipmap.install;
            case "199":
                return R.mipmap.uninstall;

            case "101":
                return R.mipmap.copy;
            case "102":
                return R.mipmap.copy;

            case "201":
                return R.mipmap.download;
            case "202":
                return R.mipmap.download;
            case "203":
                return R.mipmap.download;
            case "204":
                return R.mipmap.download;
            case "205":
                return R.mipmap.download;
            case "206":
                return R.mipmap.download;


            case "301":
                return R.mipmap.upload;
            case "302":
                return R.mipmap.upload;
            case "303":
                return R.mipmap.upload;
            case "304":
                return R.mipmap.upload;
            case "305":
                return R.mipmap.upload;
            case "306":
                return R.mipmap.upload;

            default:
                return R.mipmap.unknown;
        }
    }

    public String getTaskName(String taskId)
    {
        switch (taskId)
        {
            case "1":
                return "Deleting 1";
            case "2":
                return "Deleting 2";
            case "99":
                return "Installing";
            case "199":
                return "Uninstalling";
            case "101":
                return "Moving 1Copying 1";
            case "102":
                return "Moving 2Copying 2";

            case "201":
                return "Downloading from DropBox 1";
            case "202":
                return "Downloading from DropBox 2";
            case "203":
                return "Downloading from Google Drive 1";
            case "204":
                return "Downloading from Google Drive 2";
            case "205":
                return "Downloading from FTP Server 1";
            case "206":
                return "Downloading from FTP Server 2";

            case "301":
                return "Uploading to DropBox 1";
            case "302":
                return "Uploading to DropBox 2";
            case "303":
                return "Uploading to Google Drive 1";
            case "304":
                return "Uploading to Google Drive 2";
            case "305":
                return "Uploading to FTP Server 1";
            case "306":
                return "Uploading to FTP Server 2";


            default:
                return "unknown Task";
        }
    }


    public int getTaskLogo(int taskId)
    {
        switch (taskId)
        {
            case 1:
                return R.mipmap.delete1;
            case 2:
                return R.mipmap.delete1;
            case 99:
                return R.mipmap.install;
            case 199:
                return R.mipmap.uninstall;
            case 101:
                return R.mipmap.copy;
            case 102:
                return R.mipmap.copy;

            case 201:
                return R.mipmap.download;
            case 202:
                return R.mipmap.download;
            case 203:
                return R.mipmap.download;
            case 204:
                return R.mipmap.download;
            case 205:
                return R.mipmap.download;
            case 206:
                return R.mipmap.download;



            case 301:
                return R.mipmap.upload;
            case 302:
                return R.mipmap.upload;
            case 303:
                return R.mipmap.upload;
            case 304:
                return R.mipmap.upload;
            case 305:
                return R.mipmap.upload;
            case 306:
                return R.mipmap.upload;


            default:
                return R.mipmap.unknown;
        }
    }

    public String getTaskName(int taskId)
    {
        switch (taskId)
        {
            case 1:
                return "Deleting 1";
            case 2:
                return "Deleting 2";
            case 99:
                return "Installing";
            case 199:
                return "Uninstalling";
            case 101:
                CopyData copyData= MyCacheData.getCopyDataFromCode(taskId);
                return copyData.isMoving?"Moving 1":"Copying 1";
            case 102:
                CopyData copyData2= MyCacheData.getCopyDataFromCode(taskId);
                return copyData2.isMoving?"Moving 1":"Copying 1";

            case 201:
                return "Downloading from DropBox 1";
            case 202:
                return "Downloading from DropBox 2";
            case 203:
                return "Downloading from Google Drive 1";
            case 204:
                return "Downloading from Google Drive 2";
            case 205:
                return "Downloading from FTP Server 1";
            case 206:
                return "Downloading from FTP Server 2";


            case 301:
                return "Uploading to DropBox 1";
            case 302:
                return "Uploading to DropBox 2";
            case 303:
                return "Uploading to Google Drive 1";
            case 304:
                return "Uploading to Google Drive 2";
            case 305:
                return "Uploading to FTP Server 1";
            case 306:
                return "Uploading to FTP Server 2";


            default:
                return "unknown Task";
        }
    }


    public static void hideKeyboard(Activity activity)
    {
        try
        {
            InputMethodManager inputMethodManager=(InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
        }
        catch (Exception e)
        {
        }

    }


    public static long parseLong(String s)
    {
        try
        {
            return Long.parseLong(s);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static int parseInt(String s)
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch (Exception e)
        {
            return 0;
        }
    }


    public static String slashAppender(String a, String b)
    {
        if(a.endsWith("/") && b.startsWith("/"))
        {
            a=a.substring(0,a.length()-1);
            return a+b;
        }
        if(a.endsWith("/") || b.startsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }

    public static String getParentPath(String path)
    {
        int lastIndexOfSlash=path.lastIndexOf('/');
        if(lastIndexOfSlash<0)
            lastIndexOfSlash=0;
        String parent=path.substring(0,lastIndexOfSlash);
        if(parent.length()==0)
            parent="/";
        return parent;
    }

    public static String getNameFromPath(String path)
    {
        int lastIndexOfSlash=path.lastIndexOf('/');
        String name=path.substring(lastIndexOfSlash+1);
        return name;
    }

    public static String getInitialName(final String name)
    {
        int lastIndexOfDot=name.lastIndexOf('.');
        if(lastIndexOfDot<0)
        {
            return name;
        }
        else
        {
            return name.substring(0,lastIndexOfDot);
        }
    }

    /**
     *
     * @param name
     * @return      ''   OR     '.apk'      OR       '.mp3'
     *
     */
    public static String getExtension(final String name)
    {
        int lastIndexOfDot=name.lastIndexOf('.');
        if(lastIndexOfDot<0)
        {
            return "";
        }
        else
        {
            return name.substring(lastIndexOfDot);
        }
    }

    public static void addPageAndGoto(final MainActivity mainActivityObject,String pageName,final int currentVisiblePageIndex,String firstPath,int iconId,int pageId)
    {
        mainActivityObject.addPage(pageName,currentVisiblePageIndex+1,firstPath,iconId,pageId);

        mainActivityObject.setUpViewPager();

        gotoPage(mainActivityObject,currentVisiblePageIndex+1);
    }
    public static void gotoPage(final MainActivity mainActivityObject, final int atIndex)
    {
        Handler handler=new Handler();
        Runnable runnable=new Runnable()
        {
            @Override
            public void run()
            {
                mainActivityObject.viewPager.setCurrentItem(atIndex,true);
            }
        };
        handler.post(runnable);
    }

    public static int getIndexOfPage(final String pageName,final int pageId)
    {
        Page page;
        for(int i=0;i<MainActivity.pageList.size();i++)
        {
            page=MainActivity.pageList.get(i);
            if(page.getPageId()==pageId && page.getName().equals(pageName))
            {
                return i;
            }
        }
        return -1;
    }

    public static void removeAllPages(final String pageName,final int pageId)
    {
        Page page;
        for(int i=0;i<MainActivity.pageList.size();i++)
        {
            page=MainActivity.pageList.get(i);
            if(page.getPageId()==pageId && page.getName().equals(pageName))
            {
                MainActivity.pageList.remove(page);
                i--;
            }
        }
    }





}
