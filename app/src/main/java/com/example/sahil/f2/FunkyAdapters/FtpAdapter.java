package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.GokuFrags.ftpLoginPager;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;

import java.util.ArrayList;

import static android.graphics.Color.parseColor;

/**
 * Created by hit4man47 on 10/8/2017.
 */

public class FtpAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> ftpList;
    private TinyDB tinyDB;
    private Fragment fragment;


    public FtpAdapter(Context context, ArrayList<String> ftpList,Fragment fragment)
    {
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
        this.ftpList=ftpList;
        tinyDB=new TinyDB(context);
        this.fragment=fragment;

    }

    public int getCount()
    {
        return ftpList.size()+1;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }



    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if(position==ftpList.size())
        {
            //add
            rowView=inflater.inflate(R.layout.layoutof_row23,parent,false);
            return rowView;
        }
        else
        {
            //show ftp details
            rowView=inflater.inflate(R.layout.layoutof_row24,parent,false);
            final String fullUrl=ftpList.get(position);
            String portNumber,host,username,password;
            int lastIndex;
            if(fullUrl.contains("@"))
            {
                lastIndex=fullUrl.lastIndexOf('@');
                String hostPort=fullUrl.substring(lastIndex+1);
                String userPass=fullUrl.substring(0,lastIndex);
                lastIndex=hostPort.lastIndexOf(':');
                host=hostPort.substring(0,lastIndex);
                portNumber=hostPort.substring(lastIndex+1);
                lastIndex=userPass.lastIndexOf(':');
                username=userPass.substring(6,lastIndex);
                password=userPass.substring(lastIndex+1);
            }
            else
            {
                lastIndex=fullUrl.lastIndexOf(':');
                host=fullUrl.substring(6,lastIndex);
                portNumber=fullUrl.substring(lastIndex+1);
                username="";
                password="";
            }
            TextView tv_host,tv_userName,tv_password,tv_port;
            final ImageView iv_options=(ImageView) rowView.findViewById(R.id.row24_menu);
            tv_host=(TextView)rowView.findViewById(R.id.row24_host);
            tv_userName=(TextView)rowView.findViewById(R.id.row24_userName);
            tv_password=(TextView)rowView.findViewById(R.id.row24_password);
            tv_port=(TextView)rowView.findViewById(R.id.row24_port);

            tv_host.setText("HOST:"+host);
            tv_userName.setText("USER NAME:"+username);
            tv_password.setText("PASSWORD:"+password);
            tv_port.setText("PORT NUMBER:"+portNumber);

            iv_options.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    android.support.v7.widget.PopupMenu popup=new PopupMenu(context,iv_options);
                    popup.getMenuInflater().inflate(R.menu.menu4,popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            if(item.getItemId()==R.id.menu4_remove)
                            {
                                FtpCache.ftpList.remove(fullUrl);
                                tinyDB.putListString("ftpList",FtpCache.ftpList);
                                ftpLoginPager pager1 =(ftpLoginPager) fragment;
                                pager1.refresh();
                            }
                            return true;
                        }
                    });

                    MenuPopupHelper menuHelper=new MenuPopupHelper(context,(MenuBuilder)popup.getMenu(),iv_options);
                    menuHelper.setGravity(Gravity.END);
                    menuHelper.show();


                }
            });

            return rowView;
        }
    }


}
