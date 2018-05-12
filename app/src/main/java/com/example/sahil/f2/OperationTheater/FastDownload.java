package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.sahil.f2.Cache.Constants;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import java.io.File;

/**
 * Created by hit4man47 on 12/28/2017.
 */

public class FastDownload
{
    public final MainActivity mainActivity;
    private final TinyDB tinyDB;
    private MyFile myFile;
    private int storageId;

    public FastDownload(MainActivity mainActivity,MyFile myFile,int storageId)
    {
        this.mainActivity=mainActivity;
        tinyDB=new TinyDB(mainActivity);
        this.myFile=myFile;
        this.storageId=storageId;
    }

    public void decisionMaker()
    {
        final String localPathOfThisMyFile=tinyDB.getString(myFile.getPath());

        if(localPathOfThisMyFile.length()>0 && (new File(localPathOfThisMyFile)).exists())
        {
            //local copy exists
            final BottomSheetDialog dialog=new BottomSheetDialog(mainActivity);
            View view=mainActivity.getLayoutInflater().inflate(R.layout.layoutof_fast_download,null);
            dialog.setContentView(view);

            Button download_again=(Button) dialog.findViewById(R.id.fast_download_again);
            Button open=(Button) dialog.findViewById(R.id.fast_download_open);

            download_again.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialog.cancel();
                    doFastDownLoad();
                }
            });
            open.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialog.cancel();
                    File file=new File(localPathOfThisMyFile);
                    mainActivity.returnFileOpenerObject(file).open();
                }
            });

            dialog.show();


        }
        else
        {
            //fast download
            doFastDownLoad();
        }
    }


    //it will set TO PASTE data and will call copyToLocal which in turn call machines after preparing the cache
    private void doFastDownLoad()
    {
        PasteClipBoard.clear();
        PasteClipBoard.cutOrCopy=2;
        PasteClipBoard.isFastDownload=true;

        PasteClipBoard.fromStorageCode=storageId;
        switch (storageId)
        {
            case 4:
                PasteClipBoard.fromParentPath="Google Drive";
                break;
            case 5:
                PasteClipBoard.fromParentPath="Drop Box";
                break;
            case 6:
                PasteClipBoard.fromParentPath="FTP Server";
                break;
        }

        PasteClipBoard.pathList.add(myFile.getPath());
        PasteClipBoard.nameList.add(myFile.getName());
        PasteClipBoard.sizeLongList.add(myFile.getSizeLong());
        PasteClipBoard.isFolderList.add(myFile.isFolder());

        if(storageId==4)
        {
            PasteClipBoard.toRootPath= Constants.GOOGLEDRIVE_DOWNLOAD_PATH;
        }
        if(storageId==5)
        {
            PasteClipBoard.toRootPath= Constants.DROPBOX_DOWNLOAD_PATH;
        }
        if(storageId==6)
        {
            PasteClipBoard.toRootPath= Constants.FTP_DOWNLOAD_PATH;
        }

        PasteClipBoard.toStorageCode=1;
        PasteClipBoard.toRootPath=MainActivity.Physical_Storage_PATHS.get(0);

        mainActivity.pasteButtonDecisionMaker.copyToLocal();

    }

}
