package com.example.sahil.f2.OperationTheater;

import android.util.Log;
import android.view.View;

import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.recycleBinCache;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;


import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;
import static com.example.sahil.f2.MainActivity.pageList;


/**
 * Created by hit4man47 on 10/27/2017.
 */

public class PagerXUtilities
{

    /**
     *
     * @param name name of page
     * @return -1 if doesnot exits
     *          else return its index in pageList
     */
    public static int getPageIndexFromName(String name)
    {
        for(int i=0;i<pageList.size();i++)
        {
            if(pageList.get(i).getName().equals(name))
                return i;
        }
        return -1;
    }


    public static int getPageIndexFromFrameId(int frameId)
    {
        try
        {
            Log.e("dangerzzz","222");
            if(frameId== R.id.root_frameLayout0)
            {
                return 0;
            }
        }
        catch (Exception e)
        {}
        try
        {
            if(frameId==R.id.root_frameLayout1)
            {
                return 1;
            }
        }
        catch (Exception e)
        {}
        try
        {
            if(frameId==R.id.root_frameLayout2)
            {
                return 2;
            }
        }
        catch (Exception e)
        {}
        try
        {
            if(frameId==R.id.root_frameLayout3)
            {
                return 3;
            }
        }
        catch (Exception e)
        {}
        try
        {
            if(frameId==R.id.root_frameLayout4)
            {
                return 4;
            }
        }
        catch (Exception e)
        {}
        try
        {
            if(frameId==R.id.root_frameLayout5)
            {
                return 5;
            }
        }
        catch (Exception e)
        {}

        return -1;
    }


    /**
     *
     * @param currentViewPagerIndex index of view pager
     * @return  frame id
     *          0 pageIndex is invaild ::hope this never happens
     */
    public static int getFrameIdFromPageIndex(int currentViewPagerIndex)
    {
        switch (currentViewPagerIndex)
        {
            case 0:
                Log.e("dangerzzz","11");
                return R.id.root_frameLayout0;
            case 1:
                return R.id.root_frameLayout1;
            case 2:
                return R.id.root_frameLayout2;
            case 3:
                return R.id.root_frameLayout3;
            case 4:
                return R.id.root_frameLayout4;
            case 5:
                return R.id.root_frameLayout5;
            default:
                return 0;
        }
    }


    /**
     *
     * @param pageName name of page
     * @return storage id
     *          0 if page is not a storage
     */
    public static int getStorageIdFromPageName(String pageName)
    {

        switch (pageName)
        {
            case "Local1":
                return 1;
            case "Local2":
                return 2;
            case "Local3":
                return 3;
            case "GoogleDrive":
                return 4;
            case "DropBox":
                return 5;
            case "RecycleBin":
                return recycleBinCache.storageId;
            case "Favourites":
                return favouritesCache.storageId;
            case "StorageAnalyser":
                return MyCacheData.GlobalStorageAnalyser.storageId;
            case "FtpClient":
                return 6;
            case "AppsManager":
                return -3;//not imp
            default:
                return 66/0;
        }
    }




    public static boolean isValidStoragePage(Page page)
    {
        String currentPath=page.getCurrentPath();

        if(page.getPageId()!=12345 && page.getPageId()!=11 && page.getPageId()!=15)
            return false;
        if(currentPath.equals("SearchResult"))
            return false;
        if(currentPath.equals("RecycleBin"))
            return false;
        if(currentPath.equals("Favourites"))
            return false;
        if(currentPath.equals("FtpClient"))
            return false;

        return true;
    }

    /**
     * FOR LOCAL PATHS ONLY
     * NOT FOR DROPBOX AND GOOGLE DRIVE
     *
     * @param currentPath currentPathOfPager
     * @return PHYSICAL STORGAE PATH of current pager AND NULL(if root path)
     *
     */
    public static String getLocalHomeStoragePath(String currentPath)
    {
        if(currentPath.equals("SearchResult") || currentPath.equals("RecycleBin") || currentPath.equals("Favourites") || currentPath.equals("FtpClient"))
        {
            int zero=0;
            float x=5/zero;
            Log.e("zero:","--"+x);
        }
        for(String x:Physical_Storage_PATHS)
        {
            if(currentPath.startsWith(x))
            {
                return x;
            }
        }
        return null;//root path
    }

    public static boolean isRootPath(String path)
    {
        if(path.equals("SearchResult") || path.equals("RecycleBin") || path.equals("Favourites"))
        {
            int zero=0;
            float x=5/zero;
            Log.e("zero:","--"+x);
        }



        boolean find=false;
        for(String x:Physical_Storage_PATHS)
        {
            if(path.startsWith(x))
            {
                find=true;
            }
        }
        if(find)
            return false;
        else
            return true;
    }

    public static boolean isExternalSdCardPath(String path)
    {
        for(int i=1;i<Physical_Storage_PATHS.size();i++)
        {
            if(path.startsWith(Physical_Storage_PATHS.get(i)))
            {
                return true;
            }
        }
        return false;
    }

    public static String getExternalSdCardPath(String path)
    {
        for(int i=1;i<Physical_Storage_PATHS.size();i++)
        {
            if(path.startsWith(Physical_Storage_PATHS.get(i)))
            {
                return Physical_Storage_PATHS.get(i);
            }
        }
        return null;
    }

    /**
     *
     * @param pageName
     * @return 0 if pageName is not of gallery
     */
    public static int getGalleryType(String pageName)
    {
        int galleryType=0;
        switch (pageName)
        {
            case "ImageGallery":
                galleryType=1;
                break;
            case "VideoGallery":
                galleryType=2;
                break;
            case "AudioGallery":
                galleryType=3;
                break;
            case "ApkGallery":
                galleryType=4;
                break;
        }

        return galleryType;
    }


}
