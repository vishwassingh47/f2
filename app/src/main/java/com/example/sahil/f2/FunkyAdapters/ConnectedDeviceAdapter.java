package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sahil.f2.Classes.WiFiDevice;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

import java.util.ArrayList;

import static android.graphics.Color.parseColor;

/**
 * Created by hit4man47 on 8/28/2017.
 */

public class ConnectedDeviceAdapter extends ArrayAdapter<WiFiDevice>
{
    private Context context;
    private ArrayList<WiFiDevice> nameList;





    public ConnectedDeviceAdapter(Context context, int filetype, ArrayList<WiFiDevice> nameList)
    {
        super(context,filetype,nameList);
        this.context=context;

        this.nameList=nameList;

    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {

        ViewHolder holder=null;
        View rowView=convertView;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.layoutof_row6,parent,false);
            holder=new ViewHolder();
            holder.name=(TextView)rowView.findViewById(R.id.row6_name);
            holder.icon=(ImageView)rowView.findViewById(R.id.row6_icon);
            rowView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)rowView.getTag();
        }


        //SETTING ICONS\

        holder.icon.setImageResource(R.mipmap.internal);

        WiFiDevice wiFiDevice=nameList.get(pos);
        //SETTING  NAME
        holder.name.setText(wiFiDevice.getIp()+" ("+wiFiDevice.getDeviceName()+" )");

        return rowView;
    }//getView closed


    static class ViewHolder
    {
        TextView name;
        ImageView icon;


    }
}
