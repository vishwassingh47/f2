package com.example.sahil.f2.OperationTheater;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.FunkyAdapters.CopyListAdapter;
import com.example.sahil.f2.UiClasses.Refresher;
import com.example.sahil.f2.HomeServicesProvider.CopyService1;
import com.example.sahil.f2.HomeServicesProvider.CopyService2;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.RootUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/23/2017.
 */

public class CopingMachine extends MainOperationClass
{
    private final MainActivity mainActivity;
    private final TinyDB tinyDB;
    private final HelpingBot helpingBot;
    private Intent serviceIntent;


    private ArrayList<String> progressNameList;
    

    public CopingMachine(MainActivity mainActivity,int operationId)
    {
        super(operationId,mainActivity);
        this.mainActivity=mainActivity;
        tinyDB=new TinyDB(mainActivity);
        helpingBot=new HelpingBot();
    }


    /*
    IT IS CALLED WHEN copyCache1 IS ALL SET
     */

    private void copying()
    {

        super.setDialog(copyData.fromStorageId, copyData.toStorageId, copyData.fromRootPath, copyData.toRootPath, copyData.totalSizeToDownload, copyData.currentFileName, copyData.currentFileIndex, copyData.totalFiles, copyData.downloadedSize, copyData.progress);


        progressNameList=new ArrayList<>();
        progressNameList.addAll(copyData.namesList);

        final CopyListAdapter copyListAdapter=new CopyListAdapter(mainActivity,1,progressNameList,operationId);

        switch (operationId)
        {
            case 101:
                serviceIntent= new Intent(mainActivity, CopyService1.class);
                break;
            case 102:
                serviceIntent= new Intent(mainActivity, CopyService2.class);
                break;
        }


        copyData.dialog=super.dialog;
        super.dialog.show();
        final Handler h=new Handler();


        super.progressListView.setAdapter(copyListAdapter);



        copyData.runnable=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e(operationId+"RUNNER", copyData.timeInSec+"......................");
                ++copyData.downloadCounter;
                if(copyData.downloadCounter%2==0)
                {
                    iv_taskLogo.setVisibility(View.INVISIBLE);
                }
                else
                {
                    iv_taskLogo.setVisibility(View.VISIBLE);
                }

                copyListAdapter.notifyDataSetChanged();
                progressListView.setSelection(copyData.currentFileIndex-3);

                tv_fileName.setText(copyData.currentFileName);
                tv_itemProgress.setText(copyData.currentFileIndex+"/"+ copyData.totalFiles +" items");
                tv_sizeProgress.setText(helpingBot.sizeinwords(copyData.downloadedSize)+"/"+helpingBot.sizeinwords(copyData.totalSizeToDownload));
                tv_percent.setText(copyData.progress+" %");
                pb_progressBar.setProgress((int) copyData.progress);


                tv_speed.setText(helpingBot.sizeinwords(copyData.downloadedSize- copyData.oldDownloadedSize)+"/sec");


                copyData.oldDownloadedSize= copyData.downloadedSize;

                copyData.timeInSec++;


                if(!copyData.isServiceRunning) //when service has ended
                {
                    Refresher refresher =new Refresher(mainActivity);
                    refresher.refresh();

                    //SERVICE STOPPED BY USER
                    if(copyData.cancelDownloadingPlease)
                    {
                        h.removeCallbacks(copyData.runnable);
                    }
                    else
                    {
                        if(copyData.pauseDownloadingPlease)
                        {
                            iv_taskLogo.setVisibility(View.VISIBLE);
                            tv_speed.setText(taskName+" Paused");
                            tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                            Toast.makeText(mainActivity, "Paused", Toast.LENGTH_SHORT).show();
                            btn_pause_resume.setText("RESUME");
                            h.removeCallbacks(copyData.runnable);
                        }
                        else
                        {
                            //SERVICE HAS FACED SOME ERRORS AND MAY BE STARTED AGAIN
                            if(copyData.isDownloadError)
                            {
                                iv_taskLogo.setVisibility(View.VISIBLE);
                                tv_speed.setText(taskName+" Error");
                                tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                                Toast.makeText(mainActivity, "Error", Toast.LENGTH_SHORT).show();
                                btn_pause_resume.setText("RESUME");
                                tv_errorDetails.setText(ErrorHandler.getErrorName(copyData.downloadErrorCode)+"-->"+ copyData.errorDetails);
                                tv_errorDetails.setVisibility(View.VISIBLE);
                                btn_skip.setVisibility(View.VISIBLE);


                                h.removeCallbacks( copyData.runnable);
                            }
                            else
                            {
                                //SERVICE HAS COMPLETED DOWNLOADING
                                if(copyData.progress==100)
                                {
                                    iv_taskLogo.setVisibility(View.VISIBLE);
                                    tv_speed.setText(taskName+" Done...");
                                    Toast.makeText(mainActivity, taskName+" Successful", Toast.LENGTH_SHORT).show();

                                    btn_cancel.setVisibility(View.GONE);
                                    btn_pause_resume.setVisibility(View.GONE);
                                    btn_hide.setText("CLOSE");


                                    tinyDB.remove(operationId+"IsRunning");
                                    tinyDB.remove(operationId+"CurrentDestinationPath");
                                    tinyDB.remove(operationId+"LastDestinationPath");
                                    tasksCache.removeTask(operationId+"");

                                    h.removeCallbacks(copyData.runnable);
                                }
                            }
                        }
                    }
                }
                else
                {
                    h.postDelayed(copyData.runnable,1000);
                }

            }
        };

        copyData.isServiceRunning=true;
        mainActivity.startService(serviceIntent);

        h.postDelayed(copyData.runnable,1000);


    }

    public void checkSpaceAndCopy()
    {
        class SizeFetcherAsyncTask extends AsyncTask<String, Integer, Boolean>
        {
            private ProgressDialog pd;
            private long free,required;
            protected void onPreExecute()
            {
                super.onPreExecute();
                pd=new ProgressDialog(mainActivity);
                pd.setMessage("Checking available space");
                pd.setCancelable(false);
                pd.show();
            }

            protected Boolean doInBackground(String... arg0)
            {
                RootUtils rootUtils=new RootUtils();
                free=rootUtils.getFreeSpace(copyData.toRootPath,mainActivity);
                required=copyData.totalSizeToDownload-copyData.downloadedSize;
                return (required<free);
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                pd.cancel();
                if(result)
                {
                    copying();
                }
                else
                {
                    mainActivity.showLowSpaceError(required,free);
                }
            }

        }

        SizeFetcherAsyncTask myAsyncTask=new SizeFetcherAsyncTask();
        myAsyncTask.execute();

    }



}
