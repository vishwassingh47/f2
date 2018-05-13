package com.example.sahil.f2.GokuFrags;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.SpaceUsage;
import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.SearchData;
import com.example.sahil.f2.Cache.StorageAnalyserData;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.Classes.SortingMachine;
import com.example.sahil.f2.Classes.ThumbNailsMod;
import com.example.sahil.f2.FunkyAdapters.ListViewAdapter;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.FolderLister;
import com.example.sahil.f2.UiClasses.ClickManager;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.FileList;



import static com.example.sahil.f2.Cache.MyCacheData.GlobalStorageAnalyser.storageId;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod0;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod1;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod2;

import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getLocalHomeStoragePath;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getPageIndexFromFrameId;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.isExternalSdCardPath;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hit4man47 on 11/20/2017.
 */

public class storageAnalyser extends Fragment
{

    //UI
    private ImageView home,back,root,search,close,options,artIcon,storage1,storage2,storage3,storage4,storage5;
    private LinearLayout layer1,search_layout,loading,storageButtonsLayer,layer2,dialog;
    private TextView title,artText,dialogSize,dialogPercent,layer2_used,layer2_total;
    private ListView listView;
    private RelativeLayout artLayout;
    private Button artButton;
    private ProgressBar dialogPb;


    private final String TAG="StorageAnalyser";
    private ArrayList<MyFile> myFilesList=null;
    private View view;


    public ThumbNailsMod thumbNailsMod;
    public CommonsUtils commonsUtils;

    private Runnable thumbNail_runnable;
    private Handler thumbNail_handler;
    private  boolean thumbNailRunner_isRunning=false;


    private int currentStorageType;
    private final int INTERNAL=1;
    private final int EXTERNAL=2;
    private final int CLOUD=3;

    private boolean shouldStartNew=false,shouldStartOld=false;//VERY IMPORTANT
    private Runnable searchRunnable;
    private Handler searchHandler;
    private boolean startSearchThread=false;//very important


    private ListViewAdapter listViewAdapter;

    public boolean isFolderOk=false;

    private ThumbNailManager thumbNailManager;
    private ClickManager clickManager;
    private MyDataAnalyser dataAnalyser;
    private MyUi myUi;



    private ArrayList<String> PagePathList;
    private ArrayList<Integer> PageIndexList;

    private boolean isFirstAnalyserPath;

    final HelpingBot helpingBot=new HelpingBot();

    private int modValue;
    private int rootFrameId;
    private int pageIndex;//in view pager
    private String currentPath;
    private int currentIndex=0;
    private String localPath;
    private boolean continueThumbRun=false;
    public MainActivity mainActivityObject;

    private StorageAnalyserData storageAnalyserData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved)
    {
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_analyser,container,false);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        getStorageInfo();

        if(storageId ==0 )
        {
            //FIRST TIME OPENED
            storageId=1;
            PagePathList.clear();
            PageIndexList.clear();
            PagePathList.add(Physical_Storage_PATHS.get(0));
            PageIndexList.add(0);
            startSearchThread=true;
        }
        storageAnalyserData=MyCacheData.getStorageAnalyserData(storageId);

        myUi=new MyUi();
        dataAnalyser=new MyDataAnalyser();
        thumbNailManager=new ThumbNailManager();
        commonsUtils=new CommonsUtils();

        Log.e(TAG,"STARTED"+pageIndex);
        modValue=pageIndex%3;
        thumbNailManager.clearThumbCache();

        isFolderOk=false;

        //back button is pressed
        if(shouldStartOld)
        {
            shouldStartOld=false;
            PagePathList.remove(PagePathList.size()-1);
            PageIndexList.remove(PageIndexList.size()-1);
        }

        if(shouldStartNew)
        {
            //this fragment is new opened
            shouldStartNew=false;
            PagePathList.add(localPath);
            PageIndexList.add(0);
        }


        currentPath=PagePathList.get(PagePathList.size()-1);
        currentIndex=PageIndexList.get(PageIndexList.size()-1);

        if(PagePathList.size()==1)
        {
            isFirstAnalyserPath=true;
        }
        else
        {
            isFirstAnalyserPath=false;
        }

        if(storageId>3)
        {
            currentStorageType=CLOUD;
        }
        else
        {
            currentStorageType=isExternalSdCardPath(currentPath)?EXTERNAL:INTERNAL;
        }

        view=getView();
        myUi.initialiseAllUi();


        dialog.setVisibility(View.GONE);
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

        if(startSearchThread)
        {
            startSearchThread=false;

            storageAnalyserData.threadId +=10;
            storageAnalyserData.errorOccured=false;
            storageAnalyserData.successfullySearched =false;
            storageAnalyserData.searching =true;
            storageAnalyserData.resultMap.clear();
            storageAnalyserData.totalBytesFetched=0;
            storageAnalyserData.used=0;
            storageAnalyserData.total=0;
            if(storageId<=3)
                dataAnalyser.searchInPager123(currentPath,storageAnalyserData.threadId);
            if(storageId==4)
                dataAnalyser.searchInPager4(currentPath,storageAnalyserData.threadId);
            if(storageId==5)
                dataAnalyser.searchInPager5(currentPath,storageAnalyserData.threadId);

        }

        dataAnalyser.runFetcherRunner();
        mainActivityObject.showHideButtons(pageIndex);

    }

    private class MyDataAnalyser
    {

        private void searchInPager123(final String rootPath,final int threadId)
        {

            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    Log.e("search thread started:",threadId+"--"+rootPath);
                    boolean errorOccured=false;
                    File rootFile=new File(rootPath);
                    long total=rootFile.getTotalSpace();
                    long free=rootFile.getUsableSpace();
                    long used=total-free;

                    storageAnalyserData.used=used;
                    storageAnalyserData.total=total;

                    long x=recursion(rootFile,threadId);

                    if(x==-1)
                    {
                        errorOccured=true;
                    }

                    if(threadId==storageAnalyserData.threadId)
                    {
                        storageAnalyserData.searching =false;
                        if(!errorOccured)
                        {
                            storageAnalyserData.successfullySearched=true;
                        }
                        else
                        {
                            storageAnalyserData.errorOccured=true;
                        }
                    }

                    Log.e("search thread ended:",threadId+"--"+rootPath);
                }
            };
            thread.start();

        }

        private long recursion(final File rootFile,final int threadId)
        {
            long thisFolderSize=0;
            File[] files=rootFile.listFiles();
            try
            {
                for(File child:files)
                {
                    if(threadId!=storageAnalyserData.threadId)
                    {
                       return -1;
                    }

                    if(child.isDirectory())
                    {
                        thisFolderSize+=recursion(child,threadId);
                    }
                    else
                    {
                        thisFolderSize+=child.length();
                        storageAnalyserData.totalBytesFetched+=child.length();
                    }
                }
                storageAnalyserData.resultMap.put(rootFile.getAbsolutePath(),thisFolderSize);

            }
            catch (Exception e)
            {
                return -1;
            }
            return thisFolderSize;
        }

        private void searchInPager4(final String rootPath,final int threadId)
        {
            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    Log.e("search thread started",threadId+"--"+rootPath);
                    boolean errorOccured=false;
                    try
                    {

                        About about = GoogleDriveConnection.m_service_client.about().get().setFields("storageQuota, user").execute();
                        GoogleDriveConnection.totalSize = about.getStorageQuota().getLimit();
                        GoogleDriveConnection.usedSize = about.getStorageQuota().getUsageInDrive();
                        storageAnalyserData.total=GoogleDriveConnection.totalSize;
                        storageAnalyserData.used=GoogleDriveConnection.usedSize;

                        com.google.api.services.drive.Drive.Files.List request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,quotaBytesUsed,parents),nextPageToken");
                        List<com.google.api.services.drive.model.File> files = new ArrayList<>();
                        do
                        {
                            Log.e("New page Listed","ON gOOGLE DRIVE FILE FETCHING");
                            FileList filelist = request.execute();
                            files.addAll(filelist.getFiles());
                            request.setPageToken(filelist.getNextPageToken());
                        }
                        while (request.getPageToken() != null && request.getPageToken().length() > 0);

                        HashMap<String,ArrayList<String>> tree=new HashMap<>();

                        for(com.google.api.services.drive.model.File file:files)
                        {
                            if(threadId!=storageAnalyserData.threadId)
                            {
                                break;
                            }
                            Log.e("xxx IS:",file.getName()+"--"+file.getId());
                            if(file.getParents()==null || file.getParents().size()==0)
                                continue;


                            String id=file.getId();
                            String parent=file.getParents().get(0);

                            if(file.getMimeType().contains("folder"))
                            {
                                ArrayList<String> childrens=tree.get(parent);
                                if(childrens==null)
                                {
                                    childrens=new ArrayList<>();
                                    tree.put(parent,childrens);
                                }
                                childrens.add(id);
                            }
                            else
                            {

                                long size=file.getQuotaBytesUsed();
                                storageAnalyserData.totalBytesFetched+=size;

                                Long value= storageAnalyserData.resultMap.get(parent);
                                if(value==null)
                                {
                                    storageAnalyserData.resultMap.put(parent,size);
                                }
                                else
                                {
                                    storageAnalyserData.resultMap.put(parent,size+(long)value);
                                }
                            }
                        }
                        for(Map.Entry<String,ArrayList<String>> entry:tree.entrySet())
                        {
                            String key=entry.getKey();
                            Log.e("Parent IS:",key+"--");
                            ArrayList<String> values=entry.getValue();
                            for(String x:values)
                            {
                                Log.e("Child IS:",x+"--");
                            }

                        }
                        com.google.api.services.drive.model.File rootFile= GoogleDriveConnection.m_service_client.files().get("root").setFields("id").execute();
                        String rootFilePath=rootFile.getId();
                        recursion(rootFilePath,tree,threadId);

                    }
                    catch(final Exception e)
                    {
                        Log.e("search thread ERROR",e.getLocalizedMessage()+"--");
                        errorOccured=true;
                    }
                    finally
                    {
                        if(threadId==storageAnalyserData.threadId)
                        {
                            storageAnalyserData.searching =false;
                            if(!errorOccured)
                            {
                                storageAnalyserData.successfullySearched =true;
                            }
                            else
                            {
                                storageAnalyserData.errorOccured=true;
                            }
                        }
                        Log.e("search thread ended",threadId+"--"+rootPath);
                    }
                }
            };
            thread.start();
        }

        //for GOOGLE DRIVE and DROPBOX
        private long recursion(String root,HashMap<String,ArrayList <String>> tree,final int threadId)
        {
            if(threadId!=storageAnalyserData.threadId)
            {
                return 0;
            }

            Long thisSize=null;
            thisSize=storageAnalyserData.resultMap.get(root);

            if(thisSize==null)
            {
                thisSize=(long)0;
            }

            ArrayList<String> childrens=tree.get(root);
            if(childrens!=null)
            {
                for(String child:childrens)
                {
                    thisSize+=recursion(child,tree,threadId);
                }
            }
            if(threadId!=storageAnalyserData.threadId)
            {
                return 0;
            }

            synchronized (storageAnalyserData.resultMap)
            {
                storageAnalyserData.resultMap.put(root,thisSize);
            }

            return thisSize;
        }

        private void searchInPager5(final String rootpath,final int threadId)
        {
            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    Log.e("search thread started",threadId+"--"+rootpath);

                    boolean errorOccured=false;
                    List <com.dropbox.core.v2.files.Metadata> list=new ArrayList<>();
                    try
                    {
                        SpaceUsage spaceUsage =DropBoxConnection.mDbxClient.users().getSpaceUsage();
                        DropBoxConnection.totalSize=spaceUsage.getAllocation().getIndividualValue().getAllocated();
                        DropBoxConnection.usedSize=spaceUsage.getUsed();
                        storageAnalyserData.total=DropBoxConnection.totalSize;
                        storageAnalyserData.used=DropBoxConnection.usedSize;
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

                        HashMap<String,ArrayList<String>> tree=new HashMap<>();

                        for (Metadata file : list)
                        {
                            if(threadId!=storageAnalyserData.threadId)
                            {
                                break;
                            }

                            String path=file.getPathDisplay();
                            int lastIndexOfSlash=path.lastIndexOf('/');
                            if(lastIndexOfSlash<0)
                            {
                                //no parent found
                                continue;
                            }
                            String parent=path.substring(0,lastIndexOfSlash);


                            if(file instanceof FolderMetadata)
                            {
                                ArrayList<String> childrens=tree.get(parent);
                                if(childrens==null)
                                {
                                    childrens=new ArrayList<>();
                                    tree.put(parent,childrens);
                                }
                                childrens.add(path);
                            }
                            else if(file instanceof FileMetadata)
                            {
                                long size=((FileMetadata) file).getSize();

                                Long value= storageAnalyserData.resultMap.get(parent);
                                if(value==null)
                                {
                                    storageAnalyserData.resultMap.put(parent,size);
                                }
                                else
                                {
                                    storageAnalyserData.resultMap.put(parent,size+(long)value);
                                }
                            }
                        }
                        recursion("",tree,threadId);

                    }
                    catch(final Exception e)
                    {
                        Log.e("search thread ERROR",e.getLocalizedMessage()+"--");
                        errorOccured=true;
                    }
                    finally
                    {
                        if(threadId==storageAnalyserData.threadId)
                        {
                            storageAnalyserData.searching =false;
                            if(!errorOccured)
                            {
                                storageAnalyserData.successfullySearched=true;
                            }
                            else
                            {
                                storageAnalyserData.errorOccured=true;
                            }
                        }
                        Log.e("search thread ended",threadId+"--"+rootpath);
                    }
                }
            };
            thread.start();
        }


        private void runFetcherRunner()
        {
            searchHandler=new Handler();
            searchRunnable=new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e(TAG,"Searching");

                    if(storageAnalyserData.searching)
                    {
                        if(dialog.getVisibility()!=View.VISIBLE)
                        {
                            dialog.setVisibility(View.VISIBLE);
                        }
                        long percent;
                        if(storageAnalyserData.used==0)
                            percent=0;
                        else
                            percent=storageAnalyserData.totalBytesFetched*100/storageAnalyserData.used;

                        if(MyCacheData.GlobalStorageAnalyser.storageId<=3)
                        {
                            dialogPb.setProgress((int)percent);
                        }
                        else
                        {
                            dialogPb.setIndeterminate(true);
                        }

                        dialogPercent.setText(percent+" %");
                        dialogSize.setText(helpingBot.sizeinwords(storageAnalyserData.totalBytesFetched)+" / "+helpingBot.sizeinwords(storageAnalyserData.total));
                        searchHandler.postDelayed(searchRunnable,500);
                    }
                    else
                    {
                        runnerIsStopping();
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                }
            };
            searchHandler.postDelayed(searchRunnable,1);

        }

        private void runnerIsStopping()
        {
            Log.e("stopped","=============");

            layer2_used.setText("USED: "+helpingBot.sizeinwords(storageAnalyserData.used));
            layer2_total.setText("TOTAL: "+helpingBot.sizeinwords(storageAnalyserData.total));

            dialog.setVisibility(View.GONE);

            if(storageAnalyserData.errorOccured)
            {
                if(storageId<=3)
                {
                    myUi.showArtLayout(R.mipmap.pick_nose,"ERROR OCCURED",true);
                }
                else
                {
                    myUi.showArtLayout(R.mipmap.pick_nose,"NO INTERNET ACCESS",true);
                }

                return;
            }
            //fetching the files and setting all the lists and listView
            if(storageId>=1 && storageId<=5)
            {
                fetch(storageId);
            }
            else
            {
                Toast.makeText(mainActivityObject, "Wubb Wubba LOOB LOOB", Toast.LENGTH_SHORT).show();
            }
        }

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
                    myFilesList=folderLister.listLocalFolder(currentPath);
                    sort();
                    return 0;
                }
                if(sId==4)
                {
                    myFilesList=folderLister.listDriveFolder(currentPath,false);

                    sort();
                    return 0;
                }
                if(sId==5)
                {
                    myFilesList=folderLister.listDropBoxFolder(currentPath,false);
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


    private void sort()
    {
        int sortBy=variablesCache.sortAnalyserBy;
        SortingMachine sortingMachine =new SortingMachine(sortBy);
        sortingMachine.sortMyFile(myFilesList);
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
            title=(TextView) view.findViewById(R.id.layer1_title);
            search=(ImageView) view.findViewById(R.id.layer1_search);
            close=(ImageView) view.findViewById(R.id.layer1_close);
            options=(ImageView) view.findViewById(R.id.layer1_options);

            layer2=(LinearLayout)view.findViewById(R.id.layer2_storageAnalyser);
            layer2_used=(TextView)view.findViewById(R.id.layer2_storageAnalyser_used);
            layer2_total=(TextView)view.findViewById(R.id.layer2_storageAnalyser_total);


            storageButtonsLayer=(LinearLayout)view.findViewById(R.id.storageButtonsLayer);

            storage1=(ImageView)view.findViewById(R.id.storageButton1);
            storage2=(ImageView)view.findViewById(R.id.storageButton2);
            storage3=(ImageView)view.findViewById(R.id.storageButton3);
            storage4=(ImageView)view.findViewById(R.id.storageButton4);
            storage5=(ImageView)view.findViewById(R.id.storageButton5);


            dialog=(LinearLayout) view.findViewById(R.id.storageAnalyser_ProgressDialog);

            dialogSize=(TextView)view.findViewById(R.id.storageAnalyser_size);
            dialogPercent=(TextView)view.findViewById(R.id.storageAnalyser_percent);
            dialogPb=(ProgressBar)view.findViewById(R.id.storageAnalyser_progressbar);


            loading=(LinearLayout)view.findViewById(R.id.storageAnalyser_loading);
            listView=(ListView) view.findViewById(R.id.list_storageAnalyser);

            artLayout=(RelativeLayout)view.findViewById(R.id.art_layout);
            artIcon=(ImageView) view.findViewById(R.id.art_layout_icon);
            artText=(TextView) view.findViewById(R.id.art_layout_text);
            artButton=(Button) view.findViewById(R.id.art_layout_retry);

        }

        private void setUpStorageLayer()
        {
            if(isFirstAnalyserPath)
            {
                storageButtonsLayer.setVisibility(View.VISIBLE);

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
                                PagePathList.clear();
                                PageIndexList.clear();
                                PagePathList.add(Physical_Storage_PATHS.get(0));
                                PageIndexList.add(0);
                                storageId=1;
                                openNewStorage(!MyCacheData.getStorageAnalyserData(1).successfullySearched);
                            }
                        }
                    });
                }
                if(Physical_Storage_PATHS.size()>1)
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
                                PagePathList.clear();
                                PageIndexList.clear();
                                PagePathList.add(Physical_Storage_PATHS.get(1));
                                PageIndexList.add(0);
                                storageId=2;
                                openNewStorage(!MyCacheData.getStorageAnalyserData(2).successfullySearched);
                            }
                        }
                    });
                }
                if(Physical_Storage_PATHS.size()>2 )
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
                                PagePathList.clear();
                                PageIndexList.clear();
                                PagePathList.add(Physical_Storage_PATHS.get(2));
                                PageIndexList.add(0);
                                storageId=3;
                                openNewStorage(!MyCacheData.getStorageAnalyserData(3).successfullySearched);
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
                                PagePathList.clear();
                                PageIndexList.clear();
                                PagePathList.add("root");
                                PageIndexList.add(0);
                                storageId=4;
                                openNewStorage(!MyCacheData.getStorageAnalyserData(4).successfullySearched);
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
                                PagePathList.clear();
                                PageIndexList.clear();
                                PagePathList.add("");
                                PageIndexList.add(0);
                                storageId=5;
                                openNewStorage(!MyCacheData.getStorageAnalyserData(5).successfullySearched);
                            }
                        }
                    });
                }
            }
            else
            {
                storageButtonsLayer.setVisibility(View.GONE);
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

            root.setVisibility(View.GONE);


            setUpTitle();

            home.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if(currentPath.equals(PagePathList.get(0)))
                    {
                        Toast.makeText(mainActivityObject, "Already at main directory", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        for(int i=1;i<PagePathList.size();i++)
                        {
                            PagePathList.remove(i);
                            PageIndexList.remove(i);
                            i--;
                        }
                        reloadPager();
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

        private void setUpSearchButton()
        {
            Log.e("storage ID",storageId+"--");

            SearchData searchData= MyCacheData.getSearchData(storageId);
            searchData.clearStorage();
            searchData.whereToSearch=currentPath;

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
        }

        private void setUpOptions()
        {
            HelpingBot helpingBot=new HelpingBot();
            PopupMenu popup=helpingBot.getNicePopUpMenu(mainActivityObject,options,R.menu.popup);

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
                        variablesCache.sortAnalyserBy=10;
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
                    return true;
                }
            });
            Menu menu=popup.getMenu();

            if(!isReady())
            {
                menu.findItem(R.id.sort).setEnabled(false);
            }
            popup.show();
        }

        private void showArtLayout(int resourceId,String message,boolean toRetry)  //OK
        {
            try
            {
                loading.setVisibility(View.GONE);
                dialog.setVisibility(View.GONE);
                commonsUtils.showArtLayout(resourceId,message,toRetry,artIcon,artText,artLayout,artButton,mainActivityObject,storageId,2,getFragmentManager().findFragmentById(rootFrameId));

                listView.setVisibility(View.GONE);
            }
            catch (Exception e)
            {
                Log.e(TAG,"showArtLayout()");
            }
        }

        private void setUpStackLayout()
        {

        }

        private void setUpLayer2()
        {
            layer2.setVisibility(View.VISIBLE);
        }

    }


    private boolean analyseLocalStorage()
    {
        File thisDirectory=new File(currentPath);


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


    private boolean isReady()
    {
        return storageAnalyserData.resultMap.size()>0 && isFolderOk;
    }



    public void oldStarted()
    {
        shouldStartOld=true;
    }


    @Override
    public void onDestroy()             //OK
    {

        if(thumbNail_handler!=null)
            thumbNail_handler.removeCallbacks(thumbNail_runnable);
        thumbNailManager.clearThumbCache();

        if(searchHandler!=null)
            searchHandler.removeCallbacks(searchRunnable);

        super.onDestroy();
        Log.i(TAG,"DESTROYED");

        int pos=0;
        try
        {
            if(listView!=null)
            {
                pos=listView.getFirstVisiblePosition();
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
        MyCacheData.getStorageAnalyserData(1).threadId++;
        MyCacheData.getStorageAnalyserData(2).threadId++;
        MyCacheData.getStorageAnalyserData(3).threadId++;
        MyCacheData.getStorageAnalyserData(4).threadId++;
        MyCacheData.getStorageAnalyserData(5).threadId++;

        if(page.getPathList().size()==1)
        {
            mainActivityObject.removePage(page);
            mainActivityObject.setUpViewPager();
            return;
        }

        if((page.getPathList().get(page.getPathList().size()-2)).equals("SearchResult"))    //previous path
        {
            search_fragment backFragment=new search_fragment();
            backFragment.oldStarted();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,backFragment);
            ft.commit();
        }
        else
        {
            storageAnalyser backFragment=new storageAnalyser();
            backFragment.oldStarted();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,backFragment);
            ft.commit();
        }

    }


    private void setUpListOrGrid()
    {
        dialog.setVisibility(View.GONE);
        thumbNailManager.initialiseThumbRunner();
        listView.setVisibility(View.VISIBLE);
        long totalUsed=0;
        totalUsed=storageAnalyserData.used;

        listViewAdapter=new ListViewAdapter(mainActivityObject,myFilesList,storageId,getFragmentManager().findFragmentById(rootFrameId),modValue,5,totalUsed);
        listView.setAdapter(listViewAdapter);

        listView.setSelection(currentIndex);


        ArrayList<LinearLayout> linearLayoutArrayList=new ArrayList<>();
        linearLayoutArrayList.add(layer1);

        if(isFirstAnalyserPath)
        {
            linearLayoutArrayList.add(storageButtonsLayer);
        }
        else
        {
            linearLayoutArrayList.add(layer2);
        }
        clickManager=new ClickManager(mainActivityObject,false,5,currentPath,pageIndex,storageId,getFragmentManager().findFragmentById(rootFrameId),linearLayoutArrayList,myFilesList,null,listView,listViewAdapter);



        clickManager.longClickedList();


    }


    private void openNewStorage(boolean startThread)
    {
        /*
        if startThread is false,its simply refreshing + changing threadId
         */

        MyCacheData.getStorageAnalyserData(1).threadId++;
        MyCacheData.getStorageAnalyserData(2).threadId++;
        MyCacheData.getStorageAnalyserData(3).threadId++;
        MyCacheData.getStorageAnalyserData(4).threadId++;
        MyCacheData.getStorageAnalyserData(5).threadId++;


        storageAnalyser newFragment=new storageAnalyser();
        if(startThread)
        {
            newFragment.startSearchThread();
        }
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,newFragment);
        ft.commit();


    }

    public void startSearchThread()
    {
        this.startSearchThread=true;
    }


    public void openNewPager(String newPath)
    {
        storageAnalyser newFragment=new storageAnalyser();
        newFragment.newStarted(newPath);
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,newFragment);
        ft.commit();
    }

    public void newStarted(String path)
    {
        localPath=path;
        shouldStartNew=true;
    }

    public void reloadPager()
    {
        storageAnalyser newFragment=new storageAnalyser();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,newFragment);
        ft.commit();

    }

    private void getStorageInfo()
    {
        rootFrameId=((ViewGroup)getView().getParent()).getId();
        pageIndex=getPageIndexFromFrameId(rootFrameId);
        Page thisPage=MainActivity.pageList.get(pageIndex);
        PagePathList=thisPage.getPathList();
        PageIndexList=thisPage.getIndexList();
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


    private void notifyAdapter()
    {
        listViewAdapter.notifyDataSetChanged();
    }


}
