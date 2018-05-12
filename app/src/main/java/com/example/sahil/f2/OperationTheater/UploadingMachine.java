package com.example.sahil.f2.OperationTheater;


import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.FunkyAdapters.CopyListAdapter;
import com.example.sahil.f2.HomeServicesProvider.UploadService1;
import com.example.sahil.f2.HomeServicesProvider.UploadService2;
import com.example.sahil.f2.HomeServicesProvider.UploadService3;
import com.example.sahil.f2.HomeServicesProvider.UploadService4;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.UiClasses.Refresher;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/23/2017.
 */

public class UploadingMachine extends MainOperationClass
{
    private final MainActivity mainActivity;
    private final TinyDB tinyDB;
    private final HelpingBot helpingBot;
    private Intent serviceIntent;
    private int forceStop=0;


    private ArrayList<String> progressNameList;


    public UploadingMachine(MainActivity mainActivity,int operationId)
    {
        super(operationId,mainActivity);
        this.mainActivity=mainActivity;
        tinyDB=new TinyDB(mainActivity);
        helpingBot=new HelpingBot();
    }

    public void uploading()
    {
        if(!isSpaceOk())
        {
            return;
        }

        super.setDialog(uploadData.fromStorageId, uploadData.toStorageId, uploadData.fromRootPath, uploadData.toRootPath, uploadData.totalSizeToDownload, uploadData.currentFileName, uploadData.currentFileIndex, uploadData.totalFiles, uploadData.downloadedSize, uploadData.progress);

        progressNameList=new ArrayList<>();
        progressNameList.addAll(uploadData.namesList);

        final CopyListAdapter copyListAdapter=new CopyListAdapter(mainActivity,1,progressNameList,operationId);

        switch (operationId)
        {
            case 301:
                serviceIntent= new Intent(mainActivity, UploadService1.class);
                break;
            case 302:
                serviceIntent= new Intent(mainActivity, UploadService2.class);
                break;
            case 303:
                serviceIntent= new Intent(mainActivity, UploadService3.class);
                break;
            case 304:
                serviceIntent= new Intent(mainActivity, UploadService4.class);
                break;
        }

        uploadData.dialog=super.dialog;
        super.dialog.show();
        final Handler h=new Handler();


        super.progressListView.setAdapter(copyListAdapter);



        uploadData.runnable=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e(operationId+"RUNNER", uploadData.timeInSec+"......................");
                ++uploadData.downloadCounter;
                if(uploadData.downloadCounter%2==0)
                {
                    iv_taskLogo.setVisibility(View.INVISIBLE);
                }
                else
                {
                    iv_taskLogo.setVisibility(View.VISIBLE);
                }

                copyListAdapter.notifyDataSetChanged();
                progressListView.setSelection(uploadData.currentFileIndex-3);

                tv_fileName.setText(uploadData.currentFileName);
                tv_itemProgress.setText(uploadData.currentFileIndex+"/"+ uploadData.totalFiles +" items");
                tv_sizeProgress.setText(helpingBot.sizeinwords(uploadData.downloadedSize)+"/"+helpingBot.sizeinwords(uploadData.totalSizeToDownload));
                tv_percent.setText(uploadData.progress+" %");
                pb_progressBar.setProgress((int) uploadData.progress);


                tv_speed.setText(helpingBot.sizeinwords(uploadData.downloadedSize- uploadData.oldDownloadedSize)+"/sec");


                uploadData.oldDownloadedSize= uploadData.downloadedSize;

                uploadData.timeInSec++;


                if(uploadData.pauseDownloadingPlease)
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
                    uploadData.isServiceRunning=false;
                }


                if(!uploadData.isServiceRunning) //when service has ended
                {
                    Refresher refresher =new Refresher(mainActivity);
                    refresher.refresh();


                    //SERVICE STOPPED BY USER
                    if(uploadData.cancelDownloadingPlease)
                    {
                        h.removeCallbacks(uploadData.runnable);
                    }
                    else
                    {
                        if(uploadData.pauseDownloadingPlease)
                        {
                            iv_taskLogo.setVisibility(View.VISIBLE);
                            tv_speed.setText(taskName+" Paused");
                            tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                            Toast.makeText(mainActivity, "Paused", Toast.LENGTH_SHORT).show();
                            btn_pause_resume.setText("RESUME");
                            h.removeCallbacks(uploadData.runnable);
                        }
                        else
                        {
                            //SERVICE HAS FACED SOME ERRORS AND MAY BE STARTED AGAIN
                            if(uploadData.isDownloadError)
                            {
                                iv_taskLogo.setVisibility(View.VISIBLE);
                                tv_speed.setText(taskName+" Error");
                                tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));
                                Toast.makeText(mainActivity, "Error", Toast.LENGTH_SHORT).show();
                                btn_pause_resume.setText("RESUME");
                                tv_errorDetails.setText(ErrorHandler.getErrorName(uploadData.downloadErrorCode)+"-->"+ uploadData.errorDetails);
                                tv_errorDetails.setVisibility(View.VISIBLE);
                                btn_skip.setVisibility(View.VISIBLE);


                                h.removeCallbacks( uploadData.runnable);
                            }
                            else
                            {
                                //SERVICE HAS COMPLETED DOWNLOADING
                                if(uploadData.progress==100)
                                {
                                    iv_taskLogo.setVisibility(View.VISIBLE);
                                    tv_speed.setText(taskName+" Done...");
                                    Toast.makeText(mainActivity, taskName+" Successful", Toast.LENGTH_SHORT).show();

                                    btn_cancel.setVisibility(View.GONE);
                                    btn_pause_resume.setVisibility(View.GONE);
                                    btn_hide.setText("CLOSE");

                                    tinyDB.remove(operationId+"Url");
                                    tinyDB.remove(operationId+"ChunkStart");
                                    tinyDB.remove(operationId+"IsRunning");
                                    tinyDB.remove(operationId+"CurrentSourcePath");
                                    tinyDB.remove(operationId+"LastSourcePath");
                                    tasksCache.removeTask(operationId+"");

                                    h.removeCallbacks(uploadData.runnable);
                                }
                            }
                        }
                    }
                }
                else
                {
                    h.postDelayed(uploadData.runnable,1000);
                }
            }
        };

        uploadData.isServiceRunning=true;
        mainActivity.startService(serviceIntent);

        h.postDelayed(uploadData.runnable,1000);




    }

    private boolean isSpaceOk()
    {
        long totalSize=0;
        long usedSize=0;

        if(operationId==301|| operationId==302)
        {
            //dropbox upload
            totalSize= DropBoxConnection.totalSize;
            usedSize=DropBoxConnection.usedSize;
        }
        if(operationId==303 || operationId==304)
        {
            //google drive upload
            totalSize= GoogleDriveConnection.totalSize;
            usedSize=GoogleDriveConnection.usedSize;
        }

        long available=totalSize-usedSize;


        long totalSizeToDownload= uploadData.totalSizeToDownload;
        long downloadedSize=uploadData.downloadedSize;
        if((totalSizeToDownload-downloadedSize) >= available)
        {
            mainActivity.showLowSpaceError(totalSizeToDownload-downloadedSize,available);
            return false;
        }
        else
        {
            return true;
        }
        
    }

}
