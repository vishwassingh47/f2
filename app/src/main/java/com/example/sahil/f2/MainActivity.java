package com.example.sahil.f2;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.SpaceUsage;
import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.DownloadData;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.ThumbNailCache;
import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.Cache.appManagerCache;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.recycleBinCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.Page;

import com.example.sahil.f2.GokuFrags.appManager;
import com.example.sahil.f2.GokuFrags.ftpLoginPager;
import com.example.sahil.f2.GokuFrags.ftpServerPager;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment1;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment2;
import com.example.sahil.f2.GokuFrags.search_fragment;
import com.example.sahil.f2.GokuFrags.storageAnalyser;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.OperationTheater.PasteButtonDecisionMaker;
import com.example.sahil.f2.OperationTheater.PasteClipBoard;
import com.example.sahil.f2.OperationTheater.TaskManager;
import com.example.sahil.f2.Rooted.SuOperations;
import com.example.sahil.f2.UiClasses.Drawer1;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.plus.Plus;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.example.sahil.f2.Cache.superCache;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.CustomViewPager;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.FunkyAdapters.FileOpenerAdapter;
import com.example.sahil.f2.FunkyAdapters.PagerAdapter;

import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.zxing.Result;
import com.stericson.RootTools.RootTools;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;


import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getFrameIdFromPageIndex;


/**
 *
 * Created by Acer on 13-07-2017.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,ZXingScannerView.ResultHandler
{

    public static ArrayList<Page> pageList;
    FloatingActionButton fab_taskManager;
    public static ArrayList<String> Physical_Storage_PATHS;
    private StorageAccessFramework storageAccessFramework;
    public PasteButtonDecisionMaker pasteButtonDecisionMaker;
    public com.example.sahil.f2.OperationTheater.CreateNew createNew;
    public TaskManager taskManager;

    public Drawer1 drawer1;
    public FrameLayout touchBlocker;


    public boolean startLoginToDropbox=false;

    public TinyDB tinyDB;

    final HelpingBot helpingBot=new HelpingBot();


    public CustomViewPager viewPager;
    public PagerAdapter pagerAdapter;
    public FileOpenerAdapter foa;

    public static HashMap<String,Uri> SDCardUriMap;

    public  Toolbar toolbar;
    public  View actionbarView;
    public DrawerLayout drawer;
    public NavigationView navigationView1,navigationView2;

    public File [] files;


    public ArrayList <String>   nameList;

    public DeletingMachine deleteFromSDCARDforTheFirstTime=null;




    public TabLayout tabLayout;
    public int numberOfStorages=0;



    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_RESOLUTION = 1;


    private class InitialiseMainActivity
    {
        void setAndGetExternalStorageDirectories()       //OK
        {

            Physical_Storage_PATHS=new ArrayList<>();

            //checking for First Storage
            File f=Environment.getExternalStorageDirectory();//f is definately not null even when internal is not there and sd card is removed
            Log.e("STORAGE FOUND:",f.getAbsolutePath()+" "+Environment.getExternalStorageState()+"##isRemovable"+Environment.isExternalStorageRemovable()+"##ISeMULATED"+Environment.isExternalStorageEmulated());

            Physical_Storage_PATHS.add(f.getAbsolutePath());


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                File[] externalDirs = getExternalFilesDirs(null);

                for (File file : externalDirs)
                {
                    //file may be null
                    if(file!=null)
                    {
                        String path = file.getPath().split("/Android")[0];
                        File root=new File(path);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            Log.e("_--",path+"--"+Environment.getExternalStorageState(root) + Environment.isExternalStorageRemovable(root) +Environment.isExternalStorageEmulated(root)) ;
                        }
                        if(root.canRead())
                        {
                            if(!Physical_Storage_PATHS.contains(root.getAbsolutePath()) )
                            {
                                Physical_Storage_PATHS.add(root.getAbsolutePath());
                                Log.e("STORAGE FOUND:",root.getAbsolutePath()+" ");
                            }
                        }
                    }
                }
            }
            else
            {
                String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
                String s = "";
                try
                {
                    final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
                    process.waitFor();
                    final InputStream is = process.getInputStream();
                    final byte[] buffer = new byte[1024];
                    while (is.read(buffer) != -1)
                    {
                        s = s + new String(buffer);
                    }
                    is.close();

                    // parse output
                    final String[] lines = s.split("\n");
                    for (String line : lines)
                    {
                        if (!line.toLowerCase(Locale.US).contains("asec"))
                        {
                            if (line.matches(reg))
                            {
                                String[] parts = line.split(" ");
                                for (String part : parts)
                                {
                                    if (part.startsWith("/"))
                                        if (!part.toLowerCase(Locale.US).contains("vold"))
                                        {
                                            Log.e("storage found::xx",part+"--");
                                            File root=new File(part);
                                            if(root.exists() && root.canRead() )
                                            {
                                                if(!Physical_Storage_PATHS.contains(root.getAbsolutePath()))
                                                {
                                                    Physical_Storage_PATHS.add(root.getAbsolutePath());
                                                    Log.e("STORAGE FOUND:",root.getAbsolutePath()+" ");
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }


            numberOfStorages=Physical_Storage_PATHS.size();

        }

        void clearAllCache()
        {
            Log.e("cache cleared:","------------------------------------------");
            paster.clear();

            DropBoxConnection.clear();
            GoogleDriveConnection.clear();
            PasteClipBoard.clear();


            tasksCache.clear();
            recycleBinCache.clear();
            favouritesCache.clear();
            Constants.clear();
            superCache.clear();
            FtpCache.clear();


            appManagerCache.clear();
        }

        void setUpVariableCache()
        {
            SharedPreferences pref=getSharedPreferences("VariablesCache",0);


            variablesCache.listOrGrid=pref.getInt("listOrGrid",2);
            variablesCache.sortStorageBy=10;

            variablesCache.sortSearchBy=-1;
            variablesCache.sortGalleryBy=3;
            variablesCache.sortAnalyserBy=5;

        }

        void checkForPreviousTasks()
        {
            CopyData copyData;

            copyData= MyCacheData.getCopyDataFromCode(101);
            if(copyData.getTinyDbData(tinyDB))
            {
                copyData.isDownloadError=true;
                tasksCache.addTask("101");
            }

            copyData= MyCacheData.getCopyDataFromCode(102);
            if(copyData.getTinyDbData(tinyDB))
            {
                copyData.isDownloadError=true;
                tasksCache.addTask("102");
            }


            DownloadData downloadData;

            downloadData=MyCacheData.getDownloadDataFromCode(201);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("201");
            }

            downloadData=MyCacheData.getDownloadDataFromCode(202);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("202");
            }

            downloadData=MyCacheData.getDownloadDataFromCode(203);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("203");
            }

            downloadData=MyCacheData.getDownloadDataFromCode(204);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("204");
            }

            downloadData=MyCacheData.getDownloadDataFromCode(205);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("205");
            }
            downloadData=MyCacheData.getDownloadDataFromCode(206);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("206");
            }

            downloadData=MyCacheData.getDownloadDataFromCode(305);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("305");
            }
            downloadData=MyCacheData.getDownloadDataFromCode(306);
            if(downloadData.getTinyDbData(tinyDB))
            {
                downloadData.isDownloadError=true;
                tasksCache.addTask("306");
            }

            UploadData uploadData;

            uploadData=MyCacheData.getUploadDataFromCode(301);
            if(uploadData.getTinyDbData(tinyDB))
            {
                uploadData.isDownloadError=true;
                tasksCache.addTask("301");
            }

            uploadData=MyCacheData.getUploadDataFromCode(302);
            if(uploadData.getTinyDbData(tinyDB))
            {
                uploadData.isDownloadError=true;
                tasksCache.addTask("302");
            }

            uploadData=MyCacheData.getUploadDataFromCode(303);
            if(uploadData.getTinyDbData(tinyDB))
            {
                uploadData.isDownloadError=true;
                tasksCache.addTask("303");
            }

            uploadData=MyCacheData.getUploadDataFromCode(304);
            if(uploadData.getTinyDbData(tinyDB))
            {
                uploadData.isDownloadError=true;
                tasksCache.addTask("304");
            }

        }

        void checkDropBox()
        {
            SharedPreferences pref=getApplicationContext().getSharedPreferences("DropboxPref",0);
            String userKey=pref.getString("userKey",null);
            if(userKey==null)
            {
                DropBoxConnection.isDropboxAvailable=false;
            }
            else
            {
                DropBoxConnection.isDropboxAvailable=true;
                DropBoxConnection.userKey=userKey;
            }
        }

        void checkDrive()
        {
            SharedPreferences pref=getApplicationContext().getSharedPreferences("DrivePref",0);
            String ifDriveInstalled=pref.getString("userKey",null);
            if(ifDriveInstalled==null)
            {
                Log.e("drive not found:","--");
                GoogleDriveConnection.isDriveAvailable=false;
            }
            else
            {
                GoogleDriveConnection.isDriveAvailable=true;
            }

        }

        void initialAddPages()
        {
            pageList.add(new Page("Home","Home",0,R.drawable.home_icon,0));
            //pageList.add(new Page("AddNew","AddNew",0,R.mipmap.create,100));


            if(Physical_Storage_PATHS.size()>=1)
                addPage("Local1",pageList.size(),Physical_Storage_PATHS.get(0),R.drawable.sd_card,12345);

            if(Physical_Storage_PATHS.size()>=2)
                addPage("Local2",pageList.size(),Physical_Storage_PATHS.get(1),R.drawable.sd_card,12345);

            if(Physical_Storage_PATHS.size()>=3)
                addPage("Local3",pageList.size(),Physical_Storage_PATHS.get(2),R.drawable.sd_card,12345);

            if( GoogleDriveConnection.isDriveAvailable)
                addPage("GoogleDrive",pageList.size(),"root",R.mipmap.google_drive,12345);

            if( DropBoxConnection.isDropboxAvailable)
                addPage("DropBox",pageList.size(),"",R.mipmap.dropbox,12345);


        }
    }

    /**
     *
     * @param pageName
     * @param atIndex
     * @param firstPath
     * @param iconId
     * @param pageId if storageID is -5 page can be removed
     */
    public void addPage(String pageName,int atIndex,String firstPath,int iconId,int pageId)
    {

        pageList.add(null);//increasing list size

        Page newPage=new Page(pageName,firstPath,0,iconId,pageId);
        for(int i=pageList.size()-1;i>atIndex;i--)
        {
            pageList.set(i,pageList.get(i-1));
        }
        pageList.set(atIndex,newPage);

        for(int i=0;i<pageList.size();i++)
        {
            Log.e("***************",i+"name:"+pageList.get(i).getName()+"current path:"+pageList.get(i).getCurrentPath());
        }
    }

    public void removePage(Page page)
    {
        pageList.remove(page);
    }


    public void setUpViewPager()
    {
        viewPager=(CustomViewPager) findViewById(R.id.pager);

        pagerAdapter=new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.setCount(pageList.size());

        viewPager.setAdapter(pagerAdapter);
        tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        for(int i=0;i<pageList.size();i++)
        {
            tabLayout.getTabAt(i).setIcon(pageList.get(i).getIconId());
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if(state==SCROLL_STATE_IDLE)
                {
                    //int x=((ViewGroup)getParent()).getId();
                    //android.support.v4.app.Fragment page=getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.)
                    android.support.v4.app.Fragment x = pagerAdapter.getItem(viewPager.getCurrentItem());

                    //pasteButtonDecisionMaker.showHidePasteButton(-1);
                    //createNew.showHideAddButton(-1);
                    showHideButtons(-1);
                }
            }
        });

    }

    private void getFavouriteList()
    {
        favouritesCache.favouritePaths=tinyDB.getListString("Favourites");
        favouritesCache.favouriteStorageIdList=tinyDB.getListInt("FavouritesIds");
        for(int i=0;i<favouritesCache.favouritePaths.size();i++)
        {
            Log.e("!!!!!!!!!!!!!",favouritesCache.favouritePaths.get(i)+"--"+favouritesCache.favouriteStorageIdList.get(i));
        }
        Log.e("size of favourires:",favouritesCache.favouritePaths.size()+"--"+favouritesCache.favouriteStorageIdList.size());
    }


    @Override
    public void onStart()
    {
        super.onStart();
        Log.e("onsatrt start","---");
        ThumbNailCache.hardClear();
        drawer1.initialize();
        getFavouriteList();


        //ensureShell();
        //(new MyTask()).execute();
        //runShellCommand("ls -l /sbin");

        //runShellCommand("mount");
        //mountFileSystemRW("/sbin");
        //runShellCommand("mount");
        //mountFileSystemRO("/");
        //runShellCommand("mount");
        //mountFileSystemRW("/sbin");
        //runShellCommand("mount");
        //ummount("/");
        //runShellCommand("mount");
        //String command = "chmod %d \"%s\"";

        //runShellCommand("chmod -R 444 /sbin");

        //runShellCommand("ls -l /sbin");

        //runShellCommand("mount");

            // we mounted the filesystem as rw, let's mount it back to ro



        //runShellCommand("mount");

        FtpCache.ftpList=tinyDB.getListString("ftpList");
        Log.e("onsatrt end","---");

        //File file=new File("/cache/recovery");
        //String s[]=file.list();
        //for(String x:s)
        //{
          //  Log.e("STRING:",x+"--");
        //}


       // copy("/cache/magisk.log","/sdcard/acpi2");
        //parser();

        File myLsFile=getFileStreamPath("myls");
        if(!myLsFile.exists())
        {
            Toast.makeText(this, "Setting up Root commands", Toast.LENGTH_SHORT).show();
            myLsInitialiser();
        }
        else
        {
            if(!myLsFile.canExecute())
            {
               boolean x= myLsFile.setExecutable(true);
            }
        }

        /*
         File f=new File("/cache/magisk.log");
        Log.e("$$$$$",f.exists()+"--"+f.getAbsolutePath());
        f=new File("/cache/");
        File[] files=f.listFiles();
        for(File x:files)
        {
            if(!x.exists())
            {
                Log.e("$$$$$",x.getName()+"--");
            }
        }
         */

        Handler handler= new Handler();
        Runnable runnable=new Runnable()
        {
            @Override
            public void run()
            {
                StorageAccessFramework storageAccessFramework=new StorageAccessFramework(MainActivity.this);
                storageAccessFramework.showSaf(1,"9016-4EF8");
            }
        };
        //handler.postDelayed(runnable,3000);




    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG,"ONCREATE+++++++++++++++++++++++++++++++++++++++++++++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tinyDB=new TinyDB(MainActivity.this);
        pageList=new ArrayList<>();
        InitialiseMainActivity initialiseMainActivity=new InitialiseMainActivity();
        initialiseMainActivity.setAndGetExternalStorageDirectories();
        initialiseMainActivity.clearAllCache();

        initialiseMainActivity.checkDropBox();
        initialiseMainActivity.checkDrive();




        drawer=(DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView1 =(NavigationView) findViewById(R.id.nav_view1);
        navigationView2=(NavigationView) findViewById(R.id.nav_view2);

        setDrawerListener();




        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        actionbarView=getLayoutInflater().inflate(R.layout.layoutof_customactionbar, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT);
        actionbarView.setLayoutParams(params);
        toolbar.addView(actionbarView);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);


        fab_taskManager=(FloatingActionButton) findViewById(R.id.fab_task_manager);
        fab_taskManager.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawer.openDrawer(navigationView2);
            }
        });


        initialiseMainActivity.setUpVariableCache();

        initialiseMainActivity.initialAddPages();
        setUpViewPager();
        initialiseMainActivity.checkForPreviousTasks();



        onNewIntent(getIntent());
        //should be created only once
        pasteButtonDecisionMaker=new PasteButtonDecisionMaker(MainActivity.this);
        createNew=new com.example.sahil.f2.OperationTheater.CreateNew(MainActivity.this);
        taskManager=new TaskManager(MainActivity.this);
        storageAccessFramework=new StorageAccessFramework(MainActivity.this);
        storageAccessFramework.refreshSDCardUri();
        drawer1=new Drawer1(MainActivity.this,drawer);


        touchBlocker=(FrameLayout) findViewById(R.id.touch_blocker);
        touchBlocker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if(view.getAlpha()!=0)
                {
                    Log.e("yes it is visible","--hiding it now");
                    view.setAlpha(0);
                }
                Log.e("clicked","______________________");
                if(pasteButtonDecisionMaker.pasteMenu.isOpened())
                {
                    Log.e("consuming this click","click blocked +++++++++++++++");
                    pasteButtonDecisionMaker.closePasteMenu();
                    return true;
                }
                return false;
            }
        });

        Log.e("oncreate end","---");

        showHideButtons(-1);

    }


    private void setDrawerListener()
    {
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawer,R.string.app_name,R.string.app_name)
        {
            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                if(view==navigationView2)
                {
                    taskManager.stopRunner();
                    showHideButtons(-1);
                }
            }

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);

                if(drawerView==navigationView2)
                {
                    taskManager.showTaskManager();
                }
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
    }

    private void createShortCutToDesktop()
    {
        Intent shortCut=new Intent(MainActivity.this,MainActivity.class);
        shortCut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortCut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent=new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortCut);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"ALLAH U AKBAR");
        intent.putExtra("duplicate",false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(MainActivity.this,R.drawable.favourite));
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(intent);


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INSTALL_SHORTCUT}, 555);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INSTALL_SHORTCUT) != PackageManager.PERMISSION_GRANTED)
        {
            Log.e("permission not granted","--");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INSTALL_SHORTCUT))
            {
                Log.e("showing","--");
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INSTALL_SHORTCUT}, 555);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
            else
            {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INSTALL_SHORTCUT}, 555);
            }

        }
        else
        {
            //You already have the permission, just go ahead.
            shortCut=new Intent(MainActivity.this,MainActivity.class);
            shortCut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortCut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent=new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortCut);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"ALLAH U AKBAR");
            intent.putExtra("duplicate",false);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(MainActivity.this,R.drawable.favourite));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 8080: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e("ONRESUME START","---");
        if(startLoginToDropbox)
        {
            startLoginToDropbox=false;

            String accessToken= com.dropbox.core.android.Auth.getOAuth2Token();
            if(accessToken!=null)
            {
                try
                {
                    SharedPreferences pref=getApplicationContext().getSharedPreferences("DropboxPref",0);
                    SharedPreferences.Editor editor=pref.edit();
                    editor.putString("userKey",accessToken);
                    editor.apply();
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "Failed to save userKey.....", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            else
            {
                Toast.makeText(this, "Authentication Failed.....", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Logined Successfully.....", Toast.LENGTH_LONG).show();
            DropBoxConnection.clear();
            DropBoxConnection.isDropboxAvailable=true;
            DropBoxConnection.userKey=accessToken;

            addPage("DropBox",pageList.size(),"",R.mipmap.dropbox,12345);
            setUpViewPager();
        }
    }

    public static class paster
    {
        public static int fromStorageCode=0;
        public static String fromParentPath;
        public static String fromStoragePath;

        public static int toStorageCode=0;
        public static String toParentPath;
        public static String toStoragePath;;

        public static ArrayList<String> pastelistpath=new ArrayList<>();
        public static ArrayList<String> pastelistname=new ArrayList<>();
        public static ArrayList<String> pastesizelist=new ArrayList<>();
        public static ArrayList<Long> pastesizelistLong=new ArrayList<>();
        public static ArrayList<Boolean> pasteIsFolder=new ArrayList<>();
        public static int what=0;


        public static int timeinsec=1;


        public static ArrayList<String> ipConnectedList=new ArrayList<>();

        public static void clear()
        {
            fromStorageCode=0;
            fromParentPath="";
            fromStoragePath="";
            toStorageCode=0;
            toParentPath="";
            toStoragePath="";

            pastelistpath.clear();
            pastelistname.clear();
            pastesizelist.clear();
            pastesizelistLong.clear();
            pasteIsFolder.clear();

            what=0;
            timeinsec=1;

            ipConnectedList.clear();

        }

    }




    public void deleteInSDcardforFirstTime(ArrayList<String> deletePaths,String parent) //not ok
    {
        deleteFromSDCARDforTheFirstTime=new DeletingMachine(MainActivity.this,deletePaths,parent,2);
    }

    @Override
    public void onActivityResult(int reqcode,int resultcode,Intent resultIntent)
    {

        if(reqcode==42 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
        {
            storageAccessFramework.postSafIntent(resultcode,resultIntent);
        }
        else
        {
            //this is called after that dialog bar is cancelled or some account is chosen i.e after this intent has finished
            super.onActivityResult(reqcode, resultcode, resultIntent);

            if(resultcode==0)
            {
                Toast.makeText(this, "Hey Dev,Please Refresh Authentication", Toast.LENGTH_LONG).show();

            }

            Log.e("onActivityResult:","=="+resultcode);
            if(reqcode==REQUEST_CODE_RESOLUTION && resultcode==RESULT_OK)
            {
                GoogleDriveConnection.mGoogleApiClient.connect();
            }

        }
    }


    public class DeletingMachine            //not ok
    {
        private Dialog dialog1,dialog2,dialog3;

        //delete dialog for single items
        private TextView delete1_name,delete1_type,delete1_typemore,delete1_path,delete1_size;
        private Button delete1_yes,delete1_no;
        private ImageView delete1_logo,delete1_path_logo;

        //delete dialog for mutiple items
        private TextView delete2_files,delete2_folders,delete2_subfiles,delete2_subfolders,delete2_path,delete2_size;
        private Button delete2_yes,delete2_no;
        private ImageView delete2_path_logo;

        //deleting started
        TextView delete3_items,delete3_size,delete3_currentfile,delete3_progress;
        ImageView delete3_logo;
        long totalSize=0;
        int files=0,folders=0,subfiles=0,subfolders=0;
        int commonFolder=0;

        int counter=0;

        public Context context;

        public HelpingBot helpingBot;

        public  ArrayList<String> deletePaths;
        public String parentpath="";



        public boolean shouldDelete_Yes=false,shouldDelete_No=true;

        public int storageId;
        int currentfile=0;
        boolean isSizeUnknown=false;
        Runnable runnable1=null,runnable2=null,runnable3=null;
        private boolean thread2started=false,thread4started=false,deleted=false;
        private boolean isThreadToCountFiles1Finished=false,isThreadToCountFiles2Finished=false;
        private boolean isThreadToCountFiles1Started=false,isThreadToCountFiles2Started=false;

        private boolean isThreadToDeleteStarted=false,isThreadToDeleteFinished=false;

        private boolean isDropBoxError=false;
        int dropBoxErrorCode=0;

        public boolean isdeleteError=false;
        int deleteErrorCode=0;


        public DeletingMachine(Context context,ArrayList<String> deletePaths,String parent,int storageId)
        {

            helpingBot=new HelpingBot();

            this.context=context;
            this.storageId=storageId;
            this.parentpath=parent;
            this.deletePaths=new ArrayList<>();

            for(int i=0;i<deletePaths.size();i++)
            {
                this.deletePaths.add(i,deletePaths.get(i));
            }

            dialog1=new Dialog(context);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.layoutof_deletedialog1);
            //dialog1.setTitle("do u wanna delete single ");

            dialog2=new Dialog(context);
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog2.setContentView(R.layout.layoutof_deletedialog2);
            //dialog2.setTitle("do u wanna delete multiple");

            dialog3=new Dialog(context);
            dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog3.setContentView(R.layout.layoutof_deletedialog3);
            //dialog3.setTitle("deleting");


            delete1_name=(TextView)dialog1.findViewById(R.id.delete1_name);
            delete1_type=(TextView)dialog1.findViewById(R.id.delete1_type);
            delete1_typemore=(TextView)dialog1.findViewById(R.id.delete1_typemore);
            delete1_path=(TextView)dialog1.findViewById(R.id.delete1_path);
            delete1_size=(TextView)dialog1.findViewById(R.id.delete1_size);

            delete1_logo=(ImageView)dialog1.findViewById(R.id.delete1_logo);
            delete1_path_logo=(ImageView)dialog1.findViewById(R.id.delete1_path_logo);

            delete1_yes=(Button) dialog1.findViewById(R.id.delete1_yes);
            delete1_yes.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e("button clicked",v.toString());
                    if(shouldDelete_Yes)//MAKING SURE THAT IF THE BUTTON PRESSED MULTIPLE TIMES ,ONLY ONE TIME CALLED
                    {
                        shouldDelete_Yes=false;
                        Yes();
                        Log.e("button presssEDDD","......");
                    }

                }
            });

            delete1_no=(Button) dialog1.findViewById(R.id.delete1_no);
            delete1_no.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e("button clicked",v.toString());
                    if(shouldDelete_No)//MAKING SURE THAT IF THE BUTTON PRESSED MULTIPLE TIMES ,ONLY ONE TIME CALLED
                    {
                        shouldDelete_No=false;
                        No();
                        Log.e("button presssEDDD","......");
                    }
                }
            });


            delete2_files=(TextView)dialog2.findViewById(R.id.delete2_files);
            delete2_folders=(TextView)dialog2.findViewById(R.id.delete2_folders);
            delete2_subfiles=(TextView)dialog2.findViewById(R.id.delete2_subfiles);
            delete2_subfolders=(TextView)dialog2.findViewById(R.id.delete2_subfolders);
            delete2_path=(TextView)dialog2.findViewById(R.id.delete2_path);
            delete2_size=(TextView)dialog2.findViewById(R.id.delete2_size);

            delete2_path_logo=(ImageView)dialog2.findViewById(R.id.delete2_path_logo);

            delete2_yes=(Button) dialog2.findViewById(R.id.delete2_yes);
            delete2_yes.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e("button clicked",v.toString());
                    if(shouldDelete_Yes)//MAKING SURE THAT IF THE BUTTON PRESSED MULTIPLE TIMES ,ONLY ONE TIME CALLED
                    {
                        shouldDelete_Yes=false;
                        Yes();
                        Log.e("button presssEDDD","......");
                    }

                }
            });

            delete2_no=(Button) dialog2.findViewById(R.id.delete2_no);
            delete2_no.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e("button clicked",v.toString());
                    if(shouldDelete_No)//MAKING SURE THAT IF THE BUTTON PRESSED MULTIPLE TIMES ,ONLY ONE TIME CALLED
                    {
                        shouldDelete_No=false;
                        No();
                        Log.e("button presssEDDD","......");
                    }
                }
            });


            delete3_items=(TextView)dialog3.findViewById(R.id.delete3_items);
            delete3_size=(TextView)dialog3.findViewById(R.id.delete3_size);
            delete3_currentfile=(TextView)dialog3.findViewById(R.id.delete3_currentfile);
            delete3_progress=(TextView)dialog3.findViewById(R.id.delete3_progress);

            delete3_logo=(ImageView)dialog3.findViewById(R.id.delete3_logo);

            totalSize=0;
            files=0;
            folders=0;
            subfiles=0;
            subfolders=0;

        }



        public void prepareForDeletion()
        {

        }


        public void Yes()
        {

        }

        public void No ()
        {

        }

        public boolean deleteRecursive(File fileOrDirectory)
        {

            if (fileOrDirectory.isDirectory())
            {
                for (File child : fileOrDirectory.listFiles())
                {
                    boolean state=deleteRecursive(child);
                    if(!state)
                    {
                        return false;
                    }
                }
            }
            if(fileOrDirectory.delete())
            {
                return true;
            }
            else
            {
                //deletion failed
                return false;

            }
        }

        public boolean deleteDocumentFile(File fileOrDirectory)
        {
            DocumentFile df=fileToDocumentFileConverter(fileOrDirectory.getPath());
            if(df==null)
            {
                return false;
            }
            else
            {


                return df.delete();
            }

        }

        public DocumentFile fileToDocumentFileConverter(String path)
        {

            return  null;
        }

        public void RecursiveSize(File fileOrDirectory)
        {

            if (fileOrDirectory.isDirectory())
            {
                if(commonFolder==1)
                {
                    commonFolder=0;
                }
                else
                {
                    subfolders++;
                }

                for (File child : fileOrDirectory.listFiles())
                {
                    RecursiveSize(child);

                }
            }
            else
            {
                subfiles++;

                totalSize+=fileOrDirectory.length();
            }

        }


    }


    void fun()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        PackageManager manager = getPackageManager();
        File file=new File("/sdcard/boob.pdf");



        String extension = MimeTypeMap.getFileExtensionFromUrl("/sdcard/boob.pdf");
        String type=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Log.e("extension is:",extension+"--");

        intent.setDataAndType(Uri.fromFile(file),type);
        List<ResolveInfo> infos = manager.queryIntentActivities (intent, 0);

        for (int i = 0; i < infos.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent

            ApplicationInfo applicationInfo=infos.get(i).activityInfo.applicationInfo;
            Log.e("file is ",i+"--");
            Log.e("package ",applicationInfo.packageName+"--");
            Log.e("activity label",infos.get(i).activityInfo.loadLabel(manager)+"--");
            Log.e("activity name:",infos.get(i).activityInfo.name+"--");


        }


    }

    public class FileOpener     //not ok
    {
        private File fileToOpen;
        private String extension;
        private String MIME;

        boolean setAsDefault=false;
        FileOpener(File f)
        {
            fileToOpen=f;
            extension=MimeTypeMap.getFileExtensionFromUrl(fileToOpen.getAbsolutePath());
            if(extension!=null)
            {
                extension=extension.toLowerCase();
            }

            MIME=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if(MIME==null)
            {
                extension=helpingBot.getCustomFileExtension(fileToOpen);
                if(extension!=null)
                {
                    extension=extension.toLowerCase();
                }
                MIME=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Log.e("helper extension",extension+"--");
            }


            Log.e("extension is:",extension+"--");
            Log.e("MIME is:",MIME+"--");

        }

        public void open()
        {
            Log.e("open",",,,");

            if(MIME!=null)      //file is not unknown
            {
                //checking if file exist in shared preference or not
                SharedPreferences pref=getApplicationContext().getSharedPreferences("FileOpenerPref",0);
                String packageName=pref.getString(extension+"1",null);
                if(packageName!=null)
                {
                    //file exist in pref
                    String className=pref.getString(extension+"2",null);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName(packageName,className);

                    intent.setDataAndType(Uri.fromFile(fileToOpen),MIME);
                    try
                    {
                        startActivity(intent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        Toast.makeText(MainActivity.this, "Activity Not Found", Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor=pref.edit();
                        editor.remove(extension+"1");
                        editor.remove(extension+"2");
                        editor.apply();
                        openNew();
                    }
                }
                else
                {
                    //pref is empty
                    openNew();
                }

            }
            else    //file is unknown
            {
                openUnknown();
            }
        }

        public void openNew()
        {

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layoutof_fileopener1);
            //dialog.setTitle("open new");


            final ListView listView=(ListView)dialog.findViewById(R.id.file_opener1_list);
            final CheckBox checkBox=(CheckBox)dialog.findViewById(R.id.file_opener1_checkbox);
            final TextView textextra=(TextView)dialog.findViewById(R.id.file_opener1_extra);

            ArrayList<String> nameList=new ArrayList<>();
            ArrayList<Drawable> icons=new ArrayList<>();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            PackageManager manager = getPackageManager();


            intent.setDataAndType(Uri.fromFile(fileToOpen),MIME);

            final List<ResolveInfo> infos = manager.queryIntentActivities (intent, 0);
            for (int i = 0; i < infos.size(); i++)
            {
                // Extract the label, append it, and repackage it in a LabeledIntent

                ApplicationInfo applicationInfo=infos.get(i).activityInfo.applicationInfo;
                nameList.add(infos.get(i).activityInfo.loadLabel(manager)+"");
                icons.add(infos.get(i).activityInfo.loadIcon(manager));

                Log.e("installled apps:",infos.get(i).activityInfo.packageName+"--");
            }

            if(nameList.size()==0)
            {
                openUnknown();
                //call unknown
                return;
            }


            foa=new FileOpenerAdapter(MainActivity.this,0,nameList,icons);
            listView.setAdapter(foa);

            if(foa.getCount()>5)
            {
                View item = foa.getView(0, null, listView);
                item.measure(0, 0);
                Log.e("height",item.getMeasuredHeight()+"");
                listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) (5.5 * item.getMeasuredHeight())));
            }



            final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView <?> listv,View v,int pos,long id)
                {
                    String className=infos.get(pos).activityInfo.name;
                    String packageName=infos.get(pos).activityInfo.packageName;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName(packageName,className);

                    if(setAsDefault)
                    {
                        SharedPreferences pref=getApplicationContext().getSharedPreferences("FileOpenerPref",0);
                        SharedPreferences.Editor editor=pref.edit();
                        editor.putString(extension+"1",packageName);
                        editor.putString(extension+"2",className);
                        editor.apply();
                    }


                    intent.setDataAndType(Uri.fromFile(fileToOpen),MIME);
                    dialog.cancel();
                    startActivity(intent);
                }
            };
            listView.setOnItemClickListener(listener);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                                {
                                                   @Override
                                                   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
                                                   {
                                                       if(isChecked)
                                                       {
                                                           setAsDefault=true;
                                                           textextra.setVisibility(View.VISIBLE);
                                                       }
                                                       else
                                                       {
                                                           setAsDefault=false;
                                                           textextra.setVisibility(View.GONE);
                                                       }
                                                   }
                                               }
            );


            dialog.show();
        }

        public void openUnknown()
        {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layoutof_fileopener2);
            //dialog.setTitle("open unknown");

            final ListView listView=(ListView)dialog.findViewById(R.id.file_opener2_list);

            ArrayList<String> nameList=new ArrayList<>();
            ArrayList<Drawable> icons=new ArrayList<>();

            nameList.add("Image");
            icons.add(getResources().getDrawable(getResources().getIdentifier("@mipmap/image",null,getPackageName())));
            nameList.add("Audio");
            icons.add(getResources().getDrawable(getResources().getIdentifier("@mipmap/music",null,getPackageName())));
            nameList.add("Video");
            icons.add(getResources().getDrawable(getResources().getIdentifier("@mipmap/video",null,getPackageName())));
            nameList.add("Text");
            icons.add(getResources().getDrawable(getResources().getIdentifier("@mipmap/text",null,getPackageName())));
            nameList.add("Other");
            icons.add(getResources().getDrawable(getResources().getIdentifier("@mipmap/c",null,getPackageName())));

            foa=new FileOpenerAdapter(MainActivity.this,0,nameList,icons);
            listView.setAdapter(foa);

            final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView <?> listv,View v,int pos,long id)
                {

                    String mimiTemp="*/*";
                    switch(pos)
                    {
                        case 0:
                            mimiTemp="image/*";
                            break;
                        case 1:
                            mimiTemp="audio/*";
                            break;
                        case 2:
                            mimiTemp="video/*";
                            break;
                        case 3:
                            mimiTemp="text/*";
                            break;
                        case 4:
                            mimiTemp="*/*";
                            break;
                    }
                    openAs(mimiTemp);
                    dialog.cancel();

                }
            };
            listView.setOnItemClickListener(listener);
            dialog.show();


        }

        public void openAs(final String mimiTemp)
        {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layoutof_fileopener1);
            //dialog.setTitle("open new");

            final ListView listView=(ListView)dialog.findViewById(R.id.file_opener1_list);
            final CheckBox checkBox=(CheckBox)dialog.findViewById(R.id.file_opener1_checkbox);
            final TextView textextra=(TextView)dialog.findViewById(R.id.file_opener1_extra);

            checkBox.setVisibility(View.GONE);
            textextra.setVisibility(View.GONE);

            ArrayList<String> nameList=new ArrayList<>();
            ArrayList<Drawable> icons=new ArrayList<>();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            PackageManager manager = getPackageManager();


            intent.setDataAndType(Uri.fromFile(fileToOpen),mimiTemp);

            final List<ResolveInfo> infos = manager.queryIntentActivities (intent, 0);

            for (int i = 0; i < infos.size(); i++)
            {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ApplicationInfo applicationInfo=infos.get(i).activityInfo.applicationInfo;
                Log.e("installled apps:",infos.get(i).activityInfo.packageName+"--"+infos.get(i).activityInfo.loadLabel(manager));
                nameList.add(infos.get(i).activityInfo.loadLabel(manager)+"");
                icons.add(infos.get(i).activityInfo.loadIcon(manager));
            }

            if(nameList.size()==0)
            {
                Toast.makeText(MainActivity.this, "No application can open this file", Toast.LENGTH_LONG).show();
                //call unknown
                return;
            }

            foa=new FileOpenerAdapter(MainActivity.this,0,nameList,icons);
            listView.setAdapter(foa);

            if(foa.getCount()>7)
            {
                View item = foa.getView(0, null, listView);
                item.measure(0, 0);
                Log.e("height",item.getMeasuredHeight()+"");
                listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) (7.5 * item.getMeasuredHeight())));
            }



            final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView <?> listv,View v,int pos,long id)
                {
                    String className=infos.get(pos).activityInfo.name;
                    String packageName=infos.get(pos).activityInfo.packageName;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName(packageName,className);

                    intent.setDataAndType(Uri.fromFile(fileToOpen),mimiTemp);
                    dialog.cancel();
                    startActivity(intent);
                }
            };
            listView.setOnItemClickListener(listener);

            dialog.show();



        }

    }

    public class RootFileOpener
    {
        String toDownloadPath,toDownloadName;
        String fromDownLoadPath;

        RootFileOpener()
        {
           toDownloadPath=Physical_Storage_PATHS.get(0)+"/Downloads/f2/rootSync/";
       }

        public void open(String name,String fromPath)
        {
            fromDownLoadPath=fromPath;
            toDownloadName=name;
           if(!new File(toDownloadPath).exists())
           {
               boolean b=new File(toDownloadPath).mkdirs();
               if(!b)
               {
                   Toast.makeText(MainActivity.this, "Failed to Load this file in "+toDownloadPath, Toast.LENGTH_LONG).show();
                   return;
               }
           }

           if(new File(toDownloadPath+toDownloadName).exists())
           {
               Log.e("rename called","--");
               keepBoth();
           }
           toDownloadPath+=toDownloadName;

           class CopyTask extends AsyncTask<Void,Void,String>
           {
               private ProgressDialog pd;
               private String toPath,fromPath;

               private boolean b;

               private CopyTask(String toPath,String fromPath)
               {
                   this.toPath=toPath;
                   this.fromPath=fromPath;
               }

               @Override
               protected void onPreExecute()
               {
                   super.onPreExecute();
                   pd=new ProgressDialog(MainActivity.this);
                   pd.setMessage("Loading file to..."+toPath);
                   pd.show();
               }

               @Override
               protected String doInBackground(Void... params)
               {
                   boolean b=RootTools.copyFile(fromPath+"",toPath+"",false,true);
                   if(b)
                   {
                       return " all done";
                   }
                   else
                   {
                       return null;
                   }
               }

               @Override
               protected void onPostExecute(String x)
               {
                   if(pd.isShowing())
                       pd.dismiss();
                   File file=new File(toPath);
                   if(x==null)
                   {
                       if(file.exists())
                         b= file.delete();
                       Toast.makeText(MainActivity.this, "failed to load to "+toPath, Toast.LENGTH_LONG).show();
                       return;
                   }
                   else
                   {
                       TinyDB tinyDB=new TinyDB(MainActivity.this);
                       ArrayList<String> list=tinyDB.getListString("rootSync");
                       if(list==null)
                       {
                           list=new ArrayList<>();
                       }
                       list.add(toPath+" *:*:* "+fromPath); //INTERNAL *:*:* ROOT
                       tinyDB.putListString("rootSync",list);
                       FileOpener fileOpener=new FileOpener(file);
                       fileOpener.open();
                   }
               }

           }

           CopyTask asyncTask=new CopyTask(toDownloadPath+"",fromDownLoadPath+"");
           asyncTask.execute();

       }

        private void keepBoth()
        {

            int filenumber=0;

                String x=toDownloadName; //.....'vishwas.txt'------'mydiiiicccc'
                String x2="";
                if(x.lastIndexOf('.')>=0)
                {
                    x2=x.substring(x.lastIndexOf('.'),x.length());//.....'.txt'------''
                }


                String x1="";

                if(x.lastIndexOf('.')>=0)
                {
                    x1=x.substring(0,x.lastIndexOf('.'))+"("+(++filenumber)+")";//....'vishwas(1)'
                }
                else
                {
                    x1=x.substring(0,x.length())+"("+(++filenumber)+")";//------'mydiiiicccc(1)'
                }


                while (true)
                {
                    if(new File(toDownloadPath+x1+x2).exists())
                    {
                        if(x.lastIndexOf('.')>=0)
                        {
                            x1=x.substring(0,x.lastIndexOf('.'))+"("+(++filenumber)+")";//....'vishwas(1)'
                        }
                        else
                        {
                            x1=x.substring(0,x.length())+"("+(++filenumber)+")";//------'mydiiiicccc(1'
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                toDownloadName=x1+x2;
            Log.e(TAG,"renaming file "+toDownloadName);
            }

    }

    void fileopener(File file)
    {
        Uri uri = Uri.fromFile(file);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(file.toString().contains(".pdf")) {
            // PDF file
            intent=new Intent();
            intent.setClassName("com.adobe.reader","com.adobe.reader.AdobeReader");
            //intent.setPackage("com.adobe.reader");
            //activity not found exception
            intent.setDataAndType(uri, "application/pdf");
        } else if(file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(file.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(file.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        Log.e("opening a file..",intent.getType()+"--"+intent.getData()+"--"+intent.getScheme());
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        this.startActivity(intent);
    }



    public DeletingMachine returnDeletingObject(ArrayList<String> deletePaths,String parent,int storageid)
    {
        return new DeletingMachine(MainActivity.this,deletePaths,parent,storageid);
    }



    public Refresh returnRefreshObject()
    {
        return new Refresh();
    }

    public FileOpener returnFileOpenerObject(File fileToOpen)
    {
        return new FileOpener(fileToOpen);
    }

    public RootFileOpener returnRootFileOpenerObject()
    {
        return new RootFileOpener();
    }



    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();

    }

    public void connectToDropBox()
    {
        if(DropBoxConnection.isDropboxConnecting)
        {
            return;
        }
        else
        {
            DropBoxConnection.isDropboxConnecting=true;
        }

        DropBoxConnection.isDropboxConnected=false;
        DropBoxConnection.isErrorConnecting=false;



        final Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.e("connectiong started:","--");
                    DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("noobnoob").withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient())).build();

                    DropBoxConnection.mDbxClient = new DbxClientV2(requestConfig, DropBoxConnection.userKey);
                    Log.e("connectiong started:","--1");
                    FullAccount fullAccount= DropBoxConnection.mDbxClient.users().getCurrentAccount();
                    SpaceUsage spaceUsage =DropBoxConnection.mDbxClient.users().getSpaceUsage();
                    Log.e("connectiong started:","--2");
                    DropBoxConnection.totalSize=spaceUsage.getAllocation().getIndividualValue().getAllocated();
                    Log.e("totalsize","done");
                    DropBoxConnection.usedSize=spaceUsage.getUsed();
                    DropBoxConnection.userName=fullAccount.getName().getDisplayName();
                    DropBoxConnection.accountId=fullAccount.getAccountId();
                    if(DropBoxConnection.totalSize==0)
                    {
                        DropBoxConnection.isDropboxConnected=false;
                    }
                    else
                    {
                        DropBoxConnection.isDropboxConnected=true;
                        long prog;
                        prog=DropBoxConnection.usedSize*100/DropBoxConnection.totalSize;
                        DropBoxConnection.progress=(int)prog;
                    }

                }
                catch(Exception e)
                {
                    Log.e("dropbox error:","while connecting"+e.getMessage()+e.getCause()+e.toString());
                    DropBoxConnection.isErrorConnecting=true;
                    DropBoxConnection.isDropboxConnected=false;
                }
                DropBoxConnection.isDropboxConnecting=false;
            }
        };
        thread.start();
    }

    /*
    If drive is not connected it will retry
     */
    public void connectDriveAgain()
    {
        if(!GoogleDriveConnection.isDriveConnected)
        {
            connectToDrive();
        }
    }

    public void connectDropBoxAgain()
    {
        if(!DropBoxConnection.isDropboxConnected)
        {
            connectToDropBox();
        }
    }

    public void loginInToDropBox()
    {

        //STARTING THE DROPBOX AUTHENTICATION AND THEN GOING TO RESUME ACTIVITY
        startLoginToDropbox=true;
        com.dropbox.core.android.Auth.startOAuth2Authentication(MainActivity.this,DropBoxConnection.APP_KEY);

    }

    public void logoutFromDropBox()
    {
        SharedPreferences pref=getApplicationContext().getSharedPreferences("DropboxPref",0);
        SharedPreferences.Editor editor=pref.edit();
        editor.remove("userKey");
        editor.apply();

        DropBoxConnection.clear();
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
    }

    public void connectToDrive()
    {
        if(GoogleDriveConnection.isDriveConnecting)
        {
            return;
        }
        else
        {
            GoogleDriveConnection.isDriveConnecting=true;
        }
        Log.e("GoogleApi","connectToDrive");
        GoogleDriveConnection.whatToDo=2;//connect
        GoogleDriveConnection.isDriveConnected=false;
        GoogleDriveConnection.isErrorConnecting=false;

        /*
        GoogleDriveConnection.m_service_client = new com.google.api.services.drive.Drive.Builder
                    (
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            GoogleAccountCredential.usingOAuth2(this, Collections.singletonList(com.google.api.services.drive.DriveScopes.DRIVE_FILE)
                            )
                    ).build();
        */

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */


        GoogleDriveConnection.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                .addApi(Plus.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        GoogleDriveConnection.mGoogleApiClient.connect();

    }


    public void loginInToDrive()
    {

        GoogleDriveConnection.whatToDo=1;//login

        GoogleDriveConnection.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                .addApi(Plus.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        GoogleDriveConnection.mGoogleApiClient.connect();

    }

    public void logoutFromDrive()
    {
        SharedPreferences pref=getApplicationContext().getSharedPreferences("DrivePref",0);

        SharedPreferences.Editor editor=pref.edit();
        editor.remove("userKey");
        editor.apply();
        if(GoogleDriveConnection.mGoogleApiClient.isConnected())
        {

        }
        else
        {
            GoogleDriveConnection.mGoogleApiClient.connect();
        }

        try
        {
            GoogleDriveConnection.mGoogleApiClient.clearDefaultAccountAndReconnect();
            GoogleDriveConnection.mGoogleApiClient.disconnect();
            GoogleDriveConnection.clear();
        }
        catch (Throwable e)
        {
        }
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
    }


    private void refreshSDCardUri()
    {
        SDCardUriMap=new HashMap<>();
        SharedPreferences pref=getApplicationContext().getSharedPreferences("SDCardPref",0);

        for(int i=0;i<numberOfStorages;i++)
        {
            String breaker[]=Physical_Storage_PATHS.get(i).split("\\/");
            String name=breaker[breaker.length-1];

            if(pref.getString(name,null)==null)
            {
                //no uri stoed in sharedPref
            }
            else
            {
                Uri uri=Uri.parse(pref.getString(name,null));
                if(uri!=null)
                {
                    SDCardUriMap.put(name,uri);
                }
            }
        }

    }

    public  class Refresh                    //OKOK
    {

        /*
        @pagerId:
        it is always fixed and determined
        pager1=1,10,11
        pager2=2,20,22
        pager3=3,30,33
        pager4=4
        pager5=5
         */
        public void refreshPagerId(int pagerId)
        {


        }

        /*
        @pagerNumber: nth pager of view pager ~~~ viewPager.getCurrentItem
        it is not fixed
        pager1=1
        pager2=2
        pager3=3
        pager4=2,3,4
        pager5=2,3,4,5

         */

        public void refreshPagerNumber(int pagerNumber)
        {

        }

    }

    @Override
    public void onBackPressed()
    {
        if(pasteButtonDecisionMaker.pasteMenu.isOpened())
        {
            pasteButtonDecisionMaker.closePasteMenu();
            return;
        }
        int currentFrameId=getFrameIdFromPageIndex(viewPager.getCurrentItem());
        int currentVisiblePageIndex=viewPager.getCurrentItem();
        Page page=pageList.get(currentVisiblePageIndex);
        String pageName=page.getName();
        String currentPath=page.getCurrentPath();
        int pageId=page.getPageId();
        Log.e("Back Pressed",pageName+"--"+pageId);

        if(pageId==-5)  //ImageGallery
        {
            if(currentPath.equals("Gallery1"))
            {
                image_gallery_fragment1 fragment=(image_gallery_fragment1)getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.backPressed(page);
            }
            else
            {
                if(currentPath.equals("Gallery2"))
                {
                    image_gallery_fragment2 fragment=(image_gallery_fragment2)getSupportFragmentManager().findFragmentById(currentFrameId);
                    fragment.backPressed();
                }
            }
            return;
        }
        if(pageId==12345)
        {
            if(page.getPathList().size()==1)
            {
                super.onBackPressed();
                return;
            }

            if(currentPath.equals("SearchResult"))
            {
                search_fragment fragment=(search_fragment)getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.backPressed(page);
            }
            else
            {
                storagePager fragment=(storagePager)getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.backPressed(page);
            }
            return;
        }
        if(pageId==11)
        {
            if(currentPath.equals("SearchResult"))
            {
                search_fragment fragment=(search_fragment)getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.backPressed(page);
            }
            else
            {
                storageAnalyser fragment=(storageAnalyser)getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.backPressed(page);
            }
            return;
        }
        if(pageId==15)
        {
            if(page.getPathList().size()==1)
            {
                ftpLoginPager fragment=(ftpLoginPager)getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.backPressed(page);
            }
            else
            {
                storagePager fragment=(storagePager) getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.backPressed(page);
            }
            return;
        }
        if(pageId==16)
        {
            ftpServerPager fragment=(ftpServerPager)getSupportFragmentManager().findFragmentById(currentFrameId);
            fragment.backPressed(page);
            return;
        }
        if(pageId==696969)
        {
            appManager fragment=(appManager)getSupportFragmentManager().findFragmentById(currentFrameId);
            fragment.backPressed(page);
            return;
        }


        super.onBackPressed();

    }

    public void startReceiver()
    {
        Intent intent=new Intent(MainActivity.this, WifiReceiveActivity.class);
        intent.putExtra("start",true);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();


        if(GoogleDriveConnection.isDriveConnected)
        {
            // disconnect Google API client connection

            try
            {
                GoogleDriveConnection.mGoogleApiClient.disconnect();
                Log.e("disconnected","successfull");
            }
            catch(Exception e)
            {
                Log.e("disconnected","failed");
            }
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        GoogleDriveConnection.isDriveConnecting=false;
        Log.e(TAG, "GoogleApiClient connection failed: " + result.toString());
        if(GoogleDriveConnection.whatToDo==1)
        {
            Toast.makeText(this, "Login Failed.....", Toast.LENGTH_LONG).show();
        }
        if(GoogleDriveConnection.whatToDo==2)
        {
            /*
            WHEN WE TRY TO FETCH AFTER LOGOUT
             */
            //Toast.makeText(this, "Cant connect "+GoogleDriveConnection.userName, Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "removing "+GoogleDriveConnection.userName, Toast.LENGTH_LONG).show();

            //logout the user
            //logoutFromDrive();
            //setUpViewPager();

            return;

        }
        if(result.getErrorCode()==ConnectionResult.SIGN_IN_REQUIRED)
        {

        }
        // Called whenever the API client fails to connect.



        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try
        {

            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            GoogleDriveConnection.mGoogleApiClient.connect();

        }
        catch (IntentSender.SendIntentException e)
        {

            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * It invoked when Google API client connected
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        GoogleDriveConnection.isDriveConnecting=false;
        Log.e("GoogleApi connected:","yes"+GoogleDriveConnection.whatToDo);

       final String accountName = Plus.AccountApi.getAccountName(GoogleDriveConnection.mGoogleApiClient);
        Log.e("Drive Account:", accountName+"==");

        if(GoogleDriveConnection.whatToDo==1)   //LOGIN REQUEST BY USER successfull
        {

            GoogleDriveConnection.clear();
            GoogleDriveConnection.isDriveAvailable=true;


            SharedPreferences pref=getApplicationContext().getSharedPreferences("DrivePref",0);
            SharedPreferences.Editor editor=pref.edit();
            editor.putString("userKey",accountName);
            editor.apply();

            addPage("GoogleDrive",pageList.size(),"root",R.mipmap.google_drive,12345);
            setUpViewPager();

            Toast.makeText(this, "Logined Successfully to "+accountName, Toast.LENGTH_LONG).show();


        }
        if(GoogleDriveConnection.whatToDo==2)   //CONNECT TO DRIVE
        {

            GoogleDriveConnection.userName = accountName;

        /*

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account account : list)
        {
            if (account.type.equalsIgnoreCase("com.google"))
            {
                Log.e("account:", account.name + "--");
            }
        }

         */


        HttpTransport m_transport = AndroidHttp.newCompatibleTransport();
        JsonFactory m_jsonFactory = GsonFactory.getDefaultInstance();
        GoogleAccountCredential m_credential;
        m_credential = GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(DriveScopes.DRIVE));
        m_credential.setSelectedAccountName(accountName);
        GoogleDriveConnection.m_service_client = new com.google.api.services.drive.Drive.Builder(m_transport, m_jsonFactory, m_credential).setApplicationName("f2 explorer").build();



        Thread thread = new Thread() {
                @Override
                public void run() {

                    try
                    {
                        About about = GoogleDriveConnection.m_service_client.about().get().setFields("storageQuota, user").execute();
                        Log.e("Total quota", "--" + helpingBot.sizeinwords(about.getStorageQuota().getLimit()));
                        Log.e("Used quota", "--" + helpingBot.sizeinwords(about.getStorageQuota().getUsageInDrive()));

                        GoogleDriveConnection.totalSize = about.getStorageQuota().getLimit();
                        GoogleDriveConnection.usedSize = about.getStorageQuota().getUsageInDrive();


                        if (GoogleDriveConnection.totalSize == 0)
                        {
                            GoogleDriveConnection.isDriveConnected = false;
                        }
                        else
                        {
                            GoogleDriveConnection.isDriveConnected = true;
                            long prog;
                            prog = GoogleDriveConnection.usedSize * 100 / GoogleDriveConnection.totalSize;
                            GoogleDriveConnection.progress = (int) prog;
                        }


                    }
                    catch (UserRecoverableAuthIOException e)
                    {
                        Log.e("huge aerror999:", "+++" + e.getMessage() + e.getCause());
                        startActivityForResult(e.getIntent(), REQUEST_CODE_RESOLUTION);
                    }
                    catch (Exception e)
                    {
                        GoogleDriveConnection.isErrorConnecting = true;
                        GoogleDriveConnection.isDriveConnected = false;
                        Log.e("huge aerror:", "+++" + e.getMessage() + e.getCause());
                    }

                }

            };
            thread.start();

        }

            //About about = mGooSvc.about().get().execute(); drive.about().get()



    }

    /**
     * It invoked when connection suspend
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause)
    {
        GoogleDriveConnection.isDriveConnecting=false;
        Log.e(TAG, "GoogleApiClient connection suspended");
    }

    public void showLowSpaceError(long totalSizeToDownload,long spaceAvailable)
    {
        final Dialog dialog0 = new Dialog(MainActivity.this);
        dialog0.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog0.setContentView(R.layout.layoutof_low_space);
        //dialog0.setTitle("Low space Dialog");

        TextView lowspace_clear=(TextView) dialog0.findViewById(R.id.lowspace_clear);
        TextView lowspace_available=(TextView) dialog0.findViewById(R.id.lowspace_available);
        TextView lowspace_required=(TextView) dialog0.findViewById(R.id.lowspace_required);
        Button lowspace_button=(Button)dialog0.findViewById(R.id.lowspace_button);

        lowspace_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog0.cancel();
            }
        });



        lowspace_clear.setText("TRY CLEARING "+ helpingBot.sizeinwords(totalSizeToDownload-spaceAvailable)+" MORE");
        lowspace_available.setText("AVAILABLE SPACE :"+helpingBot.sizeinwords(spaceAvailable));
        lowspace_required.setText("REQUIRED SPACE :"+helpingBot.sizeinwords(totalSizeToDownload));

        dialog0.show();

        Toast.makeText(MainActivity.this, "OPERATION FAILED,TRY CLEARING SOME SPACE", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNewIntent(Intent intent)
    {
        Log.e("new Intent:","+++++++++++++++++++++++++++++++");
        if(intent.getIntExtra("notification",0)==5)
        {
            fab_taskManager.performClick();
        }
    }

    private void fastImages()
    {
        //inal ArrayList<IGSFile> results=new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection =// Which columns to return
                {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_MODIFIED
                };

        Cursor cursor = getContentResolver().query
                (
                        uri,
                        projection,// Which columns to return
                        null,// WHERE clause; which rows to return (all rows)
                        null, // WHERE clause selection arguments (none)
                        null // Order-by clause (ascending by name)
                );
        Log.e("total cursor:",cursor.getCount()+"--");
        String id;
        String name;
        String path;
        long date;
        long coverID;
        String folderPath;
        int pathColumnIndex,dateColumnIndex;

        ArrayList<String> ids = new ArrayList<String>();
        //mAlbumsList.clear();
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

                id = cursor.getString(columnIndex);
                //columnIndex=cursor.getColumnIndex(MediaStore.Images.Media.)

                dateColumnIndex=cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
                pathColumnIndex=cursor.getColumnIndex(MediaStore.Images.Media.DATA);




                date = Long.parseLong(cursor.getString(dateColumnIndex));

                path = cursor.getString(pathColumnIndex);

                if (!ids.contains(id))
                {
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    name = cursor.getString(columnIndex);

                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    coverID = cursor.getLong(columnIndex);
                    ids.add(id);
                    Log.e("Images found:",name+"----"+date+"-----"+path);
                }
                else
                {}
            }
            Log.e("end:","==00");
            cursor.close();
        }
    }

    public void showSmackBar(String msg)
    {
        Snackbar snackbar=Snackbar.make(drawer,msg,Snackbar.LENGTH_LONG);
        /*snackbar.setAction("UNDO", new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Action!", Snackbar.LENGTH_SHORT).show();
            }
        });*/
        snackbar.setActionTextColor(Color.BLUE);
        snackbar.show();
    }


    @Override
    public void handleResult(Result rawResult)
    {
        try
        {
            String scannedFtpUrl=rawResult.getText();
            Log.e("QR_CODE:",scannedFtpUrl);
            if(scannedFtpUrl.startsWith("ftp://"))
            {
                FtpCache.scannedQR=scannedFtpUrl;
            }
            else
            {
                Toast.makeText(this, "Invalid URL Scanned", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.e("QR_CODE EXCEPTION:",e.getLocalizedMessage());
        }

    }


    public void showHideButtons(int index)
    {

        pasteButtonDecisionMaker.showHidePasteButton(index);
        if(tasksCache.tasksId.size()>0)
        {
            fab_taskManager.setVisibility(View.VISIBLE);
            //fab_taskManager.show(false);
        }
        else
        {
            fab_taskManager.setVisibility(View.GONE);
            //fab_taskManager.hide(false);
        }
        createNew.showHideAddButton(index);

    }



    public void parser()
    {
        ArrayList<String> paths=new ArrayList<>();
        paths.add("/data/app/Amazon Now.apk");
        paths.add("/data/adb");
        paths.add("/data/anr");
        paths.add("/data/app");
        paths.add("/data/app-asec");
        paths.add("/data/app-lib");
        paths.add("/data/app-private");
        paths.add("/data/audio");
        paths.add("/data/backup");
        paths.add("/data/bootchart");
        paths.add("/data/connectivity");
        paths.add("/data/dalvik-cache");
        paths.add("/data/data");
        paths.add("/data/dpm");
        paths.add("/data/drm");
        paths.add("/data/fota");
        paths.add("/data/fpc");
        paths.add("/data/hostapd");
        paths.add("/data/local");
        paths.add("/data/lost+found");
        paths.add("/data/magisk");
        paths.add("/data/media");
        paths.add("/data/mediadrm");
        paths.add("/data/misc");
        paths.add("/data/miui");
        paths.add("/data/mqsas");
        paths.add("/data/nfc");
        paths.add("/data/baby.mp4");
        paths.add("/data/bugreports");
        paths.add("/data/tombstones");

        for(String path:paths)
        {
            File f=new File(path);
            Date date=new Date(f.lastModified());
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");

            Log.e(path,f.exists()+"  "+f.isDirectory()+"     "+f.isFile()+"        "+f.getName()+"     "+f.length()+"   "+dateFormat.format(date));
        }

    }

    private void copy(String from,String to)
    {
        try
        {
            InputStream inputStream;
            try
            {
                inputStream=new FileInputStream(from);
            }
            catch (Exception e)
            {
                inputStream=null;
            }
            if(inputStream==null)
            {
                Log.e("getting root is","--");
                SuOperations suOperations=new SuOperations();
                inputStream=suOperations.getRootInputStream(from);
            }
            Log.e("vishwas",inputStream+"++");
            long xx=inputStream.skip(200000000);
            Log.e("vishwas",inputStream+"--"+xx);

            OutputStream outputStream=new FileOutputStream(to);
            byte[] buf = new byte[61440];
            int len;
            while ((len=inputStream.read(buf))>0)
            {
                Log.e("xxx",len+"---------");
                outputStream.write(buf,0,len);
            }
            try
            {
                inputStream.close();
                outputStream.close();
            }
            catch (Exception e)
            {}
        }
        catch (Exception e)
        {
            Log.e("copying failed:",e.getMessage()+"--"+e.getLocalizedMessage());
        }
        File f=new File(to);
        Log.e("copied file",f.exists()+"--"+f.length());
    }



    private void myLsInitialiser()
    {
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    InputStream inputStream=MainActivity.this.getResources().openRawResource(R.raw.myls);
                    Log.e("---",inputStream+"--");
                    OutputStream outputStream=openFileOutput("myls",Context.MODE_PRIVATE);
                    byte[] buf = new byte[61440];
                    int len;
                    while ((len=inputStream.read(buf))>0)
                    {
                        Log.e("xxx",len+"---------");
                        outputStream.write(buf,0,len);
                    }
                    try
                    {
                        inputStream.close();
                        outputStream.close();
                    }
                    catch (Exception e)
                    {}
                    File myLsFile=getFileStreamPath("myls");
                    if(!myLsFile.canExecute())
                    {
                        boolean x= myLsFile.setExecutable(true);
                    }
                }
                catch (Exception e)
                {
                    Log.e("copying failed:",e.getMessage()+"--"+e.getLocalizedMessage());
                }
            }
        };
        thread.start();
    }
}