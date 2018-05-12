package com.example.sahil.f2.Rooted;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.sahil.f2.Cache.SearchData;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.GokuFrags.search_fragment;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.internal.RootToolsInternalMethods;

import org.apache.ftpserver.command.impl.HELP;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by hit4man47 on 11/7/2017.
 */

public class SuOperations
{

    public InputStream getRootInputStream(String path)
    {
        if(!SuperUser.hasUserEnabledSU)
        {
            return null;
        }

        InputStream inputStream;
        try
        {
            final String command="cat '"+path+"'";

            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            inputStream = process.getInputStream();
            //String err = (new BufferedReader(new InputStreamReader(process.getErrorStream()))).readLine();
            os.flush();

            /*
            if (process.waitFor() != 0 || (!"".equals(err) && null != err) && !containsIllegals(err))
            {
                Log.e("Root Error, cmd: " + cmd, err);
                return null;
            }
             */
            if (process.waitFor() != 0 )
            {
                Log.e("Root Error"," cmd: " + command);
                return null;
            }

            return inputStream;
        }
        catch (Exception e)
        {
            Log.e("Root Error"," cmd: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public static void runCommand(String c)
    {
        Log.e("started","just   "+c);

        Command command = new Command(477,25000,c)
        {
            @Override
            public void commandOutput(int id, String line)
            {
                super.commandOutput(id, line);
                Log.e("Output",line+"--"+id);
            }

            @Override
            public void commandTerminated(int id, String reason)
            {
                super.commandTerminated(id, reason);
                Log.e("Terminated",reason+"--"+id);
            }

            @Override
            public void commandCompleted(int id, int exitcode)
            {
                super.commandCompleted(id, exitcode);
                Log.e("Completed",id+"--"+exitcode);
            }
        };
        try
        {
            Shell shell= RootTools.getShell(true);
            shell.add(command);
            RootShell.commandWait(Shell.startRootShell(), command);
        }
        catch (Exception e)
        {
            Log.e("Error",e.getLocalizedMessage()+e.getMessage()+"--");
        }
        Log.e("all done","done");
    }


    public static boolean runCommand(final String commandString,final boolean asSu,final int timeOut)
    {
        Log.e("Running command",commandString);
        Command command;
        if(timeOut>0)
        {
            command= new Command(1502,timeOut,commandString)
            {
                @Override
                public void commandOutput(int id, String line)
                {
                    super.commandOutput(id, line);
                    Log.e("Output",line+"--"+id);
                }
            };
        }
        else
        {
            command= new Command(1502,commandString)
            {
                @Override
                public void commandOutput(int id, String line)
                {
                    super.commandOutput(id, line);
                    Log.e("Output",line+"--"+id);
                }
            };
        }

        try
        {
            Shell shell= RootTools.getShell(asSu);
            shell.add(command);
            RootShell.commandWait(Shell.startRootShell(), command);

            return command.getExitCode() == 0;
        }
        catch (Exception e)
        {
            Log.e("Error",e.getLocalizedMessage()+e.getMessage()+"--");
            return false;
        }
    }





    public int createNewFile(String filePath)
    {
        if(RootTools.exists(filePath,false))
        {
            return 1;
        }

        boolean b=RootTools.remount(filePath, "RW");
        if(!b)
        {
            return 2;
        }


        Command command=new Command(55,6000,"touch '"+filePath+"'");
        try
        {
            RootShell.getShell(true).add(command);
            RootShell.commandWait(Shell.startRootShell(), command);
            if(command.getExitCode()==0)
            {
                //command successed
                RootTools.remount(filePath, "RO");
                return 4;
            }
            else
            {
                return 3;
            }
        }
        catch (Exception e)
        {
            return 3;
        }
    }


    /**
     * @param folderPath complete folder path
     */
    public static boolean createNewFolder(final String folderPath)
    {
        RootTools.remount(folderPath, "RW");
        Command command=new Command(66,6000,"mkdir '"+folderPath+"'");
        try
        {
            RootShell.getShell(true).add(command);
            RootShell.commandWait(Shell.startRootShell(), command);
            RootTools.remount(folderPath, "RO");
        }
        catch (Exception e)
        {
            RootTools.remount(folderPath, "RO");
            return false;
        }
        return command.getExitCode()==0;
    }


    /**
     * //by vishwas
     */
    public static boolean doChmod(String destination,int permiss)
    {
        Command command = null;
        try
        {
            RootTools.remount(destination, "RW");
            String suCommand="chmod "+permiss+" '"+destination+"'";
            command = new Command(5555,suCommand);
            RootShell.getShell(true).add(command);
            RootShell.commandWait(Shell.startRootShell(), command);
            RootTools.remount(destination, "RO");
        }
        catch (Exception e)
        {
            return false;
        }

        return command.getExitCode()==0;
    }


    public static void runSuCommand(String c,int id)
    {
        Log.e("runSuCommand",id+"--"+c);

        Command command = new Command(id,c)
        {
            @Override
            public void commandOutput(int id, String line)
            {
                super.commandOutput(id, line);
                Log.e("Output",id+"--"+line);
            }

            @Override
            public void commandTerminated(int id, String reason)
            {
                super.commandTerminated(id, reason);
                Log.e("Terminated",reason+"--"+id);
            }

            @Override
            public void commandCompleted(int id, int exitcode)
            {
                super.commandCompleted(id, exitcode);
                Log.e("Completed",id+"--"+exitcode);
            }
        };
        try
        {
            Shell shell= RootTools.getShell(true);
            shell.add(command);
            RootShell.commandWait(Shell.startRootShell(), command);
        }
        catch (Exception e)
        {
            Log.e("Error",e.getLocalizedMessage()+e.getMessage()+"--");
        }
        Log.e("runSuCommand done",id+"--"+c);
    }


}
