package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.ThumbNailsMod;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.ExtensionUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 3/4/2018.
 */

public class ListViewAdapter extends ArrayAdapter<MyFile>
{
    private final Context context;
    private LayoutInflater layoutInflater;
    private final ArrayList<MyFile> fileList;
    private final int modValue;
    private final int storageId;
    private final int pagerId;
    private final ThumbNailsMod thumbNailsMod;
    private final ExtensionUtil extensionUtil;
    private final long totalUsed;

    public ListViewAdapter(Context context, ArrayList<MyFile> fileList,int storageId, Fragment fragment, int modValue, int pagerId)
    {

        super(context, R.layout.layoutof_row,fileList);

        this.context=context;
        layoutInflater=LayoutInflater.from(context);
        this.fileList=fileList;
        this.storageId=storageId;
        this.modValue=modValue;
        this.pagerId=pagerId;
        this.totalUsed=-5;//not in use
        extensionUtil=new ExtensionUtil();
        thumbNailsMod=new ThumbNailsMod(fragment,pagerId);
    }

    public ListViewAdapter(Context context, ArrayList<MyFile> fileList,int storageId, Fragment fragment, int modValue, int pagerId,long totalUsed)
    {

        super(context, R.layout.layoutof_row,fileList);

        this.context=context;
        layoutInflater=LayoutInflater.from(context);
        this.fileList=fileList;
        this.storageId=storageId;
        this.modValue=modValue;
        this.pagerId=pagerId;
        this.totalUsed=totalUsed;
        extensionUtil=new ExtensionUtil();
        thumbNailsMod=new ThumbNailsMod(fragment,pagerId);
    }


    @Override
    @NotNull
    public View getView(int pos, View convertView, @NotNull ViewGroup parent)
    {
        switch (pagerId)
        {
            case 1:
                return getView1(pos,convertView,parent);
            case 5:
                return getView5(pos,convertView,parent);
            default:
                return null;
        }
    }

    private View getView1(int pos, View convertView, ViewGroup parent)
    {
        MyFile myFile=fileList.get(pos);


        ViewHolder holder=null;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.layoutof_row,parent,false);
            holder=new ViewHolder();
            holder.name=(TextView)convertView.findViewById(R.id.row_name);
            holder.size=(TextView)convertView.findViewById(R.id.row_size);
            holder.icon1=(ImageView)convertView.findViewById(R.id.row_icon1);
            holder.icon2=(ImageView)convertView.findViewById(R.id.row_icon2);
            holder.icon3=(ImageView)convertView.findViewById(R.id.row_icon3);
            holder.icon4=(ImageView)convertView.findViewById(R.id.row_icon4);
            holder.linearLayout=(LinearLayout) convertView.findViewById(R.id.row);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        //SETTING FILE NAME
        holder.name.setText(myFile.getName());

        //SETTING SIZELIST
        holder.size.setText(myFile.getSize());

        //SETTING CHECKED LIST RESULTS
        if(myFile.isChecked())
        {
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_checked));
        }
        else
        {
            if(myFile.getName().startsWith("."))
            {
                //is hidden
                holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_hidden));
            }
            else
            {
                holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_normal));
            }
        }

        getThumbs(holder.icon1,holder.icon2,holder.icon3,holder.icon4, myFile.getName(),myFile.getThumbUrl(),myFile.isFolder(),myFile.getSymLink(),myFile.isFavourite());

        return convertView;

    }

    private View getView5(int pos, View convertView, ViewGroup parent)
    {
        MyFile file=fileList.get(pos);

        ViewHolder holder=null;
        View rowView=convertView;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.layoutof_row9,parent,false);
            holder=new ViewHolder();

            holder.linearLayout=(LinearLayout) rowView.findViewById(R.id.row9);
            holder.icon1=(ImageView)rowView.findViewById(R.id.row9_icon1);
            holder.icon2=(ImageView)rowView.findViewById(R.id.row9_icon2);
            holder.icon3=(ImageView)rowView.findViewById(R.id.row9_icon3);
            holder.icon4=(ImageView)rowView.findViewById(R.id.row9_icon4);
            holder.name=(TextView)rowView.findViewById(R.id.row9_name);
            holder.percent=(TextView)rowView.findViewById(R.id.row9_percent);
            holder.size=(TextView)rowView.findViewById(R.id.row9_size);
            holder.pb=(ProgressBar)rowView.findViewById(R.id.row9_pb);

            rowView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)rowView.getTag();
        }


        //SETTING FILE NAME
        holder.name.setText(file.getName());

        //SETTING SIZELIST
        holder.size.setText(file.getSize());

        double percent;
        if(totalUsed==0)
        {
            percent=0;
        }
        else
        {
            percent=((double)file.getSizeLong()/totalUsed)*100;
        }


        holder.percent.setText(String.format("%.2f",percent) + " %" );
        holder.pb.setProgress((int)Math.ceil(percent));


        //SETTING CHECKED LIST RESULTS
        if(file.isChecked())
        {
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_checked));
        }
        else
        {
            if(file.getName().startsWith("."))
            {
                //is hidden
                holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_hidden));
            }
            else
            {
                holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_normal));
            }
        }


        getThumbs(holder.icon1,holder.icon2,holder.icon3,holder.icon4,file.getName(),file.getThumbUrl(),file.isFolder(),file.getSymLink(),file.isFavourite());

        return rowView;

    }


    private void getThumbs(ImageView imageView1,ImageView imageView2,ImageView imageView3,ImageView imageView4, String name, String path, boolean isFolder, boolean isSymLink,boolean isFavourite)
    {
        /*
         * iconCode:1=image
         *          2=video
         *          3=audio
         *          4=apk
         *          0=otherwise
         */
        int iconCode=0;
        iconCode=extensionUtil.getExtensionId(name);

        //IMAGE VIEW 1
        switch (modValue)
        {
            case 0:
                thumbNailsMod.mod0(imageView1,path,isFolder,iconCode,name);
                break;
            case 1:
                thumbNailsMod.mod1(imageView1,path,isFolder,iconCode,name);
                break;
            case 2:
                thumbNailsMod.mod2(imageView1,path,isFolder,iconCode,name);
                break;
        }

        //IMAGE VIEW 2
        imageView2.setVisibility(View.VISIBLE);
        if(storageId==4)
        {
            imageView2.setImageResource(R.mipmap.google_drive);
        }
        if(storageId==5)
        {
            imageView2.setImageResource(R.mipmap.dropbox);
        }
        if(storageId==6)
        {
            imageView2.setImageResource(R.mipmap.server);
        }
        if(storageId<4)
        {
            imageView2.setVisibility(View.INVISIBLE);
        }

        //IMAGEVIEW 3
        if(isFavourite)
            imageView3.setVisibility(View.VISIBLE);
        else
            imageView3.setVisibility(View.INVISIBLE);

        //IMAGE VIEW 4
        imageView4.setVisibility(View.VISIBLE);
        if(isSymLink)
        {
            imageView4.setImageResource(R.drawable.symlink);
        }
        else
        {
            if(iconCode==2)
            {
                imageView4.setImageResource(R.drawable.video);
            }
            else
            {
                imageView4.setVisibility(View.INVISIBLE);
            }
        }

    }


    static class ViewHolder
    {
        TextView name,size;
        ImageView icon1,icon2,icon3,icon4;
        LinearLayout linearLayout;
        TextView percent;
        ProgressBar pb;
    }

}
