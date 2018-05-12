package com.example.sahil.f2.GokuFrags;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.appManagerCache;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyApp;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.Classes.SimpleYesNoDialog;
import com.example.sahil.f2.Classes.SortingMachine;
import com.example.sahil.f2.Classes.ThumbNailsMod;
import com.example.sahil.f2.FunkyAdapters.GridViewAdapter;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.UiClasses.ClickManager;
import com.example.sahil.f2.Utilities.AppManagerUtils;
import com.example.sahil.f2.Utilities.ExtensionUtil;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod0;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod1;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod2;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getPageIndexFromFrameId;
import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;

/**
 * Created by hit4man47 on 2/1/2018.
 */

public class appManager extends Fragment
{
    //**********************************
    //pager specific

    private ImageView home,back,root,search,search_backspace,close,options,artIcon,storage1,storage2,storage3,storage4,storage5;
    private LinearLayout layer1,layer2,layer3,loading,search_layout;
    private TextView title,artText,tv_system_apps,tv_installed_apps,tv_apk;
    private GridView gridview;
    private RelativeLayout artLayout;
    private Button artButton;
    private EditText search_edit;
    private TinyDB tinyDB;
    private String TAG="APP_MANAGER";

    //**********************************
    private View view;
    public MainActivity mainActivityObject;

    private GridViewAdapter gridViewAdapter;

    public CommonsUtils commonsUtils;
    private ThumbNailManager thumbNailManager;
    private HelpingBot helpingBot;
    private AppManagerUtils appManagerUtils;
    private MySearchManager mySearchManager;

    private Runnable thumbNail_runnable;
    private boolean thumbNailRunner_isRunning;
    private boolean continueThumbRun=false;
    private Handler thumbNail_handler;

    public ThumbNailsMod thumbNailsMod;
    private DataFetcher dataFetcher;
    private MyUi myUi;
    private TextWatcher myTextWatcher;
    private boolean isFolderOk=false;
    private ArrayList<String> PagePathList;
    private ArrayList<Integer> PageIndexList;
    private Page thisPage;

    private int rootFrameId,pageIndex;
    private int whatToDo;
    private int modValue;
    private String whatToSearch;
    private int searchThreadId=0;
    private boolean switchingToNewTab=false;
    private String storageHomePath;

    private ArrayList<MyFile> myFilesList;
    private ArrayList<MyFile> referenceList,searchFileList;
    private ClickManager clickManager;
    /*
    whatToDo:
    1:installed Apps
    2:system apps
    3:apk
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contaimer, Bundle saved)
    {
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_app_manager,contaimer,false);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        getInfo();

        myUi=new MyUi();
        dataFetcher =new DataFetcher();
        appManagerUtils=new AppManagerUtils(mainActivityObject);
        mySearchManager=new MySearchManager();
        thumbNailManager=new ThumbNailManager();
        thumbNailManager.clearThumbCache();

        view=getView();
        myUi.initialiseAllUi();
        commonsUtils=new CommonsUtils();
        helpingBot=new HelpingBot();

        whatToSearch=null;
        isFolderOk=false;
        switchingToNewTab=false;


        myFilesList=new ArrayList<>();
        referenceList=myFilesList;
        myUi.setUpStorageButtons();
        myUi.setUpLayer1();

        dataFetcher.fetch();
        mainActivityObject.showHideButtons(pageIndex);

    }

    private class MyUi
    {
        private void initialiseAllUi()
        {
            layer1=(LinearLayout) view.findViewById(R.id.layer1);
            home=(ImageView) view.findViewById(R.id.layer1_home);
            root=(ImageView) view.findViewById(R.id.layer1_root);
            back=(ImageView) view.findViewById(R.id.layer1_back);
            search_layout=(LinearLayout) view.findViewById(R.id.layer1_search_layout);
            search_edit=(EditText) view.findViewById(R.id.layer1_search_edit);
            search_backspace=(ImageView) view.findViewById(R.id.layer1_search_backspace);
            title=(TextView) view.findViewById(R.id.layer1_title);
            search=(ImageView) view.findViewById(R.id.layer1_search);
            close=(ImageView) view.findViewById(R.id.layer1_close);
            options=(ImageView) view.findViewById(R.id.layer1_options);

            layer2=(LinearLayout)view.findViewById(R.id.app_mananger_layer2);

            tv_system_apps=(TextView) view.findViewById(R.id.layer2_system);
            tv_installed_apps=(TextView) view.findViewById(R.id.layer2_installed);
            tv_apk=(TextView) view.findViewById(R.id.layer2_apks);

            layer3=(LinearLayout)view.findViewById(R.id.storageButtonsLayer);
            storage1=(ImageView)view.findViewById(R.id.storageButton1);
            storage2=(ImageView)view.findViewById(R.id.storageButton2);
            storage3=(ImageView)view.findViewById(R.id.storageButton3);
            storage4=(ImageView)view.findViewById(R.id.storageButton4);
            storage5=(ImageView)view.findViewById(R.id.storageButton5);

            loading=(LinearLayout)view.findViewById(R.id.app_manager_loading);
            gridview=(GridView) view.findViewById(R.id.grid_app_mamnager);

            artLayout=(RelativeLayout)view.findViewById(R.id.art_layout);
            artIcon=(ImageView) view.findViewById(R.id.art_layout_icon);
            artText=(TextView) view.findViewById(R.id.art_layout_text);
            artButton=(Button) view.findViewById(R.id.art_layout_retry);

        }

        private void setUpLayer1()
        {
            home.setVisibility(View.GONE);
            root.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);

            search_layout.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            options.setVisibility(View.VISIBLE);
            title.setText("APP MANAGER");



            search.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(isReady())
                    {
                        searchFileList=new ArrayList<>();
                        searchFileList.addAll(myFilesList);
                        whatToSearch="";
                        referenceList=searchFileList;
                        setUpGrid();
                        showSearchLayout();
                    }
                    else
                    {
                        Toast.makeText(mainActivityObject, "NOTHING TO SEARCH ", Toast.LENGTH_SHORT).show();
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

            back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mainActivityObject.onBackPressed();
                }
            });

            search_backspace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    search_edit.setText("");
                    whatToSearch="";
                }
            });

        }


        private void showSearchLayout()
        {
            search_layout.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            options.setVisibility(View.VISIBLE);

            search_edit.setHint("Search Here");
            search_edit.setText("");
            search_edit.append(whatToSearch);

            mySearchManager.setUpTextWatcher();
        }

        private void showArtLayout(int resourceId,String message,boolean toRetry)  //OK
        {
            try
            {
                loading.setVisibility(View.GONE);
                commonsUtils.showArtLayout(resourceId,message,toRetry,artIcon,artText,artLayout,artButton,mainActivityObject,-576,6,getFragmentManager().findFragmentById(rootFrameId));

                gridview.setVisibility(View.GONE);
            }
            catch (Exception e)
            {
                Log.e(TAG,"showArtLayout()");
            }
        }


        private void setUpStorageButtons()
        {

            switch (whatToDo)
            {
                case 1:
                    layer3.setVisibility(View.GONE);
                    tv_installed_apps.setBackgroundColor(Color.parseColor("#fbc02d"));
                    break;
                case 2:
                    layer3.setVisibility(View.GONE);
                    tv_system_apps.setBackgroundColor(Color.parseColor("#fbc02d"));
                    break;
                case 3:
                    layer3.setVisibility(View.VISIBLE);
                    tv_apk.setBackgroundColor(Color.parseColor("#fbc02d"));
                    break;
            }

            tv_installed_apps.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(whatToDo !=1)
                        {
                            appManagerCache.whatToDo=1;
                            switchingToNewTab=true;
                            reloadPager();
                        }
                    }
                });

            tv_system_apps.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(whatToDo !=2)
                    {
                        appManagerCache.whatToDo=2;
                        switchingToNewTab=true;
                        reloadPager();
                    }
                }
            });

            tv_apk.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(whatToDo !=3)
                    {
                        appManagerCache.whatToDo=3;
                        switchingToNewTab=true;
                        reloadPager();
                    }
                }
            });

            if(whatToDo==3)
            {
                if(Physical_Storage_PATHS.size()>0)
                {
                    storage1.setVisibility(View.VISIBLE);

                    if(appManagerCache.storageId ==1)
                    {
                        storage1.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }

                    storage1.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(appManagerCache.storageId !=1)
                            {
                                appManagerCache.storageId=1;
                                reloadPager();
                            }
                        }
                    });
                }
                if(Physical_Storage_PATHS.size()>1)
                {
                    storage2.setVisibility(View.VISIBLE);

                    if(appManagerCache.storageId ==2)
                    {
                        storage2.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }

                    storage2.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(appManagerCache.storageId !=2)
                            {
                                appManagerCache.storageId=2;

                                reloadPager();
                            }
                        }
                    });
                }
                if(Physical_Storage_PATHS.size()>2)
                {
                    storage3.setVisibility(View.VISIBLE);

                    if(appManagerCache.storageId ==3)
                    {
                        storage3.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }

                    storage3.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(appManagerCache.storageId !=3)
                            {
                                appManagerCache.storageId=3;
                                reloadPager();
                            }
                        }
                    });
                }

                if(GoogleDriveConnection.isDriveAvailable)
                {
                    storage4.setVisibility(View.VISIBLE);
                    if(appManagerCache.storageId ==4)
                    {
                        storage4.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }
                    storage4.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(appManagerCache.storageId !=4)
                            {
                                appManagerCache.storageId=4;

                                reloadPager();
                            }
                        }
                    });
                }
                if(DropBoxConnection.isDropboxAvailable)
                {
                    storage5.setVisibility(View.VISIBLE);
                    if(appManagerCache.storageId ==5)
                    {
                        storage5.setBackgroundColor(Color.parseColor("#fbc02d"));
                    }
                    storage5.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(appManagerCache.storageId !=5)
                            {
                                appManagerCache.storageId=5;
                                reloadPager();
                            }
                        }
                    });
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
                        if(isReady())
                        {
                            reloadPager();
                        }
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

                    return true;
                }
            });
            Menu menu=popup.getMenu();


            menu.findItem(R.id.customize).setVisible(false);
            menu.findItem(R.id.sort_default).setVisible(false);

            if(!isReady())
            {
                menu.findItem(R.id.sort).setEnabled(false);
            }

            MenuPopupHelper menuHelper=new MenuPopupHelper(mainActivityObject,(MenuBuilder)menu,options);
            menuHelper.setForceShowIcon(true);
            menuHelper.setGravity(Gravity.END);
            menuHelper.show();
        }

    }

    private class MySearchManager
    {
        private void setUpTextWatcher()
        {
            myTextWatcher=new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if(s.length()==0)
                    {
                        Log.e("length is zero","000000000000000000000000000000");
                        whatToSearch=s.toString().toLowerCase();
                        modifySearchAdd(++searchThreadId);
                    }
                    else
                    {
                        if(s.length()< whatToSearch.length() || whatToSearch.length()==0)
                        {
                            whatToSearch=s.toString().toLowerCase();
                            Log.e("modifySearchAdd...",(searchThreadId+1)+"---"+ whatToSearch);
                            modifySearchAdd(++searchThreadId);
                        }
                        else
                        {
                            whatToSearch=s.toString().toLowerCase();
                        }
                    }
                    modifySearchRemove();
                    Log.e("searched items size:",searchFileList.size()+"--");
                }

                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            };
            search_edit.addTextChangedListener(myTextWatcher);
        }

        private void modifySearchAdd(final int threadId)
        {
            //ADD
            for(MyFile file:myFilesList)
            {
                if(threadId!=searchThreadId)
                    return;

                if(file.getName().toLowerCase().contains(whatToSearch));
                {
                    if(!searchFileList.contains(file))
                    {
                        searchFileList.add(file);
                        notifyAdapter();
                    }
                }
            }
        }

        private void modifySearchRemove()
        {
            //REMOVE
            for(int i=0;i<searchFileList.size();i++)
            {
                if(!searchFileList.get(i).getName().toLowerCase().contains(whatToSearch))
                {
                    searchFileList.remove(i);
                    notifyAdapter();
                    i--;
                }
            }

        }

    }


    private class DataFetcher
    {
        private void fetch()
        {
            class MyAsyncTask extends AsyncTask<Void,Void,String>
            {

                private final int threadId;
                private MyAsyncTask(int threadId)
                {
                    this.threadId=threadId;
                    myFilesList.clear();
                }

                @Override
                protected void onPreExecute()
                {
                    super.onPreExecute();
                    Log.e(TAG,"onPreExecute"+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    loading.setVisibility(View.VISIBLE);
                    mainActivityObject.showHideButtons(pageIndex);
                }

                @Override
                protected String doInBackground(Void... params)
                {
                    try
                    {
                        if(whatToDo==1 || whatToDo==2)
                        {
                            return getApps();
                        }
                        if(whatToDo==3)
                        {
                            switch (appManagerCache.storageId)
                            {
                                case 1:
                                    return getApksLocal(Physical_Storage_PATHS.get(0));
                                case 2:
                                    return getApksLocal(Physical_Storage_PATHS.get(1));
                                case 3:
                                    return getApksLocal(Physical_Storage_PATHS.get(2));
                                case 4:
                                    return getApksDrive();
                                case 5:
                                    return getApksDropBox();
                                default:
                                    Log.e(TAG,"INVALID STORAGEiD--->>> "+appManagerCache.storageId);
                                    return null;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG,"ERROR WHILE FETCHING --"+e.getMessage());
                        return null;
                    }

                    return null;
                }

                private String getApps() throws Exception
                {
                    PackageManager pm=mainActivityObject.getPackageManager();
                    List<PackageInfo> packageInfoList=pm.getInstalledPackages(0);
                    boolean isSystemApp,isUpdatedSystemApp;
                    String path;
                    File file;
                    Log.e("Total Apps:",packageInfoList.size()+"--");
                    for(int i=0;i<packageInfoList.size();i++)
                    {
                        if(threadId!=appManagerCache.threadFetchId)
                        {
                            break;
                        }
                        PackageInfo packageInfo=packageInfoList.get(i);
                        MyApp app=new MyApp();
                        app.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                        app.setPackageName(packageInfo.packageName);
                        app.setVersionName(packageInfo.versionName);
                        app.setVersionCode(packageInfo.versionCode);
                        //app.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                        path=packageInfo.applicationInfo.publicSourceDir;
                        //Log.e("PATH:",path);
                        file=new File(path);
                        app.setSizeOfApk(file.length());
                        app.setLastModified(file.lastModified());
                        app.setApkPath(path);
                        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                        {
                            isSystemApp=true;
                        }
                        else
                        {
                            isSystemApp=false;
                        }

                        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
                        {
                            // APP WAS INSTALLED AS AN UPDATE TO A BUILD-IN SYSTEM APP
                            isUpdatedSystemApp=true;
                        }
                        else
                        {
                            isUpdatedSystemApp=false;
                        }
                        app.setSystemApp(isSystemApp);
                        app.setSystemAppUpdated(isUpdatedSystemApp);

                        if(app.getAppName().startsWith(".") && !variablesCache.showHidden)
                        {
                            continue;
                        }

                        if((whatToDo==1 && !isSystemApp) || (whatToDo==2 && isSystemApp) )
                        {
                            myFilesList.add(getMyFileFromMyApp(app));
                            Log.e("APPLICATION:"+i,app.getAppName()+"---"+isSystemApp+"--"+isUpdatedSystemApp);
                        }
                    }
                    sort();
                    return "all good";
                }

                private String getApksLocal(String rootPath) throws Exception
                {
                    final PackageManager pm = mainActivityObject.getPackageManager();
                    PackageInfo packageInfo;
                    String[] projection=new String[3];//Which columns to return
                    projection[0]= MediaStore.Files.FileColumns.DATA;
                    projection[1]= MediaStore.Files.FileColumns.SIZE;
                    projection[2]= MediaStore.Files.FileColumns.DATE_MODIFIED;

                    Uri uri=null;
                    Cursor cursor;
                    int it=0;
                    for(int I=1;I<=2;I++)
                    {
                        if(I==1)
                        {
                            uri = MediaStore.Files.getContentUri("internal");
                        }
                        else
                        {
                            uri = MediaStore.Files.getContentUri("external");
                        }

                        cursor = mainActivityObject.getContentResolver().query
                                (
                                        uri,
                                        projection,// Which columns to return
                                        null,// WHERE clause; which rows to return (all rows)
                                        null, // WHERE clause selection arguments (none)
                                        null // Order-by clause
                                );

                        if (cursor != null)
                        {

                            Log.e("MEDIA STORE","HAS TOTAL FILES:"+cursor.getCount());
                            while (cursor.moveToNext())
                            {
                                it++;
                                if(threadId!=appManagerCache.threadFetchId)
                                {
                                    break;
                                }

                                String path=null;
                                long lastModified=0;
                                long size=0;

                                path=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                                //Log.e("@@@@",path);
                                //if(!path.startsWith(rootPath))
                                //{
                                  //  continue;
                                //}
                                if(!path.endsWith(".apk"))
                                {
                                    continue;
                                }
                                Log.e("@@@@",path);
                                lastModified=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)));
                                size=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)));



                                String name=path.substring(path.lastIndexOf('/')+1);
                                //Log.e("PATH:",path);

                                packageInfo = pm.getPackageArchiveInfo(path, 0);
                                if(packageInfo==null)
                                {
                                    Log.e("missing",name+"--"+path);
                                    continue;
                                }


                                packageInfo.applicationInfo.sourceDir = path;
                                packageInfo.applicationInfo.publicSourceDir = path;
                                // //

                                MyApp app=new MyApp();
                                boolean isSystemApp,isUpdatedSystemApp;
                                app.setAppName(name);
                                app.setPackageName(packageInfo.packageName);
                                app.setVersionName(packageInfo.versionName);
                                app.setVersionCode(packageInfo.versionCode);
                                //app.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                                app.setSizeOfApk(size);
                                app.setLastModified(lastModified);
                                app.setApkPath(path);
                                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                                {
                                    isSystemApp=true;
                                }
                                else
                                {
                                    isSystemApp=false;
                                }

                                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
                                {
                                    // APP WAS INSTALL AS AN UPDATE TO A BUILD-IN SYSTEM APP
                                    isUpdatedSystemApp=true;
                                }
                                else
                                {
                                    isUpdatedSystemApp=false;
                                }
                                app.setSystemApp(isSystemApp);
                                app.setSystemAppUpdated(isUpdatedSystemApp);

                                if(app.getAppName().startsWith(".") && !variablesCache.showHidden)
                                {
                                    Log.e("ignoring hidden",app.getAppName()+"--");
                                    continue;
                                }

                                myFilesList.add(getMyFileFromMyApp(app));
                                Log.e("APPLICATION:",app.getAppName()+"---"+isSystemApp+"--"+isUpdatedSystemApp);

                            }
                            Log.e("MEDIA STORE","HAS TOTAL FILES:"+it);
                            cursor.close();
                        }
                    }
                    sort();
                    return "all well";
                }

                private String getApksDrive() throws Exception
                {
                    com.google.api.services.drive.Drive.Files.List request=null;
                    request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,thumbnailLink,quotaBytesUsed,modifiedTime,parents),nextPageToken");
                    List<com.google.api.services.drive.model.File> files = new ArrayList<>();
                    do
                    {
                        FileList filelist = request.execute();
                        files.addAll(filelist.getFiles());
                        request.setPageToken(filelist.getNextPageToken());
                    }
                    while (request.getPageToken() != null && request.getPageToken().length() > 0);


                    for(com.google.api.services.drive.model.File file:files)
                    {
                        if(threadId!=appManagerCache.threadFetchId)
                        {
                            break;
                        }

                        if(!file.getName().toLowerCase().endsWith(".apk"))
                        {
                            continue;
                        }

                        if(file.getParents()==null || file.getParents().size()==0)
                            continue;

                        String path=file.getId();
                        String name=file.getName();
                        //String thumbUrl=file.getThumbnailLink()+ "@#$" + file.getId();
                        long lastModified;
                        try
                        {
                            lastModified=file.getModifiedTime().getValue();
                        }
                        catch (Exception e)
                        {
                            lastModified=0;
                        }
                        long size=file.getQuotaBytesUsed();

                        MyApp app=new MyApp();
                        app.setAppName(name);
                        app.setPackageName(null);
                        app.setVersionName(null);
                        app.setVersionCode(0);
                        app.setSizeOfApk(size);
                        app.setLastModified(lastModified);
                        app.setApkPath(path);
                        app.setSystemApp(false);
                        app.setSystemAppUpdated(false);

                        if(app.getAppName().startsWith(".") && !variablesCache.showHidden)
                        {
                            continue;
                        }

                        myFilesList.add(getMyFileFromMyApp(app));
                        Log.e("APPLICATION:",app.getAppName()+"---"+false+"--"+false);
                    }

                    sort();
                    return "all well";
                }

                private String getApksDropBox() throws Exception
                {
                    ExtensionUtil extensionUtil=new ExtensionUtil();
                    List<com.dropbox.core.v2.files.Metadata> list = new ArrayList<>();
                    ListFolderResult result = DropBoxConnection.mDbxClient.files().listFolderBuilder("").withRecursive(true).start();
                    while (true)
                    {
                        for (Metadata metadata : result.getEntries())
                        {
                            list.add(metadata);
                        }
                        if (!result.getHasMore())
                        {
                            break;
                        }
                        result = DropBoxConnection.mDbxClient.files().listFolderContinue(result.getCursor());
                    }
                    for (Metadata file : list)
                    {
                        if(threadId!=appManagerCache.threadFetchId)
                        {
                            break;
                        }

                        if(file instanceof FileMetadata)
                        {
                            String name=file.getName();

                            if(!name.toLowerCase().endsWith(".apk"))
                            {
                                continue;
                            }

                            String path=file.getPathDisplay();
                            //String id=((FileMetadata)file).getId();
                            //String thumbUrl=file.getPathDisplay() + "@#$" + id;
                            long lastModified=((FileMetadata)file).getClientModified().getTime();
                            long size=((FileMetadata) file).getSize();


                            MyApp app=new MyApp();
                            app.setAppName(name);
                            app.setPackageName(null);
                            app.setVersionName(null);
                            app.setVersionCode(0);
                            app.setSizeOfApk(size);
                            app.setLastModified(lastModified);
                            app.setApkPath(path);
                            app.setSystemApp(false);
                            app.setSystemAppUpdated(false);

                            if(app.getAppName().startsWith(".") && !variablesCache.showHidden)
                            {
                                continue;
                            }

                            myFilesList.add(getMyFileFromMyApp(app));
                            Log.e("APPLICATION:",app.getAppName()+"---"+false+"--"+false);

                        }
                    }
                    sort();
                    return "all well";

                }


                @Override
                protected void onPostExecute(String x)
                {
                    Log.e(TAG,"onPostExecute"+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    loading.setVisibility(View.GONE);

                    if(x==null)
                    {
                        isFolderOk=false;
                        mainActivityObject.showHideButtons(pageIndex);
                        myUi.showArtLayout(R.mipmap.pick_nose,"READ ACCESS DENIED",true);
                        return;
                    }

                    isFolderOk=true;
                    mainActivityObject.showHideButtons(pageIndex);


                    if(myFilesList.size()==0)
                    {
                        myUi.showArtLayout(R.mipmap.empty_folder,"EMPTY",false);
                    }
                    else
                    {
                        //Setting the adapters
                        try
                        {
                            whatToSearch=null;
                            setUpGrid();
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG,"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxSUPER EXCEPTION xxxxxxxxxxxxxxxxxxxxxx");
                        }
                    }
                }

            }

            MyAsyncTask myAsyncTask=new MyAsyncTask(appManagerCache.threadFetchId);
            myAsyncTask.execute();
        }

        private MyFile getMyFileFromMyApp(MyApp app)
        {
            MyFile myFile=new MyFile();
            myFile.setChecked(false);
            myFile.setSymLink(false);
            myFile.setThumbUrl(app.getApkPath());
            myFile.setName(app.getAppName());
            myFile.setPath(app.getApkPath());
            myFile.setFavourite(favouritesCache.favouritePaths.contains(app.getApkPath()));
            myFile.setFileId(null);
            myFile.setLastModified(app.getLastModified());

            long size=app.getSizeOfApk();
            myFile.setFolder(false);
            myFile.setSizeLong(size);
            myFile.setSize(helpingBot.sizeinwords(size));

            myFile.setMyApp(app);

            return myFile;
        }

    }


    public void reloadPager()
    {
        appManagerCache.threadFetchId++;
        appManager newFragment=new appManager();
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
            thumbNailsMod=new ThumbNailsMod(1,mainActivityObject);
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


    private void setUpGrid()
    {
        thumbNailManager.initialiseThumbRunner();

        Log.e("total apps:",referenceList.size()+"--");
        gridview.setVisibility(View.VISIBLE);
        gridViewAdapter=new GridViewAdapter(mainActivityObject,referenceList,appManagerCache.storageId,getFragmentManager().findFragmentById(rootFrameId),modValue,6,0);
        gridview.setAdapter(gridViewAdapter);
        gridview.setSelection(thisPage.getCurrentIndex());
        ArrayList<LinearLayout> linearLayoutArrayList=new ArrayList<>();
        linearLayoutArrayList.add(layer1);
        linearLayoutArrayList.add(layer2);
        clickManager=new ClickManager(mainActivityObject,false,6,"App Manager",pageIndex,appManagerCache.storageId,getFragmentManager().findFragmentById(rootFrameId),linearLayoutArrayList,referenceList,null,gridview,gridViewAdapter);
        clickManager.longClickedGrid();

    }

    public void showDialog(int position)
    {
        final MyApp myApp=referenceList.get(position).getMyApp();


        final BottomSheetDialog dialog=new BottomSheetDialog(mainActivityObject);
        View view=mainActivityObject.getLayoutInflater().inflate(R.layout.layoutof_dialog12,null);
        dialog.setContentView(view);
        ImageView icon=(ImageView) dialog.findViewById(R.id.dialog12_icon);
        TextView name=(TextView) dialog.findViewById(R.id.dialog12_name);

        TextView version=(TextView) dialog.findViewById(R.id.dialog12_version);
        TextView size=(TextView) dialog.findViewById(R.id.dialog12_size);
        TextView packageName=(TextView) dialog.findViewById(R.id.dialog12_package_name);
        TextView isSystem=(TextView) dialog.findViewById(R.id.dialog12_is_systemApp);
        TextView lastModified=(TextView) dialog.findViewById(R.id.dialog12_lastModified);
        TextView path=(TextView) dialog.findViewById(R.id.dialog12_path);


        Button btn1=(Button) dialog.findViewById(R.id.dialog12_button1);
        if(appManagerCache.whatToDo==3)
        {
            btn1.setText("INSTALL");
        }
        else
        {
            btn1.setText("OPEN");
        }
        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(appManagerCache.whatToDo==3)
                {
                    appManagerUtils.installFromIntent(myApp.getApkPath());
                   //install
                    //File file=new File(myApp.getApkPath());
                    //mainActivityObject.returnFileOpenerObject(file).open();
                }
                else
                {
                    //open
                    appManagerUtils.openApp(myApp.getPackageName());
                }
            }
        });

        Button btn9=(Button) dialog.findViewById(R.id.dialog12_button9);
        if(appManagerCache.whatToDo==3)
        {
            btn9.setText("(#)INSTALL IN BACKGROUND ");
            btn9.setVisibility(View.VISIBLE);
        }
        else
        {
            btn9.setVisibility(View.GONE);
        }
        btn9.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(appManagerCache.whatToDo==3)
                {
                    //install IN BACKGROUND
                    if(SuperUser.hasUserEnabledSU)
                    {
                        SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
                        {
                            @Override
                            public void yesClicked()
                            {
                                appManagerUtils.installAsSuperUser(myApp.getPackageName(),myApp.getApkPath(),true,1);
                            }
                            @Override
                            public void noClicked()
                            {}
                        };
                        simpleYesNoDialog.showDialog(mainActivityObject,"IMPORTANT","Performing this operation may harm your device.Please proceed only if you are well aware of what you are doing.","I Agree","Cancel");
                    }
                    else
                    {
                        Toast.makeText(mainActivityObject, "Turn On Root Access to continue", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });





        Button btn2=(Button) dialog.findViewById(R.id.dialog12_button2);
        if(appManagerCache.whatToDo==3)
        {
            btn2.setText("(#)INSTALL AS SYSTEM APP");
        }
        else
        {
            btn2.setText("OPEN IN SETTINGS");
        }
        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(appManagerCache.whatToDo==3)
                {
                   //INSTALL AS SYSTEM APP
                    if(SuperUser.hasUserEnabledSU)
                    {
                        SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
                        {
                            @Override
                            public void yesClicked()
                            {
                                appManagerUtils.installAsSuperUser(myApp.getPackageName(),myApp.getApkPath(),true,3);
                            }
                            @Override
                            public void noClicked()
                            {}
                        };
                        simpleYesNoDialog.showDialog(mainActivityObject,"IMPORTANT","Performing this operation may harm your device.Please proceed only if you are well aware of what you are doing.","I Agree","Cancel");
                    }
                    else
                    {
                        Toast.makeText(mainActivityObject, "Turn On Root Access to continue", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //OPEN IN SETTINGS
                    appManagerUtils.openAppInSettings(myApp.getPackageName());
                }
            }
        });


        Button btn3=(Button) dialog.findViewById(R.id.dialog12_button3);
        btn3.setText("OPEN IN PLAY STORE");
        btn3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                appManagerUtils.openAppInStore(myApp.getPackageName());
            }
        });


        Button btn4=(Button) dialog.findViewById(R.id.dialog12_button4);
        btn4.setText("BACKUP APK");
        btn4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ArrayList<MyApp> myApps=new ArrayList<>();
                myApps.add(myApp);
                appManagerUtils.backupApk(myApps);
            }
        });

        Button btn5=(Button) dialog.findViewById(R.id.dialog12_button5);
        btn5.setText("SHARE APK");
        btn5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String apkFullName=myApp.getAppName();
                if(!apkFullName.endsWith(".apk"))
                {
                    apkFullName+=".apk";
                }
                appManagerUtils.shareOneApk(apkFullName,myApp.getApkPath());
            }
        });



        Button btn6=(Button) dialog.findViewById(R.id.dialog12_button6);
        if(appManagerCache.whatToDo==3)
        {
            btn6.setVisibility(View.GONE);
        }
        else
        {
            if(myApp.isSystemApp())
            {
                btn6.setText("(#)MAKE USER APP");
            }
            else
            {
                btn6.setText("(#)MAKE SYSTEM APP");
            }
        }
        btn6.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(SuperUser.hasUserEnabledSU)
                {
                    SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
                    {
                        @Override
                        public void yesClicked()
                        {
                            appManagerUtils.installAsSuperUser(myApp.getPackageName(),myApp.getApkPath(),true,myApp.isSystemApp()?2:3);
                        }
                        @Override
                        public void noClicked()
                        {}
                    };
                    simpleYesNoDialog.showDialog(mainActivityObject,"IMPORTANT","Performing this operation may harm your device.Please proceed only if you are well aware of what you are doing.","I Agree","Cancel");
                }
                else
                {
                    Toast.makeText(mainActivityObject, "Turn On Root Access to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Button btn7=(Button) dialog.findViewById(R.id.dialog12_button7);
        if(myApp.isSystemApp())
        {
            btn7.setText("(#)UNINSTALL");
        }
        else
        {
            btn7.setText("UNINSTALL");
        }

        if(appManagerCache.whatToDo==3)
            btn7.setVisibility(View.GONE);

        btn7.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(myApp.isSystemApp())
                {
                    if(SuperUser.hasUserEnabledSU)
                    {
                        SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
                        {
                            @Override
                            public void yesClicked()
                            {
                                appManagerUtils.unInstallAsSuperUser(myApp.getPackageName(),myApp.getApkPath(),true);
                            }
                            @Override
                            public void noClicked()
                            {}
                        };
                        simpleYesNoDialog.showDialog(mainActivityObject,"IMPORTANT","Performing this operation may harm your device.Please proceed only if you are well aware of what you are doing.","I Agree","Cancel");
                    }
                    else
                    {
                        Toast.makeText(mainActivityObject, "Turn On Root Access to continue", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    appManagerUtils.unInstallFromIntent(myApp.getPackageName());
                }
            }
        });



        Button btn8=(Button) dialog.findViewById(R.id.dialog12_button8);
        if(appManagerCache.whatToDo==3)
        {
            btn8.setVisibility(View.GONE);
        }
        else
        {
            if(myApp.isSystemApp())
            {
                if(myApp.isSystemAppUpdated())
                {
                    btn8.setText("UNINSTALL UPDATE");
                    btn8.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn8.setVisibility(View.GONE);
                }
            }
            else
            {
                btn8.setText("(#)UNINSTALL IN BACKGROUND");
                btn8.setVisibility(View.VISIBLE);
            }
        }

        btn8.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(myApp.isSystemApp() && myApp.isSystemAppUpdated())
                {
                    appManagerUtils.unInstallFromIntent(myApp.getPackageName());
                }
                if(!myApp.isSystemApp())
                {
                    //unInstall IN BACKGROUND
                    if(SuperUser.hasUserEnabledSU)
                    {
                        SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
                        {
                            @Override
                            public void yesClicked()
                            {
                                appManagerUtils.unInstallAsSuperUser(myApp.getPackageName(),myApp.getApkPath(),true);
                            }
                            @Override
                            public void noClicked()
                            {}
                        };
                        simpleYesNoDialog.showDialog(mainActivityObject,"IMPORTANT","Performing this operation may harm your device.Please proceed only if you are well aware of what you are doing.","I Agree","Cancel");
                    }
                    else
                    {
                        Toast.makeText(mainActivityObject, "Turn On Root Access to continue", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        ThumbNailsMod thumbNailsModx;//like in adapter
        thumbNailsModx=new ThumbNailsMod(getFragmentManager().findFragmentById(rootFrameId),6);
        switch (modValue)
        {
            case 0:
                thumbNailsModx.mod0(icon,myApp.getApkPath(),false,4,"abc.apk");
                break;
            case 1:
                thumbNailsModx.mod1(icon,myApp.getApkPath(),false,4,"abc.apk");
                break;
            case 2:
                thumbNailsModx.mod2(icon,myApp.getApkPath(),false,4,"abc.apk");
                break;
        }

        //icon.setImageDrawable(myApp.getIcon());

        version.setText(myApp.getVersionName()+" ( "+myApp.getVersionCode() +" )");
        name.setText(myApp.getAppName());
        size.setText(helpingBot.sizeinwords(myApp.getSizeOfApk()));
        packageName.setText(myApp.getPackageName());
        isSystem.setText(myApp.isSystemApp()?"Yes":"No");
        if(appManagerCache.whatToDo==3)
        {
            LinearLayout linearLayout=(LinearLayout) dialog.findViewById(R.id.dialog12_is_systemApp_layout);
            linearLayout.setVisibility(View.GONE);
        }
        Date date=new Date(myApp.getLastModified());
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        lastModified.setText(dateFormat.format(date));
        path.setText(myApp.getApkPath());



        dialog.show();

    }

    private boolean isReady()
    {
        return isFolderOk;
    }


    private void notifyAdapter()
    {
        gridViewAdapter.notifyDataSetChanged();
    }


    public void backPressed(Page page)
    {
        HelpingBot.hideKeyboard(mainActivityObject);

        searchThreadId++;//DOESNT MATTER

        if(whatToSearch==null)
        {
            mainActivityObject.removePage(page);
            mainActivityObject.setUpViewPager();
        }
        else
        {
            whatToSearch=null;
            reloadPager();
        }
    }


    private void sort()
    {
        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortStorageBy);
        sortingMachine.sortMyFile(referenceList);
    }


    @Override
    public void onDestroy()             //OK
    {
        if(thumbNail_handler!=null)
            thumbNail_handler.removeCallbacks(thumbNail_runnable);
        thumbNailManager.clearThumbCache();

        super.onDestroy();
        Log.e(TAG,"DESTROYED"+pageIndex);

        appManagerCache.threadFetchId++;
        int pos=0;
        try
        {
            if(gridview!=null)
            {
                pos=gridview.getFirstVisiblePosition();
            }
        }
        catch (Exception e)
        {
            pos=0;
        }

        if(switchingToNewTab)
            pos=0;
        PageIndexList.set(PageIndexList.size()-1,pos);

    }


    @Override
    public void onResume()
    {
        super.onResume();
        Log.e(TAG,"RESUMED"+pageIndex);

        int size=appManagerCache.uninstallList.size();
        if(size>0)
        {
            MyApp x=appManagerCache.uninstallList.remove(0);
            appManagerUtils.unInstallFromIntent(x.getPackageName());
        }

        if(size==0)
        {
            size=appManagerCache.installList.size();
            if(size>0)
            {
                MyApp x=appManagerCache.installList.remove(0);
                appManagerUtils.installFromIntent(x.getApkPath());
            }
        }

    }

    @Override
    public void onPause()
    {
        super.onPause();
        int pos=0;
        try
        {
            if(gridview!=null)
            {
                pos=gridview.getFirstVisiblePosition();
            }
        }
        catch (Exception e)
        {
            pos=0;
        }

        if(switchingToNewTab)
            pos=0;
        PageIndexList.set(PageIndexList.size()-1,pos);
    }


    private void getInfo()
    {
        rootFrameId=((ViewGroup)getView().getParent()).getId();
        pageIndex=getPageIndexFromFrameId(rootFrameId);

        modValue=pageIndex%3;
        thisPage=MainActivity.pageList.get(pageIndex);
        PagePathList=thisPage.getPathList();
        PageIndexList=thisPage.getIndexList();

        whatToDo= appManagerCache.whatToDo;
        if(whatToDo!=3)
            appManagerCache.storageId=1;

        switch (appManagerCache.storageId)
        {
            case 1:
                storageHomePath=Physical_Storage_PATHS.get(0);
                break;
            case 2:
                storageHomePath=Physical_Storage_PATHS.get(1);
                break;
            case 3:
                storageHomePath=Physical_Storage_PATHS.get(2);
                break;
            case 4:
                storageHomePath="Google Drive";
                break;
            case 5:
                storageHomePath="DropBox";
                break;
        }
        appManagerCache.threadFetchId++;
    }


}
