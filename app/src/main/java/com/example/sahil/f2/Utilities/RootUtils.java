package com.example.sahil.f2.Utilities;

import android.content.Context;
import android.util.Log;

import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.Rooted.SuperUser;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.File;

public class RootUtils
{
    private final String TAG="RootUtils";
    private long free=0;

    public long getFreeSpace(String path, Context context)
    {
        free=0;
        File myLsFile=context.getFileStreamPath("myls");
        if(!myLsFile.exists())
        {
            Log.e(TAG,"command to execute :" + myLsFile.getAbsolutePath() + " is missing");
            return -1;
        }

        final String commandString=myLsFile.getAbsolutePath()+" 5 '"+path+"'";
        Log.e("command run:",commandString);
        Command command= new Command(1257,10000,commandString)
        {
            @Override
            public void commandOutput(int id, String line)
            {
                super.commandOutput(id, line);
                Log.e(TAG,"Output "+line+"--"+id);

                free= HelpingBot.parseLong(line);
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
            }
            return free;
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getLocalizedMessage()+e.getMessage()+"--");
            return -1;
        }
    }
}
