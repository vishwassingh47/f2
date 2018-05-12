package com.example.sahil.f2.Cache;

import com.example.sahil.f2.MainActivity;

/**
 * Created by hit4man47 on 12/27/2017.
 */

public class recycleBinCache
{
    public static int storageId=1;

    public static String internalPath= MainActivity.Physical_Storage_PATHS.get(0)+"/f2/.RecycleBin";

    public static void clear()
    {
        storageId=1;
        internalPath= MainActivity.Physical_Storage_PATHS.get(0)+"/f2/.RecycleBin";
    }
}
