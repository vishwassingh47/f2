package com.example.sahil.f2.Classes;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 11/22/2017.
 */

public class Page
{
    private final ArrayList<String> pathList;
    private final ArrayList<Integer> indexList;
    private final String name;
    private final int iconId;
    private final int pageId;

    public Page(String name,String firstPath,int firstIndex,int iconId,int pageId)
    {
        this.name=name;
        this.iconId=iconId;
        pathList=new ArrayList<>();
        indexList=new ArrayList<>();
        this.pageId=pageId;

        pathList.add(firstPath);
        indexList.add(firstIndex);
    }

    public String getCurrentPath()
    {
        return pathList.get(pathList.size()-1);
    }

    public int getCurrentIndex()
    {
        return indexList.get(indexList.size()-1);
    }

    public int getIconId()
    {
        return iconId;
    }

    public ArrayList<String> getPathList()
    {
        return pathList;
    }

    public ArrayList<Integer> getIndexList()
    {
        return indexList;
    }

    public String getName()
    {
        return name;
    }

    public int getPageId() {return pageId;}
}
