package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
        import android.graphics.Color;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import com.example.sahil.f2.Cache.InstallData;
        import com.example.sahil.f2.Cache.UnInstallData;
        import com.example.sahil.f2.Cache.MyCacheData;
        import com.example.sahil.f2.Classes.MyFile;
        import com.example.sahil.f2.OperationTheater.HelpingBot;
        import com.example.sahil.f2.R;

        import java.util.ArrayList;

/**
 * Created by hit4man47 on 16 feb 2018.
 */

public class InstallUninstallBackgroundAdapter extends ArrayAdapter<MyFile>
{
    private ArrayList<MyFile> myFilesList;
    private Context context;
    private HelpingBot helpingBot;
    private final int operationId;
    private final InstallData installData;
    private final UnInstallData unInstallData;

    public InstallUninstallBackgroundAdapter(Context context,ArrayList<MyFile> myFilesList,int operationId)
    {
        super(context,R.layout.layoutof_row18,myFilesList);
        this.myFilesList=myFilesList;
        this.operationId=operationId;
        this.context=context;
        helpingBot=new HelpingBot();
        if(operationId==99)
        {
            installData=MyCacheData.getInstallData(operationId);
            unInstallData=null;
        }
        else
        {
            unInstallData=MyCacheData.getUnInstallData(operationId);
            installData=null;
        }
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        int currentIndex=0;
        boolean cancelDownloadingPlease=false,isError=false;

        if(operationId==99)
        {
            currentIndex=installData.currentFileIndex;
            cancelDownloadingPlease=installData.cancelPlease;
            isError=installData.isErrorList.get(pos);

        }
        else
        {
            currentIndex=unInstallData.currentFileIndex;
            cancelDownloadingPlease=unInstallData.cancelPlease;
            isError=unInstallData.isErrorList.get(pos);
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
        holder.name.setText(myFilesList.get(pos).getName());

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
                    //installed/uninstalled
                    holder.status.setText(operationId==99?"Installed":"Uninstalled");
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

        return rowView;
    }


    static class ViewHolder
    {
        TextView index,name,status;
    }


}


