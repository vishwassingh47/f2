package com.example.sahil.f2.UiClasses;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.recycleBinCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.FunkyAdapters.Drawer1_adapter1;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuperUser;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/27/2017.
 */

public class Drawer1
{
    private ListView listView;
    private Switch rootSwitch,hideSwitch;
    private ImageView recycle_bin;
    private final MainActivity mainActivity;
    private final TinyDB tinyDB;
    private Drawer1_adapter1 adapter1;
    private ArrayList<String> nameList;
    private final DrawerLayout drawerMain;


    //CONSTRUCTOR WILL BE CALLED ONLY ONCE
    public Drawer1(Activity activity, DrawerLayout drawerMain)
    {
        mainActivity=(MainActivity) activity;
        tinyDB=new TinyDB(activity);
        this.drawerMain=drawerMain;

    }

    public void initialize()
    {
        listView=(ListView) mainActivity.findViewById(R.id.drawer1_list);
        hideSwitch=(Switch) mainActivity.findViewById(R.id.drawer1_hide_switch);
        rootSwitch=(Switch) mainActivity.findViewById(R.id.drawer1_root_switch);
        recycle_bin=(ImageView) mainActivity.findViewById(R.id.drawer1_recycle);

        nameList=new ArrayList<>();
        nameList.add("Storage Analyser");
        nameList.add("Favourites");
        nameList.add("Receive Files from Wifi");
        nameList.add("FTP CLIENT");
        nameList.add("FTP SERVER");

        setUpListView();
        setUpRecycleBin();
        setUpRootSwitch();
        setUpHideSwitch();
    }


    private void setUpListView()
    {

        adapter1=new Drawer1_adapter1(mainActivity,1,nameList);

        //ERROR HAI ******************************************##############################################################
        /*
        View view=adapter1.getView(0,null,listView);
        view.measure(0,0);
        if(nameList.size()>5)
        {
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) (5.8 *view.getMeasuredHeight()));
            listView.setLayoutParams(params);
        }
         */



        listView.setAdapter(adapter1);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
            {
                switch (nameList.get(pos))
                {
                    case "FTP CLIENT":
                        mainActivity.addPage("FtpClient",1,"FtpClient",R.mipmap.client,15);
                        mainActivity.setUpViewPager();
                        mainActivity.viewPager.setCurrentItem(1,true);
                        //Toast.makeText(mainActivity, "Terminal", Toast.LENGTH_SHORT).show();
                        break;
                    case "FTP SERVER":
                        mainActivity.addPage("FtpServer",1,"FtpServer",R.mipmap.server,16);
                        mainActivity.setUpViewPager();
                        mainActivity.viewPager.setCurrentItem(1,false);
                        break;
                    case "Storage Analyser":
                        MyCacheData.GlobalStorageAnalyser.storageId=0;
                        MyCacheData.getStorageAnalyserData(1).initializer();//ULTRA HARD CLEAR WHEN FIRST STARTS
                        MyCacheData.getStorageAnalyserData(2).initializer();
                        MyCacheData.getStorageAnalyserData(3).initializer();
                        MyCacheData.getStorageAnalyserData(4).initializer();
                        MyCacheData.getStorageAnalyserData(5).initializer();

                        mainActivity.addPage("StorageAnalyser",1,"kuch bhi",R.mipmap.c_sharp,11);
                        mainActivity.setUpViewPager();
                        mainActivity.viewPager.setCurrentItem(1,true);
                        //Intent i=new Intent(mainActivity,StorageAnalyser.class);
                        //mainActivity.startActivity(i);
                        break;
                    case "Favourites":
                        drawerMain.closeDrawer(GravityCompat.START);
                        mainActivity.addPage("Favourites",1,"Favourites",R.drawable.favourite,12345);
                        mainActivity.setUpViewPager();
                        mainActivity.viewPager.setCurrentItem(1,true);
                        break;
                    case "Receive Files from Wifi":
                        mainActivity.startReceiver();
                        break;

                }
                drawerMain.closeDrawer(GravityCompat.START);
            }
        });

    }

    private void setUpRecycleBin()
    {
        recycle_bin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                recycleBinCache.clear();
                drawerMain.closeDrawer(GravityCompat.START);
                mainActivity.addPage("RecycleBin",1,"RecycleBin",R.mipmap.delete2,12345);
                mainActivity.setUpViewPager();
                mainActivity.viewPager.setCurrentItem(1,true);
            }
        });
    }


    private void setUpRootSwitch()
    {
        SharedPreferences pref=mainActivity.getApplicationContext().getSharedPreferences("SU",0);
        boolean isRooted=pref.getBoolean("hasUserEnabledSU",false);
        if(isRooted)
        {
            SuperUser.hasUserEnabledSU=true;
            rootSwitch.setChecked(true);
            /*
            if(SuperUser.turnOnRoot(MainActivity.this))
            {
                rootSwitch.setChecked(true);
            }
            else
            {
                SuperUser.turnOffRoot(MainActivity.this);
                rootSwitch.setChecked(false);
            }
             */

        }
        else
        {
            SuperUser.hasUserEnabledSU=false;
            rootSwitch.setChecked(false);
        }



        //click listener
        rootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean checked)
            {
                if(checked)
                {
                    if(SuperUser.turnOnRoot(mainActivity))
                    {
                        rootSwitch.setChecked(true);
                        Toast.makeText(mainActivity, "Root access gained", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        rootSwitch.setChecked(false);
                        if(SuperUser.isDeviceRooted)
                        {
                            Toast.makeText(mainActivity, "Please Grant SuperUser Permission First", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(mainActivity, "Sorry,Device is not Rooted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    if(SuperUser.hasUserEnabledSU)
                    {
                        SuperUser.turnOffRoot(mainActivity);
                        rootSwitch.setChecked(false);
                        Toast.makeText(mainActivity, "SuperUser Access Revoked", Toast.LENGTH_SHORT).show();
                    }
                }
                //Toast.makeText(MainActivity.this, buttonView.isChecked()+"", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void setUpHideSwitch()
    {
        variablesCache.toExpandHiddenFiles =false;
        variablesCache.showHidden=tinyDB.getBoolean("showHidden");

        if(variablesCache.showHidden)
        {
            hideSwitch.setChecked(true);
        }
        else
        {
            hideSwitch.setChecked(false);
        }


        //click listener
        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean checked)
            {
                //refresh
                /*

                 */
                tinyDB.putBoolean("showHidden",checked);
                variablesCache.showHidden=checked;
                hideSwitch.setChecked(checked);
                if(checked)
                {
                    Toast.makeText(mainActivity, "Hidden items will be shown", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(mainActivity, "Hidden items will not be shown", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }









    /*
                        Snackbar snackbar=Snackbar.make(drawer,"ahahhaha i am happy to see you here and we are having lots of funahahhaha i am happy to see you here and we are having lots of funahahhaha i am happy to see you here and we are having lots of funahahhaha i am happy to see you here and we are having lots of funahahhaha i am happy to see you here and we are having lots of funahahhaha i am happy to see you here and we are having lots of funahahhaha i am happy to see you here and we are having lots of funahahhaha i am happy to see you here and we are having lots of fun",Snackbar.LENGTH_LONG);
                        snackbar.setAction("UNDO", new View.OnClickListener()
                        {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar.make(view, "Action!", Snackbar.LENGTH_SHORT).show();
                                    }
                        });
                        snackbar.setActionTextColor(Color.BLUE);
                        snackbar.show();
                        */

}
