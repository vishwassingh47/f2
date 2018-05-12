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
import com.example.sahil.f2.R;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 11/26/2017.
 */

public class CopyListAdapter extends ArrayAdapter<String>
{
    private Context context;
    private ArrayList<String> nameList;
    private final int operationId;
    public CopyListAdapter(Context context,int garbage, ArrayList<String> nameList,int operationId)
    {
        super(context,R.layout.layoutof_row18,nameList);
        this.context=context;
        this.nameList=nameList;
        this.operationId=operationId;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        int currentIndex=0;
        boolean cancelDownloadingPlease=false,pauseDownloadingPlease=false,isDownloadError=false;

        if((operationId>=201 && operationId<=206) || operationId==305 || operationId==306)
        {
            DownloadData downloadData= MyCacheData.getDownloadDataFromCode(operationId);
            cancelDownloadingPlease= downloadData.cancelDownloadingPlease;
            pauseDownloadingPlease=downloadData.pauseDownloadingPlease;
            isDownloadError=downloadData.isDownloadError;
            currentIndex= downloadData.currentFileIndex;
        }
        if(operationId==101 || operationId==102)
        {
            CopyData copyData=MyCacheData.getCopyDataFromCode(operationId);
            cancelDownloadingPlease=copyData.cancelDownloadingPlease;
            pauseDownloadingPlease=copyData.pauseDownloadingPlease;
            isDownloadError=copyData.isDownloadError;
            currentIndex= copyData.currentFileIndex;
        }
        if(operationId>=301 && operationId<=304)
        {
            UploadData uploadData= MyCacheData.getUploadDataFromCode(operationId);
            cancelDownloadingPlease= uploadData.cancelDownloadingPlease;
            pauseDownloadingPlease=uploadData.pauseDownloadingPlease;
            isDownloadError=uploadData.isDownloadError;
            currentIndex= uploadData.currentFileIndex;
        }








        ViewHolder holder=null;
        View rowView=convertView;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.layoutof_row18,parent,false);
            holder=new ViewHolder();
            holder.index=(TextView)rowView.findViewById(R.id.row18_text1);
            holder.name=(TextView)rowView.findViewById(R.id.row18_text2);
            holder.status=(TextView)rowView.findViewById(R.id.row18_text3);
            rowView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)rowView.getTag();
        }


        holder.index.setText((pos+1)+"");
        holder.name.setText(nameList.get(pos));

        if(currentIndex==pos)
        {
            if(cancelDownloadingPlease)
            {
                holder.status.setText("Cancelled");
                holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                return rowView;
            }
            if(pauseDownloadingPlease)
            {
                holder.status.setText("Paused");
                holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                return rowView;
            }
            if(isDownloadError)
            {
                holder.status.setText("Failed");
                holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                return rowView;
            }

            holder.status.setText("In Progress");
            holder.status.setTextColor(context.getResources().getColor(R.color.yellow));
            return rowView;
        }


        if(currentIndex>pos)
        {
            holder.status.setText("Done");
            holder.status.setTextColor(context.getResources().getColor(R.color.progress_blue));
            return rowView;
        }


        if(currentIndex<pos)
        {
            if(cancelDownloadingPlease)
            {
                holder.status.setText("Cancelled");
                holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                return rowView;
            }
            if(pauseDownloadingPlease)
            {
                holder.status.setText("Paused");
                holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                return rowView;
            }
            if(isDownloadError)
            {
                holder.status.setText("Failed");
                holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                return rowView;
            }

            holder.status.setText("Pending");
            holder.status.setTextColor(context.getResources().getColor(R.color.white));
            return rowView;
        }
        return rowView;
    }


    static class ViewHolder
    {
        TextView index,name,status;
    }


}
