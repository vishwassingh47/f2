package com.example.sahil.f2.GokuFrags;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.SearchData;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.recycleBinCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.SortingMachine;
import com.example.sahil.f2.Classes.ThumbNailsMod;
import com.example.sahil.f2.FunkyAdapters.GridViewAdapter;
import com.example.sahil.f2.FunkyAdapters.ListViewAdapter;
import com.example.sahil.f2.MainActivity;


import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.FolderLister;
import com.example.sahil.f2.UiClasses.ClickManager;

import java.io.File;
import java.util.ArrayList;


import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod0;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod1;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod2;
import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;


import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getPageIndexFromFrameId;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getStorageIdFromPageName;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.isExternalSdCardPath;


/**
 * Created by Acer on 04-08-2017.
 */

public class storagePager extends Fragment
{
    //**********************************
    //pager specific

    private ImageView home,back,root,search,close,options,artIcon,storage1,storage2,storage3,storage4,storage5;
    private LinearLayout layer1,layer2,search_layout,stackLinearLayout,horizontalLinearLayout,loading,storages_layer2;
    private TextView title,artText;
    private GridView gridview;
    private ListView listView;
    private RelativeLayout artLayout;
    private Button artButton;
    public HorizontalScrollView horizontalScrollView;
    private String TAG="STORAGE_PAGE";

    //**********************************


    private int currentStorageType;
    private final int INTERNAL=1;
    private final int EXTERNAL=2;
    private final int CLOUD=3;


    private ArrayList<String> buttonNames;

    private View view;

    public MainActivity mainActivityObject;
    public ThumbNailsMod thumbNailsMod;
    public CommonsUtils commonsUtils;
    private ThumbNailManager thumbNailManager;
    private ClickManager clickManager;
    private MyUi myUi;
    final HelpingBot helpingBot=new HelpingBot();

    private boolean isThisFirstFavouritePath=false;
    private boolean isThisFirstGarbagePath=false;
    private boolean shouldStartNew=false,shouldStartOld=false;
    public boolean isFolderOk=false;
    private String localPath="";
    private int modValue;
    private int rootFrameId;
    /*
    in case of local storages storageId 1,2,3 are same
    in case of recycle bin and favourites, storageId 1,2,3,4,5 are all distinct and 1=internal,2=sdcard1,3=sdcard2,4:drive,5:dropbox
     */
    private int storageId;
    private int pageIndex;//in view pager
    private String pageName;

    /*
    currentPath can be:
    any dropbox or drive id OR FTP   /xyz   id
    any local path
    'RecycleBin'
    'Favourites'
     */
    private String currentPath;
    private int currentIndex=0;
    private boolean continueThumbRun=false;


    private GridViewAdapter gridViewAdapter;
    private ListViewAdapter listViewAdapter;

    private Runnable thumbNail_runnable;
    private Handler thumbNail_handler;
    private  boolean thumbNailRunner_isRunning=false;


    private ArrayList<MyFile> myFilesList;
    private ArrayList<String> PagePathList;
    private ArrayList<Integer> PageIndexList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contaimer,Bundle saved)
    {
        Log.e(TAG,"created");
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_storagepager,contaimer,false);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        getStorageInfo();

        myUi=new MyUi();
        thumbNailManager=new ThumbNailManager();
        commonsUtils=new CommonsUtils();
        modValue=pageIndex%3;
        thumbNailManager.clearThumbCache();

        isThisFirstGarbagePath=false;
        isThisFirstFavouritePath=false;
        isFolderOk=false;

        //new folder is opened
        if(shouldStartNew)
        {
            shouldStartNew=false;
            PagePathList.add(localPath);
            PageIndexList.add(0);
        }
        //back button is pressed
        if(shouldStartOld)
        {
            shouldStartOld=false;
            PagePathList.remove(PagePathList.size()-1);
            PageIndexList.remove(PageIndexList.size()-1);
            //searchCache.clearStorageById(storageId);//probably of no use
        }


        currentPath=PagePathList.get(PagePathList.size()-1);
        currentIndex=PageIndexList.get(PageIndexList.size()-1);
        if(currentPath.equals("RecycleBin"))
            isThisFirstGarbagePath=true;
        if(currentPath.equals("Favourites"))
            isThisFirstFavouritePath=true;


        if(storageId>3)
        {
            currentStorageType=CLOUD;
        }
        else
        {
            if(isThisFirstGarbagePath || isThisFirstFavouritePath )
            {
                currentStorageType=storageId==1?INTERNAL:EXTERNAL;
            }
            else
            {
                currentStorageType=isExternalSdCardPath(currentPath)?EXTERNAL:INTERNAL;
            }
        }


        view=getView();
        if(view==null)
        {
            Log.e(TAG,"NO VIEW TO SHOW");
            mainActivityObject.showHideButtons(pageIndex);
            return;
        }

        myUi.initialiseAllUi();


        gridview.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        artLayout.setVisibility(View.GONE);


        myUi.setUpLayer1();
        myUi.setUpStackLayout();
        myUi.setUpLayer2();
        myUi.setUpStorageLayer();

        if(storageId<=3)
        {
            if(!analyseLocalStorage())
                return;
        }

        if(storageId==4)
        {
            if(!analyseGoogleDrive())
                return;
        }

        if(storageId==5)
        {
            if(!analyseDropBox())
                return ;
        }


        if(variablesCache.listOrGrid==1)
        {
            gridview=null;
        }
        else
        {
            listView=null;
        }

        try
        {
            switch (currentStorageType)
            {
                case INTERNAL:
                    title.setTextColor(getResources().getColor(R.color.main_theme_red));
                    break;
                case EXTERNAL:
                    title.setTextColor(getResources().getColor(R.color.white));
                    break;
                case CLOUD:
                    title.setTextColor(getResources().getColor(R.color.progress_blue));
                    break;
            }
        }
        catch (Exception e)
        {}


        //fetching the files and setting all the lists and listView
        if(storageId>=1 && storageId<=6)
        {
            fetch(storageId);
        }
        else
        {
            Toast.makeText(mainActivityObject, "Wubba Wubba LOOB LOOB", Toast.LENGTH_SHORT).show();
        }

    }


    private class MyUi
    {
        private String storageFirstPath;
        private void initialiseAllUi()
        {
            layer1=(LinearLayout) view.findViewById(R.id.layer1);
            home=(ImageView) view.findViewById(R.id.layer1_home);
            root=(ImageView) view.findViewById(R.id.layer1_root);
            back=(ImageView) view.findViewById(R.id.layer1_back);
            search_layout=(LinearLayout) view.findViewById(R.id.layer1_search_layout);
            title=(TextView) view.findViewById(R.id.layer1_title);
            search=(ImageView) view.findViewById(R.id.layer1_search);
            close=(ImageView) view.findViewById(R.id.layer1_close);
            options=(ImageView) view.findViewById(R.id.layer1_options);


            stackLinearLayout=(LinearLayout)view.findViewById(R.id.stackLinearLayout_storagePager);

            layer2=(LinearLayout)view.findViewById(R.id.layer2_storagePager);
            horizontalLinearLayout = (LinearLayout)view.findViewById(R.id.horiLinearLayout_storagePager);
            horizontalScrollView = (HorizontalScrollView)view.findViewById(R.id.scroller_storagePager);


            storages_layer2=(LinearLayout)view.findViewById(R.id.storages_layer2);

            storage1=(ImageView)view.findViewById(R.id.storages_storage1);
            storage2=(ImageView)view.findViewById(R.id.storages_storage2);
            storage3=(ImageView)view.findViewById(R.id.storages_storage3);
            storage4=(ImageView)view.findViewById(R.id.storages_storage4);
            storage5=(ImageView)view.findViewById(R.id.storages_storage5);


            loading=(LinearLayout)view.findViewById(R.id.storagePager_loading);
            gridview=(GridView) view.findViewById(R.id.grid_storagePager);
            listView = (ListView) view.findViewById(R.id.list_storagePager);

            artLayout=(RelativeLayout)view.findViewById(R.id.art_layout);
            artIcon=(ImageView) view.findViewById(R.id.art_layout_icon);
            artText=(TextView) view.findViewById(R.id.art_layout_text);
            artButton=(Button) view.findViewById(R.id.art_layout_retry);

        }

        private void setUpStorageLayer()
        {
            if(isThisFirstGarbagePath || isThisFirstFavouritePath)
            {
                storages_layer2.setVisibility(View.VISIBLE);

                if(Physical_Storage_PATHS.size()>0)
                {
                    storage1.setVisibility(View.VISIBLE);

                    if(storageId ==1)
                    {
                        storage1.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }

                    storage1.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(storageId !=1)
                            {
                                if(isThisFirstGarbagePath)
                                    recycleBinCache.storageId=1;
                                else
                                    favouritesCache.storageId=1;

                                reloadPager();
                            }
                        }
                    });
                }
                if(Physical_Storage_PATHS.size()>1 && isThisFirstFavouritePath)
                {
                    storage2.setVisibility(View.VISIBLE);

                    if(storageId ==2)
                    {
                        storage2.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }

                    storage2.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(storageId !=2)
                            {
                                if(isThisFirstGarbagePath)
                                    recycleBinCache.storageId=2;
                                else
                                    favouritesCache.storageId=2;

                                reloadPager();
                            }
                        }
                    });
                }
                if(Physical_Storage_PATHS.size()>2 && isThisFirstFavouritePath)
                {
                    storage3.setVisibility(View.VISIBLE);

                    if(storageId ==3)
                    {
                        storage3.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }

                    storage3.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(storageId !=3)
                            {
                                if(isThisFirstGarbagePath)
                                    recycleBinCache.storageId=3;
                                else
                                    favouritesCache.storageId=3;

                                reloadPager();
                            }
                        }
                    });
                }

                if(GoogleDriveConnection.isDriveAvailable)
                {
                    storage4.setVisibility(View.VISIBLE);
                    if(storageId ==4)
                    {
                        storage4.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }
                    storage4.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(storageId !=4)
                            {
                                if(isThisFirstGarbagePath)
                                    recycleBinCache.storageId=4;
                                else
                                    favouritesCache.storageId=4;

                                reloadPager();
                            }
                        }
                    });
                }
                if(DropBoxConnection.isDropboxAvailable)
                {
                    storage5.setVisibility(View.VISIBLE);
                    if(storageId ==5)
                    {
                        storage5.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }
                    storage5.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(storageId !=5)
                            {
                                if(isThisFirstGarbagePath)
                                    recycleBinCache.storageId=5;
                                else
                                    favouritesCache.storageId=5;

                                reloadPager();
                            }
                        }
                    });
                }
            }
            else
            {
                storages_layer2.setVisibility(View.GONE);
            }
        }

        private void setUpLayer1()
        {
            home.setVisibility(View.VISIBLE);

            back.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
            search_layout.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            options.setVisibility(View.VISIBLE);


            if(storageId<=3 && !isThisFirstGarbagePath && !isThisFirstFavouritePath )
            {
                root.setVisibility(View.VISIBLE);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if(currentPath.equals("/"))
                        {
                            Toast.makeText(mainActivityObject, "Already at root directory", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            openNewPager("/");
                        }
                    }
                });
            }
            else
            {
                root.setVisibility(View.GONE);
            }


            setUpTitle();

            home.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    storageFirstPath=storageId==6?PagePathList.get(1):PagePathList.get(0);
                    if(storageId==6)
                    {
                        gotoFtpLoginPage();
                        return;
                    }
                    if(currentPath.equals(storageFirstPath))
                    {
                        Toast.makeText(mainActivityObject, "Already at main directory", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(isThisFirstGarbagePath || isThisFirstFavouritePath)
                        {
                            for(int i=1;i<PagePathList.size();i++)
                            {
                                PagePathList.remove(i);
                                PageIndexList.remove(i);
                                i--;
                            }
                            reloadPager();
                        }
                        else
                        {
                            openNewPager(storageFirstPath);
                        }
                    }
                }
            });

            options.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setUpOptions();
                }
            });

            if(storageId==6 || isThisFirstFavouritePath || (isThisFirstGarbagePath && storageId>1))
            {
                search.setVisibility(View.GONE);
            }
            else
            {
                search.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(isReady())
                        {
                            setUpSearchButton();
                        }
                        else
                        {
                            Toast.makeText(mainActivityObject, "Cant search now", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        private void setUpSearchButton()
        {
            SearchData searchData=MyCacheData.getSearchData(storageId);
            searchData.clearStorage();
            searchData.whereToSearch=currentPath;
            //searchCache.setWhereToSearch(storageId,runRootCommand,currentPath);//imp

            //remove previous search result from backStack if any
            if(PagePathList.contains("SearchResult"))
            {
                int index=PagePathList.indexOf("SearchResult");
                PagePathList.remove(index);
                PageIndexList.remove(index);
            }

            search_fragment newFragment=new search_fragment();
            newFragment.newStarted("SearchResult");
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,newFragment);
            ft.commit();
        }

        private void setUpTitle()
        {

            if(isThisFirstGarbagePath)
            {
                title.setText("RECYCLE BIN");
                return;
            }
            if(isThisFirstFavouritePath)
            {
                title.setText("FAVOURITES");
                return;
            }

            if(currentPath.equals("/") && storageId<=3)
            {
                title.setText("ROOT DIRECTORY");
            }
            else
            {
                if(storageId<=3)
                    title.setText(currentPath.substring(currentPath.lastIndexOf('/')+1));

                if(storageId==4)
                    title.setText("Google Drive ("+currentPath+")");

                if(storageId==5)
                {
                    if(currentPath.equals(""))
                        title.setText("Drop Box");
                    else
                        title.setText(currentPath.substring(currentPath.lastIndexOf('/')+1));
                }
                if(storageId==6)
                {
                    if(currentPath.equals("/"))
                    {
                        title.setText("FTP Main Directory");
                    }
                    else
                    {
                        title.setText(currentPath.substring(currentPath.lastIndexOf('/')+1));
                    }
                }
            }

        }

        private void setUpOptions()
        {
            android.support.v7.widget.PopupMenu popup=new PopupMenu(mainActivityObject,options);
            popup.getMenuInflater().inflate(R.menu.popup,popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                public boolean onMenuItemClick(MenuItem item)
                {
                    if(item.getItemId()==R.id.refresh)
                    {
                        reloadPager();
                    }

                    if(item.getItemId()==R.id.sort_default)
                    {
                        variablesCache.sortStorageBy=10;
                        sort();
                        notifyAdapter();
                    }

                    if(item.getItemId()==R.id.sort_name1)
                    {
                        variablesCache.sortStorageBy=1;
                        sort();
                        notifyAdapter();
                    }
                    if(item.getItemId()==R.id.sort_name2)
                    {
                        variablesCache.sortStorageBy=2;
                        sort();
                        notifyAdapter();
                    }
                    if(item.getItemId()==R.id.sort_date1)
                    {
                        variablesCache.sortStorageBy=3;
                        sort();
                        notifyAdapter();
                    }
                    if(item.getItemId()==R.id.sort_date2)
                    {
                        variablesCache.sortStorageBy=4;
                        sort();
                        notifyAdapter();
                    }
                    if(item.getItemId()==R.id.sort_size1)
                    {
                        variablesCache.sortStorageBy=5;
                        sort();
                        notifyAdapter();
                    }
                    if(item.getItemId()==R.id.sort_size2)
                    {
                        variablesCache.sortStorageBy=6;
                        sort();
                        notifyAdapter();
                    }


                    if(item.getItemId()==R.id.close)
                    {
                        mainActivityObject.onBackPressed();
                    }


                    if(item.getItemId()==R.id.options_list)
                    {
                        SharedPreferences pref=mainActivityObject.getSharedPreferences("VariablesCache",0);
                        SharedPreferences.Editor editor=pref.edit();
                        editor.putInt("listOrGrid",1);
                        editor.apply();
                        variablesCache.listOrGrid=1;

                        reloadPager();
                        mainActivityObject.returnRefreshObject().refreshPagerNumber(mainActivityObject.viewPager.getCurrentItem()+1);
                        mainActivityObject.returnRefreshObject().refreshPagerNumber(mainActivityObject.viewPager.getCurrentItem()-1);

                    }
                    if(item.getItemId()==R.id.options_grid)
                    {
                        SharedPreferences pref=mainActivityObject.getSharedPreferences("VariablesCache",0);
                        SharedPreferences.Editor editor=pref.edit();
                        editor.putInt("listOrGrid",2);
                        editor.apply();
                        variablesCache.listOrGrid=2;

                        reloadPager();
                        mainActivityObject.returnRefreshObject().refreshPagerNumber(mainActivityObject.viewPager.getCurrentItem()+1);
                        mainActivityObject.returnRefreshObject().refreshPagerNumber(mainActivityObject.viewPager.getCurrentItem()-1);
                    }


                    return true;
                }
            });
            Menu menu=popup.getMenu();

            if(!isReady())
            {
                menu.findItem(R.id.customize).setEnabled(false);
                menu.findItem(R.id.sort).setEnabled(false);
            }

            MenuPopupHelper menuHelper=new MenuPopupHelper(mainActivityObject,(MenuBuilder)menu,options);
            menuHelper.setForceShowIcon(true);
            menuHelper.setGravity(Gravity.END);
            menuHelper.show();
        }

        private void showArtLayout(int resourceId,String message,boolean toRetry)  //OK
        {
            try
            {
                loading.setVisibility(View.GONE);
                commonsUtils.showArtLayout(resourceId,message,toRetry,artIcon,artText,artLayout,artButton,mainActivityObject,storageId,1,getFragmentManager().findFragmentById(rootFrameId));

                if(variablesCache.listOrGrid==1)
                {
                    listView.setVisibility(View.GONE);
                }
                else
                {
                    gridview.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                Log.e(TAG,"showArtLayout()");
            }
        }

        private void setUpStackLayout()
        {
            if(isThisFirstGarbagePath || isThisFirstFavouritePath)
            {
                stackLinearLayout.setVisibility(View.GONE);
                return;
            }
            stackLinearLayout.removeAllViews();

            View layout;
            for(String x:PagePathList)
            {
                layout=LayoutInflater.from(getActivity()).inflate(R.layout.layoutof_stack,stackLinearLayout,false);
                ImageView imageView=(ImageView)layout.findViewById(R.id.stack_logo);
                int iconId;
                switch (x)
                {
                    case "SearchResult":
                        iconId=R.drawable.search_icon;
                        break;
                    case "RecycleBin":
                        iconId=R.mipmap.delete2;
                        break;
                    case "Favourites":
                        iconId=R.drawable.favourite;
                        break;
                    default:
                        iconId=helpingBot.getPathLogo(storageId);
                }
                imageView.setImageResource(iconId);

                stackLinearLayout.addView(layout);
            }
        }

        private void setUpLayer2()
        {
            if(isThisFirstGarbagePath || isThisFirstFavouritePath)
            {
                layer2.setVisibility(View.GONE);
                return;
            }
            horizontalLinearLayout.removeAllViews();

            if(storageId==4)
            {
                View layout=LayoutInflater.from(getActivity()).inflate(R.layout.directory_name,horizontalLinearLayout,false);
                TextView textView=(TextView)layout.findViewById(R.id.directory_name);
                LinearLayout border=(LinearLayout)layout.findViewById(R.id.directory_border);
                ImageView logo=(ImageView)layout.findViewById(R.id.directory_icon);

                border.setBackgroundColor(Color.parseColor("#ffffff"));
                textView.setText("Google Drive");
                //textView.setBackgroundColor(Color.parseColor("#f44336"));
                textView.setGravity(Gravity.CENTER);

                logo.setVisibility(View.VISIBLE);
                logo.setImageResource(R.mipmap.google_drive);
                horizontalLinearLayout.addView(layout);
            }
            else
            {
                final String temp[]=currentPath.split("\\/");
                buttonNames=new ArrayList<>();
                buttonNames.add("/");
                for(String x:temp)
                {
                    if(!x.equals(""))
                        buttonNames.add(x);
                }
                String path="/";
                int buttonid=0;
                View layout;
                for(int it=0;it<buttonNames.size();it++)
                {
                    if(it>=1)
                    {
                        path=HelpingBot.slashAppender(path,buttonNames.get(it));
                    }
                    layout=LayoutInflater.from(getActivity()).inflate(R.layout.directory_name,horizontalLinearLayout,false);
                    TextView textView=(TextView)layout.findViewById(R.id.directory_name);
                    LinearLayout border=(LinearLayout)layout.findViewById(R.id.directory_border);
                    ImageView logo=(ImageView)layout.findViewById(R.id.directory_icon);
                    border.setBackgroundColor(Color.parseColor("#ffffff"));
                    textView.setText(buttonNames.get(it));
                   // textView.setBackgroundColor(Color.parseColor("#f44336"));
                    textView.setGravity(Gravity.CENTER);
                    if(path.equals("/"))
                    {
                        logo.setVisibility(View.VISIBLE);
                        if(storageId==5)
                        {
                            logo.setImageResource(R.mipmap.dropbox);
                        }
                        if(storageId<=3)
                        {
                            logo.setImageResource(R.drawable.hash);
                        }
                        if(storageId==6)
                        {
                            logo.setImageResource(R.mipmap.cmd);
                        }
                    }
                    else
                    {
                        storageFirstPath=storageId==6?PagePathList.get(1):PagePathList.get(0);

                        if(storageId<=3 && storageFirstPath.equals(path))
                        {
                            logo.setVisibility(View.VISIBLE);
                            logo.setImageResource(R.drawable.sd_card);
                        }
                    }

                    layout.setClickable(true);
                    layout.setId(buttonid);

                    //making the current folder unclickable
                    if(it!=buttonNames.size()-1)
                    {
                        layout.setOnClickListener(new View.OnClickListener()
                        {
                            public void onClick(View view)
                            {
                                String str2="/";
                                if(storageId==5 && view.getId()==0 )
                                    str2="";
                                for(int j=1;j<=view.getId();j++)
                                {
                                    str2=HelpingBot.slashAppender(str2,buttonNames.get(j));
                                }
                                openNewPager(str2);
                            }

                        });
                    }

                    horizontalLinearLayout.addView(layout);
                    buttonid++;
                }


                horizontalScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
                {
                    @Override
                    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
                    {
                        horizontalScrollView.removeOnLayoutChangeListener(this);
                        horizontalScrollView.fullScroll(View.FOCUS_RIGHT);
                    }
                });
            }
        }

    }


    public void reloadPager()
    {
        storagePager newFragment=new storagePager();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,newFragment);
        ft.commit();
    }

    public void openNewPager(String newPath)
    {
        storagePager newFragment=new storagePager();
        newFragment.newStarted(newPath);
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,newFragment);
        ft.commit();
    }


    private boolean isReady()
    {
        return isFolderOk;
    }

    private void notifyAdapter()
    {
        if(variablesCache.listOrGrid==1)
        {
            listViewAdapter.notifyDataSetChanged();
        }
        else
        {
            gridViewAdapter.notifyDataSetChanged();
        }
    }

    private void sort()
    {
        if(myFilesList==null)
            return;

        int sortBy=variablesCache.sortStorageBy;
        if(variablesCache.listOrGrid==1 && sortBy<10)
        {
            sortBy+=10;
        }
        SortingMachine sortingMachine =new SortingMachine(sortBy);
        sortingMachine.sortMyFile(myFilesList);
    }

    private void fetch(final int sId)
    {
        class FetcherAsyncTask extends AsyncTask<Void,Void,Integer>
        {

            private FetcherAsyncTask()
            {
                myFilesList=new ArrayList<>();
            }

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                Log.e(TAG+sId,"onPreExecute"+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                loading.setVisibility(View.VISIBLE);
                mainActivityObject.showHideButtons(pageIndex);
            }

            @Override
            protected Integer doInBackground(Void... params)
            {
                FolderLister folderLister=new FolderLister();
                if(sId<=3)
                {
                    if(isThisFirstFavouritePath)
                    {
                        myFilesList=folderLister.listLocalFavouriteFolder();
                    }
                    else
                    {
                        String pathToLoad;
                        if(isThisFirstGarbagePath)
                        {
                            pathToLoad=recycleBinCache.internalPath;
                        }
                        else
                        {
                            pathToLoad=currentPath;
                        }
                        myFilesList=folderLister.listLocalFolder(pathToLoad);
                        if(myFilesList==null)
                        {
                            myFilesList=folderLister.listRootFolder(mainActivityObject,pathToLoad);
                        }
                    }

                    sort();
                    return 0;
                }
                if(sId==4)
                {
                    if(isThisFirstFavouritePath)
                    {
                        myFilesList=folderLister.listDriveFavouriteFolder();
                    }
                    else
                    {
                        myFilesList=folderLister.listDriveFolder(currentPath,isThisFirstGarbagePath);
                    }

                    sort();
                    return 0;
                }
                if(sId==5)
                {
                    if(isThisFirstFavouritePath)
                    {
                        myFilesList=folderLister.listDropBoxFavouriteFolder();
                    }
                    else
                    {
                        myFilesList=folderLister.listDropBoxFolder(currentPath,isThisFirstGarbagePath);
                    }
                    sort();
                    return 0;
                }
                if(sId==6)
                {
                    myFilesList=folderLister.listFtpFolder(currentPath);
                    sort();
                    return 0;
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer x)
            {
                Log.e(TAG+storageId,"onPostExecute"+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                loading.setVisibility(View.GONE);
                mainActivityObject.showHideButtons(pageIndex);

                if(myFilesList==null)
                {
                    isFolderOk=false;
                    myUi.showArtLayout(R.mipmap.pick_nose,"READ ACCESS DENIED",true);
                }
                else
                {
                    isFolderOk=true;
                    if(myFilesList.size()==0)
                    {
                        myUi.showArtLayout(R.mipmap.empty_folder,"EMPTY",false);
                    }
                    else
                    {
                        //Setting the adapters
                        try
                        {
                            setUpListOrGrid();
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG,"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxSUPER EXCEPTION xxxxxxxxxxxxxxxxxxxxxx");
                        }
                    }
                }

            }

        }

        FetcherAsyncTask asyncTask=new FetcherAsyncTask();
        asyncTask.execute();
    }


    @Override
    public void onDestroy()             //OK
    {
        if(thumbNail_handler!=null)
            thumbNail_handler.removeCallbacks(thumbNail_runnable);
        //error     thumbNailManager can be null
        thumbNailManager.clearThumbCache();

        super.onDestroy();
        Log.e(TAG,"DESTROYED"+pageIndex);

        int pos=0;
        try
        {
            if( variablesCache.listOrGrid==1)
            {
                if(listView!=null)
                {
                    pos=listView.getFirstVisiblePosition();
                }
            }
            else
            {
                if(gridview!=null)
                {
                    pos=gridview.getFirstVisiblePosition();
                }
            }
        }
        catch (Exception e)
        {
            pos=0;
        }

        PageIndexList.set(PageIndexList.size()-1,pos);

    }


    public void backPressed(Page page)
    {
        if((page.getPathList().get(page.getPathList().size()-2)).equals("SearchResult"))    //previous path
        {
            search_fragment backFragment=new search_fragment();
            backFragment.oldStarted();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,backFragment);
            ft.commit();
            return;
        }
        if((page.getPathList().get(page.getPathList().size()-2)).equals("FtpClient"))    //previous path
        {
            ftpLoginPager backFragment=new ftpLoginPager();
            PagePathList.remove(PagePathList.size()-1);
            PageIndexList.remove(PageIndexList.size()-1);
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,backFragment);
            ft.commit();
            return;
        }

        storagePager backFragment=new storagePager();
        backFragment.oldStarted();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,backFragment);
        ft.commit();

    }

    public void newStarted(String path)
    {
        shouldStartNew=true;
        localPath=path;
    }

    public void oldStarted()
    {
        shouldStartOld=true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    private boolean analyseLocalStorage()
    {

        if(isThisFirstFavouritePath)
        {
            return true;    //no need to analyse
        }

        File thisDirectory;
        if(isThisFirstGarbagePath)
        {
            thisDirectory=new File(recycleBinCache.internalPath);
        }
        else
        {
            thisDirectory=new File(currentPath);
        }

        if(currentStorageType==INTERNAL)
        {
            if(thisDirectory.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath()))
            {
                /*
                checking if this is primary directory and is not mounted
                 */
                String state=Environment.getExternalStorageState();
                if(!state.equals(Environment.MEDIA_MOUNTED))
                {
                    myUi.showArtLayout(R.mipmap.pick_nose,state,false);
                    mainActivityObject.showHideButtons(pageIndex);
                    return false;
                }
            }
            return true;
        }
        else
        {
            //EXTERNAL STORAGE

            if(!thisDirectory.exists())
            {
                myUi.showArtLayout(R.mipmap.pick_nose,"DOES NOT EXIST",false);
                mainActivityObject.showHideButtons(pageIndex);
                return false;
            }

            if(!thisDirectory.canRead())
            {
                myUi.showArtLayout(R.mipmap.pick_nose,"NOT READABLE",false);
                mainActivityObject.showHideButtons(pageIndex);
                return false;
            }
            return true;
        }

    }

    private boolean analyseGoogleDrive()
    {
        return true;
    }

    private boolean analyseDropBox()
    {
        return true;
    }

    public void gotoFtpLoginPage()
    {
        for(int i=1;i<PagePathList.size();i++)
        {
            PagePathList.remove(i);
            PageIndexList.remove(i);
            i--;
        }
        ftpLoginPager newFragment=new ftpLoginPager();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,newFragment);
        ft.commit();
    }

    public void runThumbNailRunner()
    {
        //if not running run
        if(!thumbNailRunner_isRunning)
        {
            thumbNailRunner_isRunning= true;
            thumbNail_handler.post(thumbNail_runnable);
        }
    }

    private class ThumbNailManager
    {
        private void initialiseThumbRunner()
        {
            thumbNailsMod=new ThumbNailsMod(storageId,mainActivityObject);
            continueThumbRun=false;
            thumbNailRunner_isRunning=false;
            thumbNail_handler=new Handler();

            thumbNail_runnable=new Runnable()
            {
                @Override
                public void run()
                {
                    continueThumbRun=false;
                    thumbNailRunner_isRunning= true;
                    if(Math.abs(mainActivityObject.viewPager.getCurrentItem()-pageIndex) <=1)
                    {
                        switch (modValue)
                        {
                            case 0:
                                continueThumbRun= thumbNailsMod.mod0Thumb();
                                break;
                            case 1:
                                continueThumbRun= thumbNailsMod.mod1Thumb();
                                break;
                            case 2:
                                continueThumbRun= thumbNailsMod.mod2Thumb();
                                break;
                            default:
                                continueThumbRun=false;
                        }

                        if(continueThumbRun)
                        {
                            thumbNail_handler.postDelayed(thumbNail_runnable, Constants.thumbRunnerDelay);
                        }
                        else
                        {
                            thumbNailRunner_isRunning = false;
                            thumbNail_handler.removeCallbacks(thumbNail_runnable);
                        }
                    }
                    else
                    {
                        thumbNailRunner_isRunning = false;
                        thumbNail_handler.removeCallbacks(thumbNail_runnable);
                        Log.e(TAG,"thumb runnable forced closed");
                    }
                }
            };

        }

        private void clearThumbCache()
        {
            switch (modValue)
            {
                case 0:
                    clear_mod0();
                    break;
                case 1:
                    clear_mod1();
                    break;
                case 2:
                    clear_mod2();
                    break;
            }
        }
    }

    private void setUpListOrGrid() throws Exception
    {
        Log.e(TAG+storageId,"setUpListOrGrid"+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        thumbNailManager.initialiseThumbRunner();

        if( variablesCache.listOrGrid==1)
        {
            listView.setVisibility(View.VISIBLE);
            listViewAdapter=new ListViewAdapter(mainActivityObject,myFilesList,storageId,getFragmentManager().findFragmentById(rootFrameId),modValue,1);
            listView.setAdapter(listViewAdapter);

            listView.setSelection(currentIndex);


            LayoutInflater layoutinflater = getLayoutInflater();
            ViewGroup footer = (ViewGroup)layoutinflater.inflate(R.layout.layoutof_lisview_footer,listView,false);
            if(listView.getFooterViewsCount()==0)
            {
                //showing only 1 footer
                listView.addFooterView(footer,null,false);
            }
        }
        else
        {
            gridview.setVisibility(View.VISIBLE);
            gridViewAdapter=new GridViewAdapter(mainActivityObject,myFilesList,storageId,getFragmentManager().findFragmentById(rootFrameId),modValue,1,0);
            gridview.setAdapter(gridViewAdapter);

            gridview.setSelection(currentIndex);
        }

        /*

         */
        ArrayList<LinearLayout> linearLayoutArrayList=new ArrayList<>();
        linearLayoutArrayList.add(layer1);
        if(isThisFirstGarbagePath || isThisFirstFavouritePath)
        {
            linearLayoutArrayList.add(storages_layer2);
        }
        else
        {
            linearLayoutArrayList.add(layer2);
        }
        if(listView!=null)
        {
            clickManager=new ClickManager(mainActivityObject,isThisFirstGarbagePath,1,currentPath,pageIndex,storageId,getFragmentManager().findFragmentById(rootFrameId),linearLayoutArrayList,myFilesList,null,listView,listViewAdapter);
        }
        if(gridview!=null)
        {
            clickManager=new ClickManager(mainActivityObject,isThisFirstGarbagePath,1,currentPath,pageIndex,storageId,getFragmentManager().findFragmentById(rootFrameId),linearLayoutArrayList,myFilesList,null,gridview,gridViewAdapter);

        }


        if(variablesCache.listOrGrid==1)
        {
            clickManager.longClickedList();
        }
        else
        {
            clickManager.longClickedGrid();
        }
    }

    private void getStorageInfo()
    {

        rootFrameId=((ViewGroup)getView().getParent()).getId();
        pageIndex=getPageIndexFromFrameId(rootFrameId);
        Page thisPage=MainActivity.pageList.get(pageIndex);
        PagePathList=thisPage.getPathList();
        PageIndexList=thisPage.getIndexList();

        pageName=thisPage.getName();
        storageId=getStorageIdFromPageName(pageName);   //(1-6)


    }

}
