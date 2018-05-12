package com.example.sahil.f2.Cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.example.sahil.f2.Classes.ThumbNails;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hit4man47 on 11/17/2017.
 */

public class ThumbNailCache
{
    public static final HashMap<String,Bitmap> path_bitmap_map=new HashMap<>();
    public static final HashMap<ImageView,String> mod0_imageView_path_map=new HashMap<>(),mod1_imageView_path_map=new HashMap<>(),mod2_imageView_path_map=new HashMap<>();
    public static final ArrayList<String> mod0_priority_pathList=new ArrayList<>(),mod1_priority_pathList=new ArrayList<>(),mod2_priority_pathList=new ArrayList<>();
    public static int mod0_threadCount=0,mod1_threadCount=0,mod2_threadCount=0;
    public static long bitmapSize=0;
    public static final ArrayList<String> alreadyProcessedList=new ArrayList<>();

    public static synchronized void mod0_minus()
    {
        -- mod0_threadCount;
    }

    public static synchronized void mod0_plus()
    {
        ++ mod0_threadCount;
    }

    public static synchronized void mod1_minus()
    {
        -- mod1_threadCount;
    }

    public static synchronized void mod1_plus()
    {
        ++ mod1_threadCount;
    }

    public static synchronized void mod2_minus()
    {
        -- mod2_threadCount;
    }

    public static synchronized void mod2_plus()
    {
        ++ mod2_threadCount;
    }




    public static void clear_mod0()
    {
        mod0_imageView_path_map.clear();
        mod0_priority_pathList.clear();
    }
    public static void clear_mod1()
    {
        mod1_imageView_path_map.clear();
        mod1_priority_pathList.clear();
    }
    public static void clear_mod2()
    {
        mod2_imageView_path_map.clear();
        mod2_priority_pathList.clear();
    }




    public static void hardClear()
    {
        path_bitmap_map.clear();
        alreadyProcessedList.clear();
        bitmapSize=0;
        clear_mod0();
        clear_mod1();
        clear_mod2();
    }

}
