package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.DownloadData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 11/26/2017.
 */

public class ClipBoardAdapter extends ArrayAdapter<String>
{
    private Context context;
    private ArrayList<String> nameList;
    private ArrayList<Long> sizeListLong;
    private final HelpingBot helpingBot;

    public ClipBoardAdapter(Context context,int garbage, ArrayList<String> nameList, ArrayList<Long> sizeListLong)
    {
        super(context,R.layout.layoutof_row18,nameList);
        this.context=context;
        this.nameList=nameList;
        this.sizeListLong=sizeListLong;
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
            rowView=inflater.inflate(R.layout.layoutof_row18,parent,false);
            holder=new ViewHolder();
            holder.index=(TextView)rowView.findViewById(R.id.row18_text1);
            holder.name=(TextView)rowView.findViewById(R.id.row18_text2);
            holder.size=(TextView)rowView.findViewById(R.id.row18_text3);
            rowView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)rowView.getTag();
        }


        holder.index.setText((pos+1)+"");
        holder.name.setText(nameList.get(pos));
        holder.size.setText(helpingBot.sizeinwords(sizeListLong.get(pos)));

        return rowView;
    }


    static class ViewHolder
    {
        TextView index,name,size;
    }


}
