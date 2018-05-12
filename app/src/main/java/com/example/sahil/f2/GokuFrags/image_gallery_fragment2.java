package com.example.sahil.f2.GokuFrags;

import android.os.Bundle;
import android.os.Handler;
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

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Cache.GalleryData;
import com.example.sahil.f2.Cache.MyCacheData;
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
import com.example.sahil.f2.OperationTheater.PagerXUtilities;
import com.example.sahil.f2.R;
import com.example.sahil.f2.UiClasses.ClickManager;

import java.util.ArrayList;
import java.util.Map;

import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod0;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod1;
import static com.example.sahil.f2.Cache.ThumbNailCache.clear_mod2;
import static com.example.sahil.f2.MainActivity.pageList;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getPageIndexFromFrameId;


/**
 * Created by hit4man47 on 11/21/2017.
 */

public class image_gallery_fragment2 extends Fragment
{

    private ImageView home,back,root,search,search_backspace,close,options,artIcon;
    private LinearLayout layer1,layer2,search_layout;
    private EditText search_edit;
    private TextView title,path_fetched,artText;
    private ProgressBar progressBar;
    private GridView gridview;
    private RelativeLayout artLayout=null;
    private Button artButton;

    private int galleryType;
    private GalleryData galleryData;

    private Runnable thumbNail_runnable;
    private Handler thumbNail_handler;

    private final String TAG="ImageGalleryFrag2";
    private boolean shouldStartNew=false;//VERY IMPORTANT
    public MainActivity mainActivityObject;
    private GridViewAdapter gridViewAdapter;
    private boolean allImagesSetted;
    private int totalImagesSetted;
    private  boolean thumbNailRunner_isRunning=false;


    private TextWatcher myTextWatcher;
    private ClickManager clickManager;
    private MySearchManager mySearchManager;
    private MyUi myUi;
    public CommonsUtils commonsUtils;

    private int modValue;
    public ThumbNailsMod thumbNailsMod;
    private ThumbNailManager thumbNailManager;
    private View view;

    private ArrayList<MyFile> myFilesList;

    private boolean gridRunner_isRunning;
    private Handler gridHandler;
    private Runnable gridRunnable;
    private Page thisPage;
    private int pageIndex;
    private int rootFrameId;
    private String pageName;
    final HelpingBot helpingBot=new HelpingBot();
    private boolean continueThumbRun=false;
    private MyContainer container;

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

        

        galleryData.softClearGallery();
        myUi=new MyUi();
        commonsUtils=new CommonsUtils();

        mySearchManager=new MySearchManager();
        thumbNailManager=new ThumbNailManager();
        modValue=pageIndex%3;
        thumbNailManager.clearThumbCache();

        if(shouldStartNew)
        {
            //this fragment is new opened
            shouldStartNew=false;
            if(thisPage.getPathList().size()==1)
            {
                thisPage.getPathList().add("Gallery2");
                thisPage.getIndexList().add(0);
                galleryData.currentMyContainer=container;
            }
            thisPage.getIndexList().set( thisPage.getIndexList().size()-1,0);
        }

        view=getView();
        myUi.initialiseAllUi();
        gridview.setNumColumns(4);
        myUi.setUpLayer1();

        String path=galleryData.currentMyContainer.getPath();
        path_fetched.setText(path);

        myFileFetcher();
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

            path_fetched=(TextView) view.findViewById(R.id.gallery_searchWhere);

            layer2=(LinearLayout)view.findViewById(R.id.gallery1_layer2);

            progressBar=(ProgressBar) view.findViewById(R.id.gallery_progress);
            gridview=(GridView) view.findViewById(R.id.grid_image_gallery);

            artLayout=(RelativeLayout)view.findViewById(R.id.art_layout);
            artIcon=(ImageView) view.findViewById(R.id.art_layout_icon);
            artText=(TextView) view.findViewById(R.id.art_layout_text);
            artButton=(Button) view.findViewById(R.id.art_layout_retry);

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
                        if(isReady() )
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

        private void setUpLayer1()
        {
            home.setVisibility(View.GONE);
            root.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);
            layer2.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

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
                        galleryData.filteredMyFileList.clear();
                        galleryData.filteredMyFileList.addAll(myFilesList);
                        myFilesList=galleryData.filteredMyFileList;
                        galleryData.whatToSearch="";
                        setGridList();

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

        private void showArtLayout(int resourceId,String message,boolean toRetry)  //OK
        {
            try
            {
                commonsUtils.showArtLayout(resourceId,message,toRetry,artIcon,artText,artLayout,artButton,mainActivityObject,galleryData.storageId,5,getFragmentManager().findFragmentById(rootFrameId));

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
            for(MyFile myFile:galleryData.currentMyContainer.getMyFileArrayList())
            {
                if(threadId!=galleryData.searchThreadId)
                    return;

                if(myFile.getName().toLowerCase().contains(galleryData.whatToSearch));
                {
                    if(!myFilesList.contains(myFile))
                    {
                        myFilesList.add(myFile);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        private void modifySearchRemove()
        {
            //REMOVE
            for(int i=0;i<myFilesList.size();i++)
            {
                if(!myFilesList.get(i).getName().toLowerCase().contains(galleryData.whatToSearch))
                {
                    myFilesList.remove(i);
                    gridViewAdapter.notifyDataSetChanged();
                    i--;
                }
            }

        }
    }


    private boolean isReady()
    {
        return myFilesList.size()>0 ;
    }


    private void setGridList()
    {
        thumbNailManager.initialiseThumbRunner();

        gridview.setVisibility(View.VISIBLE);
        gridViewAdapter=new GridViewAdapter(mainActivityObject,myFilesList,galleryData.storageId,getFragmentManager().findFragmentById(rootFrameId),modValue,4,galleryType);
        gridview.setAdapter(gridViewAdapter);
        gridview.setSelection(thisPage.getCurrentIndex());
        ArrayList<LinearLayout> linearLayoutArrayList=new ArrayList<>();
        linearLayoutArrayList.add(layer1);
        clickManager=new ClickManager(mainActivityObject,false,4,"Gallery",pageIndex,galleryData.storageId,getFragmentManager().findFragmentById(rootFrameId),linearLayoutArrayList,myFilesList,null,gridview,gridViewAdapter);
        clickManager.longClickedGrid();
    }


    private void myFileFetcher()
    {
        if(galleryData.whatToSearch==null)
        {
            myFilesList=galleryData.currentMyContainer.getMyFileArrayList();
        }
        else
        {
            myFilesList=galleryData.filteredMyFileList;
        }

        SortingMachine sortingMachine =new SortingMachine(variablesCache.sortGalleryBy);
        sortingMachine.sortMyFile(myFilesList);
        setGridList();

        if(galleryData.whatToSearch==null && gridview.getVisibility()==View.VISIBLE)
        {
            if(myFilesList==null || myFilesList.size()==0)
            {
                myUi.showArtLayout(R.mipmap.empty_folder,"EMPTY",false);
            }
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


    @Override
    public void onDestroy()             //OK
    {

        if(thumbNail_handler!=null)
            thumbNail_handler.removeCallbacks(thumbNail_runnable);
        thumbNailManager.clearThumbCache();

        if(gridHandler!=null)
            gridHandler.removeCallbacks(gridRunnable);

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


    public void backPressed()
    {
        HelpingBot.hideKeyboard(mainActivityObject);

        if(galleryData.whatToSearch==null)
        {
            image_gallery_fragment1 backFragment=new image_gallery_fragment1();
            backFragment.oldStarted();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(rootFrameId,backFragment);
            ft.commit();
        }
        else
        {
            galleryData.whatToSearch=null;
            reloadPager();
        }
    }


    public void newStarted(MyContainer container)
    {
        this.container=container;
        shouldStartNew=true;
    }


    public void reloadPager()
    {
        image_gallery_fragment2 newFragment=new image_gallery_fragment2();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(rootFrameId,newFragment);
        ft.commit();
    }


    private void getStorageInfo()
    {
        rootFrameId=((ViewGroup)getView().getParent()).getId();
        pageIndex=getPageIndexFromFrameId(rootFrameId);
        thisPage=pageList.get(pageIndex);
        pageName=thisPage.getName();
        galleryType=PagerXUtilities.getGalleryType(pageName);

        galleryData= MyCacheData.getGalleryFromCode(galleryType);
    }

}
