package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sahil.f2.Cache.DeleteData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/25/2017.
 */
public class DeleteListAdapter  extends ArrayAdapter<String>
{

    private ArrayList<String> nameList;
    private ArrayList<Long> sizeLongList;
    private int operationId;
    private Context context;
    private HelpingBot helpingBot;

    public DeleteListAdapter(Context context,int garbage, ArrayList<String> nameList,ArrayList<Long> sizeLongList,int operationId)
    {
        super(context, R.layout.layoutof_row18,nameList);
        this.sizeLongList=sizeLongList;
        this.nameList=nameList;
        this.operationId=operationId;
        this.context=context;
        helpingBot=new HelpingBot();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        int currentIndex=0,timeInSec=0;
        boolean cancelDownloadingPlease=false,isError=false;

        DeleteData deleteData= MyCacheData.getDeleteDataFromCode(operationId);

        currentIndex= deleteData.currentFileIndex;
        timeInSec=deleteData.timeInSec;
        cancelDownloadingPlease=deleteData.cancelDownloadingPlease;
        isError=deleteData.isErrorList.get(pos);

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
        if(timeInSec>0)     //service started
        {

            if(currentIndex==pos)
            {
                if(cancelDownloadingPlease)
                {
                    holder.status.setText("Cancelled");
                    holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                }
                else
                {
                    holder.status.setText("In Progress");
                    holder.status.setTextColor(context.getResources().getColor(R.color.yellow));
                }
            }
            else
            {
                if(currentIndex>pos)
                {
                    if(isError)
                    {
                        //error
                        holder.status.setText("Failed");
                        holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                    }
                    else
                    {
                        //deleted
                        holder.status.setText("Deleted");
                        holder.status.setTextColor(context.getResources().getColor(R.color.progress_blue));
                    }
                }
                else
                {
                    if(cancelDownloadingPlease)
                    {
                        holder.status.setText("Cancelled");
                        holder.status.setTextColor(context.getResources().getColor(R.color.main_theme_red));
                    }
                    else
                    {
                        holder.status.setText("Pending");
                        holder.status.setTextColor(context.getResources().getColor(R.color.black));
                    }
                }
            }
        }
        else     //service not yet started
        {
            holder.status.setText(helpingBot.sizeinwords(sizeLongList.get(pos)));
        }

        return rowView;
    }


    static class ViewHolder
    {
        TextView index,name,status;
    }


}


