package com.example.sahil.f2.Cache;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/28/2017.
 */

public class favouritesCache
{
    public static int storageId=1;
    public static ArrayList<String> favouritePaths;
    public static ArrayList<Integer> favouriteStorageIdList;

    public static void clear()
    {
        storageId=1;
        favouritePaths=new ArrayList<>();
        favouriteStorageIdList=new ArrayList<>();
    }
}
