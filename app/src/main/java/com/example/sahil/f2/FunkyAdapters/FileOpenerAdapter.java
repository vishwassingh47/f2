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

import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

import java.util.ArrayList;

import static android.graphics.Color.parseColor;

/**
 * Created by hit4man47 on 8/28/2017.
 */

public class FileOpenerAdapter extends ArrayAdapter<String>
{
    private Context context;
    private ArrayList<String> nameList;
    private ArrayList<Drawable> icons;


    public FileOpenerAdapter(Context context, int filetype, ArrayList<String> nameList,ArrayList<Drawable> icons)
    {
        super(context,filetype,nameList);
        this.context=context;

        this.nameList=nameList;
        this.icons=icons;

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

            holder.icon.setImageDrawable(icons.get(pos));

        //SETTING  NAME
        holder.name.setText(nameList.get(pos));


        return rowView;
    }//getView closed


    static class ViewHolder
    {
        TextView name;
        ImageView icon;
    }
}
