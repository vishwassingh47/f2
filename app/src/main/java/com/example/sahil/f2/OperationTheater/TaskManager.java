package com.example.sahil.f2.OperationTheater;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.DeleteData;
import com.example.sahil.f2.Cache.DownloadData;
import com.example.sahil.f2.Cache.InstallData;
import com.example.sahil.f2.Cache.MyCacheData;


import com.example.sahil.f2.Cache.UnInstallData;
import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.stericson.RootTools.RootTools;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by hit4man47 on 12/23/2017.
 */

public class TaskManager
{
    final private MainActivity mainActivity;
    private Handler handler;
    private Runnable runnable;
    final private TinyDB tinyDB;
    private ListView listView;
    private TaskAdapter taskAdapter;
    private LinearLayout no_task;

    private final String TAG="TASK MANAGER";


    public TaskManager(Activity activity)
    {
        mainActivity=(MainActivity)activity;
        tinyDB=new TinyDB(mainActivity);
        Log.e(TAG,"*******************************************created********************************************************");
        initialiseRunner();
    }

    private void initialiseRunner()
    {
        handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {
               Log.e(TAG,"tASKMANAGER IS RUNNING########################################################");
               if(tasksCache.tasksId.size()>0)
               {
                   showListView();
                   taskAdapter.notifyDataSetChanged();
               }
               else
               {
                   showNoTask();
               }


               handler.postDelayed(runnable,1000);
            }
        };

    }

    public void showTaskManager()
    {
        stopRunner();
        listView=(ListView) mainActivity.findViewById(R.id.taskManager_listView);
        taskAdapter=new TaskAdapter(mainActivity,0,tasksCache.tasksId);
        no_task=(LinearLayout) mainActivity.findViewById(R.id.taskManager_no_running);
        listView.setAdapter(taskAdapter);

        handler.post(runnable);//RUNNER STARTED

        setListClickListener();

        rootSync();





        /*superlogs.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if(event.getAction() == MotionEvent.ACTION_UP)
                {

                }
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN,navigationView2);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,navigationView2);
                Log.v("AS", "Clicked");
                return true;

            }
        });

                */




    }



    private void showListView()
    {
        if(listView.getVisibility()!=View.VISIBLE)
        {
            listView.setVisibility(View.VISIBLE);
        }
        if(no_task.getVisibility()!=View.GONE)
        {
            no_task.setVisibility(View.GONE);
        }
    }

    private void showNoTask()
    {
        if(no_task.getVisibility()!=View.VISIBLE)
        {
            no_task.setVisibility(View.VISIBLE);
        }
        if(listView.getVisibility()!=View.GONE)
        {
            listView.setVisibility(View.GONE);
        }
    }

    public void stopRunner()
    {
        handler.removeCallbacks(runnable);
    }


    private void setListClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                try
                {
                    String tid= tasksCache.tasksId.get(position);
                    Dialog dialog=null;

                    int operationId=Integer.parseInt(tid);
                    if(operationId==99 )
                    {
                        dialog=MyCacheData.getInstallData(99).dialog;
                    }
                    if(operationId==199)
                    {
                        dialog=MyCacheData.getUnInstallData(199).dialog;
                    }
                    if(operationId==101 || operationId==102)
                    {
                        dialog= MyCacheData.getCopyDataFromCode(operationId).dialog;
                    }
                    if(operationId==1 || operationId==2)
                    {
                        dialog= MyCacheData.getDeleteDataFromCode(operationId).dialog;
                    }

                    if((operationId>=201 && operationId<=206) || operationId==305 || operationId==306)
                    {
                        dialog= MyCacheData.getDownloadDataFromCode(operationId).dialog;
                    }
                    if(operationId>=301 && operationId<=304)
                    {
                        dialog= MyCacheData.getUploadDataFromCode(operationId).dialog;
                    }


                    dialog.show();
                    mainActivity.drawer.closeDrawer(mainActivity.navigationView2);
                }
                catch(Exception e)
                {
                    Toast.makeText(mainActivity, "(Details not available) Tap the Resume Button", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void rootSync()
    {
        final TinyDB tinyDB=new TinyDB(mainActivity);
        final ArrayList <String> list=tinyDB.getListString("rootSync");
        if(list!=null && list.size()>0)
        {
            final LinearLayout rootSyncLayout=(LinearLayout)mainActivity.findViewById(R.id.rootSyncer);
            Button apply=(Button)mainActivity.findViewById(R.id.rootSyncer_apply);
            Button cancel=(Button)mainActivity.findViewById(R.id.rootSyncer_cancel);
            rootSyncLayout.setVisibility(View.VISIBLE);



            cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    tinyDB.putListString("rootSync",new ArrayList<String>(0));
                    rootSyncLayout.setVisibility(View.GONE);
                }
            });

            class applySync extends AsyncTask<Void,Void,String>
            {
                private ProgressDialog pd;
                private ArrayList<String> list;
                private String breaker[];
                private boolean b;

                private applySync(ArrayList<String> list)
                {
                    this.list=list;
                }

                @Override
                protected void onPreExecute()
                {
                    super.onPreExecute();
                    pd=new ProgressDialog(mainActivity);
                    pd.setMessage("Syncing Root Directories\n[:BEWARE:] Interrupting this task can lead to file corruption");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                protected String doInBackground(Void... params)
                {

                    while(list.size()>0)
                    {
                        Log.e("copyx:",list.get(0)+"--");
                        breaker=list.get(0).split(Pattern.quote(" *:*:* "));
                        if(breaker.length==2)
                        {
                            b= RootTools.copyFile(breaker[0]+"",breaker[1]+"",true,true);
                            if(b)
                            {
                                Log.e("success sync root:",breaker[0]+ "->"+breaker[1]);
                            }
                            else
                            {
                                Log.e("failed sync root:",breaker[0]+ "->"+breaker[1]);
                            }
                        }
                        list.remove(0);
                    }
                    return "all done";
                }

                @Override
                protected void onPostExecute(String x)
                {
                    if(pd.isShowing())
                        pd.dismiss();

                    TinyDB tinyDB=new TinyDB(mainActivity);
                    tinyDB.putListString("rootSync",new ArrayList<String>(0));
                    rootSyncLayout.setVisibility(View.GONE);
                }

            }

            apply.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    applySync asyncTask=new applySync(list);
                    asyncTask.execute();
                }
            });



        }
    }


    //NEVER MESS WITH IT
    private class TaskAdapter extends ArrayAdapter<String>
    {
        private Context context;
        private ArrayList<String> task_id_list;
        private HelpingBot helpingBot;

        public TaskAdapter(Context context, int garbage,ArrayList<String> task_id_list)
        {
            super(context,garbage,task_id_list);
            this.context=context;
            helpingBot=new HelpingBot();
            this.task_id_list=task_id_list;
        }

        public View getView(final int pos, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView=inflater.inflate(R.layout.layoutof_row5,parent,false);


            TextView task_name = (TextView) rowView.findViewById(R.id.row5_task_name);
            TextView current_file_name=(TextView) rowView.findViewById(R.id.row5_task_current_filename);
            TextView items=(TextView) rowView.findViewById(R.id.row5_task_items);
            TextView percent=(TextView) rowView.findViewById(R.id.row5_task_percent);
            TextView size=(TextView) rowView.findViewById(R.id.row5_task_size);

            ImageView task_icon=(ImageView) rowView.findViewById(R.id.row5_task_icon);
            ProgressBar pb=(ProgressBar) rowView.findViewById(R.id.row5_task_progress);
            final Button cancel=(Button)rowView.findViewById(R.id.row5_task_cancel);
            final Button pause_resume=(Button)rowView.findViewById(R.id.row5_task_retry);
            cancel.setText("CANCEL");
            pause_resume.setText("RESUME");

            //SETTING TASK ICONS
            task_icon.setImageResource(helpingBot.getTaskLogo(task_id_list.get(pos)));

            //SETTING TASK NAME
            task_name.setText(helpingBot.getTaskName(task_id_list.get(pos)));


            String tid=task_id_list.get(pos);
            final int operationId = Integer.parseInt(tid);

            if( (operationId>=201 && operationId<=206) || operationId==305 || operationId==306)
            {
                final DownloadData downloadData=MyCacheData.getDownloadDataFromCode(operationId);
                if(downloadData.isServiceRunning)
                {
                    pause_resume.setText("PAUSE");
                }
                else
                {
                    pause_resume.setText("RESUME");
                }

                percent.setText((int) downloadData.progress+"%");
                items.setText(downloadData.currentFileIndex+"/"+ downloadData.totalFiles);
                size.setText(helpingBot.sizeinwords(downloadData.downloadedSize)+"/"+helpingBot.sizeinwords(downloadData.totalSizeToDownload));
                pb.setProgress((int) downloadData.progress);
                current_file_name.setText(downloadData.currentFileName);


                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        downloadData.cancelDownloadingPlease=true;
                        tinyDB.remove(operationId+"IsRunning");
                        tinyDB.remove(operationId+"CurrentDestinationPath");
                        tinyDB.remove(operationId+"LastDestinationPath");
                        tasksCache.removeTask(operationId+"");
                    }
                });
                pause_resume.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (pause_resume.getText().equals("PAUSE"))
                        {
                            if(downloadData.totalSizeToDownload!= downloadData.downloadedSize)
                            {
                                pause_resume.setText("PAUSING");
                                downloadData.pauseDownloadingPlease=true;
                            }
                        }
                        if (pause_resume.getText().equals("RESUME"))
                        {
                            mainActivity.drawer.closeDrawer(mainActivity.navigationView2);
                            downloadData.getTinyDbData(tinyDB);
                            DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,operationId);
                            downloadingMachine.downloading();
                        }
                    }
                });
            }
            if(operationId>=301 && operationId<=304)
            {
                final UploadData uploadData=MyCacheData.getUploadDataFromCode(operationId);
                if(uploadData.isServiceRunning)
                {
                    pause_resume.setText("PAUSE");
                }
                else
                {
                    pause_resume.setText("RESUME");
                }

                percent.setText((int) uploadData.progress+"%");
                items.setText(uploadData.currentFileIndex+"/"+ uploadData.totalFiles);
                size.setText(helpingBot.sizeinwords(uploadData.downloadedSize)+"/"+helpingBot.sizeinwords(uploadData.totalSizeToDownload));
                pb.setProgress((int) uploadData.progress);
                current_file_name.setText(uploadData.currentFileName);


                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        uploadData.cancelDownloadingPlease=true;
                        tinyDB.remove(operationId+"Url");
                        tinyDB.remove(operationId+"ChunkStart");
                        tinyDB.remove(operationId+"IsRunning");
                        tinyDB.remove(operationId+"CurrentSourcePath");
                        tinyDB.remove(operationId+"LastSourcePath");
                        tasksCache.removeTask(operationId+"");
                    }
                });
                pause_resume.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (pause_resume.getText().equals("PAUSE"))
                        {
                            if(uploadData.totalSizeToDownload!= uploadData.downloadedSize)
                            {
                                pause_resume.setText("PAUSING");
                                uploadData.pauseDownloadingPlease=true;
                            }
                        }
                        if (pause_resume.getText().equals("RESUME"))
                        {
                            mainActivity.drawer.closeDrawer(mainActivity.navigationView2);
                            uploadData.getTinyDbData(tinyDB);
                            UploadingMachine uploadingMachine=new UploadingMachine(mainActivity,operationId);
                            uploadingMachine.uploading();
                        }
                    }
                });
            }
            if(operationId==101 || operationId==102)
            {
                final CopyData copyData=MyCacheData.getCopyDataFromCode(operationId);
                if(copyData.isServiceRunning)
                {
                    pause_resume.setText("PAUSE");
                }
                else
                {
                    pause_resume.setText("RESUME");
                }

                percent.setText((int) copyData.progress+"%");
                items.setText(copyData.currentFileIndex+"/"+ copyData.totalFiles);
                size.setText(helpingBot.sizeinwords(copyData.downloadedSize)+"/"+helpingBot.sizeinwords(copyData.totalSizeToDownload));
                pb.setProgress((int) copyData.progress);
                current_file_name.setText(copyData.currentFileName);


                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        copyData.cancelDownloadingPlease=true;
                        tinyDB.remove(operationId+"IsRunning");
                        tinyDB.remove(operationId+"CurrentDestinationPath");
                        tinyDB.remove(operationId+"LastDestinationPath");
                        tasksCache.removeTask(operationId+"");
                    }
                });
                pause_resume.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        if (pause_resume.getText().equals("PAUSE"))
                        {
                            if(copyData.totalSizeToDownload!= copyData.downloadedSize)
                            {
                                pause_resume.setText("PAUSING");
                                copyData.pauseDownloadingPlease=true;
                            }
                        }
                        if (pause_resume.getText().equals("RESUME"))
                        {
                            mainActivity.drawer.closeDrawer(mainActivity.navigationView2);
                            copyData.getTinyDbData(tinyDB);
                            CopingMachine copingMachine=new CopingMachine(mainActivity,operationId);
                            copingMachine.checkSpaceAndCopy();
                        }
                    }
                });
            }

            if(operationId==1 || operationId==2)
            {
                final DeleteData deleteData=MyCacheData.getDeleteDataFromCode(operationId);
                pause_resume.setVisibility(View.GONE);
                percent.setVisibility(View.INVISIBLE);
                items.setText(deleteData.currentFileIndex+"/"+deleteData.totalFiles);
                size.setVisibility(View.GONE);
                pb.setIndeterminate(true);
                current_file_name.setText(deleteData.currentFileName);

                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        deleteData.cancelDownloadingPlease=true;
                        tasksCache.removeTask(operationId+"");
                    }
                });
            }
            if(operationId==99)
            {
                final InstallData installData=MyCacheData.getInstallData(operationId);
                pause_resume.setVisibility(View.GONE);
                percent.setVisibility(View.INVISIBLE);
                items.setText(installData.currentFileIndex+"/"+installData.totalFiles);
                size.setVisibility(View.GONE);
                pb.setIndeterminate(false);
                pb.setProgress((int)installData.progress);
                String currentFileName;
                int index=installData.currentFileIndex;
                if(index==installData.totalFiles)
                    index--;
                currentFileName=installData.myFilesList.get(index).getName();
                current_file_name.setText(currentFileName);

                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        installData.cancelPlease=true;
                        tasksCache.removeTask(operationId+"");
                    }
                });
            }
            if(operationId==199)
            {
                final UnInstallData unInstallData=MyCacheData.getUnInstallData(operationId);
                pause_resume.setVisibility(View.GONE);
                percent.setVisibility(View.INVISIBLE);
                items.setText(unInstallData.currentFileIndex+"/"+unInstallData.totalFiles);
                size.setVisibility(View.GONE);
                pb.setIndeterminate(false);
                pb.setProgress((int)unInstallData.progress);
                String currentFileName;
                int index=unInstallData.currentFileIndex;
                if(index==unInstallData.totalFiles)
                    index--;
                currentFileName=unInstallData.myFilesList.get(index).getName();
                current_file_name.setText(currentFileName);

                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        unInstallData.cancelPlease=true;
                        tasksCache.removeTask(operationId+"");
                    }
                });
            }


            return rowView;
        }


    }



}
