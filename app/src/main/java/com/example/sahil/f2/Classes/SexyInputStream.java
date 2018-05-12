package com.example.sahil.f2.Classes;

import android.util.Log;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Rooted.SuperUser;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by hit4man47 on 4/2/2018.
 */

public class SexyInputStream
{
    private String path;
    private InputStream inputStream=null;
    private FileInputStream fileInputStream=null;
    private boolean isOk;
    private final int MAX_BUFFER_SIZE=61440;

    public SexyInputStream(String path)
    {
        this.path=path;
        this.inputStream=null;
        this.fileInputStream=null;
        try
        {
            fileInputStream=new FileInputStream(path);
        }
        catch(Exception e)
        {
            fileInputStream=null;
        }

        if(fileInputStream==null)
        {
            inputStream=getRootInputStream(path);
        }

        isOk=fileInputStream!=null || inputStream!=null;

    }

    public boolean isOk()
    {
        return isOk;
    }

    public boolean skipBytes(long bytesToSkip)
    {
        if(bytesToSkip==0)
        {
            return true;
        }

        if(fileInputStream!=null)
        {
            try
            {
                fileInputStream.getChannel().position(bytesToSkip);
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        if(inputStream!=null)
        {
            try
            {
                long skipRemaining=bytesToSkip;
                int retry=5;
                long x;
                while(skipRemaining>0 && retry>0)
                {
                    x=inputStream.skip(skipRemaining);
                    if(x>0)
                    {
                        skipRemaining-=x;
                    }
                    else
                    {
                        return false;
                    }
                    retry--;
                }
                if(skipRemaining>0)
                {
                    int len;
                    while(skipRemaining>0)
                    {
                        int bSize=(int)Math.min(skipRemaining,MAX_BUFFER_SIZE);
                        byte []buffer=new byte[bSize];
                        len=read(buffer);
                        if(len>0)
                        {
                            skipRemaining-=len;
                        }
                        else
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        return false;
    }

    public int read(byte[] buffer)
    {
        if(fileInputStream!=null)
        {
            try
            {
                return fileInputStream.read(buffer);
            }
            catch (Exception e)
            {
                return Constants.SEXY_INPUT_STREAM_READ_FAILED;
            }
        }

        if(inputStream!=null)
        {
            try
            {
                return inputStream.read(buffer);
            }
            catch (Exception e)
            {
                return Constants.SEXY_INPUT_STREAM_READ_FAILED;
            }
        }

        return -1;
    }

    public void close()
    {
        try
        {
            if(fileInputStream!=null)
            {
                fileInputStream.close();
            }
            if(inputStream!=null)
            {
                inputStream.close();
            }
        }
        catch (Exception e)
        {

        }
    }
    private InputStream getRootInputStream(String path)
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
            os.flush();

            if (process.waitFor() != 0 )
            {
                Log.e("getRootInputStream"," cmd: " + command);
                return null;
            }

            return inputStream;
        }
        catch (Exception e)
        {
            Log.e("getRootInputStream"," cmd: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
