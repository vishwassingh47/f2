package com.example.sahil.f2.OperationTheater;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/23/2017.
 */

public class PasteClipBoard
{
    public static int fromStorageCode=0;
    public static String fromParentPath;

    public static int toStorageCode=0;
    public static String toRootPath;

    public static ArrayList<String> pathList=new ArrayList<>();
    public static ArrayList<String> nameList=new ArrayList<>();
    public static ArrayList<Long> sizeLongList=new ArrayList<>();
    public static ArrayList<Boolean> isFolderList=new ArrayList<>();

    public static int cutOrCopy=0;
    public static boolean isFastDownload=false;
    public static int timeInSec=1;
    public static boolean isFastMove=false;


    public static void clear()
    {
        fromStorageCode=0;
        fromParentPath="";
        toStorageCode=0;
        toRootPath="";

        pathList.clear();
        nameList.clear();
        sizeLongList.clear();
        isFolderList.clear();

        cutOrCopy=0;
        timeInSec=1;
        isFastDownload=false;
        isFastMove=false;
    }


}
