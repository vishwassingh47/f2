package com.example.sahil.f2.Cache;

import android.util.Log;

import com.example.sahil.f2.Classes.MyFile;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 1/21/2018.
 */

public class SearchData
{
    public String whereToSearch;
    public volatile String nameToSearch;
    public int threadId;
    public final ArrayList<MyFile> result=new ArrayList<>();

    public boolean searching=false;
    public boolean searchingError=false;
    public boolean searched=false;
    public boolean notifyUser=false;
    public boolean isDeepThreadToSearchFinished=false;
    public boolean isDeepSearch=true;

    public void clearStorage()
    {
        threadId=-5;

        //setting the edittext to blank
        nameToSearch="";
        result.clear();

        searching=false;
        searchingError=false;
        searched=false;
        notifyUser=false;
        isDeepThreadToSearchFinished=false;
        isDeepSearch=true;

    }

    public synchronized boolean removeIfNameChanged()
    {
        synchronized (result)
        {
            boolean removed=false;
            for(int i = 0; i< result.size(); i++)
            {
                if(!result.get(i).getName().toLowerCase().contains(nameToSearch))
                {
                    result.remove(i);
                    removed=true;
                    i--;
                }
            }

            return removed;
        }
    }

    public synchronized boolean containsPath(String path)
    {
        synchronized (result)
        {
            for(MyFile file:result)
            {
                if(file.getPath().equals(path))
                {
                    return true;
                }
            }
            return false;
        }
    }


}
