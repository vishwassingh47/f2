package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.DownloadData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;

import java.io.File;

/**
 * Created by hit4man47 on 12/29/2017.
 */

public class MainOperationClass
{
    public Dialog dialog;
    public TextView tv_taskName,tv_fileName,tv_fromPath,tv_toPath,tv_size,tv_itemProgress,tv_sizeProgress,tv_percent,tv_speed,tv_errorDetails;
    public ImageView iv_taskLogo,iv_toLogo,iv_fromLogo;
    public ProgressBar pb_progressBar;
    public Button btn_cancel,btn_open,btn_skip,btn_pause_resume,btn_swipe,btn_hide;
    public LinearLayout page1,page2;
    public ListView progressListView;

    private CopingMachine copingMachine;
    private DownloadingMachine downloadingMachine;
    private UploadingMachine uploadingMachine;
    public final int operationId;
    private final  MainActivity mainActivity;
    private final TinyDB tinyDB;
    private final HelpingBot helpingBot;

   final public DownloadData downloadData;
   final public UploadData uploadData;
   final public CopyData copyData;
    public String taskName;
    String localPathOfThisMyFile;
    File file;

    public MainOperationClass(int operationId, MainActivity mainActivity)
    {
        this.operationId=operationId;
        this.mainActivity=mainActivity;
        tinyDB=new TinyDB(mainActivity);
        helpingBot=new HelpingBot();

        downloadData= MyCacheData.getDownloadDataFromCode(operationId);
        copyData=MyCacheData.getCopyDataFromCode(operationId);
        uploadData=MyCacheData.getUploadDataFromCode(operationId);
    }

    public void setDialog(int fromStorageId,int toStorageId,String fromRootPath,String toRootPath,long totalSizeToDownload,String currentFileName,int currentFileIndex,int totalFiles,long downloadedSize,long progress)
    {
        dialog = new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_copy2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        tv_taskName=(TextView) dialog.findViewById(R.id.copy2_task_name);
        tv_fileName=(TextView) dialog.findViewById(R.id.copy2_name);
        tv_fromPath=(TextView) dialog.findViewById(R.id.copy2_from);
        tv_toPath=(TextView) dialog.findViewById(R.id.copy2_to);
        tv_size=(TextView) dialog.findViewById(R.id.copy2_size);
        tv_itemProgress=(TextView) dialog.findViewById(R.id.copy2_itemsProgress);
        tv_sizeProgress=(TextView) dialog.findViewById(R.id.copy2_sizeProgress);
        tv_percent=(TextView) dialog.findViewById(R.id.copy2_percent);
        tv_speed=(TextView) dialog.findViewById(R.id.copy2_speed);
        tv_errorDetails=(TextView) dialog.findViewById(R.id.copy2_errorDetails);

        iv_taskLogo=(ImageView)dialog.findViewById(R.id.copy2_logo);
        iv_fromLogo=(ImageView)dialog.findViewById(R.id.copy2_from_logo);
        iv_toLogo=(ImageView)dialog.findViewById(R.id.copy2_to_logo);

        pb_progressBar=(ProgressBar)dialog.findViewById(R.id.copy2_progressBar);

        btn_cancel=(Button)dialog.findViewById(R.id.copy2_cancel);
        btn_hide=(Button)dialog.findViewById(R.id.copy2_hide);
        btn_pause_resume=(Button)dialog.findViewById(R.id.copy2_pause_resume);
        btn_swipe=(Button)dialog.findViewById(R.id.copy2_swipe);
        btn_skip=(Button)dialog.findViewById(R.id.copy2_skip);
        btn_open=(Button)dialog.findViewById(R.id.copy2_open);

        page1=(LinearLayout)dialog.findViewById(R.id.copy2_page1);
        page2=(LinearLayout)dialog.findViewById(R.id.copy2_page2);

        progressListView=(ListView) dialog.findViewById(R.id.copy2_list);


        btn_cancel.setText("CANCEL");
        btn_hide.setText("HIDE");
        btn_pause_resume.setText("PAUSE");
        btn_swipe.setText("DETAILS");
        btn_skip.setText("SKIP");
        btn_open.setText("OPEN");


        tv_errorDetails.setVisibility(View.GONE);
        btn_open.setVisibility(View.GONE);
        btn_skip.setVisibility(View.GONE);
        page1.setVisibility(View.VISIBLE);
        page2.setVisibility(View.GONE);




        taskName=helpingBot.getTaskName(operationId);

        tv_taskName.setText(taskName);
        iv_taskLogo.setImageResource(helpingBot.getTaskLogo(operationId));

        iv_fromLogo.setImageResource(helpingBot.getPathLogo(fromStorageId));
        tv_fromPath.setText(helpingBot.getPathRelativeToLogo(fromStorageId,fromRootPath));

        iv_toLogo.setImageResource(helpingBot.getPathLogo(toStorageId));
        tv_toPath.setText(helpingBot.getPathRelativeToLogo(toStorageId,toRootPath));

        tv_size.setText(helpingBot.sizeinwords(totalSizeToDownload));


        //temporary declaration
        tv_fileName.setText(currentFileName);
        tv_itemProgress.setText(currentFileIndex+"/"+totalFiles +" items");
        tv_sizeProgress.setText(helpingBot.sizeinwords(downloadedSize)+"/"+helpingBot.sizeinwords(totalSizeToDownload));
        tv_percent.setText(progress+" %");
        tv_speed.setText("calculating..");
        pb_progressBar.setProgress(0);


        if(operationId>=201 && operationId<=206)  //for download tasks only
        {
            setUpOpenListener();
        }

        setUpCancelClickListener();
        setUpHideClickListener();
        setUpPauseResumeClickListener();
        setUpSwipeClickListener();
        setUpSkipClickListener();

    }

    private void setUpCancelClickListener()
    {
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if((operationId>=201 && operationId<=206) || operationId==305 || operationId==306 )
                {
                    downloadData.cancelDownloadingPlease=true;
                    tinyDB.remove(operationId+"IsRunning");
                    tinyDB.remove(operationId+"CurrentDestinationPath");
                    tinyDB.remove(operationId+"LastDestinationPath");
                    tasksCache.removeTask(operationId+"");
                }
                if(operationId>=301 && operationId<=304)
                {
                    uploadData.cancelDownloadingPlease=true;
                    tinyDB.remove(operationId+"Url");
                    tinyDB.remove(operationId+"ChunkStart");
                    tinyDB.remove(operationId+"IsRunning");
                    tinyDB.remove(operationId+"CurrentSourcePath");
                    tinyDB.remove(operationId+"LastSourcePath");
                    tasksCache.removeTask(operationId+"");
                }
                if(operationId==101 || operationId==102)
                {
                    copyData.cancelDownloadingPlease=true;
                    tinyDB.remove(operationId+"IsRunning");
                    tinyDB.remove(operationId+"CurrentDestinationPath");
                    tinyDB.remove(operationId+"LastDestinationPath");
                    tasksCache.removeTask(operationId+"");
                }

                iv_toLogo.setVisibility(View.VISIBLE);
                tv_speed.setText(taskName+" Cancelled...");
                tv_speed.setTextColor(mainActivity.getResources().getColor(R.color.main_theme_red));

                btn_pause_resume.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_hide.setText("CLOSE");


            }

        });
    }

    private void setUpHideClickListener()
    {
        btn_hide.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(btn_hide.getText().equals("HIDE"))
                {
                    dialog.hide();
                }
                if (btn_hide.getText().equals("CLOSE"))
                {
                    dialog.cancel();
                }
                mainActivity.showHideButtons(-1);
            }

        });
    }

    private void setUpPauseResumeClickListener()
    {
        btn_pause_resume.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if((operationId>=201 && operationId<=206) || operationId==305 || operationId==306)
                {
                    if (btn_pause_resume.getText().equals("PAUSE"))
                    {
                        if(downloadData.totalSizeToDownload!= downloadData.downloadedSize)
                        {
                            btn_pause_resume.setText("PAUSING");
                            downloadData.pauseDownloadingPlease=true;
                        }
                    }
                    if (btn_pause_resume.getText().equals("RESUME"))
                    {
                        dialog.cancel();
                        downloadData.getTinyDbData(tinyDB);
                        DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,operationId);
                        downloadingMachine.checkSpaceAndDownload();
                    }
                    return;
                }
                if(operationId>=301 && operationId<=304)
                {
                    if (btn_pause_resume.getText().equals("PAUSE"))
                    {
                        if(uploadData.totalSizeToDownload!= uploadData.downloadedSize)
                        {
                            btn_pause_resume.setText("PAUSING");
                            uploadData.pauseDownloadingPlease=true;
                        }
                    }
                    if (btn_pause_resume.getText().equals("RESUME"))
                    {
                        dialog.cancel();
                        uploadData.getTinyDbData(tinyDB);
                        UploadingMachine uploadingMachine=new UploadingMachine(mainActivity,operationId);
                        uploadingMachine.checkSpaceAndUpload();
                    }
                    return;
                }
                if(operationId==101 || operationId==102)
                {
                    if (btn_pause_resume.getText().equals("PAUSE"))
                    {
                        if(copyData.totalSizeToDownload!= copyData.downloadedSize)
                        {
                            btn_pause_resume.setText("PAUSING");
                            copyData.pauseDownloadingPlease=true;
                        }
                    }
                    if (btn_pause_resume.getText().equals("RESUME"))
                    {
                        dialog.cancel();
                        copyData.getTinyDbData(tinyDB);
                        CopingMachine copingMachine=new CopingMachine(mainActivity,operationId);
                        copingMachine.checkSpaceAndCopy();
                    }
                    return;
                }
            }

        });
    }

    private void setUpSwipeClickListener()
    {
        btn_swipe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (page1.getVisibility()==View.GONE)
                {
                    btn_swipe.setText("DETAILS");
                    page1.setVisibility(View.VISIBLE);
                    page2.setVisibility(View.GONE);
                }
                else
                {
                    btn_swipe.setText("PROGRESS");
                    page1.setVisibility(View.GONE);
                    page2.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void setUpSkipClickListener()
    {
        btn_skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if((operationId>=201 && operationId<=206) || operationId==305 || operationId==306)
                {
                    dialog.cancel();
                    tinyDB.putInt(operationId+"currentFileIndex", downloadData.currentFileIndex+1);
                    downloadData.getTinyDbData(tinyDB);
                    downloadingMachine=new DownloadingMachine(mainActivity,operationId);
                    downloadingMachine.checkSpaceAndDownload();
                    return;
                }
                if(operationId>=301 && operationId<=304)
                {
                    dialog.cancel();
                    tinyDB.remove(operationId+"Url");
                    tinyDB.remove(operationId+"ChunkStart");
                    tinyDB.remove(operationId+"CurrentSourcePath");
                    tinyDB.remove(operationId+"LastSourcePath");
                    tinyDB.putInt(operationId+"currentFileIndex", uploadData.currentFileIndex+1);
                    uploadData.getTinyDbData(tinyDB);
                    uploadingMachine=new UploadingMachine(mainActivity,operationId);
                    uploadingMachine.checkSpaceAndUpload();
                    return;
                }
                if(operationId==101 || operationId==102)
                {
                    dialog.cancel();
                    tinyDB.putInt(operationId+"currentFileIndex", copyData.currentFileIndex+1);
                    copyData.getTinyDbData(tinyDB);
                    copingMachine=new CopingMachine(mainActivity,operationId);
                    copingMachine.checkSpaceAndCopy();
                    return;
                }

            }

        });
    }

    private void setUpOpenListener()
    {
        btn_open.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if(operationId>=201 && operationId<=206)
                {
                    dialog.cancel();
                    localPathOfThisMyFile=tinyDB.getString(downloadData.pathsList.get(0));
                    file=new File(localPathOfThisMyFile);
                    mainActivity.returnFileOpenerObject(file).open();
                    return;
                }
            }
        });
    }



}
