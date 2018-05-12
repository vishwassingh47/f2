package com.example.sahil.f2.FunkyAdapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sahil.f2.Cache.ThumbNailCache;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.Classes.ThumbNailsMod;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment1;
import com.example.sahil.f2.GokuFrags.image_gallery_fragment2;
import com.example.sahil.f2.GokuFrags.search_fragment;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.ExtensionUtil;

import java.util.ArrayList;

import static android.graphics.Color.parseColor;

/**
 * Created by hit4man47 on 10/8/2017.
 */

public class GridViewAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<MyFile> fileList;

    private final int modValue,galleryType;

    private final int storageId;
    private final int pagerId;
    private HelpingBot helpingBot;
    private Fragment fragment;
    private final ThumbNailsMod thumbNailsMod;
    private ExtensionUtil extensionUtil;


    /**
     *
     * @param context
     * @param fileList
     * @param storageId
     * @param fragment
     * @param modValue
     * @param pagerId 1:storagePage
     *                2:searchFragment
     *                3:imageFrag1
     *                4:imageFrag2
     *                6:appManager
     */
    public GridViewAdapter(Context context, ArrayList<MyFile> fileList, int storageId, Fragment fragment, int modValue, int pagerId,int galleryType)
    {
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
        this.fileList=fileList;

        this.storageId=storageId;
        this.fragment=fragment;
        this.modValue=modValue;
        this.galleryType=galleryType;
        this.pagerId=pagerId;

        helpingBot=new HelpingBot();
        extensionUtil=new ExtensionUtil();
        thumbNailsMod=new ThumbNailsMod(fragment,pagerId);

    }

    public int getCount()
    {
        return fileList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }



    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(pagerId==1 || pagerId==2 || pagerId==6)
        {
            return getView12(position,convertView,parent);
        }
        else
        {
            if(pagerId==3)
            {
                return getView3(position,convertView,parent);
            }
            else
            {
                if(pagerId==4)
                {
                    return getView4(position,convertView,parent);
                }
            }
        }

        return null;
    }

    //FOR pager ID= 1,2
    private View getView12(int position, View convertView, ViewGroup parent)
    {
        MyFile file=fileList.get(position);

        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.layoutof_row11, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.row11_name);
            holder.icon1 = (ImageView) convertView.findViewById(R.id.row11_icon);
            holder.icon2 = (ImageView) convertView.findViewById(R.id.row11_icon2);
            holder.icon3 = (ImageView) convertView.findViewById(R.id.row11_icon3);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.row11);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        //SETTING FILE NAME
        holder.name.setText(file.getName());


        //SETTING CHECKED LIST RESULTS
        if (file.isChecked())
        {
            //when checked
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_checked));
            //holder.layout.setBackgroundColor(parseColor("#9ccc65"));
        }
        else
        {
            //not checked
            if(file.getName().startsWith("."))
            {
                //is hidden
                holder.layout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_hidden));
                //holder.layout.setBackgroundColor(parseColor("#e8f5e9"));
            }
            else
            {
                //not hidden
                holder.layout.setBackgroundColor(context.getResources().getColor(R.color.theme1_item_normal));
                //holder.layout.setBackgroundColor(parseColor("#c5e1a5"));
            }
        }


        getThumbs(holder.icon1,holder.icon2,holder.icon3,pagerId==6?"abc.apk":file.getName(),file.getThumbUrl(),file.isFolder(),file.getSymLink(),file.isFavourite());




        return convertView;

    }

    //FOR pager ID= 3
    private View getView3(int position, View convertView, ViewGroup parent)
    {
        MyFile file=fileList.get(position);

        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.layoutof_row12,parent,false);
            holder = new ViewHolder();

            holder.name=(TextView)convertView.findViewById(R.id.row12_name);
            holder.path=(TextView)convertView.findViewById(R.id.row12_shortpath);
            holder.items=(TextView)convertView.findViewById(R.id.row12_items);
            holder.icon1=(ImageView)convertView.findViewById(R.id.row12_icon1);
            holder.icon2=(ImageView)convertView.findViewById(R.id.row12_icon2);
            holder.icon3=(ImageView)convertView.findViewById(R.id.row12_icon3);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.row12_linear_layout);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        //SETTING FILE NAME
        holder.name.setText(file.getName());

        //SETTING ITEMS
        holder.items.setText( file.getContainerItems() + "" );

        //SETTING CHECKED LIST RESULTS
        if (file.isChecked())
        {
            //when checked
            holder.layout.setBackgroundColor(parseColor("#f44336"));//RED
        }
        else
        {
            holder.layout.setBackgroundColor(parseColor("#000000"));//BLACK
        }

        switch (storageId)
        {
            case 1:
                holder.icon3.setVisibility(View.GONE);
                holder.path.setText(file.getPath());
                break;
            case 2:
                holder.icon3.setVisibility(View.GONE);
                holder.path.setText(file.getPath());
                break;
            case 3:
                holder.icon3.setVisibility(View.GONE);
                holder.path.setText(file.getPath());
                break;
            case 4:
                holder.icon3.setVisibility(View.VISIBLE);
                holder.icon3.setImageResource(R.mipmap.google_drive);
                holder.path.setText("Google Drive");
                break;
            case 5:
                holder.icon3.setVisibility(View.VISIBLE);
                holder.icon3.setImageResource(R.mipmap.dropbox);
                holder.path.setText(file.getPath());
                break;
        }

        if(file.isFavourite())
        {
            holder.icon2.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.icon2.setVisibility(View.GONE);
        }

        switch (galleryType)
        {
            case 1:
                getThumbs(holder.icon1,null,null,"abc.jpg",file.getThumbUrl(),false,false,false);
                break;
            case 2:
                getThumbs(holder.icon1,null,null,"abc.mp4",file.getThumbUrl(),false,false,false);
                break;
            case 3:
                getThumbs(holder.icon1,null,null,"abc.mp3",file.getThumbUrl(),false,false,false);
                break;
            case 4:
                getThumbs(holder.icon1,null,null,"abc.apk",file.getThumbUrl(),false,false,false);
                break;
        }



        return convertView;

    }

    //FOR pager ID= 4
    private View getView4(int position, View convertView, ViewGroup parent)
    {
        MyFile file=fileList.get(position);

        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.layoutof_row13,parent,false);
            holder = new ViewHolder();

            holder.name=(TextView)convertView.findViewById(R.id.row13_name);
            holder.icon1=(ImageView)convertView.findViewById(R.id.row13_icon1);
            holder.icon2=(ImageView)convertView.findViewById(R.id.row13_icon2);
            holder.icon3=(ImageView)convertView.findViewById(R.id.row13_icon3);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.row13_linear_layout);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        //SETTING FILE NAME
        holder.name.setText(file.getName());


        //SETTING CHECKED LIST RESULTS
        if (file.isChecked())
        {
            //when checked
            holder.layout.setBackgroundColor(parseColor("#f44336"));//RED
        }
        else
        {
            holder.layout.setBackgroundColor(parseColor("#000000"));//BLACK
        }

        switch (storageId)
        {
            case 1:
                holder.icon3.setVisibility(View.GONE);
                break;
            case 2:
                holder.icon3.setVisibility(View.GONE);
                break;
            case 3:
                holder.icon3.setVisibility(View.GONE);
                break;
            case 4:
                holder.icon3.setVisibility(View.VISIBLE);
                holder.icon3.setImageResource(R.mipmap.google_drive);
                break;
            case 5:
                holder.icon3.setVisibility(View.VISIBLE);
                holder.icon3.setImageResource(R.mipmap.dropbox);
                break;
        }

        if(file.isFavourite())
            holder.icon2.setVisibility(View.VISIBLE);
        else
            holder.icon2.setVisibility(View.GONE);


        switch (galleryType)
        {
            case 1:
                getThumbs(holder.icon1,null,null,"abc.jpg",file.getThumbUrl(),false,false,false);
                break;
            case 2:
                getThumbs(holder.icon1,null,null,"abc.mp4",file.getThumbUrl(),false,false,false);
                break;
            case 3:
                getThumbs(holder.icon1,null,null,"abc.mp3",file.getThumbUrl(),false,false,false);
                break;
            case 4:
                getThumbs(holder.icon1,null,null,"abc.apk",file.getThumbUrl(),false,false,false);
                break;
        }


        return convertView;

    }



    private void getThumbs(ImageView imageView1,ImageView imageView2,ImageView imageView3,String name,String path,boolean isFolder,boolean isSymLink,boolean isFavourite)
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
        if(imageView2!=null)
        {
            if( (isFolder && (storageId>=4 && storageId<=6)) || isSymLink || iconCode==2 )
            {
                if(isFolder)
                {
                    if(storageId==4)
                    {
                        imageView2.setImageResource(R.mipmap.google_drive);
                    }
                    if (storageId==5)
                    {
                        imageView2.setImageResource(R.mipmap.dropbox);
                    }
                    if (storageId==6)
                    {
                        imageView2.setImageResource(R.mipmap.server);
                    }
                }
                else
                {
                    if(isSymLink)
                    {
                        imageView2.setImageResource(R.drawable.symlink);
                    }
                }

                if(iconCode==2)
                {
                    imageView2.setImageResource(R.drawable.video);
                }

                imageView2.setVisibility(View.VISIBLE);
            }
            else
            {
                imageView2.setVisibility(View.GONE);
            }
        }


        //IMAGE VIEW3
        if(imageView3!=null)
        {
            if(isFavourite)
            {
                imageView3.setVisibility(View.VISIBLE);
            }
            else
            {
                imageView3.setVisibility(View.GONE);
            }
        }
    }


    static class ViewHolder
    {
        //pagerId 1,2
        TextView name;
        ImageView icon1;
        ImageView icon2;
        ImageView icon3;
        LinearLayout layout;

        //pagerId 3
        /*
        TextView name;
        ImageView icon1;
        ImageView icon2;
        ImageView icon3;
        LinearLayout layout;
         */
        TextView items,path;

        //pagerId 4
        /*
        TextView name;
        ImageView icon1;
        ImageView icon2;
        ImageView icon3;
        LinearLayout layout;
         */

    }


}
