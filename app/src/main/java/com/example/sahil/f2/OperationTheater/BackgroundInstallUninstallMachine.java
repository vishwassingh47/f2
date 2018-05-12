package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Cache.InstallData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.UnInstallData;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.SimpleYesNoDialog;
import com.example.sahil.f2.FunkyAdapters.InstallUninstallBackgroundAdapter;
import com.example.sahil.f2.HomeServicesProvider.InstallService;
import com.example.sahil.f2.HomeServicesProvider.UnInstallService;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuOperations;
import com.example.sahil.f2.UiClasses.Refresher;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 2/16/2018.
 */



public class BackgroundInstallUninstallMachine
{
    public final MainActivity mainActivity;
    private ArrayList<Integer> selectedIndexList;
    private ArrayList<MyFile> myFilesList;
    private Runnable runnable;
    private android.support.v4.app.Fragment fragment;
    private TextView title,details;
    private Dialog dialog;
    private ProgressBar progressBar;
    private ListView listView;
    private InstallUninstallBackgroundAdapter installUninstallBackgroundAdapter;
    private Button hide_close_btn,cancel_btn;

    private InstallData installData;
    private UnInstallData unInstallData;
    private Intent serviceIntent;
    private final int operationId;
    private final int pmOrUserOrSystem;


    public BackgroundInstallUninstallMachine(MainActivity mainActivity, android.support.v4.app.Fragment fragment, ArrayList<MyFile> myFilesList,ArrayList<Integer> selectedIndexList,int operationId,int pmOrUserOrSystem)
    {
        this.mainActivity=mainActivity;
        this.selectedIndexList=selectedIndexList;
        this.myFilesList=myFilesList;
        this.fragment=fragment;
        this.operationId=operationId;
        this.pmOrUserOrSystem=pmOrUserOrSystem;
    }

    public void decisionMaker()
    {
        if(operationId!=99 && operationId!=199)
        {
            Toast.makeText(mainActivity, "invalid operation id", Toast.LENGTH_SHORT).show();
            return;
        }

        if(operationId==99)
        {
            installData= MyCacheData.getInstallData(99);
            if(!installData.isServiceRunning)
            {
                start();
                return;
            }
        }
        else
        {
            unInstallData= MyCacheData.getUnInstallData(199);
            if(!unInstallData.isServiceRunning)
            {
                start();
                return;
            }
        }

        Toast.makeText(mainActivity, "Server Busy", Toast.LENGTH_LONG).show();
    }

    private void start()
    {

        ArrayList<MyFile> referenceList;

        if(operationId==99)
        {
            installData.clear();
            referenceList=installData.myFilesList;
            installData.pmOrUserOrSystem=pmOrUserOrSystem;
        }
        else
        {
            unInstallData.clear();
            referenceList=unInstallData.myFilesList;
        }


        for (Integer item : selectedIndexList)
        {
            MyFile file=myFilesList.get(item);
            if(operationId==99)
            {
                installData.isErrorList.add(false);
                installData.myFilesList.add(file);
                installData.totalFiles++;
            }
            if(operationId==199)
            {
                unInstallData.isErrorList.add(false);
                unInstallData.myFilesList.add(file);
                unInstallData.totalFiles++;
            }
        }


        dialog = new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_dialog5);
        dialog.setCanceledOnTouchOutside(false);
        title=(TextView) dialog.findViewById(R.id.dialog5_title);
        details=(TextView) dialog.findViewById(R.id.dialog5_details);
        listView=(ListView) dialog.findViewById(R.id.dialog5_list);
        hide_close_btn=(Button) dialog.findViewById(R.id.dialog5_hide_close);
        cancel_btn=(Button) dialog.findViewById(R.id.dialog5_cancel);
        progressBar=(ProgressBar) dialog.findViewById(R.id.dialog5_progress);

        details.setVisibility(View.GONE);

        if(operationId==99)
        {
            title.setText("Installing Applications");
            installUninstallBackgroundAdapter=new InstallUninstallBackgroundAdapter(mainActivity,installData.myFilesList,operationId);
            installData.dialog=dialog;
        }
        else
        {
            title.setText("UnInstalling Applications");
            installUninstallBackgroundAdapter=new InstallUninstallBackgroundAdapter(mainActivity,unInstallData.myFilesList,operationId);
            unInstallData.dialog=dialog;
        }





        View view=installUninstallBackgroundAdapter.getView(0,null,listView);
        view.measure(0,0);
        if(referenceList.size()>5)
        {
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) (5.8 *view.getMeasuredHeight()));
            listView.setLayoutParams(params);
        }

        listView.setAdapter(installUninstallBackgroundAdapter);

        hideCloseButtonClickListener();
        cancelButtonClickListener();

        cancel_btn.setText("CANCEL");
        hide_close_btn.setText("HIDE");

        progressBar.setVisibility(View.VISIBLE);

        dialog.show();

        startServiceAndRunner();

    }

    private void startServiceAndRunner()
    {
        if(operationId==99)
        {
            serviceIntent= new Intent(mainActivity, InstallService.class);
        }
        if(operationId==199)
        {
            serviceIntent= new Intent(mainActivity, UnInstallService.class);
        }

        final Handler handler=new Handler();
        runnable =new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("99_199_RUNNER",(operationId==99?installData.timeInSec:unInstallData.timeInSec)+"......................");
                if(operationId==99)
                {
                    installData.timeInSec++;
                }
                if(operationId==199)
                {
                    unInstallData.timeInSec++;
                }


                installUninstallBackgroundAdapter.notifyDataSetChanged();
                listView.setSelection((operationId==99?installData.currentFileIndex-3:unInstallData.currentFileIndex-3));

                if(operationId==99?installData.isServiceRunning:unInstallData.isServiceRunning)
                {
                    if(progressBar.getVisibility()!=View.VISIBLE)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    handler.postDelayed(runnable,1000);
                }
                else
                {
                    int totalErrors=0;
                    for(boolean x:operationId==99?installData.isErrorList:unInstallData.isErrorList)
                    {
                        if(x)
                        {
                            totalErrors++;
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                    if(operationId==99?installData.cancelPlease:unInstallData.cancelPlease)
                    {
                        details.setText("Operation cancelled");
                        Toast.makeText(mainActivity, "Operation cancelled", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(totalErrors==0)
                        {
                            details.setText("Operation Completed Successfully");
                            Toast.makeText(mainActivity, "Operation Completed Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String s="Operation Completed with "+totalErrors+" error(s)";
                            details.setText(s);
                            Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
                        }
                    }
                    cancel_btn.setVisibility(View.GONE);
                    hide_close_btn.setText("CLOSE");

                    Refresher refresher =new Refresher(mainActivity);
                    refresher.refresh();

                    if(operationId==99?installData.rebootRequired:unInstallData.rebootRequired)
                    {
                        Toast.makeText(mainActivity, "Reboot your device to apply changes", Toast.LENGTH_LONG).show();
                        showRebootDialog();
                    }

                    handler.removeCallbacks(runnable);
                }
            }
        };

        if(operationId==99)
        {
            installData.isServiceRunning=true;
        }
        else
        {
            unInstallData.isServiceRunning=true;
        }
        mainActivity.startService(serviceIntent);

        handler.post(runnable);


    }

    private void hideCloseButtonClickListener()
    {
        hide_close_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(hide_close_btn.getText().equals("HIDE"))
                {
                    dialog.hide();
                }
                if(hide_close_btn.getText().equals("CLOSE"))
                {
                    dialog.cancel();
                }
            }
        });
    }

    private void cancelButtonClickListener()
    {
        cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(operationId==99)
                {
                    if(installData.isServiceRunning)
                    {
                        installData.cancelPlease=true;
                        cancel_btn.setText("Cancelling");
                        return;
                    }
                }
                else
                {
                    if(unInstallData.isServiceRunning)
                    {
                        unInstallData.cancelPlease=true;
                        cancel_btn.setText("Cancelling");
                        return;
                    }
                }

                dialog.cancel();
            }
        });
    }


    private void showRebootDialog()
    {
        SimpleYesNoDialog simpleYesNoDialog=new SimpleYesNoDialog()
        {
            @Override
            public void yesClicked()
            {
                Thread thread=new Thread()
                {
                    @Override
                    public void run()
                    {
                        SuOperations.runCommand("reboot");
                    }
                };
                thread.start();
            }

            @Override
            public void noClicked()
            {

            }
        };
        simpleYesNoDialog.showDialog(mainActivity,"REBOOT","Please Reboot your device to apply the Changes.","Reboot","Cancel");

    }

}
