package com.example.sahil.f2.OperationTheater;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Classes.CreateNewItems;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.FunkyAdapters.CreateNewItemAdapter;
import com.example.sahil.f2.GokuFrags.pager0;
import com.example.sahil.f2.GokuFrags.storageAnalyser;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.UiClasses.Refresher;
import com.example.sahil.f2.Utilities.CreateNewUtils;
import com.example.sahil.f2.Utilities.RenameUtils;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getFrameIdFromPageIndex;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getLocalHomeStoragePath;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getStorageIdFromPageName;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.isValidStoragePage;

/**
 * Created by hit4man47 on 1/1/2018.
 */

public class CreateNew
{
    public FloatingActionButton addButton;
    private Dialog dialog,dialog0;
    private EditText editName,editExtension;
    private Button cancel,ok;
    private TextView title,extensionLabel;
    private ListView listView;

    private String currentPath;
    private boolean isSaf=false;
    private boolean isRoot=false;
    private int storageId;
    private MainActivity mainActivityObject;

    //CONSTRUCTOR WILL BE CALLED ONLY ONCE
    public CreateNew(Activity activity)
    {
        this.mainActivityObject=(MainActivity)activity ;
        addButton = (FloatingActionButton) activity.findViewById(R.id.fab_new);
        setClickListeners();
    }

    private void setClickListeners()
    {
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addButtonClicked();
            }
        });
    }

    private void addButtonClicked()
    {
        final int currentVisiblePageIndex=mainActivityObject.viewPager.getCurrentItem();
        if(currentVisiblePageIndex==0)
        {
            //REFRESH PAGER 0
            int currentFrameId=getFrameIdFromPageIndex(currentVisiblePageIndex);
            try
            {
                pager0 fragment=(pager0)mainActivityObject.getSupportFragmentManager().findFragmentById(currentFrameId);
                fragment.openNewPager();
            }
            catch (Exception e)
            {}

            return;
        }

        boolean fileFolderEnabled=canCreateFileFolder();

        Page page=MainActivity.pageList.get(currentVisiblePageIndex);
        ArrayList<String> PagePathList=page.getPathList();
        currentPath=PagePathList.get(PagePathList.size()-1);
        storageId=getStorageIdFromPageName(page.getName());



        final ArrayList<CreateNewItems> itemsArrayList=new ArrayList<>();
        final ArrayList<Boolean> isItemEnabled=new ArrayList<>();

        itemsArrayList.add(new CreateNewItems("Folder",null,null,R.mipmap.folder5,1));
        isItemEnabled.add(fileFolderEnabled);

        itemsArrayList.add(new CreateNewItems("File",null,null,R.mipmap.txt,2));
        isItemEnabled.add(fileFolderEnabled && storageId!=5 && storageId!=6);


        for(int i=0;i<MainActivity.Physical_Storage_PATHS.size();i++)
        {
            if(i==0)
            {
                itemsArrayList.add(new CreateNewItems("Root Tab","Local1","/",R.mipmap.root,12345));
                isItemEnabled.add(true);
                itemsArrayList.add(new CreateNewItems("Local Storage Tab","Local1",MainActivity.Physical_Storage_PATHS.get(0),R.drawable.sd_card,12345));
                isItemEnabled.add(true);
            }
            if(i==1)
            {
                itemsArrayList.add(new CreateNewItems("Local Storage Tab","Local2",MainActivity.Physical_Storage_PATHS.get(1),R.drawable.sd_card,12345));
                isItemEnabled.add(true);
            }
            if(i==2)
            {
                itemsArrayList.add(new CreateNewItems("Local Storage Tab","Local3",MainActivity.Physical_Storage_PATHS.get(2),R.drawable.sd_card,12345));
                isItemEnabled.add(true);
            }
        }
        if(GoogleDriveConnection.isDriveAvailable)
        {
            itemsArrayList.add(new CreateNewItems("Google Drive Tab","GoogleDrive","root",R.mipmap.google_drive,12345));
            isItemEnabled.add(true);
        }
        if(DropBoxConnection.isDropboxAvailable)
        {
            itemsArrayList.add(new CreateNewItems("DropBox Tab","DropBox","",R.mipmap.dropbox,12345));
            isItemEnabled.add(true);
        }



        dialog0 = new Dialog(mainActivityObject);
        dialog0.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog0.setContentView(R.layout.layoutof_create_new);

        listView=(ListView)dialog0.findViewById(R.id.createNew_listView) ;
        CreateNewItemAdapter createNewItemAdapter=new CreateNewItemAdapter(mainActivityObject,itemsArrayList,isItemEnabled);
        listView.setAdapter(createNewItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
            {

                if(!isItemEnabled.get(pos))
                {
                    return;
                }

                CreateNewItems createNewItem=itemsArrayList.get(pos);
                dialog0.cancel();
                switch (createNewItem.getItemId())
                {
                    case 1:
                        openSecondDialog(true);
                        break;
                    case 2:
                        openSecondDialog(false);
                        break;
                    case 12345:
                        HelpingBot.addPageAndGoto(mainActivityObject,createNewItem.getPageName(),currentVisiblePageIndex,createNewItem.getFirstPath(),createNewItem.getIconId(),12345);
                        break;
                }
            }
        });

        dialog0.show();
    }

    private boolean isStorageOk()
    {
        isSaf=false;
        isRoot=false;
        if(storageId<=3)
        {
            String storageHomePath=getLocalHomeStoragePath(currentPath);
            boolean rootOperation=storageHomePath==null;
            if(rootOperation)
            {
                if(!SuperUser.hasUserEnabledSU)
                {
                    //if not rooted
                    Toast.makeText(mainActivityObject, "Root Access Required", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                {
                    //root
                    isRoot=true;
                    return true;
                }
            }
            else
            {
                File file=new File(storageHomePath);
                if(!file.canWrite())
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        isSaf=true;

                        String [] breaker=storageHomePath.split("\\/");
                        String storageName=breaker[breaker.length-1];
                        if(MainActivity.SDCardUriMap.get(storageName)==null)
                        {
                            StorageAccessFramework storageAccessFramework=new StorageAccessFramework(mainActivityObject);
                            storageAccessFramework.showSaf(3,storageName);
                            return false;
                        }
                    }
                    else
                    {
                        Toast.makeText(mainActivityObject, "Error:This directory is not Writable", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
        }
        return true;
    }

    private void openSecondDialog(final boolean isFolder)
    {

        if(!isStorageOk())
        {
            return;
        }

        dialog = new Dialog(mainActivityObject);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_rename);
        dialog.setCanceledOnTouchOutside(false);

        editName=(EditText)dialog.findViewById(R.id.rename_edittext1);
        editExtension =(EditText)dialog.findViewById(R.id.rename_edittext2);
        cancel=(Button)dialog.findViewById(R.id.rename_cancel);
        ok=(Button)dialog.findViewById(R.id.rename_ok);
        title=(TextView) dialog.findViewById(R.id.renameTitle);
        extensionLabel=(TextView) dialog.findViewById(R.id.extension_label);

        if(isFolder)
        {
            title.setText("CREATE A NEW FOLDER");
        }
        else
        {
            title.setText("CREATE A BLANK FILE");
        }

        if(isFolder)
        {
            editName.setText("");
            editExtension.setVisibility(View.GONE);
            extensionLabel.setVisibility(View.GONE);
        }
        else
        {
            editExtension.setVisibility(View.VISIBLE);
            extensionLabel.setVisibility(View.VISIBLE);
            editName.setText("");
            editExtension.setText("");
        }


        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startCreating(isFolder,currentPath);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
            }
        });

        dialog.show();

    }


    private void startCreating(final boolean createFolder,final String parentPath)
    {
        String newName;
        if(createFolder)
        {
            newName=editName.getText().toString();
        }
        else
        {
            final String extension=editExtension.getText().toString();
            final String initialName=editName.getText().toString();

            newName=initialName+extension;
            if(extension.length()>0 && !extension.startsWith("."))
            {
                Toast.makeText(mainActivityObject, "Extensions starts with '.'", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(newName.length()==0)
        {
            Toast.makeText(mainActivityObject, "Name Too Short", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] illegal={"\"","\\","/","*",":","?","|","<",">"};
        for(String s:illegal)
        {
            if(newName.contains(s))
            {
                Toast.makeText(mainActivityObject, "\" / \\ < > ? | : * are not allowed in file name", Toast.LENGTH_LONG).show();
                return;
            }
        }

        final String newPath=slashAppender(parentPath,newName);

        if(storageId<=3)
        {
            File newFile=new File(newPath);
            if(newFile.exists())
            {
                Toast.makeText(mainActivityObject, "Item with same name already exists", Toast.LENGTH_LONG).show();
                return;
            }
        }


        class MyAsyncTask extends AsyncTask<String, Integer, String>
        {
            private final String newName,parentPath;
            private final CreateNewUtils createNewUtils;

            private MyAsyncTask(String newName,String parentPath)
            {
                this.newName=newName;
                this.parentPath=parentPath;
                createNewUtils=new CreateNewUtils(mainActivityObject);
            }

            private ProgressDialog pd;

            protected void onPreExecute()
            {
                super.onPreExecute();
                pd=new ProgressDialog(mainActivityObject);
                pd.setTitle("Creation in Progress");
                pd.setMessage(newName);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(true);
                pd.show();
            }

            protected String doInBackground(String... arg0)
            {
                boolean x=false;
                final String fullPath=slashAppender(parentPath,newName);
                if(storageId<=3)
                {
                    if(isSaf)
                    {
                        x=createNewUtils.createInSaf(fullPath,createFolder,false);
                    }
                    else
                    {
                        if(isRoot)
                        {
                            //root
                            x=createNewUtils.createInRoot(fullPath,createFolder,false);
                        }
                        else
                        {
                            x=createNewUtils.createInInternal(fullPath,createFolder,false);
                        }
                    }
                }
                if(storageId==4)
                {
                    x=createNewUtils.createInDrive(parentPath,newName,createFolder);
                }
                if(storageId==5)
                {
                    x=createNewUtils.createInDropBox(fullPath,createFolder,true);
                }
                if(storageId==6)
                {
                    x=createNewUtils.createInFtp(fullPath);
                }
                if(x)
                {
                    return "all done";
                }
                else
                {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String xx)
            {
                if(xx==null)
                {
                    Toast.makeText(mainActivityObject, "Creation Failed", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(mainActivityObject, "Success", Toast.LENGTH_SHORT).show();
                    Refresher refresher =new Refresher(mainActivityObject);
                    refresher.refresh();
                }
                pd.cancel();
                //refresh
            }

        }

        dialog.cancel();
        MyAsyncTask myAsyncTask=new MyAsyncTask(newName,parentPath);
        myAsyncTask.execute();



    }


    /**
     *
     * @param pageIndex if >=0 : it is called from fragment and should only work if that fragment is visible
     *                  if =-1 : viewPager called it after every swipe and should always work
     */
    public void showHideAddButton(int pageIndex)
    {
        Log.e("showHideAddButton"+pageIndex,"called########################################################################################3");
        if(pageIndex>=0)
        {
            //called from fragment
            if(mainActivityObject.viewPager.getCurrentItem()!=pageIndex)
            {
                //not in foreground and should not work
                Log.e("showHideAddButton"+pageIndex,"background########################################################################################");
                return;
            }
        }
        try
        {
            showButton(refreshOrCreate());
        }
        catch (Exception e)
        {

        }

        /*
        try
        {
            int x=;//-1 hide   0 refresh   1 createNew
            if(x>=0)
            {
                Log.e("showHideAddButton"+pageIndex,"showing########################################################################################");
                showButton(x);
            }
            else
            {
                Log.e("showHideAddButton"+pageIndex,"hiding########################################################################################");
                hideButton();
            }
        }
        catch (Exception e)
        {
            Log.e("MainActivity","showPasteButton"+e.getMessage()+e.getLocalizedMessage());
        }
         */

    }


    private boolean canCreateFileFolder()
    {
        int currentVisiblePageIndex=mainActivityObject.viewPager.getCurrentItem();

        int currentFrameId=getFrameIdFromPageIndex(currentVisiblePageIndex);

        Page page=MainActivity.pageList.get(currentVisiblePageIndex);


        if(isValidStoragePage(page))
        {
            if(page.getPageId()==11)
            {
                storageAnalyser fragment=(storageAnalyser) mainActivityObject.getSupportFragmentManager().findFragmentById(currentFrameId);
                return fragment.isFolderOk;
            }
            if(page.getPageId()==12345 || (page.getPageId()==15 && !page.getCurrentPath().equals("FtpClient")))
            {
                storagePager fragment=(storagePager)mainActivityObject.getSupportFragmentManager().findFragmentById(currentFrameId);
                return fragment.isFolderOk;
            }
        }
        return false;
    }

    /**
     *
     * @return
     *          0 show refresh button
     *          1 show create new button
     * @throws Exception
     */
    private int refreshOrCreate() throws Exception        //OKOK
    {
        int currentVisiblePageIndex=mainActivityObject.viewPager.getCurrentItem();

        if(currentVisiblePageIndex==0)
            return 0;
        else
            return 1;
    }


    private void showButton(int refreshOrCreate)    //0-refresh     1-create
    {
        if(refreshOrCreate==0)
        {
            addButton.setImageResource(R.mipmap.refresh);
        }
        else
        {
            addButton.setImageResource(R.mipmap.create);
        }

        addButton.setVisibility(View.VISIBLE);
        //addButton.show(true);
    }

    private void hideButton()
    {
        addButton.setVisibility(View.GONE);
        //addButton.hide(true);
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
