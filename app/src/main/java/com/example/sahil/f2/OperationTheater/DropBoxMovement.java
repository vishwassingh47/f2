package com.example.sahil.f2.OperationTheater;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.UiClasses.Refresher;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/30/2017.
 */

public class DropBoxMovement
{
    private final MainActivity mainActivity;
    private ArrayList<String> pathList,nameList;
    private ArrayList<Long> sizeListLong;
    private ArrayList<Boolean> isFolderList;
    private String toRootPath;
    private int totalFiles;
    private boolean isMoving;
    private long totalSize;

    public DropBoxMovement(MainActivity mainActivity)
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

        long remaining =DropBoxConnection.totalSize-DropBoxConnection.usedSize;
        if(remaining<=totalSize && !isMoving)
        {
            mainActivity.showLowSpaceError(totalSize,remaining);
            return;
        }

        class MyAsyncTask extends AsyncTask<String, Integer, String>
        {

            private ProgressDialog pd;
            private String error=null;
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
                for(int i = 0; i< nameList.size(); i++)
                {

                    if(nameList.get(i).contains("/"))
                    {
                        continue;
                    }

                    if(toRootPath.startsWith(pathList.get(i)))
                    {
                        error="Error : Cannot transfer the parent to its sunDirectories";
                        return null;
                    }

                    String name=keepBoth(i);
                    if(name==null)
                    {
                        return null;
                    }
                    Metadata metadata=null;
                    try
                    {
                        if(isMoving)
                        {
                            metadata=DropBoxConnection.mDbxClient.files().moveV2(pathList.get(i),slashAppender(toRootPath,name)).getMetadata();
                        }
                        else
                        {
                            metadata=DropBoxConnection.mDbxClient.files().copyV2(pathList.get(i),slashAppender(toRootPath,name)).getMetadata();
                        }
                         Log.e("file done","...."+name);
                    }
                    catch(DbxException e)
                    {
                        Log.e("error dropbox movement:",e.getLocalizedMessage()+e.getMessage());
                        return null;
                    }
                    if(metadata==null)
                        return null;

                    publishProgress((i+1)*100/totalFiles);
                }
                return "ALL done";
            }

            @Override
            protected void onProgressUpdate(Integer...progress)
            {
                pd.setMessage(progress[0]+ "%");
            }

            @Override
            protected void onPostExecute(String toPath)
            {
                if(toPath==null)
                {
                    if(error==null)
                    {
                        Toast.makeText(mainActivity, "Operation Failed", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(mainActivity, error, Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(mainActivity, "Operation Done", Toast.LENGTH_SHORT).show();
                    Refresher refresher =new Refresher(mainActivity);
                    refresher.refresh();
                }
                pd.cancel();
            }

        }

        MyAsyncTask myAsyncTask=new MyAsyncTask();
        myAsyncTask.execute();
    }

    private String slashAppender(String a,String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }


    private String keepBoth(int i)
    {

        int filenumber=0;

        if(isFolderList.get(i))
        {
            String x1=nameList.get(i);

            while(true)
            {
                try
                {
                    Metadata m=DropBoxConnection.mDbxClient.files().getMetadata(slashAppender(toRootPath,x1));
                    //this path already exists
                    x1=nameList.get(i)+"("+(++filenumber)+")";
                }
                catch (GetMetadataErrorException e)
                {
                    if(e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound())
                    {
                        return x1;
                        //this path doesnt exist and we can use this as ours
                    }
                    else
                    {
                        return null;
                    }
                }
                catch (DbxException e)
                {
                    return null;
                }
            }
        }
        else //is a file
        {
            String x=nameList.get(i); //.....'vishwas.txt'------'mydiiiicccc'
            String x2="";
            if(x.lastIndexOf('.')>=0)
            {
                x2=x.substring(x.lastIndexOf('.'),x.length());//.....'.txt'------''
            }


            String x1="";

            if(x.lastIndexOf('.')>=0)
            {
                x1=x.substring(0,x.lastIndexOf('.'));//....'vishwas'
            }
            else
            {
                x1=x.substring(0,x.length());//------'mydiiiicccc'
            }


            while (true)
            {
                try
                {
                    Metadata m=DropBoxConnection.mDbxClient.files().getMetadata(slashAppender(toRootPath,x1+x2));
                    //this path already exists
                    if(x.lastIndexOf('.')>=0)
                    {
                        x1=x.substring(0,x.lastIndexOf('.'))+"("+(++filenumber)+")";//....'vishwas(1)'
                    }
                    else
                    {
                        x1=x.substring(0,x.length())+"("+(++filenumber)+")";//------'mydiiiicccc(1'
                    }
                }
                catch (GetMetadataErrorException e)
                {
                    if(e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound())
                    {
                        return x1+x2;
                        //this path doesnt exist and we can use this as ours
                    }
                    else
                    {
                        return null;
                    }
                }
                catch (DbxException e)
                {
                    return null;
                }
            }
        }
    }


}
