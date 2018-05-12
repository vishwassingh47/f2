package com.example.sahil.f2.OperationTheater;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.UiClasses.Refresher;
import com.example.sahil.f2.Utilities.RenameUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hit4man47 on 2/17/2018.
 */

public class HidingUnhidingMachine
{
    private final MainActivity mainActivity;
    private final int storageId;
    private final ArrayList<MyFile> selectedMyFiles;
    private boolean isSaf;
    private boolean isRoot;

    public HidingUnhidingMachine(MainActivity mainActivity, int storageId,ArrayList<MyFile> selectedMyFiles)
    {
        this.mainActivity=mainActivity;
        this.storageId=storageId;
        this.selectedMyFiles=selectedMyFiles;
    }

    public void start(final boolean toHide)
    {
        if(!isStorageOk())
        {
            return;
        }

        class MyAsyncTask extends AsyncTask<String, Integer, String>
        {

            private ProgressDialog pd;
            private final RenameUtils renameUtils;

            private MyAsyncTask()
            {
                renameUtils=new RenameUtils(mainActivity);
            }

            protected void onPreExecute()
            {
                super.onPreExecute();
                pd=new ProgressDialog(mainActivity);
                String s=toHide?"Hiding":"UnHiding";
                pd.setTitle(s+" in Progress");
                pd.setMessage(selectedMyFiles.size()+" items");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(true);
                pd.show();
            }

            protected String doInBackground(String... arg0)
            {
                for(MyFile myFile:selectedMyFiles)
                {
                    String newName;
                    final String oldName=myFile.getName();
                    if(toHide)
                    {
                        if(oldName.startsWith("."))
                        {
                            continue;
                        }
                        else
                        {
                            newName="."+oldName;
                        }
                    }
                    else
                    {
                        if(oldName.startsWith("."))
                        {
                            if(oldName.length()>=2)
                            {
                                newName=oldName.substring(1);
                            }
                            else
                            {
                                continue;
                            }
                        }
                        else
                        {
                            continue;
                        }
                    }
                    String parentPath=HelpingBot.getParentPath(myFile.getPath());

                    if(storageId<=3)
                    {
                        if(isSaf)
                        {
                            renameUtils.renameInSaf(myFile,parentPath,newName,true);
                        }
                        else
                        {
                            if(isRoot)
                            {
                                renameUtils.renameInRoot(myFile,parentPath,newName,true);
                            }
                            else
                            {
                                renameUtils.renameInInternal(myFile,parentPath,newName,true);
                            }
                        }
                    }
                    if(storageId==4)
                    {
                        renameUtils.renameInDrive(myFile,newName);
                    }
                    if(storageId==5)
                    {
                        renameUtils.renameInDropBox(myFile,parentPath,newName,true);
                    }
                    if(storageId==6)
                    {
                        renameUtils.renameInFtp(myFile,parentPath,newName);
                    }
                }
                return "all done";
            }

            @Override
            protected void onPostExecute(String toPath)
            {
                pd.cancel();
                Refresher refresher =new Refresher(mainActivity);
                refresher.refresh();
                //refresh
            }

        }
        MyAsyncTask myAsyncTask=new MyAsyncTask();
        myAsyncTask.execute();


    }


    private boolean isStorageOk()
    {
        isSaf=false;
        isRoot=false;

        if(storageId<=3)
        {
            MyFile myFile=selectedMyFiles.get(0);
            String storageHomePath=PagerXUtilities.getLocalHomeStoragePath(myFile.getPath());
            boolean rootOperation=storageHomePath==null;
            if(rootOperation)
            {
                if(!SuperUser.hasUserEnabledSU)
                {
                    //if not rooted
                    Toast.makeText(mainActivity, "Root Access Required", Toast.LENGTH_SHORT).show();
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
                            StorageAccessFramework storageAccessFramework=new StorageAccessFramework(mainActivity);
                            storageAccessFramework.showSaf(3,storageName);
                            return false;
                        }
                    }
                    else
                    {
                        Toast.makeText(mainActivity, "Error:This directory is not Writable", Toast.LENGTH_SHORT).show();
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


}
