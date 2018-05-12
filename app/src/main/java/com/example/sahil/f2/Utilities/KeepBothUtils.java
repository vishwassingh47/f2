package com.example.sahil.f2.Utilities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBoxUtils;
import com.example.sahil.f2.OperationTheater.HelpingBot;

import org.apache.ftpserver.command.impl.HELP;

import java.io.File;

/**
 * Created by hit4man47 on 2/18/2018.
 */

public class KeepBothUtils
{
    private final String TAG="KEEP_BOTH_UTILS";


    /**
     *
     * @param path our expected unique path
     * @param isFolder if we want our unique path to be folder
     * @return non existing path
     */
    public String getUniquePathLocal(final String path,final boolean isFolder)
    {

        File file=new File(path);
        if(file.exists())
        {
            final String parentPath= HelpingBot.getParentPath(path);
            final String oldName=file.getName();
            int fileNumber=0;

            if(isFolder)
            {
                String newName=oldName+"("+(++fileNumber)+")";

                while(true)
                {
                    File newFile=new File(slashAppender(parentPath,newName));
                    if(newFile.exists())
                    {
                        newName=oldName+"("+(++fileNumber)+")";
                    }
                    else
                    {
                        break;
                    }
                }
                return slashAppender(parentPath,newName);
            }
            else
            {
                String initialName,extension;
                final int lastIndexOfDot=oldName.lastIndexOf('.');
                if(lastIndexOfDot<0)
                {
                    extension="";
                    initialName=oldName;
                }
                else
                {
                    initialName=oldName.substring(0,lastIndexOfDot);
                    extension=oldName.substring(lastIndexOfDot);
                }

                String newName=initialName+"("+(++fileNumber)+")"+extension;
                while(true)
                {
                    File newFile=new File(slashAppender(parentPath,newName));
                    if(newFile.exists())
                    {
                        newName=initialName+"("+(++fileNumber)+")"+extension;
                    }
                    else
                    {
                        break;
                    }
                }
                return slashAppender(parentPath,newName);
            }
        }
        else
        {
            return path;
        }
    }

    /**
     * @param path our expected unique path
     * @param isFolder if we want our unique path to be folder
     * @return non existing path or null if error
     */
    public String getUniquePathDropBox(final String path,final boolean isFolder)
    {

        try
        {
            if(DropBoxUtils.exist(path))
            {
                final String parentPath= HelpingBot.getParentPath(path);
                final String oldName= HelpingBot.getNameFromPath(path);
                int fileNumber=0;

                if(isFolder)
                {
                    String newName=oldName+"("+(++fileNumber)+")";

                    while(true)
                    {
                        if(DropBoxUtils.exist(slashAppender(parentPath,newName)))
                        {
                            newName=oldName+"("+(++fileNumber)+")";
                        }
                        else
                        {
                            break;
                        }
                    }
                    return slashAppender(parentPath,newName);
                }
                else
                {
                    String initialName,extension;
                    final int lastIndexOfDot=oldName.lastIndexOf('.');
                    if(lastIndexOfDot<0)
                    {
                        extension="";
                        initialName=oldName;
                    }
                    else
                    {
                        initialName=oldName.substring(0,lastIndexOfDot);
                        extension=oldName.substring(lastIndexOfDot);
                    }

                    String newName=initialName+"("+(++fileNumber)+")"+extension;
                    while(true)
                    {
                        if(DropBoxUtils.exist(slashAppender(parentPath,newName)))
                        {
                            newName=initialName+"("+(++fileNumber)+")"+extension;
                        }
                        else
                        {
                            break;
                        }
                    }
                    return slashAppender(parentPath,newName);
                }
            }
            else
            {
                return path;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage()+"--"+e.getLocalizedMessage());
            return null;
        }
    }


    @NonNull
    private String slashAppender(String a, String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }

}
