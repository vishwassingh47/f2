package com.example.sahil.f2.Classes;

import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.GokuFrags.appManager;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment1;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment2;
import com.example.sahil.f2.GokuFrags.pager0;
import com.example.sahil.f2.GokuFrags.search_fragment;
import com.example.sahil.f2.GokuFrags.storageAnalyser;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

/**
 * Created by hit4man47 on 1/23/2018.
 */

public class CommonsUtils
{
    public void showArtLayout(int resourceId, String message, boolean toRetry, ImageView artIcon, TextView artText, RelativeLayout artLayout, Button artButton, final MainActivity mainActivityObject, final int storageId, final int fragmentType, final Fragment fragment)  //OK
    {
        /*
        fragmentType:
        1:storagePager
        2:storageAnalyser
        3:searchFragment
        4:image gallery 1
        5:image gallery 2
        6:app manager
         */
        try
        {
            artIcon.setImageResource(resourceId);
            artText.setText(message);
            artLayout.setVisibility(View.VISIBLE);

            if(toRetry)
            {
                if(storageId==4)
                {
                    GoogleDriveConnection.isDriveConnected=false;
                }
                if(storageId==5)
                {
                    DropBoxConnection.isDropboxConnected=false;
                }
                artButton.setVisibility(View.VISIBLE);
                artButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(storageId==6 && fragmentType==1)
                        {
                            FtpCache.mFTPClient=null;
                            storagePager pager =(storagePager) fragment;
                            pager.gotoFtpLoginPage();
                        }
                        else
                        {
                            if(storageId==4)
                            {
                                mainActivityObject.connectDriveAgain();
                            }
                            if(storageId==5)
                            {
                                mainActivityObject.connectDropBoxAgain();
                            }

                            switch (fragmentType)
                            {
                                case 1:
                                    storagePager pager1 =(storagePager) fragment;
                                    pager1.reloadPager();
                                    break;
                                case 2:
                                    storageAnalyser pager2 =(storageAnalyser) fragment;
                                    pager2.reloadPager();
                                    break;
                                case 3:
                                    search_fragment pager3 =(search_fragment) fragment;
                                    pager3.reloadPager();
                                    break;
                                case 4:
                                    image_gallery_fragment1 pager4=(image_gallery_fragment1) fragment;
                                    pager4.openNewStorage(true);
                                    break;
                                case 5:
                                    image_gallery_fragment2 pager5=(image_gallery_fragment2) fragment;
                                    pager5.reloadPager();
                                    break;
                                case 6:
                                    appManager pager6=(appManager) fragment;
                                    pager6.reloadPager();
                            }
                        }
                    }
                });
            }
            else
            {
                artButton.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            Log.e("CommonsUtils","showArtLayout()");
        }
    }



    public void showOptions(final MainActivity mainActivityObject, ImageView options, int operationCode, final int dropBoxOrDrive, final Fragment fragment)
    {
        android.support.v7.widget.PopupMenu popup=new PopupMenu(mainActivityObject,options);
        switch (operationCode)
        {
            case 0:
                popup.getMenuInflater().inflate(R.menu.cloud_options,popup.getMenu());
                break;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem item)
            {
                if(item.getItemId()==R.id.cloud_options_Refresh)
                {
                    pager0 pager =(pager0) fragment;
                    pager.refreshCloud(dropBoxOrDrive);
                }
                if(item.getItemId()==R.id.cloud_options_Logout)
                {
                    if(dropBoxOrDrive==1)
                    {
                        mainActivityObject.logoutFromDropBox();
                        HelpingBot.removeAllPages("DropBox",12345);
                    }
                    else
                    {
                        mainActivityObject.logoutFromDrive();
                        HelpingBot.removeAllPages("GoogleDrive",12345);
                    }
                    mainActivityObject.setUpViewPager();
                }


                return true;
            }
        });

        MenuPopupHelper menuHelper=new MenuPopupHelper(mainActivityObject,(MenuBuilder)popup.getMenu(),options);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();

    }


}
