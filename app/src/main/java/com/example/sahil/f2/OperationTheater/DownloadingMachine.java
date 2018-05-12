package com.example.sahil.f2.OperationTheater;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.FunkyAdapters.CopyListAdapter;
import com.example.sahil.f2.HomeServicesProvider.DownloadService1;
import com.example.sahil.f2.HomeServicesProvider.DownloadService2;
import com.example.sahil.f2.HomeServicesProvider.DownloadService3;
import com.example.sahil.f2.HomeServicesProvider.DownloadService4;
import com.example.sahil.f2.HomeServicesProvider.DownloadService5;
import com.example.sahil.f2.HomeServicesProvider.DownloadService6;
import com.example.sahil.f2.HomeServicesProvider.UploadService5;
import com.example.sahil.f2.HomeServicesProvider.UploadService6;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.UiClasses.Refresher;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/23/2017.
 */

public class DownloadingMachine extends MainOperationClass
{
    private final MainActivity mainActivity;
    private final TinyDB tinyDB;
    private final HelpingBot helpingBot;
    private Intent serviceIntent;
    private int forceStop=0;



    private ArrayList<String> progressNameList;


    public DownloadingMachine(MainActivity mainActivity,int operationId)
    {
        super(operationId,mainActivity);
        this.mainActivity=mainActivity;
        tinyDB=new TinyDB(mainActivity);
        helpingBot=new HelpingBot();
    }

    public void downloading()  //ok
    {

        if(operationId!=305 && operationId!=306)
        if(!isSpaceOk(downloadData.totalSizeToDownload,downloadData.downloadedSize, downloadData.toRootPath))
        {
            return;
        }

        super.setDialog(downloadData.fromStorageId, downloadData.toStorageId, downloadData.fromRootPath, downloadData.toRootPath, downloadData.totalSizeToDownload, downloadData.currentFileName, downloadData.currentFileIndex, downloadData.totalFiles, downloadData.downloadedSize, downloadData.progress);


        progressNameList=new ArrayList<>();
        progressNameList.addAll(downloadData.namesList);

        final CopyListAdapter copyListAdapter=new CopyListAdapter(mainActivity,1,progressNameList,operationId);

        switch (operationId)
        {
            case 201:
                serviceIntent= new Intent(mainActivity, DownloadService1.class);
                break;
            case 202:
                serviceIntent= new Intent(mainActivity, DownloadService2.class);
                break;
            case 203:
                serviceIntent= new Intent(mainActivity, DownloadService3.class);
                break;
            case 204:
                serviceIntent= new Intent(mainActivity, DownloadService4.class);
                break;
            case 205:
                serviceIntent= new Intent(mainActivity, DownloadService5.class);
                break;
            case 206:
                serviceIntent= new Intent(mainActivity, DownloadService6.class);
                break;
            case 305:
                serviceIntent= new Intent(mainActivity, UploadService5.class);
                break;
            case 306:
                serviceIntent= new Intent(mainActivity, UploadService6.class);
                break;
        }


        downloadData.dialog=super.dialog;
        super.dialog.show();
        final Handler h=new Handler();


        super.progressListView.setAdapter(copyListAdapter);




        downloadData.runnable=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e(operationId+"RUNNER", downloadData.timeInSec+"......................");
                ++downloadData.downloadCounter;
                if(downloadData.downloadCounter%2==0)
                {
                    iv_taskLogo.setVisibility(View.INVISIBLE);
                }
                else
                {
                    iv_taskLogo.setVisibility(View.VISIBLE);
                }

                copyListAdapter.notifyDataSetChanged();
                progressListView.setSelection(downloadData.currentFileIndex-3);

                tv_fileName.setText(downloadData.currentFileName);
                tv_itemProgress.setText(downloadData.currentFileIndex+"/"+ downloadData.totalFiles +" items");
                tv_sizeProgress.setText(helpingBot.sizeinwords(downloadData.downloadedSize)+"/"+helpingBot.sizeinwords(downloadData.totalSizeToDownload));
                tv_percent.setText(downloadData.progress+" %");
                pb_progressBar.setProgress((int) downloadData.progress);


                tv_speed.setText(helpingBot.sizeinwords(downloadData.downloadedSize- downloadData.oldDownloadedSize)+"/sec");


                downloadData.oldDownloadedSize= downloadData.downloadedSize;

                downloadData.timeInSec++;

                if(downloadData.pauseDownloadingPlease)
                {
                    forceStop++;
                }
                else
                {
                    forceStop=0;
                }
                if(forceStop==5)
                {
                    mainActivity.stopService(serviceIntent);
                    downloadData.isServiceRunning=false;
                }




                if(!downloadData.isServiceRunning) //when service has ended
                {
                    Refresher refresher =new Refresher(mainActivity);
                    refresher.refresh();


                    //SERVICE STOPPED BY USER
                    if(downloadData.cancelDownloadingPlease)
                    {
                        h.removeCallbacks(downloadData.runnable);
                    }
                    else
                    {
                        if(downloadData.pauseDownloadingPlease)
                        {
                            iv_taskLogo.setVisibility(View.VISIBLE);
                            tv_speed.setText(taskName+" Paused");
                            tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                            Toast.makeText(mainActivity, "Paused", Toast.LENGTH_SHORT).show();
                            btn_pause_resume.setText("RESUME");
                            h.removeCallbacks(downloadData.runnable);
                        }
                        else
                        {
                            //SERVICE HAS FACED SOME ERRORS AND MAY BE STARTED AGAIN
                            if(downloadData.isDownloadError)
                            {
                                iv_taskLogo.setVisibility(View.VISIBLE);
                                tv_speed.setText(taskName+" Error");
                                tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                                Toast.makeText(mainActivity, "Error", Toast.LENGTH_SHORT).show();
                                btn_pause_resume.setText("RESUME");
                                tv_errorDetails.setText(ErrorHandler.getErrorName(downloadData.downloadErrorCode)+"-->"+ downloadData.errorDetails);
                                tv_errorDetails.setVisibility(View.VISIBLE);
                                btn_skip.setVisibility(View.VISIBLE);


                                h.removeCallbacks( downloadData.runnable);
                            }
                            else
                            {
                                //SERVICE HAS COMPLETED DOWNLOADING
                                if(downloadData.progress==100)
                                {

                                    iv_taskLogo.setVisibility(View.VISIBLE);
                                    tv_speed.setText(taskName+" Done...");
                                    Toast.makeText(mainActivity, taskName+" Successful", Toast.LENGTH_SHORT).show();

                                    btn_cancel.setVisibility(View.GONE);
                                    btn_pause_resume.setVisibility(View.GONE);
                                    btn_hide.setText("CLOSE");

                                    if(operationId!=305 && operationId!=306)
                                    if(downloadData.isFastDownload)
                                    {
                                        btn_open.setVisibility(View.VISIBLE);
                                    }

                                    tinyDB.remove(operationId+"IsRunning");
                                    tinyDB.remove(operationId+"CurrentDestinationPath");
                                    tinyDB.remove(operationId+"LastDestinationPath");
                                    tasksCache.removeTask(operationId+"");

                                    h.removeCallbacks(downloadData.runnable);
                                }
                            }
                        }
                    }
                }
                else
                {
                    h.postDelayed(downloadData.runnable,1000);
                }
            }
        };

        downloadData.isServiceRunning=true;
        mainActivity.startService(serviceIntent);

        h.postDelayed(downloadData.runnable,1000);


    }

    private boolean isSpaceOk(long totalSizeToDownload,long downloaded,String toRootPath)
    {
        File toRootFile=new File(toRootPath);
        if(!toRootFile.exists())
        {
            boolean x=toRootFile.mkdirs();
        }

        if((totalSizeToDownload-downloaded) >= toRootFile.getUsableSpace())
        {
            long spaceAvailable=toRootFile.getUsableSpace();
            mainActivity.showLowSpaceError(totalSizeToDownload-downloaded,spaceAvailable);
            return false;
        }
        else
        {
            return true;
        }

    }

}
