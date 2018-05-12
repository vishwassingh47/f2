package com.example.sahil.f2.UiClasses;

import android.util.Log;

import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment1;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment2;
import com.example.sahil.f2.GokuFrags.search_fragment;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.PagerXUtilities;

import static com.example.sahil.f2.MainActivity.pageList;

/**
 * Created by hit4man47 on 1/1/2018.
 */

public class Refresher
{
    private MainActivity mainActivity;
    private int lastIndexOfPages;
    private final String TAG="REFRESHER";
    private final int currentIndex;

    public Refresher(MainActivity mainActivity)
    {
       this.mainActivity=mainActivity;
       lastIndexOfPages=pageList.size()-1;
       currentIndex=mainActivity.viewPager.getCurrentItem();
    }

    private void refreshFragment(int pageIndex) throws Exception
    {
        if(pageIndex>=1 && pageIndex<=lastIndexOfPages)
        {
            Page page=pageList.get(pageIndex);
            int pageId=page.getPageId();
            int currentFrameId= PagerXUtilities.getFrameIdFromPageIndex(pageIndex);
            String currentPath=page.getCurrentPath();
            switch (pageId)
            {
                case 12345:
                    if(currentPath.equals("SearchResult"))
                    {
                        search_fragment fragment=(search_fragment)mainActivity.getSupportFragmentManager().findFragmentById(currentFrameId);
                        fragment.reloadPager();
                    }
                    else
                    {
                        storagePager fragment=(storagePager)mainActivity.getSupportFragmentManager().findFragmentById(currentFrameId);
                        fragment.reloadPager();
                    }
                    break;
                case -5:
                    if(currentPath.equals("Gallery1"))
                    {
                        image_gallery_fragment1 fragment=(image_gallery_fragment1)mainActivity.getSupportFragmentManager().findFragmentById(currentFrameId);
                        fragment.reloadPager();
                    }
                    else
                    {
                        image_gallery_fragment2 fragment=(image_gallery_fragment2)mainActivity.getSupportFragmentManager().findFragmentById(currentFrameId);
                        fragment.reloadPager();
                    }
                    break;
            }
        }
    }

    public void refresh()
    {
        try
        {
            refreshFragment(currentIndex);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception Caught");
        }


        try
        {
            refreshFragment(currentIndex-1);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception Caught");
        }


        try
        {
            refreshFragment(currentIndex+1);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception Caught");
        }


    }


}
