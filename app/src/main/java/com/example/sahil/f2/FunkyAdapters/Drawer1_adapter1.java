package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sahil.f2.R;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/25/2017.
 */
public class Drawer1_adapter1  extends ArrayAdapter<String>
{

    private ArrayList<String> nameList;
    private Context context;

    public Drawer1_adapter1(Context context,int garbage, ArrayList<String> nameList)
    {
        super(context, R.layout.layoutof_row20,nameList);
        this.nameList=nameList;
        this.context=context;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {

        ViewHolder holder=null;
        View rowView=convertView;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.layoutof_row20,parent,false);
            holder=new ViewHolder();
            holder.icon=(ImageView) rowView.findViewById(R.id.row20_icon);
            holder.name=(TextView)rowView.findViewById(R.id.row20_label);
            rowView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)rowView.getTag();
        }

        holder.name.setText(nameList.get(pos));
        switch (nameList.get(pos))
        {
            case "FTP SERVER":
                holder.icon.setImageResource(R.mipmap.server);
                break;
            case "FTP CLIENT":
                holder.icon.setImageResource(R.mipmap.client);
                break;
            case "Storage Analyser":
                holder.icon.setImageResource(R.drawable.sd_card);
                break;
            case "Favourites":
                holder.icon.setImageResource(R.drawable.favourite);
                break;
            case "Receive Files from Wifi":
                holder.icon.setImageResource(R.mipmap.wifishare);
                break;

        }



        return rowView;
    }


    static class ViewHolder
    {
        TextView name;
        ImageView icon;
    }


}


