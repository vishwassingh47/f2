package com.example.sahil.f2.Classes;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.example.sahil.f2.Cache.ThumbNailCache;
import com.example.sahil.f2.GokuFrags.appManager;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment1;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment2;
import com.example.sahil.f2.GokuFrags.search_fragment;
import com.example.sahil.f2.GokuFrags.storageAnalyser;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.ExtensionUtil;

import java.util.Map;

import static com.example.sahil.f2.Cache.ThumbNailCache.alreadyProcessedList;
import static com.example.sahil.f2.Cache.ThumbNailCache.bitmapSize;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod0_imageView_path_map;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod0_plus;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod0_priority_pathList;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod0_threadCount;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod1_imageView_path_map;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod1_plus;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod1_priority_pathList;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod1_threadCount;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod2_imageView_path_map;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod2_plus;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod2_priority_pathList;
import static com.example.sahil.f2.Cache.ThumbNailCache.mod2_threadCount;
import static com.example.sahil.f2.Cache.ThumbNailCache.path_bitmap_map;

/**
 * Created by hit4man47 on 1/7/2018.
 */

public class ThumbNailsMod
{
    private final String TAG;
    private final int storageId;
    private final MainActivity mainActivityObject;
    private final HelpingBot helpingBot;
    private int totalImagesSetted;
    private boolean allImagesSetted;
    private int pagerId;
    private Fragment fragment;
    private final ExtensionUtil extensionUtil;

    public ThumbNailsMod(int storageId, MainActivity mainActivity)
    {
        TAG="ThumbNail:"+storageId;
        this.storageId=storageId;
        this.mainActivityObject=mainActivity;
        this.helpingBot=new HelpingBot();
        extensionUtil=new ExtensionUtil();
    }

    public ThumbNailsMod(Fragment fragment,int pagerId)     //for adapters
    {
        this.pagerId=pagerId;
        this.fragment=fragment;

        TAG="ThumbNail:";
        this.storageId=-55;
        this.mainActivityObject=null;
        this.helpingBot=new HelpingBot();
        extensionUtil=new ExtensionUtil();

    }

    public boolean mod0Thumb()
    {
        Log.e(TAG,"ThumbRunner running mod 0:"+ helpingBot.sizeinwords(bitmapSize)+" threads="+mod0_threadCount+" total bitmaps="+path_bitmap_map.size()+" Q size="+mod0_priority_pathList.size());
        totalImagesSetted=0;
        synchronized (mod0_imageView_path_map)
        {
            for(Map.Entry<ImageView,String> entry: mod0_imageView_path_map.entrySet())
            {
                if(entry.getValue()==null)
                {
                    totalImagesSetted++;
                }
                else
                {
                    if(path_bitmap_map.containsKey(entry.getValue()))
                    {
                        ImageView i=entry.getKey();
                        if(path_bitmap_map.get(entry.getValue())!=null)
                        {
                            /*
                            if bitmap is null dont replace it
                            */
                            i.setImageBitmap(path_bitmap_map.get(entry.getValue()));
                        }
                        entry.setValue(null);
                        totalImagesSetted++;
                    }
                }
            }
            Log.e("ThumbRunner",totalImagesSetted+"--"+mod0_imageView_path_map.size());
            allImagesSetted=totalImagesSetted==mod0_imageView_path_map.size();
        }

        String path;
        int iconType;
        synchronized (mod0_priority_pathList)
        {
            if (mod0_priority_pathList.size() > 0)
            {

                while (mod0_threadCount < 25 && mod0_priority_pathList.size() > 0)
                {
                    path = mod0_priority_pathList.get(mod0_priority_pathList.size() - 1);
                    mod0_priority_pathList.remove(path);

                    if(!alreadyProcessedList.contains(path))
                    {
                        alreadyProcessedList.add(path);
                        if(storageId<=3)
                        {
                            iconType=extensionUtil.getExtensionId(path.substring(path.lastIndexOf('/')+1));
                        }
                        else
                        {
                            iconType=1234;
                        }

                        mod0_plus();

                        ThumbNails thumbNails = new ThumbNails(mainActivityObject, path, iconType, storageId,0);
                        thumbNails.start();
                    }
                }

                return true;
            }
            else
            {
                if (allImagesSetted)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }

    }

    public boolean mod1Thumb()
    {
        Log.e(TAG,"ThumbRunner running mod 1:"+ helpingBot.sizeinwords(bitmapSize)+" threads="+mod1_threadCount+" total bitmaps="+path_bitmap_map.size()+" Q size="+mod1_priority_pathList.size());
        totalImagesSetted=0;
        synchronized (mod1_imageView_path_map)
        {
            for(Map.Entry<ImageView,String> entry: mod1_imageView_path_map.entrySet())
            {
                if(entry.getValue()==null)
                {
                    totalImagesSetted++;
                }
                else
                {
                    if(path_bitmap_map.containsKey(entry.getValue()))
                    {
                        ImageView i=entry.getKey();
                        if(path_bitmap_map.get(entry.getValue())!=null)
                        {
                            /*
                            if bitmap is null dont replace it
                            */
                            i.setImageBitmap(path_bitmap_map.get(entry.getValue()));
                        }
                        entry.setValue(null);
                        totalImagesSetted++;
                    }
                }
            }
            Log.e("ThumbRunner",totalImagesSetted+"--"+mod0_imageView_path_map.size());
            allImagesSetted=totalImagesSetted==mod1_imageView_path_map.size();
        }

        String path;
        int iconType;
        synchronized (mod1_priority_pathList)
        {
            if (mod1_priority_pathList.size() > 0)
            {

                while (mod1_threadCount < 25 && mod1_priority_pathList.size() > 0)
                {
                    path = mod1_priority_pathList.get(mod1_priority_pathList.size() - 1);
                    mod1_priority_pathList.remove(path);

                    if(!alreadyProcessedList.contains(path))
                    {
                        alreadyProcessedList.add(path);
                        if(storageId<=3)
                        {
                            iconType=extensionUtil.getExtensionId(path.substring(path.lastIndexOf('/')+1));
                        }
                        else
                        {
                            iconType=1234;
                        }

                        mod1_plus();

                        ThumbNails thumbNails = new ThumbNails(mainActivityObject, path, iconType, storageId,1);
                        thumbNails.start();
                    }
                }

                return true;
            }
            else
            {
                if (allImagesSetted)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
    }

    public boolean mod2Thumb()
    {
        Log.e(TAG,"ThumbRunner running mod 2:"+ helpingBot.sizeinwords(bitmapSize)+" threads="+mod2_threadCount+" total bitmaps="+path_bitmap_map.size()+" Q size="+mod2_priority_pathList.size());
        totalImagesSetted=0;
        synchronized (mod2_imageView_path_map)
        {
            for(Map.Entry<ImageView,String> entry: mod2_imageView_path_map.entrySet())
            {
                if(entry.getValue()==null)
                {
                    totalImagesSetted++;
                }
                else
                {
                    if(path_bitmap_map.containsKey(entry.getValue()))
                    {
                        ImageView i=entry.getKey();
                        if(path_bitmap_map.get(entry.getValue())!=null)
                        {
                            /*
                            if bitmap is null dont replace it
                            */
                            i.setImageBitmap(path_bitmap_map.get(entry.getValue()));
                        }
                        entry.setValue(null);
                        totalImagesSetted++;
                    }
                }
            }
            Log.e("ThumbRunner",totalImagesSetted+"--"+mod0_imageView_path_map.size());
            allImagesSetted=totalImagesSetted==mod2_imageView_path_map.size();
        }

        String path;
        int iconType;
        synchronized (mod2_priority_pathList)
        {
            if (mod2_priority_pathList.size() > 0)
            {

                while (mod2_threadCount < 25 && mod2_priority_pathList.size() > 0)
                {
                    path = mod2_priority_pathList.get(mod2_priority_pathList.size() - 1);
                    mod2_priority_pathList.remove(path);

                    if(!alreadyProcessedList.contains(path))
                    {
                        alreadyProcessedList.add(path);
                        if(storageId<=3)
                        {
                            iconType=extensionUtil.getExtensionId(path.substring(path.lastIndexOf('/')+1));
                        }
                        else
                        {
                            iconType=1234;
                        }

                        mod2_plus();

                        ThumbNails thumbNails = new ThumbNails(mainActivityObject, path, iconType, storageId,2);
                        thumbNails.start();
                    }
                }

                return true;
            }
            else
            {
                if (allImagesSetted)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
    }


    public void mod0(ImageView imageView1,String path,boolean isFolder,int iconCode,String name)
    {
        if(isFolder)
        {
            synchronized(ThumbNailCache.mod0_imageView_path_map)
            {
                imageView1.setImageResource(R.mipmap.folder5);
                ThumbNailCache.mod0_imageView_path_map.put(imageView1,null);
            }
        }
        else
        {
            if(ThumbNailCache.path_bitmap_map.containsKey(path))
            {
                if(ThumbNailCache.path_bitmap_map.get(path)!=null)
                {
                    imageView1.setImageBitmap(ThumbNailCache.path_bitmap_map.get(path));
                }
                else
                {
                    imageView1.setImageResource(extensionUtil.getKnownIcons(name));
                }

                ThumbNailCache.mod0_imageView_path_map.put(imageView1,null);
            }
            else
            {
                imageView1.setImageResource(extensionUtil.getKnownIcons(name));

                synchronized (ThumbNailCache.mod0_priority_pathList)
                {
                    if(iconCode==1 || iconCode==2 ||iconCode==3 || iconCode==4)
                    {
                        if(ThumbNailCache.mod0_priority_pathList.contains(path))
                        {
                            ThumbNailCache.mod0_priority_pathList.remove(path);
                        }
                        ThumbNailCache.mod0_priority_pathList.add(path);
                        synchronized(ThumbNailCache.mod0_imageView_path_map)
                        {
                            ThumbNailCache.mod0_imageView_path_map.put(imageView1,path);
                        }
                        runThumbNailRunner();
                    }
                    else
                    {
                        synchronized(ThumbNailCache.mod0_imageView_path_map)
                        {
                            ThumbNailCache.mod0_imageView_path_map.put(imageView1,null);
                        }
                    }
                }
            }
        }
    }

    public void mod1(ImageView imageView1,String path,boolean isFolder,int iconCode,String name)
    {
        if(isFolder)
        {
            synchronized(ThumbNailCache.mod1_imageView_path_map)
            {
                imageView1.setImageResource(R.mipmap.folder5);
                ThumbNailCache.mod1_imageView_path_map.put(imageView1,null);
            }
        }
        else
        {
            if(ThumbNailCache.path_bitmap_map.containsKey(path))
            {
                if(ThumbNailCache.path_bitmap_map.get(path)!=null)
                {
                    imageView1.setImageBitmap(ThumbNailCache.path_bitmap_map.get(path));
                }
                else
                {
                    imageView1.setImageResource(extensionUtil.getKnownIcons(name));
                }

                ThumbNailCache.mod1_imageView_path_map.put(imageView1,null);
            }
            else
            {
                imageView1.setImageResource(extensionUtil.getKnownIcons(name));

                synchronized (ThumbNailCache.mod1_priority_pathList)
                {
                    if(iconCode==1 || iconCode==2 ||iconCode==3 || iconCode==4)
                    {
                        if(ThumbNailCache.mod1_priority_pathList.contains(path))
                        {
                            ThumbNailCache.mod1_priority_pathList.remove(path);
                        }
                        ThumbNailCache.mod1_priority_pathList.add(path);
                        synchronized(ThumbNailCache.mod1_imageView_path_map)
                        {
                            ThumbNailCache.mod1_imageView_path_map.put(imageView1,path);
                        }

                        runThumbNailRunner();
                    }
                    else
                    {
                        synchronized(ThumbNailCache.mod1_imageView_path_map)
                        {
                            ThumbNailCache.mod1_imageView_path_map.put(imageView1,null);
                        }
                    }
                }
            }
        }
    }

    public void mod2(ImageView imageView1,String path,boolean isFolder,int iconCode,String name)
    {
        if(isFolder)
        {
            synchronized(ThumbNailCache.mod2_imageView_path_map)
            {
                imageView1.setImageResource(R.mipmap.folder5);
                ThumbNailCache.mod2_imageView_path_map.put(imageView1,null);
            }
        }
        else
        {
            if(ThumbNailCache.path_bitmap_map.containsKey(path))
            {
                if(ThumbNailCache.path_bitmap_map.get(path)!=null)
                {
                    imageView1.setImageBitmap(ThumbNailCache.path_bitmap_map.get(path));
                }
                else
                {
                    imageView1.setImageResource(extensionUtil.getKnownIcons(name));
                }

                ThumbNailCache.mod2_imageView_path_map.put(imageView1,null);
            }
            else
            {
                imageView1.setImageResource(extensionUtil.getKnownIcons(name));

                synchronized (ThumbNailCache.mod2_priority_pathList)
                {
                    if(iconCode==1 || iconCode==2 ||iconCode==3 || iconCode==4)
                    {
                        if(ThumbNailCache.mod2_priority_pathList.contains(path))
                        {
                            ThumbNailCache.mod2_priority_pathList.remove(path);
                        }
                        ThumbNailCache.mod2_priority_pathList.add(path);
                        synchronized(ThumbNailCache.mod2_imageView_path_map)
                        {
                            ThumbNailCache.mod2_imageView_path_map.put(imageView1,path);
                        }

                        runThumbNailRunner();
                    }
                    else
                    {
                        synchronized(ThumbNailCache.mod2_imageView_path_map)
                        {
                            ThumbNailCache.mod2_imageView_path_map.put(imageView1,null);
                        }
                    }
                }
            }
        }
    }


    private void runThumbNailRunner()
    {
        switch (pagerId)
        {
            case 1:
                storagePager pager1 =(storagePager) fragment;
                pager1.runThumbNailRunner();
                break;
            case 2:
                search_fragment pager2 =(search_fragment) fragment;
                pager2.runThumbNailRunner();
                break;
            case 3:
                image_gallery_fragment1 pager3=(image_gallery_fragment1) fragment;
                pager3.runThumbNailRunner();
                break;
            case 4:
                image_gallery_fragment2 pager4=(image_gallery_fragment2) fragment;
                pager4.runThumbNailRunner();
                break;
            case 5:
                storageAnalyser pager5 =(storageAnalyser) fragment;
                pager5.runThumbNailRunner();
                break;
            case 6:
                appManager pager6=(appManager) fragment;
                pager6.runThumbNailRunner();
                break;
        }
    }


}
