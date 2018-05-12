package com.example.sahil.f2.Classes;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 10/8/2017.
 */

public class SuperGoogleDriveList
{
    public int lastItem=0,totalHidden=0,totalFolders=0;
    public String pathId,pathName;
    public ArrayList<String> nameList,sizeList,fileIdList;
    public ArrayList<Boolean> checkList,isFolder;
    public ArrayList<Long> sizeLong;

    public SuperGoogleDriveList(String pathid,String pathname,int totalHidden,int totalFolders,ArrayList<String>nList,ArrayList<String>sList,ArrayList<Boolean>cList,ArrayList<Boolean>fList,ArrayList<Long> sLong,ArrayList<String> fIdList)
    {
        this.pathId=pathid;
        this.pathName=pathname;
        this.totalFolders=totalFolders;
        this.totalHidden=totalHidden;
        nameList=new ArrayList<>();
        checkList=new ArrayList<>();
        isFolder=new ArrayList<>();
        sizeList=new ArrayList<>();
        sizeLong=new ArrayList<>();
        fileIdList=new ArrayList<>();


        if(nList!=null)
            for(int i=0;i<nList.size();i++)
            {
                nameList.add(nList.get(i));
                sizeList.add(sList.get(i));
                checkList.add(cList.get(i));
                isFolder.add(fList.get(i));
                sizeLong.add(sLong.get(i));
                fileIdList.add(fIdList.get(i));
            }
    }

    public void setLastItem(int last)
    {
        lastItem=last;
    }

}
