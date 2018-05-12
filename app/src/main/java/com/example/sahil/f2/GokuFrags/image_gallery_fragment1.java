package com.example.sahil.f2.GokuFrags;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.provider.DocumentFile;
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
import android.webkit.MimeTypeMap;
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
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.GalleryData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.GalleryData;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.CommonsUtils;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyContainer;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.Classes.SortingMachine;
import com.example.sahil.f2.Classes.ThumbNails;
import com.example.sahil.f2.Classes.ThumbNailsMod;
import com.example.sahil.f2.FunkyAdapters.GridViewAdapter;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.UiClasses.ClickManager;
import com.example.sahil.f2.Utilities.ExtensionUtil;
import com.google.api.services.drive.model.FileList;


import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod0;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod1;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod2;
import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;
import static com.example.sahil.f2.MainActivity.pageList;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getPageIndexFromFrameId;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by hit4man47 on 11/20/2017.
 */

public class image_gallery_fragment1 extends Fragment
{

    //UI
    private ImageView home,back,root,search,search_backspace,close,options,storage1,storage2,storage3,storage4,storage5,artIcon;
    private LinearLayout layer1,layer2,search_layout;
    private EditText search_edit;
    private TextView title,path_fetched,artText;
    private ProgressBar progressBar;
    private GridView gridview;
    private RelativeLayout artLayout=null;
    private Button artButton;

    private int galleryType;
    private GalleryData galleryData;

    private final String TAG="ImageGalleryFrag1";
    private ArrayList<MyFile> myFilesList=null;
    private ArrayList<MyContainer> myContainerList=null;
    private ArrayList<MyContainer> globalStorageResultReference;

    private View view;

    private boolean shouldStartNew=false,shouldStartOld=false;//VERY IMPORTANT

    private Runnable thumbNailRunnable,searchRunnable;
    private boolean thumbNailRunner_isRunning;
    private Handler thumbNailHandler,searchHandler;
    private boolean startSearchThread=false;//very important

    private ExtensionUtil extensionUtil;

    private ThumbNailManager thumbNailManager;
    public CommonsUtils commonsUtils;

    private int modValue;
    private Runnable thumbNail_runnable;
    private Handler thumbNail_handler;

    public ThumbNailsMod thumbNailsMod;

    private GridViewAdapter gridViewAdapter;


    private TextWatcher myTextWatcher;
    private ClickManager clickManager;
    private MySearchManager mySearchManager;
    private MyImageFetcher myImageFetcher;
    private MyUi myUi;


    final HelpingBot helpingBot=new HelpingBot();
    private Page thisPage;
    private  int rootFrameId;
    private int pageIndex;
    private String pageName;
    public MainActivity mainActivityObject;

    private boolean continueThumbRun=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contaimer, Bundle saved)
    {
        mainActivityObject=(MainActivity)getActivity();
        return inflater.inflate(R.layout.layoutof_imagegallery,contaimer,false);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        getStorageInfo();

        if(galleryData.storageId ==0 && galleryData.storagePath ==null && galleryData.threadIdPager1 ==-5)
        {
            //FIRST TIME OPENED
            shouldStartNew=true;
            startSearchThread=true;
            galleryData.storagePath =Physical_Storage_PATHS.get(0);
            galleryData.storageId =1;
        }



        extensionUtil=new ExtensionUtil();
        galleryData.softClearGallery();
        myImageFetcher =new MyImageFetcher();
        myUi=new MyUi();
        commonsUtils=new CommonsUtils();

        thumbNailManager=new ThumbNailManager();
        mySearchManager=new MySearchManager();
        modValue=pageIndex%3;
        thumbNailManager.clearThumbCache();
        //back button is pressed
        if(shouldStartOld)
        {
            shouldStartOld=false;
            thisPage.getPathList().remove(thisPage.getPathList().size()-1);
            thisPage.getIndexList().remove(thisPage.getIndexList().size()-1);
        }

        if(shouldStartNew)
        {
            //this fragment is new opened
            shouldStartNew=false;
            thisPage.getIndexList().set( thisPage.getIndexList().size()-1,0);
        }
        else
        {
            //this fragment is revisited after swipes
            startSearchThread=false;
            //we dont need to start search again as thread was already started  in this fragment was new opened
        }


        view=getView();
        myUi.initialiseAllUi();



        if(startSearchThread)
        {
            startSearchThread=false;
            switch (galleryData.storageId)
            {
                case 1:
                    galleryData.threadIdPager1 +=10;
                    galleryData.successfullySearchedPager1 =false;
                    galleryData.searchingPager1 =true;
                    galleryData.searchResultsPager1.clear();
                    myImageFetcher.searchInPager123(galleryData.storagePath,galleryData.threadIdPager1,1);
                    break;

                case 2:
                    galleryData.threadIdPager2 +=10;
                    galleryData.successfullySearchedPager2 =false;
                    galleryData.searchingPager2 =true;
                    galleryData.searchResultsPager2.clear();
                    myImageFetcher.searchInPager123(galleryData.storagePath,galleryData.threadIdPager2,2);
                    break;

                case 3:
                    galleryData.threadIdPager3 +=10;
                    galleryData.successfullySearchedPager3 =false;
                    galleryData.searchingPager3 =true;
                    galleryData.searchResultsPager3.clear();
                    myImageFetcher.searchInPager123(galleryData.storagePath,galleryData.threadIdPager3,3);
                    break;
                case 4:
                    galleryData.threadIdPager4 +=10;
                    galleryData.successfullySearchedPager4 =false;
                    galleryData.searchingPager4 =true;
                    galleryData.searchResultsPager4.clear();
                    myImageFetcher.searchInPager4(galleryData.storagePath,galleryData.threadIdPager4);
                    break;
                case 5:
                    galleryData.threadIdPager5 +=10;
                    galleryData.successfullySearchedPager5 =false;
                    galleryData.searchingPager5 =true;
                    galleryData.searchResultsPager5.clear();
                    myImageFetcher.searchInPager5(galleryData.storagePath,galleryData.threadIdPager5);
                    break;
            }
        }

        myImageFetcher.runFetcherRunner();


        if(galleryData.storageId >=4)
        {
            makeNesessaryDirectories();
        }


        /*
        checking for storages available

         */
        myUi.setUpStorageButtons();
        myUi.setUpLayer1();

        String path="";
        if(galleryType==1)
            path="IMAGES IN : "+galleryData.storagePath;
        if(galleryType==2)
            path="VIDEOS IN : "+galleryData.storagePath;
        if(galleryType==3)
            path="AUDIOS IN : "+galleryData.storagePath;
        if(galleryType==4)
            path="APK's IN : "+galleryData.storagePath;
        
        path_fetched.setText(path);

        setGlobalStorageResultReference(galleryData.storageId);
        //gridShortClickSetter();

        mainActivityObject.showHideButtons(pageIndex);

    }


    private void setGlobalStorageResultReference(int storageId)
    {
        switch (storageId)
        {
            case 1:
                globalStorageResultReference=galleryData.searchResultsPager1;
                break;
            case 2:
                globalStorageResultReference=galleryData.searchResultsPager2;
                break;
            case 3:
                globalStorageResultReference=galleryData.searchResultsPager3;
                break;
            case 4:
                globalStorageResultReference=galleryData.searchResultsPager4;
                break;
            case 5:
                globalStorageResultReference=galleryData.searchResultsPager5;
                break;
        }
    }



    public void openInnerPager(MyContainer container)
    {
        galleryData.whatToSearch=null;
        try
        {
            image_gallery_fragment2 newFragment=new image_gallery_fragment2();
            newFragment.newStarted(container);
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,newFragment);
            ft.commit();
        }
        catch (Exception e)
        {}
       
        
    }

    private void makeNesessaryDirectories()
    {
        if(Physical_Storage_PATHS.size()==0)
        {
            Toast.makeText(mainActivityObject, "cannot write to any local storage to save the cloud thumbnails", Toast.LENGTH_LONG).show();
            return;
        }

        final String DROPBOX_THUMB_PATH=Physical_Storage_PATHS.get(0)+"/f2/Cache/.ThumbNails/DropBox";
        final String DRIVE_THUMB_PATH=  Physical_Storage_PATHS.get(0)+"/f2/Cache/.ThumbNails/Google Drive";

        File rootFolder=new File(Physical_Storage_PATHS.get(0));
        File dropbox=new File(DROPBOX_THUMB_PATH);
        if(!dropbox.exists())
        {
            if(rootFolder.canWrite())
            {
                boolean x=dropbox.mkdirs();
            }
            else
            {
                //DocumentFile df = StorageAccessFramework.fileToDocumentFileConverter(dropbox.getAbsolutePath(),true,mainActivityObject,Physical_Storage_PATHS.get(0));
            }
        }
        File drive=new File(DRIVE_THUMB_PATH);
        if(!drive.exists())
        {
            if(rootFolder.canWrite())
            {
                boolean x=drive.mkdirs();
            }
            else
            {
                //DocumentFile df = StorageAccessFramework.fileToDocumentFileConverter(drive.getAbsolutePath(),true,mainActivityObject,Physical_Storage_PATHS.get(0));
            }
        }

        if(!drive.exists() || !dropbox.exists())
        {
            Toast.makeText(mainActivityObject, "cannot write to any local storage to save the cloud thumbnails", Toast.LENGTH_LONG).show();
        }
    }

    private void recurrsionPager123(final File folder,final int layer,final int threadId,final int storageId)
    {

        if(layer==0)
            return ;

        String thumbPath="";
        long dateModified=0;
        int total=0;

        File[] files=folder.listFiles();
        ArrayList<File> imageFileList=new ArrayList<>();
        for(File child:files)
        {

            if(storageId==1 && threadId!=galleryData.threadIdPager1)
            {
                return;
            }
            if(storageId==2 && threadId!=galleryData.threadIdPager2)
            {
                return;
            }
            if(storageId==3 && threadId!=galleryData.threadIdPager3)
            {
                return;
            }

            if(child.isDirectory())
            {
                recurrsionPager123(child,layer-1,threadId,storageId);
            }
            else
            {       //IS FILE
                if(child.getName().endsWith(".png") || child.getName().endsWith(".jpg") ||child.getName().endsWith("jpeg"))
                {

                    imageFileList.add(child);
                    if(dateModified<child.lastModified())
                    {
                        dateModified=child.lastModified();
                        thumbPath=child.getAbsolutePath();
                    }
                    total++;
                }
            }



        }

        if(total>0 && storageId==1 && threadId==galleryData.threadIdPager1)
        {
            synchronized (galleryData.searchResultsPager1)
            {
                //galleryData.searchResultsPager1.add(new IGSFile(folder.getAbsolutePath(),thumbPath,dateModified,total,imageFileList));
            }
            return;
        }
        if(total>0 && storageId==2 && threadId==galleryData.threadIdPager2)
        {
            synchronized (galleryData.searchResultsPager2)
            {
                //galleryData.searchResultsPager2.add(new IGSFile(folder.getAbsolutePath(),thumbPath,dateModified,total,imageFileList));
            }
            return;
        }

        if(total>0 && storageId==3 && threadId==galleryData.threadIdPager3)
        {
            synchronized (galleryData.searchResultsPager3)
            {
                //galleryData.searchResultsPager3.add(new IGSFile(folder.getAbsolutePath(),thumbPath,dateModified,total,imageFileList));
            }
        }

    }


    private class MyImageFetcher
    {

        private void searchInPager123(final String rootPath,final int threadId,final int storageId)
        {

            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    Log.e("search thread started:",threadId+"--"+rootPath);
                    boolean errorOccured=false;
                    try
                    {
                        ArrayList<MyContainer> searchResultReference=null;
                        switch (storageId)
                        {
                            case 1:
                                searchResultReference=galleryData.searchResultsPager1;
                                break;
                            case 2:
                                searchResultReference=galleryData.searchResultsPager2;
                                break;
                            case 3:
                                searchResultReference=galleryData.searchResultsPager3;
                                break;
                        }

                        String[] projection=new String[3];//Which columns to return
                        String extension=null;
                        switch (galleryType)
                        {
                            //MediaStore.Images.Media._ID,
                            //MediaStore.Images.Media.BUCKET_ID,
                            //MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                            case 1:
                                projection[0]= MediaStore.Images.Media.DATA;
                                projection[1]= MediaStore.Images.Media.SIZE;
                                projection[2]= MediaStore.Images.Media.DATE_MODIFIED;
                                extension=null;
                                break;
                            case 2:
                                projection[0]= MediaStore.Video.Media.DATA;
                                projection[1]= MediaStore.Video.Media.SIZE;
                                projection[2]= MediaStore.Video.Media.DATE_MODIFIED;
                                extension=null;
                                break;
                            case 3:
                                projection[0]= MediaStore.Audio.Media.DATA;
                                projection[1]= MediaStore.Audio.Media.SIZE;
                                projection[2]= MediaStore.Audio.Media.DATE_MODIFIED;
                                extension=null;
                                break;
                            case 4:
                                projection[0]= MediaStore.Files.FileColumns.DATA;
                                projection[1]= MediaStore.Files.FileColumns.SIZE;
                                projection[2]= MediaStore.Files.FileColumns.DATE_MODIFIED;
                                extension=".apk";
                        }


                        Uri uri=null;
                        Cursor cursor;
                        for(int I=1;I<=2;I++)
                        {
                            if(storageId==1 && threadId!=galleryData.threadIdPager1)
                            {
                                break;
                            }
                            if(storageId==2 && threadId!=galleryData.threadIdPager2)
                            {
                                break;
                            }
                            if(storageId==3 && threadId!=galleryData.threadIdPager3)
                            {
                                break;
                            }

                            switch (galleryType)
                            {
                                case 1:
                                    if(I==1)
                                    {
                                        uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                                    }
                                    else
                                    {
                                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    }
                                    break;
                                case 2:
                                    if(I==1)
                                    {
                                        uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                                    }
                                    else
                                    {
                                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                    }
                                    break;
                                case 3:
                                    if(I==1)
                                    {
                                        uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                                    }
                                    else
                                    {
                                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                    }
                                    break;
                                case 4:
                                    if(I==1)
                                    {
                                        uri = MediaStore.Files.getContentUri("internal");
                                    }
                                    else
                                    {
                                        uri = MediaStore.Files.getContentUri("external");
                                    }
                                    break;
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
                                    if(storageId==1 && threadId!=galleryData.threadIdPager1)
                                    {
                                        break;
                                    }
                                    if(storageId==2 && threadId!=galleryData.threadIdPager2)
                                    {
                                        break;
                                    }
                                    if(storageId==3 && threadId!=galleryData.threadIdPager3)
                                    {
                                        break;
                                    }


                                    String path=null;
                                    long lastModified=0;
                                    long size=0;


                                    switch (galleryType)
                                    {
                                        case 1:
                                            path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                            lastModified=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                                            size=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
                                            break;
                                        case 2:
                                            path=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                                            lastModified=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)));
                                            size=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                                            break;
                                        case 3:
                                            path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                                            lastModified=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));
                                            size=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                                            break;
                                        case 4:
                                            path=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                                            lastModified=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)));
                                            size=HelpingBot.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)));
                                            break;
                                    }

                                    //for ".apks"
                                    if(extension!=null && !path.endsWith(extension))
                                    {
                                        continue;
                                    }

                                    String folderPath=path.substring(0,path.lastIndexOf('/'));
                                    String name=path.substring(path.lastIndexOf('/')+1);

                                    if(path.contains(rootPath))
                                    {
                                        int index=-1;
                                        for(MyContainer x: searchResultReference)
                                        {
                                            if(x.getPath().equals(folderPath))
                                            {
                                                index=searchResultReference.indexOf(x);
                                                break;
                                            }
                                        }

                                        MyFile myFile=new MyFile();

                                        myFile.setSymLink(false);
                                        myFile.setChecked(false);
                                        myFile.setThumbUrl(path);
                                        myFile.setFileId(null);
                                        myFile.setName(name);
                                        myFile.setPath(path);
                                        myFile.setFavourite(favouritesCache.favouritePaths.contains(path));

                                        myFile.setLastModified(lastModified);

                                        myFile.setFolder(false);
                                        myFile.setSizeLong(size);
                                        myFile.setSize(helpingBot.sizeinwords(size));


                                        if(index<0)
                                        {
                                            //folder not present in search result
                                            synchronized (searchResultReference)
                                            {
                                                String folderName=folderPath.substring(folderPath.lastIndexOf('/')+1);
                                                MyContainer myContainer=new MyContainer();
                                                myContainer.setPath(folderPath);
                                                myContainer.setThumbUrl(path);
                                                myContainer.setLastModified(lastModified);
                                                myContainer.getMyFileArrayList().add(myFile);
                                                myContainer.setName(folderName);


                                                MyFile myFileFolder=new MyFile();
                                                myFileFolder.setSymLink(false);
                                                myFileFolder.setChecked(false);
                                                myFileFolder.setThumbUrl(path);
                                                myFileFolder.setFileId(null);
                                                myFileFolder.setName(folderName);
                                                myFileFolder.setPath(folderPath);
                                                myFileFolder.setFavourite(favouritesCache.favouritePaths.contains(folderPath));
                                                myFileFolder.setLastModified(lastModified);
                                                myFileFolder.setFolder(true);
                                                myFileFolder.setSizeLong(0);
                                                myFileFolder.setSize("");

                                                myContainer.setMyFile(myFileFolder);

                                                searchResultReference.add(myContainer);
                                            }
                                        }
                                        else
                                        {
                                            MyContainer myContainer=searchResultReference.get(index);

                                            if(myContainer.getLastModified()<lastModified)
                                            {

                                                myContainer.setLastModified(lastModified);
                                                myContainer.setThumbUrl(path);
                                                myContainer.getMyFile().setLastModified(lastModified);
                                                myContainer.getMyFile().setThumbUrl(path);
                                            }
                                            myContainer.getMyFileArrayList().add(myFile);
                                        }
                                    }
                                }
                                cursor.close();
                            }
                        }
                    }
                    catch (final Exception e)
                    {
                        Log.e("search thread ERROR",e.getLocalizedMessage()+"--");
                        errorOccured=true;
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                myUi.showArtLayout(R.mipmap.pick_nose,"ERROR OCCURED ( "+e.getLocalizedMessage()+e.getMessage()+e.getStackTrace().toString()+" )",true);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                    finally
                    {
                        if(storageId==1 && threadId==galleryData.threadIdPager1)
                        {
                            galleryData.searchingPager1 =false;
                            if(!errorOccured)
                            {
                                galleryData.successfullySearchedPager1 =true;
                            }
                        }
                        if(storageId==2 && threadId==galleryData.threadIdPager2)
                        {
                            galleryData.searchingPager2 =false;
                            if(!errorOccured)
                            {
                                galleryData.successfullySearchedPager2 =true;
                            }
                        }
                        if(storageId==3 && threadId==galleryData.threadIdPager3)
                        {
                            galleryData.searchingPager3 =false;

                            if(!errorOccured)
                            {
                                galleryData.successfullySearchedPager3 =true;
                            }
                        }
                        Log.e("search thread ended:",threadId+"--"+rootPath);
                    }
                }
            };
            thread.start();

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
                        com.google.api.services.drive.Drive.Files.List request=null;
                        switch (galleryType)
                        {
                            case 1:
                                request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,thumbnailLink,quotaBytesUsed,modifiedTime,parents),nextPageToken").setQ("mimeType contains 'image/'");
                                break;
                            case 2:
                                request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,thumbnailLink,quotaBytesUsed,modifiedTime,parents),nextPageToken").setQ("mimeType contains 'video/'");
                                break;
                            case 3:
                                request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,thumbnailLink,quotaBytesUsed,modifiedTime,parents),nextPageToken").setQ("mimeType contains 'audio/'");
                                break;
                            case 4:
                                request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,thumbnailLink,quotaBytesUsed,modifiedTime,parents),nextPageToken");
                                break;
                        }
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
                            if(threadId!=galleryData.threadIdPager4)
                            {
                                break;
                            }
                            if(galleryType==4)
                                if(!file.getName().endsWith(".apk"))
                                {
                                    return;
                                }

                            if(file.getParents()==null || file.getParents().size()==0)
                                continue;

                            String path=file.getId();
                            String folderPath=file.getParents().get(0);
                            String name=file.getName();
                            String thumbUrl=file.getThumbnailLink()+ "@#$" + file.getId();
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


                            int index=-1;
                            for(MyContainer x: galleryData.searchResultsPager4)
                            {
                                if(x.getPath().equals(folderPath))
                                {
                                    index=galleryData.searchResultsPager4.indexOf(x);
                                    break;
                                }
                            }

                            MyFile myFile=new MyFile();

                            myFile.setSymLink(false);
                            myFile.setChecked(false);
                            myFile.setThumbUrl(thumbUrl);
                            myFile.setFileId(path);
                            myFile.setName(name);
                            myFile.setPath(path);
                            myFile.setFavourite(favouritesCache.favouritePaths.contains(path));
                            myFile.setLastModified(lastModified);
                            myFile.setFolder(false);
                            myFile.setSizeLong(size);
                            myFile.setSize(helpingBot.sizeinwords(size));



                            if(index<0)
                            {
                                //folder not present in search result
                                synchronized (galleryData.searchResultsPager4)
                                {
                                    MyContainer myContainer=new MyContainer();
                                    myContainer.setPath(folderPath);
                                    myContainer.setThumbUrl(thumbUrl);
                                    myContainer.setLastModified(lastModified);
                                    myContainer.setName(folderPath);
                                    myContainer.getMyFileArrayList().add(myFile);


                                    MyFile myFileFolder=new MyFile();
                                    myFileFolder.setSymLink(false);
                                    myFileFolder.setChecked(false);
                                    myFileFolder.setThumbUrl(thumbUrl);
                                    myFileFolder.setFileId(folderPath);
                                    myFileFolder.setName(folderPath);
                                    myFileFolder.setPath(folderPath);
                                    myFileFolder.setFavourite(favouritesCache.favouritePaths.contains(folderPath));
                                    myFileFolder.setLastModified(lastModified);
                                    myFileFolder.setFolder(true);
                                    myFileFolder.setSizeLong(0);
                                    myFileFolder.setSize("");

                                    myContainer.setMyFile(myFileFolder);

                                    galleryData.searchResultsPager4.add(myContainer);
                                }
                            }
                            else
                            {
                                MyContainer myContainer=galleryData.searchResultsPager4.get(index);

                                if(myContainer.getLastModified()<lastModified)
                                {
                                    myContainer.setLastModified(lastModified);
                                    myContainer.setThumbUrl(thumbUrl);
                                    myContainer.getMyFile().setLastModified(lastModified);
                                    myContainer.getMyFile().setThumbUrl(thumbUrl);
                                }
                                myContainer.getMyFileArrayList().add(myFile);
                            }
                        }
                    }
                    catch(final Exception e)
                    {
                        Log.e("search thread ERROR",e.getLocalizedMessage()+"--");
                        errorOccured=true;
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                myUi.showArtLayout(R.mipmap.pick_nose,"NO INTERNET ACCESS ( "+e.getLocalizedMessage()+" )",true);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                    finally
                    {
                        if(threadId==galleryData.threadIdPager4)
                        {
                            galleryData.searchingPager4 =false;
                            if(!errorOccured)
                            {
                                galleryData.successfullySearchedPager4 =true;
                            }
                        }
                        Log.e("search thread ended",threadId+"--"+rootPath);
                    }
                }
            };
            thread.start();
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
                            if(threadId!=galleryData.threadIdPager5)
                            {
                                break;
                            }
                            int extensionId=extensionUtil.getExtensionId(file.getName());

                            if( file instanceof FileMetadata && ( galleryType==extensionId) )
                            {

                                String path=file.getPathDisplay();
                                String folderPath=path.substring(0,path.lastIndexOf('/'));
                                String name=file.getName();
                                String id=((FileMetadata)file).getId();
                                String thumbUrl=file.getPathDisplay() + "@#$" + id;
                                long lastModified=((FileMetadata)file).getClientModified().getTime();
                                long size=((FileMetadata) file).getSize();


                                int index=-1;
                                for(MyContainer x: galleryData.searchResultsPager5)
                                {
                                    if(x.getPath().equals(folderPath))
                                    {
                                        index=galleryData.searchResultsPager5.indexOf(x);
                                        break;
                                    }
                                }


                                MyFile myFile=new MyFile();

                                myFile.setSymLink(false);
                                myFile.setChecked(false);
                                myFile.setThumbUrl(thumbUrl);
                                myFile.setFileId(id);
                                myFile.setName(name);
                                myFile.setPath(path);
                                myFile.setFavourite(favouritesCache.favouritePaths.contains(path));
                                myFile.setLastModified(lastModified);
                                myFile.setFolder(false);
                                myFile.setSizeLong(size);
                                myFile.setSize(helpingBot.sizeinwords(size));


                                if(index<0)
                                {
                                    //folder not present in search result
                                    synchronized (galleryData.searchResultsPager5)
                                    {
                                        String folderName=folderPath.substring(folderPath.lastIndexOf('/')+1);

                                        MyContainer myContainer=new MyContainer();
                                        myContainer.setPath(folderPath);
                                        myContainer.setThumbUrl(thumbUrl);
                                        myContainer.setLastModified(lastModified);
                                        myContainer.setName(folderName);
                                        myContainer.getMyFileArrayList().add(myFile);


                                        MyFile myFileFolder=new MyFile();
                                        myFileFolder.setSymLink(false);
                                        myFileFolder.setChecked(false);
                                        myFileFolder.setThumbUrl(thumbUrl);
                                        myFileFolder.setFileId(folderPath);
                                        myFileFolder.setName(folderName);
                                        myFileFolder.setPath(folderPath);
                                        myFileFolder.setFavourite(favouritesCache.favouritePaths.contains(folderPath));
                                        myFileFolder.setLastModified(lastModified);
                                        myFileFolder.setFolder(true);
                                        myFileFolder.setSizeLong(0);
                                        myFileFolder.setSize("");


                                        myContainer.setMyFile(myFileFolder);

                                        galleryData.searchResultsPager5.add(myContainer);
                                    }
                                }
                                else
                                {
                                    MyContainer myContainer=galleryData.searchResultsPager5.get(index);

                                    if(myContainer.getLastModified()<lastModified)
                                    {
                                        myContainer.setLastModified(lastModified);
                                        myContainer.setThumbUrl(thumbUrl);
                                        myContainer.getMyFile().setLastModified(lastModified);
                                        myContainer.getMyFile().setThumbUrl(thumbUrl);
                                    }
                                    myContainer.getMyFileArrayList().add(myFile);
                                }
                            }
                        }

                    }
                    catch(final Exception e)
                    {
                        Log.e("search thread ERROR",e.getLocalizedMessage()+"--");
                        errorOccured=true;
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                myUi.showArtLayout(R.mipmap.pick_nose,"NO INTERNET ACCESS ( "+e.getLocalizedMessage()+" )",true);
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                    finally
                    {
                        if(threadId==galleryData.threadIdPager5)
                        {
                            galleryData.searchingPager5 =false;
                            if(!errorOccured)
                            {
                                galleryData.successfullySearchedPager5 =true;
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

                    switch (galleryData.storageId)
                    {
                        case 1:
                            if(galleryData.searchingPager1)
                            {
                                if(progressBar.getVisibility()!= View.VISIBLE)
                                    progressBar.setVisibility(View.VISIBLE);
                                searchHandler.postDelayed(searchRunnable,500);
                            }
                            else
                            {
                                runnerIsStopping(galleryData.searchResultsPager1,galleryData.successfullySearchedPager1);
                                searchHandler.removeCallbacks(searchRunnable);
                            }
                            break;
                        case 2:
                            if(galleryData.searchingPager2)
                            {
                                if(progressBar.getVisibility()!= View.VISIBLE)
                                    progressBar.setVisibility(View.VISIBLE);
                                searchHandler.postDelayed(searchRunnable,500);
                            }
                            else
                            {
                                runnerIsStopping(galleryData.searchResultsPager2,galleryData.successfullySearchedPager2);
                                searchHandler.removeCallbacks(searchRunnable);
                            }
                            break;
                        case 3:
                            if(galleryData.searchingPager3)
                            {
                                if(progressBar.getVisibility()!= View.VISIBLE)
                                    progressBar.setVisibility(View.VISIBLE);
                                searchHandler.postDelayed(searchRunnable,500);
                            }
                            else
                            {
                                runnerIsStopping(galleryData.searchResultsPager3,galleryData.successfullySearchedPager3);
                                searchHandler.removeCallbacks(searchRunnable);
                            }
                            break;
                        case 4:
                            if(galleryData.searchingPager4)
                            {
                                if(progressBar.getVisibility()!= View.VISIBLE)
                                    progressBar.setVisibility(View.VISIBLE);
                                searchHandler.postDelayed(searchRunnable,500);
                            }
                            else
                            {
                                runnerIsStopping(galleryData.searchResultsPager4,galleryData.successfullySearchedPager4);
                                searchHandler.removeCallbacks(searchRunnable);
                            }
                            break;
                        case 5:
                            if(galleryData.searchingPager5)
                            {
                                if(progressBar.getVisibility()!= View.VISIBLE)
                                    progressBar.setVisibility(View.VISIBLE);
                                searchHandler.postDelayed(searchRunnable,500);
                            }
                            else
                            {
                                runnerIsStopping(galleryData.searchResultsPager5,galleryData.successfullySearchedPager5);
                                searchHandler.removeCallbacks(searchRunnable);
                            }
                            break;
                    }
                }
            };
            searchHandler.postDelayed(searchRunnable,1);

        }

        private void runnerIsStopping(ArrayList<MyContainer> containerList,boolean successfullySearched)
        {
            progressBar.setVisibility(View.GONE);
            if(successfullySearched)
            {
                if(galleryData.whatToSearch==null)
                {
                    myContainerList=containerList;
                }
                else
                {
                    myContainerList=galleryData.filteredMyContainerList;
                }
                SortingMachine sortingMachine =new SortingMachine(variablesCache.sortGalleryBy);
                sortingMachine.sortMyContainer(myContainerList);
                setGridList();
            }

            if(galleryData.whatToSearch==null && gridview.getVisibility()==View.VISIBLE)
            {
                if(myContainerList==null || myContainerList.size()==0)
                {
                    myUi.showArtLayout(R.mipmap.empty_folder,"EMPTY",false);
                }
            }
        }

        private void setGridList()
        {
            setMyFilesList();// myFilesList=set;

            thumbNailManager.initialiseThumbRunner();

            gridview.setVisibility(View.VISIBLE);
            gridViewAdapter=new GridViewAdapter(mainActivityObject,myFilesList,galleryData.storageId,getFragmentManager().findFragmentById(rootFrameId),modValue,3,galleryType);
            gridview.setAdapter(gridViewAdapter);
            gridview.setSelection(thisPage.getCurrentIndex());


            ArrayList<LinearLayout> linearLayoutArrayList=new ArrayList<>();
            linearLayoutArrayList.add(layer1);
            linearLayoutArrayList.add(layer2);
            clickManager=new ClickManager(mainActivityObject,false,3,"Gallery",pageIndex,galleryData.storageId,getFragmentManager().findFragmentById(rootFrameId),linearLayoutArrayList,myFilesList,myContainerList,gridview,gridViewAdapter);
            clickManager.longClickedGrid();

        }

        private void setMyFilesList()
        {
            myFilesList=new ArrayList<>();
            for(MyContainer container:myContainerList)
            {
                MyFile myFile=container.getMyFile();
                myFile.setContainerItems(container.getMyFileArrayList().size());
                myFilesList.add(myFile);
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
            search_edit=(EditText) view.findViewById(R.id.layer1_search_edit);
            search_backspace=(ImageView) view.findViewById(R.id.layer1_search_backspace);
            title=(TextView) view.findViewById(R.id.layer1_title);
            search=(ImageView) view.findViewById(R.id.layer1_search);
            close=(ImageView) view.findViewById(R.id.layer1_close);
            options=(ImageView) view.findViewById(R.id.layer1_options);

            path_fetched=(TextView) view.findViewById(R.id.gallery_searchWhere);

            layer2=(LinearLayout)view.findViewById(R.id.gallery1_layer2);

            storage1=(ImageView)view.findViewById(R.id.gallery_storage1);
            storage2=(ImageView)view.findViewById(R.id.gallery_storage2);
            storage3=(ImageView)view.findViewById(R.id.gallery_storage3);
            storage4=(ImageView)view.findViewById(R.id.gallery_storage4);
            storage5=(ImageView)view.findViewById(R.id.gallery_storage5);

            progressBar=(ProgressBar) view.findViewById(R.id.gallery_progress);
            gridview=(GridView) view.findViewById(R.id.grid_image_gallery);

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

            if(galleryData.whatToSearch==null)
            {
                search_layout.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);
                title.setText("Image Gallery");
            }
            else
            {
                showSearchLayout();
            }


            search.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(isReady())
                    {
                        galleryData.filteredMyContainerList.clear();
                        galleryData.filteredMyContainerList.addAll(globalStorageResultReference);
                        myContainerList=galleryData.filteredMyContainerList;
                        galleryData.whatToSearch="";
                        myImageFetcher.setGridList();

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
                    galleryData.whatToSearch="";
                }
            });

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
                        variablesCache.sortGalleryBy=1;
                        reloadPager();
                    }
                    if(item.getItemId()==R.id.sort_name2)
                    {
                        variablesCache.sortGalleryBy=2;
                        reloadPager();
                    }
                    if(item.getItemId()==R.id.sort_date1)
                    {
                        variablesCache.sortGalleryBy=3;
                        reloadPager();
                    }
                    if(item.getItemId()==R.id.sort_date2)
                    {
                        variablesCache.sortGalleryBy=4;
                        reloadPager();
                    }
                    if(item.getItemId()==R.id.sort_size1)
                    {
                        variablesCache.sortGalleryBy=5;
                        reloadPager();
                    }
                    if(item.getItemId()==R.id.sort_size2)
                    {
                        variablesCache.sortGalleryBy=6;
                        reloadPager();
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

        private void showSearchLayout()
        {
            search_layout.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            options.setVisibility(View.VISIBLE);

            search_edit.setHint("Search Here");
            search_edit.setText("");
            search_edit.append(galleryData.whatToSearch);

            mySearchManager.setUpTextWatcher();
        }

        private void showArtLayout(int resourceId,String message,boolean toRetry)  //OK
        {
            try
            {
                commonsUtils.showArtLayout(resourceId,message,toRetry,artIcon,artText,artLayout,artButton,mainActivityObject,galleryData.storageId,4,getFragmentManager().findFragmentById(rootFrameId));

                if(gridview!=null)
                {
                    gridview.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                Log.e(TAG,"showArtLayout()");
            }
        }

        private void setUpStorageButtons()
        {
            if(Physical_Storage_PATHS.size()>0)
            {
                storage1.setVisibility(View.VISIBLE);

                if(galleryData.storageId ==1)
                {
                    storage1.setBackgroundColor(Color.parseColor("#fbc02d"));
                }

                storage1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(galleryData.storageId !=1)
                        {
                            galleryData.whatToSearch=null;
                            galleryData.storageId=1;
                            galleryData.storagePath=Physical_Storage_PATHS.get(0);
                            openNewStorage(!galleryData.successfullySearchedPager1);
                        }
                    }
                });
            }

            if(Physical_Storage_PATHS.size()>1)
            {
                storage2.setVisibility(View.VISIBLE);
                if(galleryData.storageId ==2)
                {
                    storage2.setBackgroundColor(Color.parseColor("#fbc02d"));
                }
                storage2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(galleryData.storageId !=2)
                        {
                            galleryData.whatToSearch=null;
                            galleryData.storageId=2;
                            galleryData.storagePath=Physical_Storage_PATHS.get(1);
                            openNewStorage(!galleryData.successfullySearchedPager2);
                        }
                    }
                });
            }

            if(Physical_Storage_PATHS.size()>2)
            {
                storage3.setVisibility(View.VISIBLE);
                if(galleryData.storageId ==3)
                {
                    storage3.setBackgroundColor(Color.parseColor("#fbc02d"));
                }
                storage3.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(galleryData.storageId !=3)
                        {
                            galleryData.whatToSearch=null;
                            galleryData.storageId=3;
                            galleryData.storagePath=Physical_Storage_PATHS.get(2);
                            openNewStorage(!galleryData.successfullySearchedPager3);
                        }
                    }
                });
            }


            if(GoogleDriveConnection.isDriveAvailable)
            {
                storage4.setVisibility(View.VISIBLE);
                if(galleryData.storageId ==4)
                {
                    storage4.setBackgroundColor(Color.parseColor("#fbc02d"));
                }
                storage4.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(galleryData.storageId !=4)
                        {
                            galleryData.whatToSearch=null;
                            galleryData.storageId=4;
                            galleryData.storagePath="Google Drive";
                            openNewStorage(!galleryData.successfullySearchedPager4);
                        }
                    }
                });
            }
            if(DropBoxConnection.isDropboxAvailable)
            {
                storage5.setVisibility(View.VISIBLE);
                if(galleryData.storageId ==5)
                {
                    storage5.setBackgroundColor(Color.parseColor("#fbc02d"));
                }
                storage5.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(galleryData.storageId !=5)
                        {
                            galleryData.whatToSearch=null;
                            galleryData.storageId=5;
                            galleryData.storagePath="DropBox";
                            openNewStorage(!galleryData.successfullySearchedPager5);
                        }
                    }
                });
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
                    if(s.length()==0)
                    {
                        Log.e("length is zero","000000000000000000000000000000");
                        galleryData.whatToSearch=s.toString().toLowerCase();
                        modifySearchAdd(++galleryData.searchThreadId);
                    }
                    else
                    {
                        if(s.length()< galleryData.whatToSearch.length() || galleryData.whatToSearch.length()==0)
                        {
                            galleryData.whatToSearch=s.toString().toLowerCase();
                            Log.e("modifySearchAdd...",(galleryData.searchThreadId+1)+"---"+ galleryData.whatToSearch);
                            modifySearchAdd(++galleryData.searchThreadId);
                        }
                        else
                        {
                            galleryData.whatToSearch=s.toString().toLowerCase();
                        }
                    }
                    modifySearchRemove();
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
            for(MyContainer container:globalStorageResultReference)
            {
                if(threadId!=galleryData.searchThreadId)
                    return;

                if(container.getName().toLowerCase().contains(galleryData.whatToSearch));
                {
                    if(!myContainerList.contains(container))
                    {
                        myContainerList.add(container);
                        myFilesList.add(container.getMyFile());
                        gridViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        private void modifySearchRemove()
        {
            //REMOVE
            for(int i=0;i<myContainerList.size();i++)
            {
                if(!myContainerList.get(i).getName().toLowerCase().contains(galleryData.whatToSearch))
                {
                    myContainerList.remove(i);
                    myFilesList.remove(i);
                    gridViewAdapter.notifyDataSetChanged();
                    i--;
                }
            }

        }
    }


    private boolean isReady()
    {
        return globalStorageResultReference.size()>0 && myFilesList!=null;
    }



    public void openNewStorage(boolean startThread)
    {
        /*
        if startThread is false,its simply refreshing + changing threadId
         */
        galleryData.threadIdPager1++;
        galleryData.threadIdPager2++;
        galleryData.threadIdPager3++;
        galleryData.threadIdPager4++;
        galleryData.threadIdPager5++;


        image_gallery_fragment1 fragment=new image_gallery_fragment1();
        if(startThread)
        {
            fragment.startSearchThread();
        }
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,fragment);
        ft.commit();
    }

    public void startSearchThread()
    {
        this.shouldStartNew=true;
        this.startSearchThread=true;
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

        if(thumbNailHandler !=null)
            thumbNailHandler.removeCallbacks(thumbNailRunnable);
        if(searchHandler!=null)
            searchHandler.removeCallbacks(searchRunnable);

        super.onDestroy();
        Log.i(TAG,"DESTROYED");

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

        thisPage.getIndexList().set(thisPage.getIndexList().size()-1,pos);


    }


    public void backPressed(Page page)
    {
        HelpingBot.hideKeyboard(mainActivityObject);

        galleryData.threadIdPager1++;
        galleryData.threadIdPager2++;
        galleryData.threadIdPager3++;
        galleryData.threadIdPager4++;
        galleryData.threadIdPager5++;


        if(galleryData.whatToSearch==null)
        {
            mainActivityObject.removePage(page);
            mainActivityObject.setUpViewPager();
        }
        else
        {
            galleryData.whatToSearch=null;
            reloadPager();
        }
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
            thumbNailsMod=new ThumbNailsMod(galleryData.storageId,mainActivityObject);
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



    public void reloadPager()
    {
        openNewStorage(false);
    }

    private void getStorageInfo()
    {
        rootFrameId=((ViewGroup)getView().getParent()).getId();
        pageIndex=getPageIndexFromFrameId(rootFrameId);
        thisPage=pageList.get(pageIndex);
        pageName=thisPage.getName();
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
            default:
                galleryType=0;
                break;
        }
        
        galleryData= MyCacheData.getGalleryFromCode(galleryType);
        
    }



}
