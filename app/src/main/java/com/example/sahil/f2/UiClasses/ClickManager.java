package com.example.sahil.f2.UiClasses;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Cache.appManagerCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyApp;
import com.example.sahil.f2.Classes.MyContainer;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.OneFile;
import com.example.sahil.f2.Classes.SimpleYesNoDialog;
import com.example.sahil.f2.FunkyAdapters.GridViewAdapter;
import com.example.sahil.f2.FunkyAdapters.ListViewAdapter;
import com.example.sahil.f2.GokuFrags.appManager;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment1;
import com.example.sahil.f2.GokuFrags.search_fragment;
import com.example.sahil.f2.GokuFrags.storageAnalyser;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.BackgroundInstallUninstallMachine;
import com.example.sahil.f2.OperationTheater.DeletingMachine;
import com.example.sahil.f2.OperationTheater.FastDownload;
import com.example.sahil.f2.OperationTheater.Favourites;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.OperationTheater.HidingUnhidingMachine;
import com.example.sahil.f2.OperationTheater.PagerXUtilities;
import com.example.sahil.f2.OperationTheater.PasteClipBoard;
import com.example.sahil.f2.OperationTheater.PropertiesMachine;
import com.example.sahil.f2.OperationTheater.RenameMachine;
import com.example.sahil.f2.OperationTheater.RestoringMachine;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.FolderLister;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.Utilities.AppManagerUtils;
import com.example.sahil.f2.Utilities.ExtensionUtil;
import com.example.sahil.f2.WifiSendActivity;
import com.google.api.services.drive.model.FileList;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.sahil.f2.MainActivity.SDCardUriMap;

/**
 * Created by hit4man47 on 12/19/2017.
 */

public class ClickManager
{

    private Menu contextualMenuGlobal;
    private boolean isThreadToLoadFilesStarted=false;
    private boolean isThreadToLoadFilesFinished=false;
    private ArrayList<String> selectedNameList;
    private ArrayList<Integer> selectedIndexList;

    final private MyFileOperations myFileOperations;
    final private AppManagerUtils appManagerUtils;
    final private MyAppOperations myAppOperations;

    final private ListView listView;
    final private GridView gridView;
    final private ListViewAdapter listViewAdapter;
    final private GridViewAdapter gridViewAdapter;

    final private ExtensionUtil extensionUtil;

    final private ArrayList<MyFile> myFilesList;
    final private ArrayList<MyContainer> containerList;
    final private MainActivity mainActivityObject;

    final private ArrayList<LinearLayout> linearLayoutsListToHide;
    final private int storageId;
    final private int pagerId;
    final private Fragment fragment;
    final private boolean isRecycleBin;
    final private boolean runRootCommand=false;


    /**
     *
     * @param pagerId 1:storagePager
     *                2:searchPager
     *                3:imageFragment1
     *                4:imageFragment2
     *                5:storageAnalyser
     *                6:app Manager
     */
    public ClickManager(MainActivity mainActivityObject,boolean isRecycleBin, int pagerId, String currentPath, int pageIndex, int storageId, Fragment fragment,ArrayList<LinearLayout> linearLayoutsListToHide, ArrayList<MyFile> myFilesList, ArrayList<MyContainer> containerList,ListView listView,ListViewAdapter listViewAdapter)
    {
        this.mainActivityObject=mainActivityObject;
        this.pagerId=pagerId;
        myFileOperations=new MyFileOperations(currentPath,pageIndex);
        this.linearLayoutsListToHide=linearLayoutsListToHide;
        this.storageId=storageId;
        this.fragment=fragment;
        this.myFilesList=myFilesList;
        this.containerList=containerList;
        this.listView=listView;
        this.gridView=null;
        this.listViewAdapter=listViewAdapter;
        this.gridViewAdapter=null;
        this.isRecycleBin=isRecycleBin;

        extensionUtil=new ExtensionUtil();
        myAppOperations=new MyAppOperations();

        appManagerUtils=new AppManagerUtils(mainActivityObject);
    }

    /**
     *
     * @param pagerId 1:storagePager
     *                2:searchPager
     *                3:imageFragment1
     *                4:imageFragment2
     *                5:storageAnalyser
     *                6:app Manager
     */
    public ClickManager(MainActivity mainActivityObject,boolean isRecycleBin, int pagerId, String currentPath, int pageIndex, int storageId, Fragment fragment,ArrayList<LinearLayout> linearLayoutsListToHide, ArrayList<MyFile> myFilesList, ArrayList<MyContainer> containerList,GridView gridView,GridViewAdapter gridViewAdapter)
    {
        this.mainActivityObject=mainActivityObject;
        this.pagerId=pagerId;
        myFileOperations=new MyFileOperations(currentPath,pageIndex);
        this.linearLayoutsListToHide=linearLayoutsListToHide;
        this.storageId=storageId;
        this.fragment=fragment;
        this.myFilesList=myFilesList;
        this.containerList=containerList;
        this.listView=null;
        this.gridView=gridView;
        this.listViewAdapter=null;
        this.gridViewAdapter=gridViewAdapter;
        this.isRecycleBin=isRecycleBin;

        extensionUtil=new ExtensionUtil();
        myAppOperations=new MyAppOperations();

        appManagerUtils=new AppManagerUtils(mainActivityObject);
    }

    public void longClickedList()
    {
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                onItemCheckedStateChangedCommon(mode,position,id,checked,1);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                return onActionItemClickedCommon(mode,item,1);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                return onCreateActionModeCommon(menu);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                onDestroyActionModeCommon();
            }
        });

        final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView <?> listv,View v,int position,long id)
            {
                shortClicked(position);
            }
        };
        listView.setOnItemClickListener(listener);

    }

    public void longClickedGrid()
    {
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        gridView.setMultiChoiceModeListener(new GridView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                onItemCheckedStateChangedCommon(mode,position,id,checked,2);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                return onActionItemClickedCommon(mode,item,2);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                return onCreateActionModeCommon(menu);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                onDestroyActionModeCommon();
            }

        });

        final AdapterView.OnItemClickListener listener=new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView <?> listv,View v,int position,long id)
            {
                shortClicked(position);
            }

        };
        gridView.setOnItemClickListener(listener);
    }

    private void shortClicked(int position)
    {
        if(isRecycleBin && storageId>3)
        {
            mainActivityObject.showSmackBar("Restore to continue...(Long Press->Options->Restore");
            return;
        }
        if(pagerId==6)
        {
            appManager pager6=(appManager) fragment;
            pager6.showDialog(position);
            return;
        }

        Log.e("--",position+"--");
        if(myFilesList.get(position).isFolder())
        {
            if(pagerId==3)
            {
                openNewPager(containerList.get(position));
            }
            else
            {
                openNewPager(myFilesList.get(position).getPath());
            }
        }
        else
        {
            if(storageId>3)     //OPENING DROPBOX AND DRIVE FILES and FTP
            {
                myFileOperations.fastDownload(myFilesList.get(position));
                return;
            }

            if(runRootCommand)   //ROOTED DIR FILE OPENED
            {
                mainActivityObject.returnRootFileOpenerObject().open(myFilesList.get(position).getName(),myFilesList.get(position).getPath());
            }
            else                            //NON ROOTED
            {
                File file=new File(myFilesList.get(position).getPath());
                mainActivityObject.returnFileOpenerObject(file).open();
            }
            Log.e("opener called",",,,");
        }
    }

    //***************************************COMMON START********************************************
    private void onItemCheckedStateChangedCommon(ActionMode mode, int position, long id, boolean checked,int listOrGrid)
    {
        Log.e("long clicked at ",position+"--");

        if (checked)
        {
            myFilesList.get(position).setChecked(true);
            selectedNameList.add(myFilesList.get(position).getName());
            selectedIndexList.add(position);
            Log.i("selected list",myFilesList.get(position).getName()+" -"+myFilesList.get(position).isChecked()+selectedNameList.size());

        }
        else
        {
            myFilesList.get(position).setChecked(false);
            selectedNameList.remove(myFilesList.get(position).getName());
            selectedIndexList.remove((Integer)position);
            Log.i("un selected list",myFilesList.get(position).getName()+" -"+myFilesList.get(position).isChecked()+selectedNameList.size());

        }

        int size=selectedIndexList.size();
        boolean x=pagerId==6 && appManagerCache.whatToDo!=3;//user and system apps
        if(size==1)
        {
            contextualMenuGlobal.findItem(R.id.cab1_rename).setEnabled(true);
            if(myFilesList.get(selectedIndexList.get(0)).isFavourite())
            {
                contextualMenuGlobal.findItem(R.id.cab1_favourite).setVisible(false);
                contextualMenuGlobal.findItem(R.id.cab1_unfavourite).setVisible(true);
            }
            else
            {
                contextualMenuGlobal.findItem(R.id.cab1_favourite).setVisible(true);
                contextualMenuGlobal.findItem(R.id.cab1_unfavourite).setVisible(false);
            }
            if(!x)
            {
                if(selectedNameList.get(0).startsWith("."))
                {
                    contextualMenuGlobal.findItem(R.id.cab1_hide).setVisible(false);
                    contextualMenuGlobal.findItem(R.id.cab1_unhide).setVisible(true);
                }
                else
                {
                    contextualMenuGlobal.findItem(R.id.cab1_hide).setVisible(true);
                    contextualMenuGlobal.findItem(R.id.cab1_unhide).setVisible(false);
                }
            }
        }
        if(size==2)
        {
            contextualMenuGlobal.findItem(R.id.cab1_rename).setEnabled(false);
            contextualMenuGlobal.findItem(R.id.cab1_favourite).setVisible(true);
            contextualMenuGlobal.findItem(R.id.cab1_unfavourite).setVisible(true);

            if(!x)
            {
                contextualMenuGlobal.findItem(R.id.cab1_hide).setVisible(true);
                contextualMenuGlobal.findItem(R.id.cab1_unhide).setVisible(true);
            }

        }

        contextualMenuGlobal.getItem(0).setTitle(selectedNameList.size()+" Selected");

        if(listOrGrid==1)
        {
            listViewAdapter.notifyDataSetChanged();
        }
        if(listOrGrid==2)
        {
            gridViewAdapter.notifyDataSetChanged();
        }

    }

    private boolean onActionItemClickedCommon(ActionMode mode, MenuItem item,int listOrGrid)
    {
        PasteClipBoard.clear();

        isThreadToLoadFilesFinished=false;
        isThreadToLoadFilesStarted=false;

        switch (item.getItemId())
        {

            case R.id.cab1_cut:
                PasteClipBoard.cutOrCopy=1;
                if(storageId<=3)
                    myFileOperations.setUpCopyAndCutLocal();
                if(storageId==4)
                    myFileOperations.setUpCopyAndCutDrive();
                if(storageId==5)
                    myFileOperations.setUpCopyAndCutDropbox();
                if(storageId==6)
                    myFileOperations.setUpCopyAndCutFtp();
                mode.finish();
                return true;


            case R.id.cab1_copy:
                // FAB2.show();
                PasteClipBoard.cutOrCopy=2;
                if(storageId<=3)
                    myFileOperations.setUpCopyAndCutLocal();
                if(storageId==4)
                    myFileOperations.setUpCopyAndCutDrive();
                if(storageId==5)
                    myFileOperations.setUpCopyAndCutDropbox();
                if(storageId==6)
                    myFileOperations.setUpCopyAndCutFtp();
                mode.finish();
                return true;


            case R.id.cab1_delete:
                myFileOperations.setUpDelete();
                //automatically notifyDataSetChanged for adapter
                mode.finish();
                return true;

            case R.id.cab1_rename:
                myFileOperations.setUpRename();
                mode.finish();
                return true;

            case R.id.cab1_clear_default:
                myFileOperations.clearDefaults();
                mode.finish();
                return true;

            case R.id.cab1_properties:
                myFileOperations.setUpProperties();
                mode.finish();
                return true;


            case R.id.cab1_restore:
                myFileOperations.setUpRestore();
                mode.finish();
                return true;

            case R.id.cab1_favourite:
                myFileOperations.addToFavourites();
                mode.finish();
                return true;

            case R.id.cab1_unfavourite:
                myFileOperations.removeFromFavourites();
                mode.finish();
                return true;

            case R.id.cab1_select_all:

                selectedNameList.clear();
                selectedIndexList.clear();
                //because we are adding items to it again,
                // if we dont clear we willl get extra duplicate items

                for(int i=0;i<myFilesList.size();i++)
                {
                    if(listOrGrid==1)
                    {
                        listView.setItemChecked(i,true);
                    }
                    if(listOrGrid==2)
                    {
                        gridView.setItemChecked(i,true);
                    }
                            /*
                           THIS AUTOMATICALLY CALLS above function
                           "public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)'
                            */
                }

                return true;

            case R.id.cab1_select_none:

                selectedNameList.clear();
                selectedIndexList.clear();
                for(int i=0;i<myFilesList.size();i++)
                {
                    if(listOrGrid==1)
                    {
                        listView.setItemChecked(i,false);
                    }
                    if(listOrGrid==2)
                    {
                        gridView.setItemChecked(i,false);
                    }
                            /*
                            THIS AUTOMATICALLY CALLS above function "public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)'
                             */
                }
                selectedNameList.clear();
                selectedIndexList.clear();

                return true;



            case R.id.cab1_share_wifi:
                if(storageId<=3)
                {
                    myFileOperations.setUpCopyAndCutLocal();
                    Intent intent=new Intent(mainActivityObject, WifiSendActivity.class);
                    intent.putExtra("start",true);
                    mainActivityObject.startActivity(intent);
                }

                mode.finish();
                return true;

            case R.id.cab1_install:
                if(pagerId==6 && appManagerCache.whatToDo==3)
                {
                    myAppOperations.installFromIntent();//install user app
                }
                mode.finish();
                return true;

            case R.id.cab1_install_in_background:
                if(pagerId==6 && appManagerCache.whatToDo==3)
                {
                    myAppOperations.installAsSuperUser(1);
                }
                mode.finish();
                return true;

            case R.id.cab1_install_as_system_app:
                if(pagerId==6 && appManagerCache.whatToDo==3)
                {
                    myAppOperations.installAsSuperUser(3);
                }
                mode.finish();
                return true;

            case R.id.cab1_makeSystemApp:
                if(pagerId==6 && appManagerCache.whatToDo==1)
                {
                    myAppOperations.installAsSuperUser(3);
                }
                mode.finish();
                return true;

            case R.id.cab1_makeUserApp:
                if(pagerId==6 && appManagerCache.whatToDo==2)
                {
                    myAppOperations.installAsSuperUser(2);
                }
                mode.finish();
                return true;



            case R.id.cab1_uninstall:
                if(pagerId==6)
                {
                    if(appManagerCache.whatToDo==1)//uninstall user app
                    {
                        myAppOperations.uninstallFromIntent();
                    }
                    if(appManagerCache.whatToDo==2)//uninstall system app
                    {
                        myAppOperations.uninstallAsSuperUser();
                    }
                }
                mode.finish();
                return true;

            case R.id.cab1_uninstall_in_background:
                if(pagerId==6 && (appManagerCache.whatToDo==1 || appManagerCache.whatToDo==2 ))
                {
                    myAppOperations.uninstallAsSuperUser();
                }
                mode.finish();
                return true;

            case R.id.cab1_backup:
                if(pagerId==6 && (appManagerCache.whatToDo==1 || appManagerCache.whatToDo==2 ))
                {
                    myAppOperations.backUpApps();
                }
                mode.finish();
                return true;


            case R.id.cab1_hide:
                myFileOperations.hideUnHide(true);
                mode.finish();
                return true;

            case R.id.cab1_unhide:
                myFileOperations.hideUnHide(false);
                mode.finish();
                return true;


            default:
                //Log.w("ass","default");
                return false;
        }
    }

    private void setUpMenu(Menu menu)
    {
        if(isRecycleBin || (pagerId==6 && appManagerCache.whatToDo!=3))
        {
            menu.findItem(R.id.cab1_copy).setVisible(false);
            menu.findItem(R.id.cab1_cut).setVisible(false);
            menu.findItem(R.id.cab1_rename).setVisible(false);

        }
        if(pagerId==6 && appManagerCache.whatToDo!=3)
        {
            if(appManagerCache.whatToDo==1)
            {
                menu.findItem(R.id.cab1_uninstall).setTitle("Uninstall");
            }
            else
            {
                menu.findItem(R.id.cab1_uninstall).setTitle("(#)Uninstall");
            }

            menu.findItem(R.id.cab1_delete).setVisible(false);
            menu.findItem(R.id.cab1_hide).setVisible(false);
            menu.findItem(R.id.cab1_unhide).setVisible(false);
        }
        if(!isRecycleBin)
        {
            menu.findItem(R.id.cab1_restore).setVisible(false);
        }

        if(pagerId!=6 || appManagerCache.whatToDo==3)
        {
            menu.findItem(R.id.cab1_makeSystemApp).setVisible(false);
            menu.findItem(R.id.cab1_makeUserApp).setVisible(false);
        }
        else
        {
            if(appManagerCache.whatToDo==1)
            {
                menu.findItem(R.id.cab1_makeUserApp).setVisible(false);
            }
            if(appManagerCache.whatToDo==2)
            {
                menu.findItem(R.id.cab1_makeSystemApp).setVisible(false);
            }
        }

        if(pagerId!=6 || appManagerCache.whatToDo!=3)
        {
            menu.findItem(R.id.cab1_install).setVisible(false);
            menu.findItem(R.id.cab1_install_as_system_app).setVisible(false);
            menu.findItem(R.id.cab1_install_in_background).setVisible(false);
        }
        if(pagerId!=6 || appManagerCache.whatToDo==3)
        {
            menu.findItem(R.id.cab1_uninstall).setVisible(false);
            menu.findItem(R.id.cab1_uninstall_in_background).setVisible(false);
            menu.findItem(R.id.cab1_backup).setVisible(false);
        }
        if(storageId>3)
        {
            menu.findItem(R.id.cab1_clear_default).setVisible(false);
            menu.findItem(R.id.cab1_share_wifi).setVisible(false);
        }


    }

    private boolean onCreateActionModeCommon(Menu menu)
    {
        MenuInflater menuInflater = mainActivityObject.getMenuInflater();
        menuInflater.inflate(R.menu.menuin_contextualactionbar, menu);

        selectedNameList=new ArrayList<>();
        selectedIndexList=new ArrayList<>();

        for (int i = 0; i < 4; i++)
        {
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        setUpMenu(menu);

        /*
        if(isRecycleBin && storageId>3)
        {
            menu.findItem(R.id.menu_cut).setVisible(false);
            menu.findItem(R.id.menu_rename).setVisible(false);
            menu.findItem(R.id.menu_copy).setVisible(false);
        }
        else
        {
            for (int i = 0; i < 4; i++)
            {
                menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
        if(isRecycleBin)
        {
            menu.findItem(R.id.menu_Restore).setVisible(true);
            menu.findItem(R.id.menu_Restore).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        else
        {
            menu.findItem(R.id.menu_Restore).setVisible(false);
        }
        */
        //displaying 4 items at any cost


        //PREVETING THE USER TO ACCESS DRAWER
        mainActivityObject.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //PREVENTING THE USER TO SWIPE THE PAGER VIEW
        mainActivityObject.viewPager.setPagingEnabled(false);

        for(LinearLayout layout:linearLayoutsListToHide)
        {
            if(layout!=null)
                layout.setVisibility(View.GONE);
        }
        contextualMenuGlobal=menu;
        return true;
    }

    private void onDestroyActionModeCommon()
    {

        for (int position : selectedIndexList)
        {
            //setting to false one by one
            myFilesList.get(position).setChecked(false);
        }

        mainActivityObject.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mainActivityObject.viewPager.setPagingEnabled(true);

        for(LinearLayout layout:linearLayoutsListToHide)
        {
            if(layout!=null)
                layout.setVisibility(View.VISIBLE);
        }


        //myArrayAdapter1.notifyDataSetChanged(); *********automatically called************
    }
    //***************************************COMMON END********************************************

    private void openNewPager(String path)
    {
        switch (pagerId)
        {
            case 1:
                storagePager pager1 =(storagePager) fragment;
                pager1.openNewPager(path);
                break;
            case 2:
                search_fragment pager2 =(search_fragment) fragment;
                pager2.openNewPager(path);
                break;
            case 5:
                storageAnalyser pager3=(storageAnalyser) fragment;
                pager3.openNewPager(path);
                break;
        }
    }

    private void openNewPager(MyContainer container)    //pager id =3
    {
        Log.e("OPEN INNER","--"+container.getName());
        image_gallery_fragment1 pager3=(image_gallery_fragment1) fragment;
        pager3.openInnerPager(container);
    }


    private class MyFileOperations
    {
        private int waitimage=0;
        private String waittext="";
        private int relativeIndexOfSlash;
        private boolean someSelectedFilesMissing=false;

        private Runnable runnable1;
        private final String currentPath;
        private final int pageIndex;

        MyFileOperations(String currentPath,int pageIndex)
        {
            this.currentPath=currentPath;
            this.pageIndex=pageIndex;
        }

        private void setUpCopyAndCutLocal()
        {
            someSelectedFilesMissing=false;
            PasteClipBoard.fromParentPath=currentPath;
            PasteClipBoard.fromStorageCode=storageId;


            final Dialog dialog1=new Dialog(mainActivityObject);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.layoutof_waiting1);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.setCancelable(false);
            //dialog1.setTitle("waiting");
            final TextView textitem=(TextView)dialog1.findViewById(R.id.wait1_item);
            final ImageView image1=(ImageView)dialog1.findViewById(R.id.wait1_logo1);
            final ImageView image2=(ImageView)dialog1.findViewById(R.id.wait1_logo2);
            final ImageView image3=(ImageView)dialog1.findViewById(R.id.wait1_logo3);


            image1.setVisibility(View.INVISIBLE);
            image2.setVisibility(View.INVISIBLE);
            image3.setVisibility(View.INVISIBLE);
            textitem.setText("...");
            dialog1.show();


            waitimage=0;
            waittext="";

            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    for (Integer itemo : selectedIndexList)
                    {
                        if(pagerId==3)
                        {
                            MyContainer container=containerList.get(itemo);
                            String folderName=container.getName();

                            PasteClipBoard.pathList.add(container.getPath());
                            PasteClipBoard.nameList.add(folderName);
                            PasteClipBoard.sizeLongList.add((long)0);
                            PasteClipBoard.isFolderList.add(true);

                            for(MyFile file:container.getMyFileArrayList())
                            {
                                PasteClipBoard.pathList.add(file.getPath());
                                PasteClipBoard.nameList.add(folderName+"/"+file.getName());
                                PasteClipBoard.sizeLongList.add(file.getSizeLong());
                                PasteClipBoard.isFolderList.add(file.isFolder());
                            }
                        }
                        else
                        {
                            MyFile myFile=myFilesList.get(itemo);
                            waittext = myFile.getName();
                            if(myFile.isFolder())
                            {
                                PasteClipBoard.pathList.add(myFile.getPath());
                                PasteClipBoard.nameList.add(myFile.getName());
                                PasteClipBoard.sizeLongList.add((long)0);
                                PasteClipBoard.isFolderList.add(true);

                                relativeIndexOfSlash=myFile.getPath().length()-myFile.getName().length();
                                RecursiveLocal(myFile.getPath());

                            }
                            else
                            {
                                PasteClipBoard.pathList.add(myFile.getPath());
                                PasteClipBoard.nameList.add(myFile.getName());
                                PasteClipBoard.sizeLongList.add(myFile.getSizeLong());
                                PasteClipBoard.isFolderList.add(false);
                            }
                        }

                    }
                    isThreadToLoadFilesFinished = true;
                }
            };



            final Handler handler=new Handler();
            waitimage=0;
            waittext="";


            runnable1=new Runnable()
            {
                @Override
                public void run()
                {
                    waitimage++;
                    if(waitimage==4)
                    {
                        waitimage=1;
                    }
                    if(!isThreadToLoadFilesStarted)
                    {
                        isThreadToLoadFilesStarted=true;
                        thread.start();
                    }

                    if( isThreadToLoadFilesFinished)
                    {
                        if(someSelectedFilesMissing)
                        {
                            Toast.makeText(mainActivityObject, "Some Selected files are not copied to clipboard ,either they does not exist OR are not readable",Toast.LENGTH_LONG).show();
                        }

                        if(PasteClipBoard.nameList.size()>0)
                        {
                            mainActivityObject.pasteButtonDecisionMaker.showHidePasteButton(pageIndex);
                            Toast.makeText(mainActivityObject, "Ready to Paste", Toast.LENGTH_SHORT).show();

                            for(int i=0;i<PasteClipBoard.nameList.size();i++)
                            {
                                Log.e("ClipBoard",PasteClipBoard.nameList.get(i)+"----"+PasteClipBoard.isFolderList.get(i)+"-----"+PasteClipBoard.pathList.get(i)+"-------"+PasteClipBoard.sizeLongList.get(i));
                            }
                        }
                        else
                        {
                            PasteClipBoard.clear();
                            Toast.makeText(mainActivityObject, "Nothing to Copy to Clipboard", Toast.LENGTH_SHORT).show();
                        }

                        dialog1.cancel();
                        handler.removeCallbacks(runnable1);
                    }
                    else
                    {
                        textitem.setText(waittext);
                        switch (waitimage)
                        {
                            case 1:
                                image1.setVisibility(View.VISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.VISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.VISIBLE);
                                break;

                        }
                        handler.postDelayed(runnable1,100);
                    }
                }
            };
            handler.postDelayed(runnable1,1);

        }

        private void setUpCopyAndCutDrive()
        {
            someSelectedFilesMissing=false;
            PasteClipBoard.fromParentPath=currentPath;
            PasteClipBoard.fromStorageCode=storageId;

            final Dialog dialog1=new Dialog(mainActivityObject);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.layoutof_waiting1);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.setCancelable(false);
            //dialog1.setTitle("waiting");
            final TextView textitem=(TextView)dialog1.findViewById(R.id.wait1_item);
            final ImageView image1=(ImageView)dialog1.findViewById(R.id.wait1_logo1);
            final ImageView image2=(ImageView)dialog1.findViewById(R.id.wait1_logo2);
            final ImageView image3=(ImageView)dialog1.findViewById(R.id.wait1_logo3);


            image1.setVisibility(View.INVISIBLE);
            image2.setVisibility(View.INVISIBLE);
            image3.setVisibility(View.INVISIBLE);
            textitem.setText("...");
            dialog1.show();


            waitimage=0;
            waittext="";

            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {

                    for (Integer itemo : selectedIndexList)
                    {
                        if(pagerId==3)
                        {
                            MyContainer container=containerList.get(itemo);
                            String folderName=container.getName();

                            PasteClipBoard.pathList.add(container.getPath());
                            PasteClipBoard.nameList.add(folderName);
                            PasteClipBoard.sizeLongList.add((long)0);
                            PasteClipBoard.isFolderList.add(true);

                            for(MyFile file:container.getMyFileArrayList())
                            {
                                PasteClipBoard.pathList.add(file.getPath());
                                PasteClipBoard.nameList.add(folderName+"/"+file.getName());
                                PasteClipBoard.sizeLongList.add(file.getSizeLong());
                                PasteClipBoard.isFolderList.add(file.isFolder());
                            }
                        }
                        else
                        {
                            String pathId=myFilesList.get(itemo).getPath();
                            waittext = myFilesList.get(itemo).getName();

                            if (myFilesList.get(itemo).isFolder())
                            {
                                Recursive(pathId,null);
                            }
                            else
                            {
                                PasteClipBoard.pathList.add(pathId);
                                PasteClipBoard.nameList.add(myFilesList.get(itemo).getName());
                                PasteClipBoard.sizeLongList.add(myFilesList.get(itemo).getSizeLong());
                                PasteClipBoard.isFolderList.add(false);
                            }
                        }
                    }

                    isThreadToLoadFilesFinished = true;
                }
            };



            final Handler handler=new Handler();
            waitimage=0;
            waittext="";


            runnable1=new Runnable()
            {
                @Override
                public void run()
                {
                    waitimage++;
                    if(waitimage==4)
                    {
                        waitimage=1;
                    }
                    if(!isThreadToLoadFilesStarted)
                    {
                        isThreadToLoadFilesStarted=true;
                        thread.start();
                    }

                    if( isThreadToLoadFilesFinished)
                    {
                        if(someSelectedFilesMissing)
                        {
                            Toast.makeText(mainActivityObject, "Some Selected files are not copied to clipboard ,either they does not exist OR Internet Connection is Not Available",Toast.LENGTH_LONG).show();
                        }

                        if(PasteClipBoard.nameList.size()>0)
                        {
                            mainActivityObject.pasteButtonDecisionMaker.showHidePasteButton(pageIndex);
                            Toast.makeText(mainActivityObject, "Ready to Paste", Toast.LENGTH_SHORT).show();
                            for(int i=0;i<PasteClipBoard.nameList.size();i++)
                            {
                                Log.e("ClipBoard",PasteClipBoard.nameList.get(i)+"----"+PasteClipBoard.isFolderList.get(i)+"-----"+PasteClipBoard.pathList.get(i)+"-------"+PasteClipBoard.sizeLongList.get(i));
                            }
                        }
                        else
                        {
                            PasteClipBoard.clear();
                            Toast.makeText(mainActivityObject, "Nothing to Copy to Clipboard", Toast.LENGTH_SHORT).show();
                        }


                        dialog1.cancel();
                        handler.removeCallbacks(runnable1);
                    }
                    else
                    {
                        textitem.setText(waittext);
                        switch (waitimage)
                        {
                            case 1:
                                image1.setVisibility(View.VISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.VISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.VISIBLE);
                                break;

                        }
                        handler.postDelayed(runnable1,100);
                    }

                }
            };
            handler.postDelayed(runnable1,1);

        }

        private void setUpCopyAndCutDropbox()
        {
            someSelectedFilesMissing=false;
            PasteClipBoard.fromParentPath=currentPath;
            PasteClipBoard.fromStorageCode=storageId;

            final Dialog dialog1=new Dialog(mainActivityObject);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.layoutof_waiting1);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.setCancelable(false);
            //dialog1.setTitle("waiting");
            final TextView textitem=(TextView)dialog1.findViewById(R.id.wait1_item);
            final ImageView image1=(ImageView)dialog1.findViewById(R.id.wait1_logo1);
            final ImageView image2=(ImageView)dialog1.findViewById(R.id.wait1_logo2);
            final ImageView image3=(ImageView)dialog1.findViewById(R.id.wait1_logo3);


            image1.setVisibility(View.INVISIBLE);
            image2.setVisibility(View.INVISIBLE);
            image3.setVisibility(View.INVISIBLE);
            textitem.setText("...");
            dialog1.show();

            waitimage=0;
            waittext="";

            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    for (Integer itemo : selectedIndexList)
                    {
                        if(pagerId==3)
                        {
                            MyContainer container=containerList.get(itemo);
                            String folderName=container.getName();

                            PasteClipBoard.pathList.add(container.getPath());
                            PasteClipBoard.nameList.add(folderName);
                            PasteClipBoard.sizeLongList.add((long)0);
                            PasteClipBoard.isFolderList.add(true);

                            for(MyFile file:container.getMyFileArrayList())
                            {
                                PasteClipBoard.pathList.add(file.getPath());
                                PasteClipBoard.nameList.add(folderName+"/"+file.getName());
                                PasteClipBoard.sizeLongList.add(file.getSizeLong());
                                PasteClipBoard.isFolderList.add(file.isFolder());
                            }
                        }
                        else
                        {
                            MyFile myFile=myFilesList.get(itemo);
                            waittext = myFile.getName();
                            if (myFile.isFolder())
                            {
                                relativeIndexOfSlash=myFile.getPath().length()-myFile.getName().length();
                                Recursive(myFile.getPath());
                            }
                            else
                            {
                                PasteClipBoard.pathList.add(myFile.getPath());
                                PasteClipBoard.nameList.add(myFile.getName());
                                PasteClipBoard.sizeLongList.add(myFile.getSizeLong());
                                PasteClipBoard.isFolderList.add(false);
                            }
                        }
                    }
                    isThreadToLoadFilesFinished=true;
                }
            };



            final Handler handler=new Handler();
            waitimage=0;
            waittext="";


            runnable1=new Runnable()
            {
                @Override
                public void run()
                {
                    waitimage++;
                    if(waitimage==4)
                    {
                        waitimage=1;
                    }
                    if(!isThreadToLoadFilesStarted)
                    {
                        isThreadToLoadFilesStarted=true;
                        thread.start();
                    }

                    if( isThreadToLoadFilesFinished)
                    {

                        if(someSelectedFilesMissing)
                        {
                            Toast.makeText(mainActivityObject, "Some Selected files are not copied to clipboard ,either they does not exist OR Internet Connection is Not Available",Toast.LENGTH_LONG).show();
                        }
                        if(PasteClipBoard.nameList.size()>0)
                        {
                            mainActivityObject.pasteButtonDecisionMaker.showHidePasteButton(pageIndex);
                            Toast.makeText(mainActivityObject, "Ready to Paste", Toast.LENGTH_SHORT).show();
                            for(int i=0;i<PasteClipBoard.nameList.size();i++)
                            {
                                Log.e("ClipBoard",PasteClipBoard.nameList.get(i)+"----"+PasteClipBoard.isFolderList.get(i)+"-----"+PasteClipBoard.pathList.get(i)+"-------"+PasteClipBoard.sizeLongList.get(i));
                            }
                        }
                        else
                        {
                            PasteClipBoard.clear();
                            Toast.makeText(mainActivityObject, "Nothing to Copy to Clipboard", Toast.LENGTH_SHORT).show();
                        }

                        dialog1.cancel();
                        handler.removeCallbacks(runnable1);
                    }
                    else
                    {
                        textitem.setText(waittext);
                        switch (waitimage)
                        {
                            case 1:
                                image1.setVisibility(View.VISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.VISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.VISIBLE);
                                break;

                        }
                        handler.postDelayed(runnable1,100);
                    }

                }
            };
            handler.postDelayed(runnable1,1);

        }


        private void setUpCopyAndCutFtp()
        {
            someSelectedFilesMissing=false;
            PasteClipBoard.fromParentPath=currentPath;
            PasteClipBoard.fromStorageCode=storageId;



            final Dialog dialog1=new Dialog(mainActivityObject);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.layoutof_waiting1);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.setCancelable(false);
            //dialog1.setTitle("waiting");
            final TextView textitem=(TextView)dialog1.findViewById(R.id.wait1_item);
            final ImageView image1=(ImageView)dialog1.findViewById(R.id.wait1_logo1);
            final ImageView image2=(ImageView)dialog1.findViewById(R.id.wait1_logo2);
            final ImageView image3=(ImageView)dialog1.findViewById(R.id.wait1_logo3);


            image1.setVisibility(View.INVISIBLE);
            image2.setVisibility(View.INVISIBLE);
            image3.setVisibility(View.INVISIBLE);
            textitem.setText("...");
            dialog1.show();


            waitimage=0;
            waittext="";

            final Thread thread=new Thread()
            {
                @Override
                public void run()
                {

                    for (Integer itemo : selectedIndexList)
                    {
                        MyFile myFile=myFilesList.get(itemo);
                        waittext = myFile.getName();

                        if (myFile.isFolder())
                        {
                            relativeIndexOfSlash=myFile.getPath().length()-myFile.getName().length();
                            RecursiveFtp(myFile.getPath());
                        }
                        else
                        {
                            PasteClipBoard.pathList.add(myFile.getPath());
                            PasteClipBoard.nameList.add(myFile.getName());
                            PasteClipBoard.sizeLongList.add(myFile.getSizeLong());
                            PasteClipBoard.isFolderList.add(false);
                        }
                    }
                    isThreadToLoadFilesFinished = true;
                }
            };



            final Handler handler=new Handler();
            waitimage=0;
            waittext="";


            runnable1=new Runnable()
            {
                @Override
                public void run()
                {
                    waitimage++;
                    if(waitimage==4)
                    {
                        waitimage=1;
                    }
                    if(!isThreadToLoadFilesStarted)
                    {
                        isThreadToLoadFilesStarted=true;
                        thread.start();
                    }

                    if( isThreadToLoadFilesFinished)
                    {
                        if(someSelectedFilesMissing)
                        {
                            Toast.makeText(mainActivityObject, "Some Selected files are not copied to clipboard ,either they does not exist OR are not readable",Toast.LENGTH_LONG).show();
                        }

                        if(PasteClipBoard.nameList.size()>0)
                        {
                            mainActivityObject.pasteButtonDecisionMaker.showHidePasteButton(pageIndex);
                            Toast.makeText(mainActivityObject, "Ready to Paste", Toast.LENGTH_SHORT).show();
                            for(int i=0;i<PasteClipBoard.nameList.size();i++)
                            {
                                Log.e("ClipBoard",PasteClipBoard.nameList.get(i)+"----"+PasteClipBoard.isFolderList.get(i)+"-----"+PasteClipBoard.pathList.get(i)+"-------"+PasteClipBoard.sizeLongList.get(i));
                            }
                        }
                        else
                        {
                            PasteClipBoard.clear();
                            Toast.makeText(mainActivityObject, "Nothing to Copy to Clipboard", Toast.LENGTH_SHORT).show();
                        }

                        dialog1.cancel();
                        handler.removeCallbacks(runnable1);
                    }
                    else
                    {
                        textitem.setText(waittext);
                        switch (waitimage)
                        {
                            case 1:
                                image1.setVisibility(View.VISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.VISIBLE);
                                image3.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                image1.setVisibility(View.INVISIBLE);
                                image2.setVisibility(View.INVISIBLE);
                                image3.setVisibility(View.VISIBLE);
                                break;

                        }
                        handler.postDelayed(runnable1,100);
                    }
                }
            };
            handler.postDelayed(runnable1,1);

        }

        private void hideUnHide(boolean toHide)
        {
            ArrayList<MyFile> selectedMyFilesList=new ArrayList<>();
            for(Integer index:selectedIndexList)
            {
                selectedMyFilesList.add(myFilesList.get(index));
            }
            HidingUnhidingMachine hidingUnhidingMachine=new HidingUnhidingMachine(mainActivityObject,storageId,selectedMyFilesList);
            hidingUnhidingMachine.start(toHide);
        }

        private void setUpDelete()
        {
            if(storageId==5 && isRecycleBin)
            {
                mainActivityObject.showSmackBar("Dropbox Items will be deleted permanently after 30 days of deletion.");
            }
            else
            {
                DeletingMachine deletingMachine=new DeletingMachine(mainActivityObject,pagerId,isRecycleBin,storageId,runRootCommand,myFilesList,containerList,selectedIndexList);
                deletingMachine.decisionMaker();
            }
        }

        private void setUpRename()
        {
            if(selectedIndexList.size()==1)
            {
                RenameMachine renameMachine=new RenameMachine(mainActivityObject,myFilesList.get(selectedIndexList.get(0)),storageId);
                renameMachine.setUpRename();
            }
            else
            {
                Toast.makeText(mainActivityObject, "Select one item only to rename", Toast.LENGTH_SHORT).show();
            }
        }

        private void addToFavourites()
        {
            Favourites favourites=new Favourites(mainActivityObject,storageId,myFilesList,selectedIndexList);
            favourites.addToFavourites();
        }

        private void removeFromFavourites()
        {
            Favourites favourites=new Favourites(mainActivityObject,storageId,myFilesList,selectedIndexList);
            favourites.removeFromFavourites();
        }

        public void setUpRestore()
        {
            RestoringMachine restoringMachine=new RestoringMachine(mainActivityObject,storageId,fragment,myFilesList,selectedIndexList);
            restoringMachine.showDialog();
        }


        private void RecursiveLocal(String pathToLoad)    //OKOK
        {
            waittext=pathToLoad;

            FolderLister folderLister=new FolderLister();
            ArrayList<MyFile> myFileArrayList=folderLister.listLocalFolder(pathToLoad);

            if(myFileArrayList==null)
            {
                myFileArrayList=folderLister.listRootFolder(mainActivityObject,pathToLoad);
            }
            if(myFileArrayList==null)
            {
                someSelectedFilesMissing=true;
                return;
            }


            for (MyFile child :myFileArrayList)
            {
                if(child.isFolder())
                {
                    String x2=child.getPath().substring(relativeIndexOfSlash);
                    PasteClipBoard.nameList.add(x2);
                    PasteClipBoard.pathList.add(child.getPath());
                    PasteClipBoard.sizeLongList.add((long)0);
                    PasteClipBoard.isFolderList.add(true);

                    RecursiveLocal(child.getPath());
                }
                else
                {
                    String x2=child.getPath().substring(relativeIndexOfSlash);
                    PasteClipBoard.nameList.add(x2);
                    PasteClipBoard.pathList.add(child.getPath());
                    PasteClipBoard.sizeLongList.add(child.getSizeLong());
                    PasteClipBoard.isFolderList.add(false);
                }
            }

        }

        private void Recursive(String folderId,String root)
        {
            try
            {
                String xpath;
                com.google.api.services.drive.model.File folder= GoogleDriveConnection.m_service_client.files().get(folderId).setFields("name").execute();

                if(root==null)
                {
                    xpath=folder.getName();
                }
                else
                {
                    xpath=root+"/"+folder.getName();
                }
                PasteClipBoard.nameList.add(xpath);
                PasteClipBoard.pathList.add(folderId);
                PasteClipBoard.sizeLongList.add((long)0);
                PasteClipBoard.isFolderList.add(true);

                waittext=xpath;

                com.google.api.services.drive.Drive.Files.List request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,quotaBytesUsed)").setQ("'" +folderId+ "' in parents");
                List<com.google.api.services.drive.model.File> filesInFolder= new ArrayList<com.google.api.services.drive.model.File>();
                do
                {
                    try
                    {
                        FileList filelist = request.execute();
                        filesInFolder.addAll(filelist.getFiles());
                        request.setPageToken(filelist.getNextPageToken());
                    }
                    catch (IOException e)
                    {
                        System.out.println("An error occurred: " + e);
                        request.setPageToken(null);
                        someSelectedFilesMissing=true;
                    }
                }
                while (request.getPageToken() != null && request.getPageToken().length() > 0);

                for(com.google.api.services.drive.model.File f:filesInFolder)
                {
                    if(f.getMimeType().contains("folder"))
                    {
                        Recursive(f.getId(),xpath);
                    }
                    else
                    {
                        PasteClipBoard.nameList.add(xpath+"/"+f.getName());
                        PasteClipBoard.pathList.add(f.getId());
                        PasteClipBoard.sizeLongList.add(f.getQuotaBytesUsed());
                        PasteClipBoard.isFolderList.add(false);
                    }
                }
            }
            catch (IOException e)
            {
                someSelectedFilesMissing=true;
            }
        }

        private void Recursive(String path)
        {
            String x=path.substring(relativeIndexOfSlash);
            PasteClipBoard.nameList.add(x);
            PasteClipBoard.pathList.add(path);
            PasteClipBoard.sizeLongList.add((long)0);
            PasteClipBoard.isFolderList.add(true);

            waittext=x;

            List <com.dropbox.core.v2.files.Metadata> list=new ArrayList<>();
            try
            {

                ListFolderResult result = DropBoxConnection.mDbxClient.files().listFolder(path);
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

            }
            catch (Exception e)
            {
                someSelectedFilesMissing=true;
                return;
            }

            for (Metadata child : list)
            {
                if(child instanceof FolderMetadata)
                {
                    Recursive(child.getPathDisplay());
                }
                else
                {
                    String x2=child.getPathDisplay().substring(relativeIndexOfSlash);
                    PasteClipBoard.nameList.add(x2);
                    PasteClipBoard.pathList.add(child.getPathDisplay());
                    PasteClipBoard.sizeLongList.add(((FileMetadata)child).getSize());
                    PasteClipBoard.isFolderList.add(false);
                }
            }
        }

        private void RecursiveFtp(String folderPath)    //OKOK
        {

            String x=folderPath.substring(relativeIndexOfSlash);
            PasteClipBoard.nameList.add(x);
            PasteClipBoard.pathList.add(folderPath);
            PasteClipBoard.sizeLongList.add((long)0);
            PasteClipBoard.isFolderList.add(true);

            waittext=x;

            FTPFile[] filesFtp=null;
            try
            {
                filesFtp= FtpCache.mFTPClient.listFiles(folderPath);
            }
            catch (Exception e)
            {
                filesFtp=null;
            }

            if(filesFtp==null)
            {
                someSelectedFilesMissing=true;
            }
            else
            {
                for(FTPFile child:filesFtp)
                {
                    String name=child.getName();
                    String path=slashAppender(folderPath,name);
                    if(child.isDirectory() )
                    {
                        RecursiveFtp(path);
                    }
                    else
                    {
                        String x2=path.substring(relativeIndexOfSlash);
                        PasteClipBoard.nameList.add(x2);
                        PasteClipBoard.pathList.add(path);
                        PasteClipBoard.sizeLongList.add(child.getSize());
                        PasteClipBoard.isFolderList.add(false);
                    }
                }
            }
        }

        private void clearDefaults()
        {
            for(Integer index:selectedIndexList)
            {
                MyFile myFile=myFilesList.get(index);
                String extension=extensionUtil.getExtension(myFile.getName());
                if(extension!=null)
                {
                    SharedPreferences pref=mainActivityObject.getSharedPreferences("FileOpenerPref",0);
                    SharedPreferences.Editor editor=pref.edit();
                    editor.remove(extension+"1");
                    editor.remove(extension+"2");
                    editor.apply();
                }
            }
            Toast.makeText(mainActivityObject, "Defaults Cleared", Toast.LENGTH_SHORT).show();

        }

        void setUpProperties()
        {
            PropertiesMachine propertiesMachine=new PropertiesMachine(mainActivityObject,pagerId,currentPath,storageId,runRootCommand,myFilesList,containerList,selectedIndexList);
            propertiesMachine.decisionMaker();
        }

        void fastDownload(MyFile myFile)
        {
            FastDownload fastDownload=new FastDownload(mainActivityObject,myFile,storageId);
            fastDownload.decisionMaker();
        }

    }

    private class MyAppOperations
    {
        private void installFromIntent()
        {
            appManagerCache.installList.clear();
            for(Integer i:selectedIndexList)
            {
                appManagerCache.installList.add(myFilesList.get(i).getMyApp());
            }
            int size=appManagerCache.installList.size();
            if(size>0)
            {
                MyApp x=appManagerCache.installList.remove(0);
                appManagerUtils.installFromIntent(x.getApkPath());
            }
        }

        private void uninstallFromIntent()
        {
            appManagerCache.uninstallList.clear();
            for(Integer i:selectedIndexList)
            {
                appManagerCache.uninstallList.add(myFilesList.get(i).getMyApp());
            }
            int size=appManagerCache.uninstallList.size();
            if(size>0)
            {
                MyApp x=appManagerCache.uninstallList.remove(0);
                appManagerUtils.unInstallFromIntent(x.getPackageName());
            }
        }

        private void uninstallAsSuperUser()
        {
            //uninstall IN BACKGROUND
            if(SuperUser.hasUserEnabledSU)
            {
                SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
                {
                    @Override
                    public void yesClicked()
                    {
                        BackgroundInstallUninstallMachine backgroundInstallUninstallMachine=new BackgroundInstallUninstallMachine(mainActivityObject,fragment,myFilesList,selectedIndexList,199,0);
                        backgroundInstallUninstallMachine.decisionMaker();
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

        private void installAsSuperUser(final int pmOrUserOrSystem)
        {
            //install IN BACKGROUND
            if(SuperUser.hasUserEnabledSU)
            {
                SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
                {
                    @Override
                    public void yesClicked()
                    {
                        BackgroundInstallUninstallMachine backgroundInstallUninstallMachine=new BackgroundInstallUninstallMachine(mainActivityObject,fragment,myFilesList,selectedIndexList,99,pmOrUserOrSystem);
                        backgroundInstallUninstallMachine.decisionMaker();
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

        private void backUpApps()
        {
            ArrayList<MyApp> myAppArrayList=new ArrayList<>();
            for(Integer index:selectedIndexList)
            {
                MyFile f=myFilesList.get(index);
                myAppArrayList.add(f.getMyApp());
            }
            appManagerUtils.backupApk(myAppArrayList);
        }

    }

    @NonNull
    private String slashAppender(String a, String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }
}
