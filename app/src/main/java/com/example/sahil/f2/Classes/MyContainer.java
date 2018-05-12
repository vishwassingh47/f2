package com.example.sahil.f2.Classes;

import com.example.sahil.f2.Cache.variablesCache;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/20/2017.
 */

public class MyContainer
{
    private String path,thumbUrl,name;
    private long lastModified;
    final private ArrayList<MyFile> myFileArrayList;
    private MyFile myFile;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public MyFile getMyFile()
    {
        return myFile;
    }

    public void setMyFile(MyFile myFile)
    {
        this.myFile = myFile;
    }

    public MyContainer()
    {
        myFileArrayList=new ArrayList<>();
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getThumbUrl()
    {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl)
    {
        this.thumbUrl = thumbUrl;
    }

    public long getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(long lastModified)
    {
        this.lastModified = lastModified;
    }

    public ArrayList<MyFile> getMyFileArrayList()
    {
        return myFileArrayList;
    }



}
