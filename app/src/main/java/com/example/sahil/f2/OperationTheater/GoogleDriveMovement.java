package com.example.sahil.f2.OperationTheater;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.UiClasses.Refresher;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hit4man47 on 12/30/2017.
 */

public class GoogleDriveMovement
{
    private final MainActivity mainActivity;
    private ArrayList<String> pathList,nameList;
    private ArrayList<Long> sizeListLong;
    private ArrayList<Boolean> isFolderList;
    private String toRootPath;
    private int totalFiles;
    private boolean isMoving;
    private long totalSize;
    private String fromRootPath;


    public GoogleDriveMovement(MainActivity mainActivity)
    {
        this.mainActivity=mainActivity;
    }


    public void start()
    {
        nameList=new ArrayList<>();
        sizeListLong=new ArrayList<>();
        pathList=new ArrayList<>();
        isFolderList=new ArrayList<>();

        totalSize=0;
        toRootPath=PasteClipBoard.toRootPath;
        fromRootPath=PasteClipBoard.fromParentPath;
        totalFiles=PasteClipBoard.pathList.size();
        isMoving=PasteClipBoard.cutOrCopy==1;

        for(int i=0;i<totalFiles;i++)
        {
            totalSize+= PasteClipBoard.sizeLongList.get(i);
            nameList.add(i,PasteClipBoard.nameList.get(i));
            sizeListLong.add(i,PasteClipBoard.sizeLongList.get(i));
            pathList.add(i,PasteClipBoard.pathList.get(i));
            isFolderList.add(i,PasteClipBoard.isFolderList.get(i));
        }

        PasteClipBoard.clear();

        long remaining = GoogleDriveConnection.totalSize-GoogleDriveConnection.usedSize;
        if(remaining<=totalSize && !isMoving)
        {
            mainActivity.showLowSpaceError(totalSize,remaining);
            return;
        }

        class MyAsyncTask extends AsyncTask<String, Integer, String>
        {

            private ProgressDialog pd;

            protected void onPreExecute()
            {
                super.onPreExecute();
                String taskName;
                pd=new ProgressDialog(mainActivity);
                taskName=isMoving?"Moving":"Copying";
                pd.setTitle(taskName+" in Progress");
                pd.setMessage("0 %");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
            }

            protected String doInBackground(String... arg0)
            {

                if(isMoving)
                {
                    for(int i=0;i<nameList.size();i++)
                    {
                        try
                        {
                            File created=null;
                            int lastIndex=nameList.get(i).lastIndexOf('/');
                            if(lastIndex>=0)
                            {
                                continue;
                            }
                            created=GoogleDriveConnection.m_service_client.files().update(pathList.get(i),null)
                                    .setAddParents(toRootPath)
                                    .setRemoveParents(fromRootPath)
                                    .setFields("id")
                                    .execute();
                            if(created==null || created.getId()==null)
                            {
                                return null;
                            }
                        }
                        catch(IOException e)
                        {
                            Log.e("errorDRIVEMoveMovement",e.getLocalizedMessage()+e.getMessage());
                            return null;
                        }
                        publishProgress((i+1)*100/totalFiles);
                    }
                }
                else
                {
                    for(int i = 0; i< nameList.size(); i++)
                    {
                        try
                        {
                            File created=null;
                            int lastIndex=nameList.get(i).lastIndexOf('/');
                            String name=nameList.get(i).substring(lastIndex+1);
                            String newParentId;
                            if(lastIndex<0)
                            {
                                newParentId=toRootPath;
                            }
                            else
                            {
                                String parentName=nameList.get(i).substring(0,lastIndex);
                                int parentIndex=nameList.indexOf(parentName);
                                newParentId=pathList.get(parentIndex);
                            }

                            if(isFolderList.get(i))//is a folder
                            {
                                File file=new File();
                                file.setName(name);
                                file.setMimeType("application/vnd.google-apps.folder");
                                List<String> parents=new ArrayList<>();
                                parents.add(newParentId);
                                file.setParents(parents);
                                created=GoogleDriveConnection.m_service_client.files().create(file).setFields("id").execute();

                                if(created==null)
                                    return null;
                                pathList.set(i,created.getId());
                            }
                            else//is a file
                            {
                                File file=new File();
                                file.setName(name);
                                List<String> parents=new ArrayList<>();
                                parents.add(newParentId);
                                file.setParents(parents);
                                created=GoogleDriveConnection.m_service_client.files().copy(pathList.get(i),file).execute();
                                if(created==null)
                                    return null;
                            }
                        }
                        catch(IOException e)
                        {
                            Log.e("errorDRIVECopyMovement",e.getLocalizedMessage()+e.getMessage());
                            return null;
                        }
                        publishProgress((i+1)*100/totalFiles);
                        Log.e("file done","...."+nameList.get(i));
                    }
                }
                return "all is well";
            }

            @Override
            protected void onPostExecute(String toPath)
            {
                if(toPath==null)
                {
                    Toast.makeText(mainActivity, "Operation Failed", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Refresher refresher =new Refresher(mainActivity);
                    refresher.refresh();
                    Toast.makeText(mainActivity, "Operation Done", Toast.LENGTH_SHORT).show();
                }
                pd.cancel();
            }

            @Override
            protected void onProgressUpdate(Integer...progress)
            {
                pd.setMessage(progress[0]+ "%");
            }

        }

        MyAsyncTask myAsyncTask=new MyAsyncTask();
        myAsyncTask.execute();
    }

}
