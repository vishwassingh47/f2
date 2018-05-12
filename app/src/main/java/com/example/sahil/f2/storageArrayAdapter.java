package com.example.sahil.f2;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sahil.f2.OperationTheater.HelpingBot;

import java.io.File;
import java.util.ArrayList;

import static android.graphics.Color.parseColor;

/**
 * Created by sahil on 13-07-2017.
 */

public class storageArrayAdapter extends ArrayAdapter<String>
{
    private Context context;
    String [] storageDevices;
    Boolean[] isWorking;
    HelpingBot helpingBot;


    public storageArrayAdapter(Context context,int x,String [] storageDevices,Boolean[] isWorking)
    {
        super(context,x,storageDevices);
        this.context=context;

        this.isWorking=new Boolean[storageDevices.length];
        this.storageDevices=new String[storageDevices.length];

        for(int i=0;i<storageDevices.length;i++)
        {
            this.storageDevices[i]=storageDevices[i];
            this.isWorking[i]=isWorking[i];
        }

        helpingBot=new HelpingBot();

    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {


        ViewHolder holder=null;
        View rowView=convertView;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.layoutof_row2,parent,false);
            holder=new ViewHolder();

            holder.row=(LinearLayout)rowView.findViewById(R.id.row2layout);
            holder.icon=(ImageView)rowView.findViewById(R.id.storage_icon);
            holder.name=(TextView)rowView.findViewById(R.id.storage_name);
            holder.free=(TextView)rowView.findViewById(R.id.storage_free);
            holder.size=(TextView)rowView.findViewById(R.id.storage_size);
            holder.pb=(ProgressBar)rowView.findViewById(R.id.storage_progress);

            rowView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)rowView.getTag();
        }

        if(isWorking[pos])
        {
            holder.row.setBackgroundColor(Color.parseColor("#ff9800"));
        }
        else
        {
            holder.row.setBackgroundColor(Color.parseColor("#424242"));
        }


        if(pos==0)
        {
            holder.icon.setImageResource(R.drawable.internal);
            holder.name.setText("INTERNAL STORAGE");

        }
        else
        {
            holder.icon.setImageResource(R.drawable.sd_card);
            String []s=storageDevices[pos].split("\\/");
            holder.name.setText(s[s.length-1]);
        }


        File file=new File(storageDevices[pos]);
        holder.free.setText("FREE: "+helpingBot.sizeinwords(file.getUsableSpace()));
        holder.size.setText(helpingBot.sizeinwords(file.getTotalSpace()-file.getUsableSpace())+" USED OF "+helpingBot.sizeinwords(file.getTotalSpace()));
        long progress=(file.getTotalSpace()-file.getUsableSpace())*100/file.getTotalSpace();
        holder.pb.setProgress((int)progress);


        return rowView;
    }//getView closed


    static class ViewHolder
    {
        LinearLayout row;
        ImageView icon;
        TextView name;
        TextView free;
        TextView size;
        ProgressBar pb;


    }
}
