package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListRevisionsResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.restoreCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyContainer;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.FunkyAdapters.DeleteListAdapter;
import com.example.sahil.f2.FunkyAdapters.RestoreAdapter;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.UiClasses.Refresher;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/27/2017.
 */

public class RestoringMachine
{
    public final MainActivity mainActivity;
    private int storageId;
    private ArrayList<Integer> selectedIndexList;
    private ArrayList<MyFile> myFilesList;
    private Runnable runnable;
    private android.support.v4.app.Fragment fragment;
    private TextView title,details;
    private Dialog dialog;
    private ProgressBar progressBar;
    private ListView listView;
    private RestoreAdapter restoreAdapter;
    private Button close;
    private ArrayList<String> pathsList;
    private ArrayList<String> namesList;
    final private TinyDB tinyDB;
    private boolean threadStopped=false;

    public RestoringMachine(MainActivity mainActivity,int storageId, android.support.v4.app.Fragment fragment, ArrayList<MyFile> myFilesList, ArrayList<Integer> selectedIndexList)
    {
        this.mainActivity=mainActivity;
        this.storageId=storageId;
        this.selectedIndexList=selectedIndexList;
        this.myFilesList=myFilesList;
        this.fragment=fragment;
        tinyDB=new TinyDB(mainActivity);
    }


    public void showDialog()
    {

        namesList=new ArrayList<>();
        pathsList=new ArrayList<>();
        restoreCache.destinationList=new ArrayList<>();
        for(Integer item:selectedIndexList)
        {
            MyFile file=myFilesList.get(item);
            namesList.add(file.getName());
            pathsList.add(file.getPath());
        }


        dialog = new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_restore);
        dialog.setCanceledOnTouchOutside(false);
        title=(TextView) dialog.findViewById(R.id.restore_title);
        details=(TextView) dialog.findViewById(R.id.restore_details);
        listView=(ListView) dialog.findViewById(R.id.restore_list);
        close=(Button) dialog.findViewById(R.id.restore_close);
        progressBar=(ProgressBar) dialog.findViewById(R.id.restore_progress);


        restoreAdapter=new RestoreAdapter(mainActivity,1,namesList);


        View view=restoreAdapter.getView(0,null,listView);
        view.measure(0,0);
        if((selectedIndexList.size()>5))
        {
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) (5.8 *view.getMeasuredHeight()));
            listView.setLayoutParams(params);
        }


        listView.setAdapter(restoreAdapter);

        details.setVisibility(View.GONE);
        close.setText("CLOSE");
        close.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.cancel();
                Refresher refresher =new Refresher(mainActivity);
                refresher.refresh();
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        dialog.show();

        threadStopped=false;
        final Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                switch (storageId)
                {
                    case 1:
                        restoreLocal();
                        break;
                    case 4:
                        restoreGoogleDrive();
                        break;
                    case 5:
                        restoreDropBox();
                        break;
                }
                threadStopped=true;
            }
        };
        thread.start();

        final Handler handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("Restoring..","-----------------------------------");
                restoreAdapter.notifyDataSetChanged();
                listView.setSelection(restoreCache.destinationList.size());
                if(progressBar.getVisibility()!=View.VISIBLE)
                {
                    progressBar.setVisibility(View.VISIBLE);
                }


                if(threadStopped)
                {
                    int totalErrors=0;
                    for(String x:restoreCache.destinationList)
                    {
                        if(x.equals("FAILED"))
                        {
                            totalErrors++;
                        }
                    }
                    if(totalErrors==0)
                    {
                        String s="Restored Successfully";
                        if(storageId>3)
                        {
                            s=s+" ,may take a while to reflect changes.";
                        }
                        details.setText(s);
                        Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String s="Restoration Complete with "+totalErrors+" error(s)";
                        details.setText(s);
                        Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
                    }
                    details.setVisibility(View.VISIBLE);
                    close.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    storagePager pager=(storagePager)fragment;
                    pager.reloadPager();
                    handler.removeCallbacks(runnable);
                }
                else
                {
                    handler.postDelayed(runnable,500);
                }
            }
        };
        handler.post(runnable);
    }

    private void restoreLocal()
    {
        for(int i=0;i<namesList.size();i++)
        {
            String pathFrom=pathsList.get(i);
            File fromFile=new File(pathFrom);
            String pathToRestore=tinyDB.getString(pathFrom);
            if(pathToRestore.length()==0)
            {
                pathToRestore=MainActivity.Physical_Storage_PATHS.get(0)+"/"+namesList.get(i);
            }
            File toFile=new File(pathToRestore);
            if(toFile.exists())
            {
                toFile=keepBoth(toFile);
            }
            pathToRestore=toFile.getAbsolutePath();

            if(fromFile.renameTo(toFile))
            {
                restoreCache.destinationList.add(pathToRestore);
            }
            else
            {
                restoreCache.destinationList.add("FAILED");
            }
        }
    }

    private void restoreDropBox()
    {
        for(int i=0;i<namesList.size();i++)
        {
            String path=pathsList.get(i);
            try
            {
                ListRevisionsResult revisionsResult = DropBoxConnection.mDbxClient.files().listRevisions(path);
                if(revisionsResult.getIsDeleted())
                {
                    String revisionId=null;
                    long lastModified=0;
                    for(FileMetadata fm:revisionsResult.getEntries())
                    {
                        if(fm.getClientModified().getTime()>lastModified && fm.getRev()!=null)
                        {
                            revisionId=fm.getRev();
                            lastModified=fm.getClientModified().getTime();
                        }
                    }
                    Metadata metadata=DropBoxConnection.mDbxClient.files().restore(path,revisionId);
                    String toPath=metadata.getPathDisplay();
                    restoreCache.destinationList.add(toPath);
                }
                else
                {
                    restoreCache.destinationList.add("FAILED");
                }
            }
            catch (Exception e)
            {
                restoreCache.destinationList.add("FAILED");
            }
        }
    }

    private void restoreGoogleDrive()
    {
        for(int i=0;i<namesList.size();i++)
        {
            String id=pathsList.get(i);
            try
            {
                com.google.api.services.drive.model.File newContent=new com.google.api.services.drive.model.File();
                newContent.setTrashed(false);
                com.google.api.services.drive.model.File file=GoogleDriveConnection.m_service_client.files().update(id,newContent).execute();
                if(file.getId()!=null)
                {
                    restoreCache.destinationList.add("Google Drive");
                }
                else
                {
                    restoreCache.destinationList.add("FAILED");
                }
            }
            catch (Exception e)
            {
                restoreCache.destinationList.add("FAILED");
            }
        }
    }

    public File keepBoth(final File file)
    {

        int fileNumber=0;
        String path=file.getAbsolutePath();
        int lastIndex=path.lastIndexOf('/');
        String rootPath=path.substring(0,lastIndex);
        if(file.isDirectory())        //is a directory
        {
            String x1=file.getName()+"("+(++fileNumber)+")";

            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
            *
            *
            * like if a.txt and a(1).txt is present how to copy???
            *
            */
            while(true)
            {
                if(new File(slashAppender(rootPath,x1)).exists())
                {
                    x1=file.getName()+"("+(++fileNumber)+")";
                }
                else
                {
                    break;
                }
            }
            return new File(slashAppender(rootPath,x1));
        }

        else //is a file
        {
            String x=file.getName(); //.....'vishwas.txt'------'mydiiiicccc'
            String x2="";
            if(x.lastIndexOf('.')>=0)
            {
                x2=x.substring(x.lastIndexOf('.'),x.length());//.....'.txt'------''
            }

            String x1="";

            if(x.lastIndexOf('.')>=0)
            {
                x1=x.substring(0,x.lastIndexOf('.'))+"("+(++fileNumber)+")";//....'vishwas(1)'
            }
            else
            {
                x1=x.substring(0,x.length())+"("+(++fileNumber)+")";//------'mydiiiicccc(1'
            }


            while (true)
            {
                if(new File(slashAppender(rootPath,x1+x2)).exists())
                {
                    if(x.lastIndexOf('.')>=0)
                    {
                        x1=x.substring(0,x.lastIndexOf('.'))+"("+(++fileNumber)+")";//....'vishwas(1)'
                    }
                    else
                    {
                        x1=x.substring(0,x.length())+"("+(++fileNumber)+")";//------'mydiiiicccc(1'
                    }
                }
                else
                {
                    break;
                }
            }
            return new File(slashAppender(rootPath,x1+x2));
        }
    }

    private String slashAppender(String a,String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }

}
