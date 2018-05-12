package com.example.sahil.f2.Cache;

import java.util.HashMap;

/**
 * Created by hit4man47 on 1/22/2018.
 */

public class StorageAnalyserData
{
    public boolean searching;
    public boolean successfullySearched;
    public boolean errorOccured;
    public int threadId;

    public long totalBytesFetched=0;
    public final HashMap<String,Long> resultMap=new HashMap<>();
    public long used=0;
    public long total=0;


    public void initializer()
    {
        searching=false;
        successfullySearched =false;
        errorOccured=false;
        threadId =-5;
        resultMap.clear();
        totalBytesFetched=0;
        used=0;
        total=0;
    }

}
