package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
        import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

        import com.example.sahil.f2.Cache.CopyData;
        import com.example.sahil.f2.Cache.DownloadData;
        import com.example.sahil.f2.Cache.MyCacheData;
        import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.Classes.CreateNewItems;
import com.example.sahil.f2.R;

        import java.util.ArrayList;

/**
 * Created by hit4man47 on 20/1/2018.
 */

public class CreateNewItemAdapter extends ArrayAdapter<CreateNewItems>
{
    private Context context;
    private ArrayList<CreateNewItems> createNewItemsArrayList;
    private ArrayList<Boolean> isItemEnabled;

    public CreateNewItemAdapter(Context context, ArrayList<CreateNewItems> createNewItemsArrayList,ArrayList<Boolean> isItemEnabled)
    {
        super(context,R.layout.layoutof_row25,createNewItemsArrayList);
        this.context=context;
        this.createNewItemsArrayList=createNewItemsArrayList;
        this.isItemEnabled=isItemEnabled;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        CreateNewItems createNewItem=createNewItemsArrayList.get(pos);
        ViewHolder holder=null;
        View rowView=convertView;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.layoutof_row25,parent,false);
            holder=new ViewHolder();
            holder.name=(TextView)rowView.findViewById(R.id.row25_name);
            holder.path=(TextView)rowView.findViewById(R.id.row25_path);
            holder.icon=(ImageView) rowView.findViewById(R.id.row25_icon);
            rowView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)rowView.getTag();
        }

        holder.icon.setImageResource(createNewItem.getIconId());
        holder.name.setText(createNewItem.getName());

        if(createNewItem.getFirstPath()==null)
        {
            holder.path.setVisibility(View.GONE);
        }
        else
        {
            holder.path.setVisibility(View.VISIBLE);
            holder.path.setText(createNewItem.getFirstPath());
        }
        rowView.setAlpha(isItemEnabled.get(pos)?1:((float) 0.6));
        return rowView;
    }

    static class ViewHolder
    {
        TextView name,path;
        ImageView icon;
    }




}
