package com.example.sahil.f2.Classes;

import android.content.Context;
import android.util.Log;

import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.Rooted.SuperUser;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.File;

/**
 * Created by hit4man47 on 3/29/2018.
 */

public class OneFile
{
    private boolean exist,file,directory,hasAccess,isJavaFile,canWrite;
    private long length;
    private String name,absolutePath;



    public boolean isCanWrite() {
        return canWrite;
    }

    public OneFile(final String path, Context context)
    {
        final String TAG="createOneFile";
        File jFile=new File(path);
        if(jFile.exists())
        {
            isJavaFile=true;
            canWrite=jFile.canWrite();
            hasAccess=true;
            directory=jFile.isDirectory();
            file=jFile.isFile();
            exist=true;
            length=jFile.length();
            name=jFile.getName();
            absolutePath=jFile.getAbsolutePath();
        }
        else
        {
            canWrite=false;
            isJavaFile=false;
            File myLsFile=context.getFileStreamPath("myls");
            if(!myLsFile.exists())
            {
                Log.e(TAG,"command to execute :" + myLsFile.getAbsolutePath() + " is missing");
                hasAccess=true;
                exist=false;
                return;
            }

            final String commandString=myLsFile.getAbsolutePath()+" 4 '"+path+"'";
            Log.e("commans run:",commandString);
            Command command= new Command(9898,10000,commandString)
            {
                @Override
                public void commandOutput(int id, String line)
                {
                    super.commandOutput(id, line);
                    Log.e(TAG,"Output "+line+"--"+id);

                    String s[]=line.split("#@\\$");
                    if(s.length==2)
                    {
                        int x= HelpingBot.parseInt(s[1]);
                        switch (x)
                        {
                            case 2:
                                exist=false;
                                hasAccess=true;
                                break;
                            case 13:
                                hasAccess=false;
                                exist=true;
                                break;
                            default:
                                exist=false;
                                hasAccess=true;
                                break;
                        }
                    }
                    else
                    {
                        if(s.length==4)
                        {
                            String nameX=HelpingBot.getNameFromPath(path);
                            exist=true;
                            hasAccess=true;
                            name=nameX;
                            absolutePath=path;
                            //oneFile.setLastModified((long)(HelpingBot.parseLong(s[3])*1000));
                            int x=HelpingBot.parseInt(s[0]);
                            if(x==1 || x==4)
                            {
                                file=true;
                                directory=false;
                                long size=HelpingBot.parseLong(s[2]);
                                length=size;
                            }
                            if(x==2 || x==5)
                            {
                                file=false;
                                directory=true;
                                length=0;
                            }
                        }
                        else
                        {
                            hasAccess=true;
                            exist=false;
                        }
                    }
                }
            };

            try
            {
                Shell shell= RootTools.getShell(SuperUser.hasUserEnabledSU);
                shell.add(command);
                RootShell.commandWait(Shell.startRootShell(), command);
                if(command.getExitCode() != 0)
                {
                    Log.e(TAG,"UnKnown Error");
                    exist=false;
                    hasAccess=true;
                }
            }
            catch (Exception e)
            {
                Log.e(TAG,e.getLocalizedMessage()+e.getMessage()+"--");
                exist=false;
                hasAccess=true;
            }
        }
    }

    public boolean isExist() {
        return exist;
    }

    public boolean isFile() {
        return file;
    }

    public boolean isHavingAccess() {
        return hasAccess;
    }

    public boolean isDirectory() {
        return directory;
    }

    public long getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isJavaFile() {
        return isJavaFile;
    }

}
