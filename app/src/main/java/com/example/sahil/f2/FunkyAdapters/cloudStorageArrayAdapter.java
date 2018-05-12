package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;

/**
 * Created by Acer on 09-08-2017.
 */


public class cloudStorageArrayAdapter extends ArrayAdapter<String>
{
    private Context context;
    String [] storageDevices;
    Boolean[] isWorking;
    HelpingBot helpingBot;
    String freeSize=null;
    String totalSize=null;
    int progress=0;


    public cloudStorageArrayAdapter(Context context,int x,String [] storageDevices,Boolean[] isWorking,String free,String size,int progress)
    {




        super(context,x,storageDevices);
        this.context=context;

        this.isWorking=new Boolean[storageDevices.length];
        this.storageDevices=new String[storageDevices.length];
        this.totalSize=size;
        this.freeSize=free;
        this.progress=progress;

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

            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView=null;

        if(isWorking[pos])
        {

            TextView free=(TextView)rowView.findViewById(R.id.storage_free);
            TextView size=(TextView)rowView.findViewById(R.id.storage_size);
            ProgressBar pb=(ProgressBar)rowView.findViewById(R.id.storage_progress);

            free.setText(freeSize);
            size.setText(totalSize);
            pb.setProgress(progress);

        }

        else
        {
        rowView=inflater.inflate(R.layout.layoutof_row4,parent,false);
        }


        return rowView;
    }//getView closed


}
