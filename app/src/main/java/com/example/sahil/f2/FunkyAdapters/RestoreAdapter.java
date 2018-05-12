package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sahil.f2.Cache.restoreCache;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/25/2017.
 */
public class RestoreAdapter  extends ArrayAdapter<String>
{

    private ArrayList<String> nameList;
    private Context context;

    public RestoreAdapter(Context context,int garbage, ArrayList<String> nameList)
    {
        super(context, R.layout.layoutof_row19,nameList);
        this.nameList=nameList;
        this.context=context;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        int currentIndex= restoreCache.destinationList.size();


        ViewHolder holder=null;
        View rowView=convertView;
        if(convertView==null)
        {

            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.layoutof_row19,parent,false);
            holder=new ViewHolder();
            holder.index=(TextView)rowView.findViewById(R.id.row19_text1);
            holder.name=(TextView)rowView.findViewById(R.id.row19_text2);
            holder.destination=(TextView)rowView.findViewById(R.id.row19_text3);
            holder.layout=(LinearLayout) rowView.findViewById(R.id.row19_layout);
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
            holder.destination.setText("In Progress");
            holder.layout.setBackgroundColor(Color.parseColor("#fff59d"));//yellow
        }
        else
        {
            if(currentIndex>pos)
            {
                String string=restoreCache.destinationList.get(pos);
                holder.destination.setText(string);
                if(string.equals("FAILED"))
                {
                    holder.layout.setBackgroundColor(Color.parseColor("#e57373"));//red
                }
                else
                {
                    holder.layout.setBackgroundColor(Color.parseColor("#81c784"));//green
                }
            }
            else
            {
                holder.destination.setText("Pending");
                holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));//white
            }
        }

        return rowView;
    }


    static class ViewHolder
    {
        TextView index,name,destination;
        LinearLayout layout;
    }


}


