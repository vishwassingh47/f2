package com.example.sahil.f2.Cache;

import com.example.sahil.f2.Classes.MyApp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hit4man47 on 2/3/2018.
 */

public class appManagerCache
{
    /*
    whatToDo:
    1:installed Apps
    2:system apps
    3:apk
     */
    public static int whatToDo;
    public static int threadFetchId;
    public static int storageId;
    public static ArrayList<MyApp> uninstallList;
    public static ArrayList<MyApp> installList;

    public static void clear()
    {
        whatToDo=1;
        threadFetchId=1;
        storageId=1;
        uninstallList=new ArrayList<>();
        installList=new ArrayList<>();
    }
}
