package com.example.sahil.f2.Utilities;

import android.content.Context;
import android.os.Build;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.OperationTheater.PagerXUtilities;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by hit4man47 on 2/19/2018.
 */

public class CommonUtils
{
    private final String TAG="COMMON_UTILS";
    private final KeepBothUtils keepBothUtils;

    public CommonUtils()
    {
        keepBothUtils=new KeepBothUtils();
    }


    public boolean copyFile(String fromPath,String toPath)
    {
        try
        {
            toPath=keepBothUtils.getUniquePathLocal(toPath,false);

            InputStream inputStream=new FileInputStream(fromPath);
            OutputStream outputStream=new FileOutputStream(toPath);
            byte[] buf = new byte[61440];
            int len;
            while ((len=inputStream.read(buf))>0)
            {
                outputStream.write(buf,0,len);
            }
            try
            {
                inputStream.close();
                outputStream.close();
            }
            catch (Exception e)
            {}

            File toFile=new File(toPath);
            if(toFile.exists())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,"COPYFILE FAILED:---"+e.getMessage());
            return false;
        }
    }

    public OutputStream getOutputStream(String toPath, boolean toResume, Context context)
    {
        OutputStream outputStream=null;
        try
        {
            if(toResume)
            {
                outputStream=new BufferedOutputStream(new FileOutputStream(toPath,true));
            }
            else
            {
                outputStream= new BufferedOutputStream(new FileOutputStream(toPath));
            }
        }
        catch (Exception e)
        {
            outputStream=null;
        }

        if(outputStream!=null)
        {
            return outputStream;
        }


        if(PagerXUtilities.isExternalSdCardPath(toPath))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                //SAF
                //assuming that we are having SAF permissions at this point
                try
                {
                    DocumentFile df = StorageAccessFramework.fileToDocumentFileConverter(toPath,false,context);
                    if(df!=null)
                    {
                        if(toResume)
                        {
                            outputStream = new BufferedOutputStream(context.getContentResolver().openOutputStream(df.getUri(),"wa"));
                        }
                        else
                        {
                            outputStream = new BufferedOutputStream(context.getContentResolver().openOutputStream(df.getUri()));
                        }
                    }
                }
                catch (Exception e)
                {
                    outputStream=null;
                }
            }
            else
            {
                //MEDIA STORE HACK
            }
        }
        return outputStream;
    }


}
