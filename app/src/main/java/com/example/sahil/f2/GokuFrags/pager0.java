package com.example.sahil.f2.GokuFrags;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.sahil.f2.MainActivity.SDCardUriMap;

import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.appManagerCache;
import com.example.sahil.f2.Cache.superCache;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.FunkyAdapters.cloudStorageArrayAdapter;
import com.example.sahil.f2.MainActivity;

import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuOperations;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.storageArrayAdapter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getFrameIdFromPageIndex;


/**
 * Created by Acer on 06-08-2017.
 */


public class pager0 extends Fragment
{
    View view;
    public MainActivity mainActivityObject;
    public  LinearLayout images_gallery_layout,video_gallery_layout,audio_gallery_layout,apk_gallery_layout;
    public HelpingBot helpingBot;
    private LinearLayout localStorage_Layout,cloudStorage_Layout;
    private View DropBoxView,GoogleDriveView;
    private DriveManager driveManager;
    private DropBoxManager dropBoxManager;
    private CommonsUtils commonsUtils;
    private boolean shouldStartOld=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contaimer, Bundle saved)
    {
        Log.e("pager0","createdView()");
        helpingBot=new HelpingBot();
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_pager0,contaimer,false);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        Log.e("pager0","onstart()");

        if(shouldStartOld)
        {
            shouldStartOld=false;

        }


       view=getView();
        commonsUtils=new CommonsUtils();
        addLocalStorage();
        addCloudStorage();

        setUpImageGallery();
        setUpVideoGallery();
        setUpAudioGallery();
        setUpApkGallery();


        /*
        retry2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DriveManager driveManager=new DriveManager();
                driveManager.getUserInfo();
            }
        });
        */

        superCache.pager0OpeningFirstTime=false;

    }

    @Override
    public void onDestroy()
    {

        super.onDestroy();
        Log.e("destroying....","--");
    }

    public void addLocalStorage()
    {
        Log.e("HAHA","--");
        localStorage_Layout=(LinearLayout) view.findViewById(R.id.pager0LocalStorageLayout);
        localStorage_Layout.removeAllViews();
        //locals
        for(int i=0;i<Physical_Storage_PATHS.size();i++)
        {
            View layout=LayoutInflater.from(mainActivityObject).inflate(R.layout.layoutof_row15,localStorage_Layout,false);
            ImageView icon=(ImageView)layout.findViewById(R.id.row15_icon);
            TextView name=(TextView)layout.findViewById(R.id.row15_name);
            final ProgressBar pb=(ProgressBar)layout.findViewById(R.id.row15_progress);
            TextView free=(TextView) layout.findViewById(R.id.row15_free);
            TextView total=(TextView)layout.findViewById(R.id.row15_total);
            
            name.setText(Physical_Storage_PATHS.get(i));
            icon.setImageResource(R.drawable.sd_card);
            File file=new File(Physical_Storage_PATHS.get(i));
            long freeSpace=file.getUsableSpace();
            long totalSpace=file.getTotalSpace();
            final long percentUsed=totalSpace==0?0:(totalSpace-freeSpace)*100/totalSpace;

            int color=Color.parseColor("#ffffff");

            Resources resources=getResourceX();
            if(resources!=null)
            {
                if(percentUsed<85)
                {
                    color=resources.getColor(R.color.progress_blue);
                }
                else
                {
                    color=resources.getColor(R.color.main_theme_red);
                }
            }



            //pb.setSecondaryProgress(0);
            pb.setIndeterminate(superCache.pager0OpeningFirstTime);

            Drawable drawable=pb.getProgressDrawable().mutate();
            Drawable drawable2=pb.getIndeterminateDrawable().mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawable2.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            pb.setProgressDrawable(drawable);
            pb.setIndeterminateDrawable(drawable2);


            if(superCache.pager0OpeningFirstTime)
            {
                Handler handler=new Handler();
                Runnable runnable=new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pb.setIndeterminate(false);
                        pb.setProgress((int) percentUsed);
                    }
                };
                handler.postDelayed(runnable,1000);
            }
            else
            {
                pb.setIndeterminate(false);
                pb.setProgress((int) percentUsed);
            }


            Log.e("color is :",color+"---");

            free.setText(helpingBot.sizeinwords(freeSpace));
            free.setTextColor(color);
            total.setText(helpingBot.sizeinwords(totalSpace));
            total.setTextColor(color);

            localStorage_Layout.addView(layout);
            

                final int storageIndex=i;
                layout.setOnClickListener(new View.OnClickListener()
                                                   {
                                                       @Override
                                                       public void onClick(View v)
                                                       {
                                                           String pageName;
                                                           String firstPath;
                                                           switch (storageIndex)
                                                           {
                                                               case 0:
                                                                   pageName="Local1";
                                                                   firstPath=MainActivity.Physical_Storage_PATHS.get(0);
                                                                   break;
                                                               case 1:
                                                                   pageName="Local2";
                                                                   firstPath=MainActivity.Physical_Storage_PATHS.get(1);
                                                                   break;
                                                               case 2:
                                                                   pageName="Local3";
                                                                   firstPath=MainActivity.Physical_Storage_PATHS.get(2);
                                                                   break;
                                                               default:
                                                                   return;
                                                           }
                                                           int index=HelpingBot.getIndexOfPage(pageName,12345);
                                                           if(index==-1)
                                                           {
                                                               HelpingBot.addPageAndGoto(mainActivityObject,pageName,0,firstPath,R.drawable.sd_card,12345);
                                                           }
                                                           else
                                                           {
                                                               HelpingBot.gotoPage(mainActivityObject,index);
                                                           }
                                                       }
                                                   }
                );

        }
        
    }
    
    private void addCloudStorage()
    {
        cloudStorage_Layout=(LinearLayout) view.findViewById(R.id.pager0CloudStorageLayout);
        cloudStorage_Layout.removeAllViews();
        
        //GOOGLE DRIVE
        if(GoogleDriveConnection.isDriveAvailable)
        {
            GoogleDriveView=LayoutInflater.from(mainActivityObject).inflate(R.layout.layoutof_row22,cloudStorage_Layout,false);
            driveManager=new DriveManager();


            final ImageView options=(ImageView)GoogleDriveView.findViewById(R.id.row22_menu);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    commonsUtils.showOptions(mainActivityObject,options,0,2,getFragmentManager().findFragmentById(R.id.root_frameLayout0));
                }
            });


            if(!GoogleDriveConnection.isDriveConnected || superCache.pager0OpeningFirstTime)
            {
                driveManager.getUserInfo();
            }
            else
            {
                //user info is already there--just set it
                driveManager.setUserInfo();
            }
        }
        else
        {
            GoogleDriveView=LayoutInflater.from(mainActivityObject).inflate(R.layout.layoutof_row21,cloudStorage_Layout,false);
            ((TextView)GoogleDriveView.findViewById(R.id.row21_title)).setText("Google Drive");
            ((TextView)GoogleDriveView.findViewById(R.id.row21_info)).setText("Manage your Google Drive Files");
            ((ImageView)GoogleDriveView.findViewById(R.id.row21_logo)).setImageResource(R.mipmap.google_drive);

            googleDriveViewClick();
        }
        cloudStorage_Layout.addView(GoogleDriveView);





        //DropBox
        if(DropBoxConnection.isDropboxAvailable)
        {
            DropBoxView=LayoutInflater.from(mainActivityObject).inflate(R.layout.layoutof_row22,cloudStorage_Layout,false);
            dropBoxManager=new DropBoxManager();

            final ImageView options=(ImageView)DropBoxView.findViewById(R.id.row22_menu);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    commonsUtils.showOptions(mainActivityObject,options,0,1,getFragmentManager().findFragmentById(R.id.root_frameLayout0));
                }
            });


            if(!DropBoxConnection.isDropboxConnected || superCache.pager0OpeningFirstTime)
            {
                dropBoxManager.getUserInfo();
            }
            else
            {
                //user info is already there--just set it
                dropBoxManager.setUserInfo();
            }
        }
        else
        {
            DropBoxView=LayoutInflater.from(mainActivityObject).inflate(R.layout.layoutof_row21,cloudStorage_Layout,false);
            ((TextView)DropBoxView.findViewById(R.id.row21_title)).setText("DropBox");
            ((TextView)DropBoxView.findViewById(R.id.row21_info)).setText("Manage your DropBox Files");
            ((ImageView)DropBoxView.findViewById(R.id.row21_logo)).setImageResource(R.mipmap.dropbox);

            dropBoxViewClick();
        }
        cloudStorage_Layout.addView(DropBoxView);
        
    }


    public class DropBoxManager
    {
        Runnable runnable=null;


        int timer=0;


        void getUserInfo()
        {
            ((TextView)DropBoxView.findViewById(R.id.row22_userName)).setText("connecting...");
            ((TextView)DropBoxView.findViewById(R.id.row22_total)).setText("--?--");
            ((TextView)DropBoxView.findViewById(R.id.row22_free)).setText("--?--");
            ((ImageView)DropBoxView.findViewById(R.id.row22_logo)).setImageResource(R.mipmap.dropbox);
            ((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).setIndeterminate(true);
            //((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).setProgress(0);


            mainActivityObject.connectToDropBox();

            final Handler handler=new Handler();
            runnable=new Runnable()
            {
                @Override
                public void run()
                {
                    timer++;

                    if(DropBoxConnection.isErrorConnecting||DropBoxConnection.isDropboxConnected||timer>10)
                    {
                        if(DropBoxConnection.isDropboxConnected)
                        {
                            setUserInfo();
                        }
                        else
                        {
                            ((TextView)DropBoxView.findViewById(R.id.row22_userName)).setText("failed to connect...");

                            ((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).setIndeterminate(false);
                            ((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).setProgress(0);

                            dropBoxViewClick();
                        }

                        handler.removeCallbacks(runnable);
                    }
                    else
                    {
                        handler.postDelayed(runnable,1000);
                    }

                }
            };
            handler.postDelayed(runnable,500);

        }

        void setUserInfo()
        {
            ((TextView)DropBoxView.findViewById(R.id.row22_userName)).setText(DropBoxConnection.userName+"");
            ((TextView)DropBoxView.findViewById(R.id.row22_total)).setText(helpingBot.sizeinwords(DropBoxConnection.totalSize));
            ((TextView)DropBoxView.findViewById(R.id.row22_free)).setText(helpingBot.sizeinwords(DropBoxConnection.totalSize-DropBoxConnection.usedSize));
            ((ImageView)DropBoxView.findViewById(R.id.row22_logo)).setImageResource(R.mipmap.dropbox);
            ((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).setIndeterminate(false);
            ((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).setProgress(DropBoxConnection.progress);


            int color=Color.parseColor("#ffffff");

            Resources resources=getResourceX();
            if(resources!=null)
            {
                if(DropBoxConnection.progress<85)
                {
                    color=resources.getColor(R.color.progress_blue);
                }
                else
                {
                    color=resources.getColor(R.color.main_theme_red);
                }
            }


            Drawable drawable=((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).getProgressDrawable().mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            ((ProgressBar)DropBoxView.findViewById(R.id.row22_progress)).setProgressDrawable(drawable);


            dropBoxViewClick();

        }


    }

    public class DriveManager
    {
        Runnable runnable=null;

        int timer=0;

        void getUserInfo()
        {
            ((TextView)GoogleDriveView.findViewById(R.id.row22_userName)).setText("connecting...");
            ((TextView)GoogleDriveView.findViewById(R.id.row22_total)).setText("--?--");
            ((TextView)GoogleDriveView.findViewById(R.id.row22_free)).setText("--?--");
            ((ImageView)GoogleDriveView.findViewById(R.id.row22_logo)).setImageResource(R.mipmap.google_drive);
            ((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).setIndeterminate(true);
            //((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).setProgress(0);


            Log.e("GoogleApi","from pager 0");
            mainActivityObject.connectToDrive();

            final Handler handler=new Handler();
            runnable=new Runnable()
            {
                @Override
                public void run()
                {
                    timer++;

                    if(GoogleDriveConnection.isErrorConnecting||GoogleDriveConnection.isDriveConnected||timer>10)
                    {
                        if(GoogleDriveConnection.isDriveConnected)
                        {
                            setUserInfo();
                        }
                        else
                        {
                            ((TextView)GoogleDriveView.findViewById(R.id.row22_userName)).setText("failed to connect...");

                            ((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).setIndeterminate(false);
                            ((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).setProgress(0);

                            googleDriveViewClick();
                        }

                        handler.removeCallbacks(runnable);
                    }
                    else
                    {
                        handler.postDelayed(runnable,1000);
                    }

                }
            };
            handler.postDelayed(runnable,500);
        }

        void setUserInfo()
        {
            ((TextView)GoogleDriveView.findViewById(R.id.row22_userName)).setText(GoogleDriveConnection.userName+"");
            ((TextView)GoogleDriveView.findViewById(R.id.row22_total)).setText(helpingBot.sizeinwords(GoogleDriveConnection.totalSize));
            ((TextView)GoogleDriveView.findViewById(R.id.row22_free)).setText(helpingBot.sizeinwords(GoogleDriveConnection.totalSize-GoogleDriveConnection.usedSize));
            ((ImageView)GoogleDriveView.findViewById(R.id.row22_logo)).setImageResource(R.mipmap.google_drive);
            ((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).setIndeterminate(false);
            ((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).setProgress(GoogleDriveConnection.progress);

            int color=Color.parseColor("#ffffff");

            Resources resources=getResourceX();
            if(resources!=null)
            {
                if(GoogleDriveConnection.progress<85)
                {
                    color=resources.getColor(R.color.progress_blue);
                }
                else
                {
                    color=resources.getColor(R.color.main_theme_red);
                }
            }


            Drawable drawable=((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).getProgressDrawable().mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            ((ProgressBar)GoogleDriveView.findViewById(R.id.row22_progress)).setProgressDrawable(drawable);

            googleDriveViewClick();
        }

    }


    private void googleDriveViewClick()
    {
        GoogleDriveView.setOnClickListener(new View.OnClickListener()
                                           {
                                               @Override
                                               public void onClick(View v)
                                               {
                                                   if(!GoogleDriveConnection.isDriveAvailable)
                                                   {
                                                       mainActivityObject.loginInToDrive();
                                                       return;
                                                   }


                                                   if(GoogleDriveConnection.isDriveConnected)
                                                   {
                                                       int index=HelpingBot.getIndexOfPage("GoogleDrive",12345);
                                                       if(index==-1)
                                                       {
                                                           HelpingBot.addPageAndGoto(mainActivityObject,"GoogleDrive",0,"root",R.mipmap.google_drive,12345);
                                                       }
                                                       else
                                                       {
                                                           HelpingBot.gotoPage(mainActivityObject,index);
                                                       }
                                                   }
                                                   else
                                                   {
                                                       driveManager.getUserInfo();
                                                   }
                                               }
                                           }
        );
    }

    private void dropBoxViewClick()
    {
        DropBoxView.setOnClickListener(new View.OnClickListener()
                                           {
                                               @Override
                                               public void onClick(View v)
                                               {
                                                   if(!DropBoxConnection.isDropboxAvailable)
                                                   {
                                                       mainActivityObject.loginInToDropBox();
                                                       return;
                                                   }


                                                   if(DropBoxConnection.isDropboxConnected)
                                                   {
                                                       int index=HelpingBot.getIndexOfPage("DropBox",12345);
                                                       if(index==-1)
                                                       {
                                                           HelpingBot.addPageAndGoto(mainActivityObject,"DropBox",0,"",R.mipmap.dropbox,12345);
                                                       }
                                                       else
                                                       {
                                                           HelpingBot.gotoPage(mainActivityObject,index);
                                                       }
                                                   }
                                                   else
                                                   {
                                                       dropBoxManager.getUserInfo();
                                                   }
                                               }
                                           }
        );
    }


    public void spinnerListener2()
    {
        /*
        drive_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==1)
                {
                    mainActivityObject.logoutFromDrive();
                    mainActivityObject.setUpViewPager();
                }
                if(position==2)
                {
                    DriveManager driveManager=new DriveManager();
                    driveManager.getUserInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        */
    }


    public void oldStarted()
    {
        shouldStartOld=true;
    }


    private void setUpImageGallery()
    {
        images_gallery_layout=(LinearLayout)view.findViewById(R.id.pager0_images);

        images_gallery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isReadyToShowGallery())
                {
                    MyCacheData.galleryData1=null;//ULTRA HARD CLEAR WHEN FIRST STARTS
                    mainActivityObject.addPage("ImageGallery",1,"Gallery1",R.mipmap.zeroimages,-5);
                    mainActivityObject.setUpViewPager();
                    mainActivityObject.viewPager.setCurrentItem(1,true);
                }

            }
        });
    }

    private void setUpVideoGallery()
    {
        video_gallery_layout=(LinearLayout)view.findViewById(R.id.pager0_movies);

        video_gallery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isReadyToShowGallery())
                {
                    MyCacheData.galleryData2=null;//ULTRA HARD CLEAR WHEN FIRST STARTS
                    mainActivityObject.addPage("VideoGallery",1,"Gallery1",R.mipmap.zeromovies,-5);
                    mainActivityObject.setUpViewPager();
                    mainActivityObject.viewPager.setCurrentItem(1,true);
                }
            }
        });
    }


    private void setUpAudioGallery()
    {
        audio_gallery_layout=(LinearLayout)view.findViewById(R.id.pager0_music);

        audio_gallery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isReadyToShowGallery())
                {
                    MyCacheData.galleryData3=null;//ULTRA HARD CLEAR WHEN FIRST STARTS
                    mainActivityObject.addPage("AudioGallery",1,"Gallery1",R.mipmap.zeromusic,-5);
                    mainActivityObject.setUpViewPager();
                    mainActivityObject.viewPager.setCurrentItem(1,true);
                }
            }
        });
    }



    private void setUpApkGallery()
    {
        apk_gallery_layout=(LinearLayout)view.findViewById(R.id.pager0_apk);

        apk_gallery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isReadyToShowGallery())
                {
                    /*
                    MyCacheData.galleryData4=null;//ULTRA HARD CLEAR WHEN FIRST STARTS
                    mainActivityObject.addPage("ApkGallery",1,"Gallery1",R.mipmap.apk,-5);
                    mainActivityObject.setUpViewPager();
                    mainActivityObject.viewPager.setCurrentItem(1,true);
                     */


                    appManagerCache.clear();
                    mainActivityObject.addPage("AppsManager",1,"AppsManager",R.mipmap.zeroapps,696969);
                    mainActivityObject.setUpViewPager();
                    mainActivityObject.viewPager.setCurrentItem(1,true);

                    /*\
                    Thread thread1=new Thread()
                    {
                        @Override
                        public void run()
                        {
                            String command1="find / -name \"*.zip\" -exec ls -la {} \\;";
                            SuOperations.runSuCommand(command1,1);
                        }
                    };
                    thread1.start();
                    Thread thread2=new Thread()
                    {
                        @Override
                        public void run()
                        {
                            String command2="find / -name \"*.apk\" -exec ls -la {} \\;";
                            SuOperations.runSuCommand(command2,2);
                        }
                    };
                    thread2.start();
                     */

                }
            }
        });
    }

    private boolean isReadyToShowGallery()
    {
        if(Physical_Storage_PATHS.size()==0)
        {
            if(!DropBoxConnection.isDropboxAvailable && !GoogleDriveConnection.isDriveAvailable)
            {
                Toast.makeText(mainActivityObject, "No Local/Cloud Storage Found", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else
        {
            File rootFolder=new File(Physical_Storage_PATHS.get(0));
            if(!rootFolder.canWrite())
            {
                if(Physical_Storage_PATHS.get(0).equals(Environment.getExternalStorageDirectory().getAbsolutePath()))
                {
                    //checking the primary storage state for all android versions
                    if (! Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
                    {
                        Toast.makeText(mainActivityObject, "Storage is not mounted,access denied", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    String state=Environment.getExternalStorageState(rootFolder);
                    if(! state.equals(Environment.MEDIA_MOUNTED))
                    {
                        Toast.makeText(mainActivityObject, "Storage is not mounted, access denied", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    String [] breaker=Physical_Storage_PATHS.get(0).split("\\/");
                    String storageName=breaker[breaker.length-1];
                    if(SDCardUriMap.get(storageName)==null)
                    {
                        StorageAccessFramework storageAccessFramework=new StorageAccessFramework(mainActivityObject);
                        storageAccessFramework.showSaf(6666,storageName);
                        return false;
                    }
                }
            }
        }
        return  true;
    }


    public void openNewPager()
    {
        Log.e("open new pager","$$$$$$$$$$$$$$$$$$$$$$$$$$$4444");
        superCache.pager0OpeningFirstTime=true;
        int currentFrameId=getFrameIdFromPageIndex(0);
        pager0 newFragment=new pager0();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(currentFrameId,newFragment);
        ft.commit();
    }

    public void refreshCloud(int dropboxOrDrive)
    {
        if(dropboxOrDrive==1)
        {
            dropBoxManager.getUserInfo();
        }
        else
        {
            driveManager.getUserInfo();
        }
    }

    public Resources getResourceX()
    {
        try
        {
            return getResources();
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
