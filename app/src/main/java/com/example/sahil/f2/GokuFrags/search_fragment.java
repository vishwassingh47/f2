package com.example.sahil.f2.GokuFrags;


import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.SearchData;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.Classes.SortingMachine;
import com.example.sahil.f2.Classes.ThumbNailsMod;
import com.example.sahil.f2.FunkyAdapters.GridViewAdapter;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuOperations;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.UiClasses.ClickManager;
import com.google.api.services.drive.model.FileList;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod0;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod1;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod2;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getLocalHomeStoragePath;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getPageIndexFromFrameId;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getStorageIdFromPageName;


/**
 * Created by hit4man47 on 9/5/2017.
 */

public class search_fragment extends Fragment
{

    private ImageView home,back,root,search,search_backspace,close,options,artIcon;
    private LinearLayout layer1,layer2,search_layout,stackLinearLayout;
    private EditText search_edit;
    private TextView title,artText,deep_search;
    private ProgressBar progressBar;
    private GridView gridview;
    private RelativeLayout artLayout=null;
    private Button artButton;

    private TextWatcher myTextWatcher;
    private ClickManager clickManager;
    private MySearchManager mySearchManager;
    private ThumbNailManager thumbNailManager;
    private MyUi myUi;
    public CommonsUtils commonsUtils;

    private GridViewAdapter gridViewAdapter;


    private final String TAG="SearchFragment";
    private boolean shouldStartNew=false,shouldStartOld=false;//VERY IMPORTANT

    public String localPath="";

    private Runnable thumbNail_runnable,searchRunnable;
    private Handler thumbNail_handler,searchHandler;
    private boolean continueThumbRun=false;
    private boolean searchRunnableRunning=false;
    private boolean thumbNailRunner_isRunning=false;


    public ThumbNailsMod thumbNailsMod;

    private View view;

    final HelpingBot helpingBot=new HelpingBot();
    public MainActivity mainActivityObject;

    ArrayList<String> PagePathList;
    ArrayList<Integer> PageIndexList;

    private Page thisPage;
    private int modValue;
    private int rootFrameId;
    private int storageId;
    private int pageIndex;//in view pager
    private String pageName;
    private int currentIndex=0;
    private String previousPath;
    private String whereToSearch;
    private String storageFirstPath,storageHomePath;


    private SearchData searchData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved)
    {
        Log.e(TAG,"created");
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_search_fragment1,container,false);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        getStorageInfo();

        thumbNailManager=new ThumbNailManager();
        mySearchManager=new MySearchManager();
        myUi=new MyUi();
        commonsUtils=new CommonsUtils();

        Log.e(TAG,"STARTED"+pageIndex);
        modValue=pageIndex%3;
        thumbNailManager.clearThumbCache();


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
        }

        previousPath=PagePathList.get(PagePathList.size()-2);//path previous to search
        currentIndex=PageIndexList.get(PageIndexList.size()-1);
        if(storageId<=3)
        {
            //sd cards can be opened from internal storage pager
            storageHomePath=getLocalHomeStoragePath(previousPath);
        }
        else
        {   //FOR DROP BOX AND DRIVE
            storageHomePath=storageFirstPath;
        }

        Log.i(pageName,"SEARCH FRAG"+"--"+currentIndex+"--"+previousPath);


        view=getView();
        if(view==null)
        {
            Log.e(TAG,"NO VIEW TO SHOW");
            mainActivityObject.showHideButtons(pageIndex);
            return;
        }
        myUi.initialiseAllUi();


        myUi.setUpLayer1();
        myUi.setUpStackLayout();
        myUi.setUpDeepSearch();
        setUpGrid();
        myUi.setUpEditText();

        deep_search.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        searchRunnableRunning=false;
        mySearchManager.startRunner();

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

    private class MySearchManager
    {

        private void setUpTextWatcher()
        {
            myTextWatcher=new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    Log.e("s is :",s+"--");
                    if(s.length()==0)
                    {
                        Log.e("length is zero","000000000000000000000000000000");
                        searchData.threadId++;
                        searchData.nameToSearch=s.toString().toLowerCase();
                        return;
                    }
                    else
                    {
                        if(s.length()< searchData.nameToSearch.length() || searchData.nameToSearch.length()==0 || searchData.searchingError)
                        {
                            searchData.nameToSearch = s.toString().toLowerCase();
                            Log.e("calling a thread...", (searchData.threadId + 1) + "---" + searchData.nameToSearch);
                            startSearchThread(++searchData.threadId, 5);
                        }
                    }

                    searchData.nameToSearch=s.toString().toLowerCase();

                    if(!searchRunnableRunning)
                        startRunner();

                }

                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            };
            search_edit.addTextChangedListener(myTextWatcher);
        }

        private void startRunner()
        {
            searchHandler=new Handler();
            searchRunnable=new Runnable()
            {
                @Override
                public void run()
                {
                    searchRunnableRunning=true;
                    if(runner())
                    {
                        Log.e("runner running","file to search--:"+ searchData.nameToSearch+"--result size:"+ searchData.result.size());
                        searchHandler.postDelayed(searchRunnable,1000);
                    }
                    else
                    {
                        if(searchData.removeIfNameChanged())
                        {
                            Log.e("removing items...","-------");
                            notifyGridView();
                        }
                        Log.e("runner running","file to search--:"+ searchData.nameToSearch+"--result size:"+ searchData.result.size());
                        Log.e("runner Stopped","file to search--:"+ searchData.nameToSearch+"--result size:"+ searchData.result.size());
                        searchRunnableRunning=false;
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                }
            };
            searchHandler.postDelayed(searchRunnable,1);
        }


        private boolean runner()
        {
            if(searchData.removeIfNameChanged())
            {
                Log.e("removing items...","-------");
                notifyGridView();
            }
            if(!searchData.searching && searchData.searched)   //all the threads has been succesfully completed after searching
            {
                progressBar.setVisibility(View.GONE);
                if(!searchData.isDeepThreadToSearchFinished)
                {
                    deep_search.setVisibility(View.VISIBLE);
                }
                //notifying only one time
                if(searchData.notifyUser && !searchData.searchingError)
                {
                    searchData.notifyUser=false;
                    Log.e("full searched..","************************Search is Completed**************************************");
                    Toast.makeText(mainActivityObject, "******Search is Completed******", Toast.LENGTH_SHORT).show();
                }

                if(searchData.searchingError)
                {
                    if(storageId>=4)
                    {
                        myUi.showArtLayout(R.mipmap.pick_nose,"NO INTERNET CONNECTION",true);
                    }
                    else
                    {
                        myUi.showArtLayout(R.mipmap.pick_nose,"OOPS ERROR OCCURED",true);
                    }
                }
                else if(searchData.result.size()==0 && searchData.nameToSearch.length()>0)
                {
                    myUi.showArtLayout(R.mipmap.empty_folder,"RESULT NOT FOUND",false);
                }

                return false;
            }
            else        //some  thread is running OR NO threads ARE YET STARTED
            {
                progressBar.setVisibility(View.VISIBLE);
                deep_search.setVisibility(View.GONE);
                return true;
            }
        }
        
        private void startSearchThread(final int threadId, final int layers)
        {
            searchData.isDeepSearch=true;
            searchData.searchingError=false;
            searchData.notifyUser=false;
            searchData.isDeepThreadToSearchFinished=false;
            searchData.searched=false;
            searchData.searching=true;

            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {

                    Log.e("thread started...","thread number:    "+threadId+"  --searching in "+whereToSearch);
                    File x = new File(whereToSearch);
                    try
                    {
                        if(storageId<=3)
                            RecursiveStorage(x,threadId,layers);
                        if(storageId==4)
                            searchInDrive(threadId);
                        if(storageId==5)
                            searchInDropBox(threadId);

                    }
                    catch (Exception e)
                    {
                        searchData.searchingError=true;
                        Log.e("ERROR WHILE SEARCHING","---");
                    }


                    if(threadId== searchData.threadId)
                    {
                        searchData.notifyUser=true;
                        Log.e("whole searched",".............................++++++++++++++++++++++++++++");
                        searchData.searched=true;
                        searchData.searching=false;
                        if(searchData.isDeepSearch)
                        {
                            //have traversed the whole storage
                            searchData.isDeepThreadToSearchFinished=true;
                        }
                    }
                    Log.e("thread ended...","thread number:    "+threadId+"      vs current"+ searchData.threadId);
                }
            };
            thread.start();
        }

        private void searchInDrive(int threadId) throws Exception
        {
            com.google.api.services.drive.Drive.Files.List request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id, name, parents,mimeType,quotaBytesUsed,thumbnailLink,webContentLink)").setQ("name contains '"+ searchData.nameToSearch+"'");
            List<com.google.api.services.drive.model.File> files = new ArrayList<com.google.api.services.drive.model.File>();
            do
            {
                FileList filelist = request.execute();
                files.addAll(filelist.getFiles());
                request.setPageToken(filelist.getNextPageToken());
            }
            while (request.getPageToken() != null && request.getPageToken().length() > 0);
            Log.e("files list size",files.size()+"==");
            if(threadId!= searchData.threadId )
            {
                return;
            }
            for(com.google.api.services.drive.model.File child:files)
            {
                if(searchData.nameToSearch.length()>0)
                    if(child.getName().toLowerCase().contains(searchData.nameToSearch))
                    {
                        if(child.getParents()==null || child.getParents().size()==0)
                            continue;
                        if(child.getParents().get(0).equals(whereToSearch))
                        {
                            //not working
                        }
                        String path=child.getId();
                        if(!searchData.containsPath(path)) //not added before
                        {
                            final MyFile file=new MyFile();
                            file.setName(child.getName());
                            file.setPath(path);
                            file.setFavourite(favouritesCache.favouritePaths.contains(path));
                            file.setChecked(false);
                            file.setSymLink(false);
                            file.setFileId(path);
                            file.setThumbUrl(child.getThumbnailLink()+ "@#$" + file.getFileId());
                            try
                            {
                                file.setLastModified(child.getModifiedTime().getValue());
                            }
                            catch (Exception e)
                            {
                                file.setLastModified(0);
                            }

                            if(child.getMimeType().contains("folder"))
                            {
                                file.setFolder(true);
                                file.setSize("");
                                file.setSizeLong(0);
                            }
                            else
                            {
                                file.setFolder(false);
                                file.setSize(helpingBot.sizeinwords(child.getQuotaBytesUsed()));
                                file.setSizeLong(child.getQuotaBytesUsed());
                            }
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    synchronized (searchData.result)
                                    {
                                        searchData.result.add(file);
                                        notifyGridView();
                                    }
                                }
                            };
                            mainHandler.post(myRunnable);
                        }
                    }
            }
        }

        private void searchInDropBox(int threadId) throws Exception
        {
            List <com.dropbox.core.v2.files.Metadata> list=new ArrayList<>();
            ListFolderResult result = DropBoxConnection.mDbxClient.files().listFolderBuilder(whereToSearch).withRecursive(true).start();
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

            if(threadId!= searchData.threadId )
            {
                return;
            }

            for (Metadata child : list)
            {
                if(searchData.nameToSearch.length()>0)
                    if(child.getName().toLowerCase().contains(searchData.nameToSearch))
                    {
                        String path=child.getPathDisplay();
                        if(!searchData.containsPath(path)) //not added before
                        {
                            final MyFile file=new MyFile();
                            file.setName(child.getName());
                            file.setPath(path);
                            file.setFavourite(favouritesCache.favouritePaths.contains(path));
                            file.setChecked(false);
                            file.setSymLink(false);
                            if( child instanceof FolderMetadata)
                            {
                                file.setFolder(true);
                                file.setSize("");
                                file.setSizeLong(0);
                                file.setFileId(((FolderMetadata)child).getId());
                                file.setThumbUrl(null);
                                file.setLastModified(0);//DROPBOX FOLDER HAS NO MODIFIED TIME VARIABLE
                            }
                            else
                            {
                                if(child instanceof FileMetadata)
                                {
                                    file.setFolder(false);
                                    file.setSize(helpingBot.sizeinwords(((FileMetadata) child).getSize()));
                                    file.setSizeLong(((FileMetadata) child).getSize());
                                    file.setFileId(((FileMetadata)child).getId());
                                    file.setThumbUrl(path + "@#$" + file.getFileId());
                                    file.setLastModified(((FileMetadata)child).getClientModified().getTime());
                                }
                                else
                                {
                                    continue;
                                }
                            }

                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    synchronized (searchData.result)
                                    {
                                        searchData.result.add(file);
                                        notifyGridView();
                                    }
                                }
                            };
                            mainHandler.post(myRunnable);
                        }
                    }
            }
        }

        private void RecursiveStorage(final File fileOrDirectory,final int threadId,final int layer) throws Exception
        {
            String path;
            if(layer<=0)
            {
                searchData.isDeepSearch=false;
            }
            if(threadId!= searchData.threadId)
            {
                return;
            }
            File[] files;
            try
            {
                files=fileOrDirectory.listFiles();
            }
            catch (Exception e)
            {
                files=null;
            }
            if(files==null && SuperUser.hasUserEnabledSU)
            {
                /*
                SEARCH AS ROOT
                THIS IS A BLOCKING CALL
                 */
                searchInRootX(threadId);
                return;
            }


            for (File child : files)
            {
                if(searchData.nameToSearch.length()>0)
                    if(child.getName().toLowerCase().contains(searchData.nameToSearch))
                    {
                        path=child.getAbsolutePath();
                        if(!searchData.containsPath(path)) //not added before
                        {
                            final MyFile file=new MyFile();
                            file.setName(child.getName());
                            file.setPath(child.getAbsolutePath());
                            file.setFavourite(favouritesCache.favouritePaths.contains(child.getAbsolutePath()));
                            file.setChecked(false);
                            file.setSymLink(false);
                            file.setFileId(null);
                            file.setThumbUrl(path);
                            file.setLastModified(child.lastModified());

                            if(child.isDirectory())
                            {
                                file.setFolder(true);
                                file.setSize("");
                                file.setSizeLong(0);
                            }
                            else
                            {
                                file.setFolder(false);
                                file.setSize(helpingBot.sizeinwords(child.length()));
                                file.setSizeLong(child.length());
                            }
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    synchronized (searchData.result)
                                    {
                                        searchData.result.add(file);
                                        notifyGridView();
                                    }
                                }
                            };
                            mainHandler.post(myRunnable);
                        }
                    }

                if(child.isDirectory())
                {
                    RecursiveStorage(child,threadId,layer-1);
                }
            }
        }


        private void searchInRootX(final int threadId) throws Exception
        {
            File myLsFile=mainActivityObject.getFileStreamPath("myls");
            if(searchData.nameToSearch.length()==0)
            {
                return;
            }
            final String commandString=myLsFile.getAbsolutePath()+" 3 '"+whereToSearch+"' '"+searchData.nameToSearch+"'";
            Log.e("Running command",commandString);
            Command command= new Command(threadId,60000,commandString)
            {
                @Override
                public void commandOutput(int id, String line)
                {
                    super.commandOutput(id, line);
                    if(searchData.threadId!=threadId)
                    {
                        return;
                    }
                    Log.e("Output",line+"--"+id);

                    String s[]=line.split("#@\\$");
                    if(s.length==6)
                    {
                        if(searchData.containsPath(s[5]))
                        {
                            return;
                        }

                        final MyFile file=new MyFile();
                        file.setPermission(s[1]);
                        file.setName(s[2]);
                        file.setPath(s[5]);
                        file.setLastModified((long)(HelpingBot.parseLong(s[4])*1000));
                        int x=HelpingBot.parseInt(s[0]);
                        long size;
                        switch (x)
                        {
                            case 1:
                                file.setSymLink(false);
                                file.setFolder(false);
                                size=HelpingBot.parseLong(s[3]);
                                file.setSizeLong(size);
                                file.setSize(helpingBot.sizeinwords(size));
                                break;
                            case 2:
                                file.setSymLink(false);
                                file.setFolder(true);
                                file.setSizeLong(0);
                                file.setSize("");
                                break;
                            default:
                                return;
                        }
                        file.setChecked(false);
                        file.setFavourite(favouritesCache.favouritePaths.contains(s[5]));
                        file.setFileId(null);
                        file.setThumbUrl(s[5]);

                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                synchronized (searchData.result)
                                {
                                    searchData.result.add(file);
                                    notifyGridView();
                                }
                            }
                        };
                        mainHandler.post(myRunnable);

                    }
                }
            };

            try
            {
                Shell shell= RootTools.getShell(true);
                shell.add(command);
                RootShell.commandWait(Shell.startRootShell(), command);
            }
            catch (Exception e)
            {
                Log.e("Error",e.getLocalizedMessage()+e.getMessage()+"--");
            }
        }
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


            stackLinearLayout=(LinearLayout)view.findViewById(R.id.stackLinearLayout_searchPager);

            layer2=(LinearLayout)view.findViewById(R.id.layer2_searchPager);
            search_edit=(EditText) view.findViewById(R.id.searchPager_editText);
            search_backspace=(ImageView) view.findViewById(R.id.searchPager_backspace);


            progressBar=(ProgressBar) view.findViewById(R.id.searchPager_progress);
            deep_search=(TextView)view.findViewById(R.id.searchPager_deep_search);

            gridview=(GridView) view.findViewById(R.id.grid_searchPager);

            artLayout=(RelativeLayout)view.findViewById(R.id.art_layout);
            artIcon=(ImageView) view.findViewById(R.id.art_layout_icon);
            artText=(TextView) view.findViewById(R.id.art_layout_text);
            artButton=(Button) view.findViewById(R.id.art_layout_retry);

        }

        private void setUpLayer1()
        {
            home.setVisibility(View.VISIBLE);

            back.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
            search_layout.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
            options.setVisibility(View.VISIBLE);


            if(storageId<=3)
            {
                root.setVisibility(View.VISIBLE);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        openNewPager("/");
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
                    openNewPager(storageFirstPath);
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

        }

        private void setUpTitle()
        {
            if(previousPath.equals("/") && storageId<=3)
            {
                title.setText("ROOT DIRECTORY");
            }
            else
            {
                if(storageId<=3)
                    title.setText(previousPath.substring(previousPath.lastIndexOf('/')+1));

                if(storageId==4)
                    title.setText("Google Drive ("+previousPath+")");

                if(storageId==5)
                {
                    if(previousPath.equals(""))
                        title.setText("Drop Box");
                    else
                        title.setText(previousPath.substring(previousPath.lastIndexOf('/')+1));
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
                        variablesCache.sortSearchBy=10;
                        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortSearchBy);
                        sortingMachine.sortMyFile(searchData.result);
                        gridViewAdapter.notifyDataSetChanged();
                    }

                    if(item.getItemId()==R.id.sort_name1)
                    {
                        variablesCache.sortSearchBy=1;
                        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortSearchBy);
                        sortingMachine.sortMyFile(searchData.result);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                    if(item.getItemId()==R.id.sort_name2)
                    {
                        variablesCache.sortSearchBy=2;
                        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortSearchBy);
                        sortingMachine.sortMyFile(searchData.result);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                    if(item.getItemId()==R.id.sort_date1)
                    {
                        variablesCache.sortSearchBy=3;
                        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortSearchBy);
                        sortingMachine.sortMyFile(searchData.result);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                    if(item.getItemId()==R.id.sort_date2)
                    {
                        variablesCache.sortSearchBy=4;
                        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortSearchBy);
                        sortingMachine.sortMyFile(searchData.result);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                    if(item.getItemId()==R.id.sort_size1)
                    {
                        variablesCache.sortSearchBy=5;
                        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortSearchBy);
                        sortingMachine.sortMyFile(searchData.result);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                    if(item.getItemId()==R.id.sort_size2)
                    {
                        variablesCache.sortSearchBy=6;
                        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortSearchBy);
                        sortingMachine.sortMyFile(searchData.result);
                        gridViewAdapter.notifyDataSetChanged();
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

            if(!isReady())
            {
                menu.findItem(R.id.sort).setEnabled(false);
            }

            MenuPopupHelper menuHelper=new MenuPopupHelper(mainActivityObject,(MenuBuilder)menu,options);
            menuHelper.setForceShowIcon(true);
            menuHelper.setGravity(Gravity.END);
            menuHelper.show();
        }

        private void setUpBackSpace()
        {
            search_backspace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    search_edit.setText("");
                    searchData.nameToSearch="";
                }
            });
        }

        private void showArtLayout(int resourceId,String message,boolean toRetry)  //OK
        {
            try
            {
                commonsUtils.showArtLayout(resourceId,message,toRetry,artIcon,artText,artLayout,artButton,mainActivityObject,storageId,3,getFragmentManager().findFragmentById(rootFrameId));
                gridview.setVisibility(View.GONE);
            }
            catch (Exception e)
            {
                Log.e(TAG,"showArtLayout()");
            }
        }

        private void setUpDeepSearch()
        {
            deep_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    mySearchManager.startSearchThread(++searchData.threadId,10000);

                    if(!searchRunnableRunning)
                        mySearchManager.startRunner();
                    //Log.e("calling deep thread...",(searchCache.threadId+1)+"--------------");
                    deep_search.setVisibility(View.GONE);
                }
            });
        }

        private void setUpEditText()// we have to start search thread again when this fragment is reopeened && if errorSearching is true
        {
            search_edit.setHint("Search in :"+previousPath);
            if(storageId==4)
                search_edit.setHint("Search in :Google Drive");
            if(storageId==5 && storageFirstPath.equals(previousPath))
                search_edit.setHint("Search in :DropBox");

            search_edit.setText("");
            boolean watcherSet=false;
            if(searchData.searchingError)
            {
                watcherSet=true;
                mySearchManager.setUpTextWatcher();
            }
            search_edit.append(searchData.nameToSearch);//MOVING THE CURSOR AT THE END


            if(!watcherSet)//to prevent setting twice
            {
                mySearchManager.setUpTextWatcher();
            }


            setUpBackSpace();
        }

        private void setUpStackLayout()
        {
            stackLinearLayout.removeAllViews();

            View layout;
            for(String x:PagePathList)
            {
                layout=LayoutInflater.from(getActivity()).inflate(R.layout.layoutof_stack,stackLinearLayout,false);
                ImageView imageView=(ImageView)layout.findViewById(R.id.stack_logo);
                if(x.equals("SearchResult"))
                    imageView.setImageResource(R.drawable.search_icon);
                else
                    imageView.setImageResource(MainActivity.pageList.get(pageIndex).getIconId());

                stackLinearLayout.addView(layout);
            }

        }

    }

    private boolean isReady()
    {
        return !searchRunnableRunning;
    }


    private void setUpGrid()
    {
        thumbNailManager.initialiseThumbRunner();

        gridview.setVisibility(View.VISIBLE);

        gridViewAdapter=new GridViewAdapter(mainActivityObject,searchData.result,storageId,getFragmentManager().findFragmentById(rootFrameId),modValue,2,0);
        gridview.setAdapter(gridViewAdapter);
        gridview.setSelection(currentIndex);

        ArrayList<LinearLayout> linearLayoutArrayList=new ArrayList<>();
        linearLayoutArrayList.add(layer1);
        linearLayoutArrayList.add(layer2);
        clickManager=new ClickManager(mainActivityObject,false,2,"Search Results",pageIndex,storageId,getFragmentManager().findFragmentById(rootFrameId),linearLayoutArrayList,searchData.result,null,gridview,gridViewAdapter);
        clickManager.longClickedGrid();
    }


    public void notifyGridView()
    {
        if(artLayout.getVisibility()==View.VISIBLE)
        {
            artLayout.setVisibility(View.GONE);
        }

        if(gridview.getVisibility()!=View.VISIBLE)
        {
            gridview.setVisibility(View.VISIBLE);
        }

        gridViewAdapter.notifyDataSetChanged();
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


    public void reloadPager()
    {
        search_fragment currentFragment=new search_fragment();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,currentFragment);
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


    public void backPressed(Page page)
    {
        HelpingBot.hideKeyboard(mainActivityObject);
        searchData.threadId++;


        if(page.getPageId()==12345)
        {
            storagePager backFragment=new storagePager();
            backFragment.oldStarted();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,backFragment);
            ft.commit();
        }
        if(page.getPageId()==11)
        {
            storageAnalyser backFragment=new storageAnalyser();
            backFragment.oldStarted();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,backFragment);
            ft.commit();
        }

    }

    @Override
    public void onDestroy()             //OK
    {
        if(thumbNail_handler !=null)
            thumbNail_handler.removeCallbacks(thumbNail_runnable);

        thumbNailManager.clearThumbCache();

        if(searchHandler!=null)
            searchHandler.removeCallbacks(searchRunnable);

        super.onDestroy();
        Log.e(TAG,"DESTROYED"+pageIndex);

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

        PageIndexList.set(PageIndexList.size()-1,pos);

    }

    private void getStorageInfo()
    {

        rootFrameId=((ViewGroup)getView().getParent()).getId();

        pageIndex=getPageIndexFromFrameId(rootFrameId);
        thisPage=MainActivity.pageList.get(pageIndex);
        PagePathList=thisPage.getPathList();
        PageIndexList=thisPage.getIndexList();
        storageFirstPath=PagePathList.get(0);
        pageName=MainActivity.pageList.get(pageIndex).getName();
        if(thisPage.getPageId()==11)
        {
            storageId= MyCacheData.GlobalStorageAnalyser.storageId;
        }
        else//12345
        {
            storageId=getStorageIdFromPageName(pageName);
        }

        searchData=MyCacheData.getSearchData(storageId);
        whereToSearch= searchData.whereToSearch;
    }



}
