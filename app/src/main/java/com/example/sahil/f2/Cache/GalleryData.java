package com.example.sahil.f2.Cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.sahil.f2.Classes.MyContainer;
import com.example.sahil.f2.Classes.MyFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hit4man47 on 9/26/2017.
 */

public class GalleryData
{
    
    public  final ArrayList<MyContainer> searchResultsPager1,searchResultsPager2,searchResultsPager3,searchResultsPager4,searchResultsPager5;
    public  boolean searchingPager1, searchingPager2, searchingPager3, searchingPager4, searchingPager5;
    public  boolean successfullySearchedPager1, successfullySearchedPager2, successfullySearchedPager3, successfullySearchedPager4, successfullySearchedPager5;
    public  int threadIdPager1, threadIdPager2, threadIdPager3, threadIdPager4, threadIdPager5;
    public  int storageId;
    public  String storagePath;
    public  MyContainer currentMyContainer;
    public  final HashMap<String,Bitmap> path_bitmap_map;
    public  final HashMap<ImageView,String> imageView_path_map ;
    public  final ArrayList<String> priority_pathList;
    public  final ArrayList<String> alreadyProcessed;


    public  String whatToSearch=null;
    public  int searchThreadId=-5;
    public  final ArrayList<MyContainer> filteredMyContainerList;
    public  final ArrayList<MyFile> filteredMyFileList;

    public  int threadCount =0;
    public  long bitmapSize =0;
    public  int totalThreadCalled =0;


    public  synchronized void minus()
    {
        --threadCount;
    }

    public  synchronized void plus()
    {
        ++threadCount;
    }


    public GalleryData()
    {
        searchResultsPager1=new ArrayList<>();
        searchResultsPager2=new ArrayList<>();
        searchResultsPager3=new ArrayList<>();
        searchResultsPager4=new ArrayList<>();
        searchResultsPager5=new ArrayList<>();
        path_bitmap_map =new HashMap<>();
        imageView_path_map =new HashMap<>();
        priority_pathList =new ArrayList<>();
        alreadyProcessed =new ArrayList<>();
        filteredMyContainerList=new ArrayList<>();
        filteredMyFileList=new ArrayList<>();


        whatToSearch=null;
        searchThreadId=-5;

        searchingPager1 =false;
        searchingPager2 =false;
        searchingPager3 =false;
        searchingPager4 =false;
        searchingPager5 =false;

        successfullySearchedPager1 =false;
        successfullySearchedPager2 =false;
        successfullySearchedPager3 =false;
        successfullySearchedPager4 =false;
        successfullySearchedPager5 =false;

        threadIdPager1 =-5;
        threadIdPager2 =-5;
        threadIdPager3 =-5;
        threadIdPager4 =-5;
        threadIdPager5 =-5;

        threadCount =0;
        bitmapSize =0;
        totalThreadCalled =0;

        storageId =0;
        storagePath =null;
    }


    public void softClearGallery()
    {
        // pager1_path_bitmap_map.clear();
        priority_pathList.clear();
        imageView_path_map.clear();
        totalThreadCalled =0;
        //pager1_bitmapSize=0;
        //pager1_threadCount=0;

    }

    public void hardClearGallery1()
    {
        path_bitmap_map.clear();
        alreadyProcessed.clear();
        bitmapSize =0;
        threadCount =0;
    }






}


