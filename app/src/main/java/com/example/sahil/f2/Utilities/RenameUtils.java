package com.example.sahil.f2.Utilities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.StorageAccessFramework;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.IOException;

/**
 * Created by hit4man47 on 2/17/2018.
 */

public class RenameUtils
{
    private final ExtensionUtil extensionUtil=new ExtensionUtil();
    private final MainActivity mainActivity;
    private final KeepBothUtils keepBothUtils;

    public RenameUtils(MainActivity mainActivity)
    {
        this.mainActivity=mainActivity;
        keepBothUtils=new KeepBothUtils();
    }

    public boolean renameInInternal(MyFile myFile,String parentPath,String newName,boolean automaticKeepBoth)
    {
        String oldPath=myFile.getPath();

        File oldFile=new File(oldPath);
        String newPath=slashAppender(parentPath,newName);

        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            newPath=keepBothUtils.getUniquePathLocal(newPath,myFile.isFolder());
            newName=HelpingBot.getNameFromPath(newPath);
        }
        File newFile=new File(newPath);

        if(oldFile.renameTo(newFile))
        {
            myFile.setName(newName);//to reflect in gallery and search results
            int extensionId=extensionUtil.getExtensionId(newName);
            if(!myFile.isFolder())
            {
                DeleteUtils deleteUtils=new DeleteUtils(null,mainActivity);
                switch (extensionId)
                {
                    case 1:
                        deleteUtils.deleteImageFromMediaStore(oldPath);
                        break;
                    case 2:
                        deleteUtils.deleteVideoFromMediaStore(oldPath);
                        break;
                    case 3:
                        deleteUtils.deleteAudioFromMediaStore(oldPath);
                        break;
                }
                deleteUtils.deleteFileFromMediaStore(oldPath);
                addToMediaStore(newFile);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean renameInSaf(MyFile myFile,String parentPath,String newName,boolean automaticKeepBoth)
    {
        String oldPath=myFile.getPath();
        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            String newPath=slashAppender(parentPath,newName);
            newPath =keepBothUtils.getUniquePathLocal(newPath,myFile.isFolder());
            newName= HelpingBot.getNameFromPath(newPath);
        }




        DocumentFile df= StorageAccessFramework.fileToDocumentFileConverter(oldPath,myFile.isFolder(),mainActivity);
        if(df==null || !df.renameTo(newName) )
        {
            return false;
        }
        else
        {
            myFile.setName(newName);//to reflect in gallery and search results
            int extensionId=extensionUtil.getExtensionId(newName);

            if(!myFile.isFolder())
            {
                DeleteUtils deleteUtils=new DeleteUtils(null,mainActivity);
                switch (extensionId)
                {
                    case 1:
                        deleteUtils.deleteImageFromMediaStore(oldPath);
                        break;
                    case 2:
                        deleteUtils.deleteVideoFromMediaStore(oldPath);
                        break;
                    case 3:
                        deleteUtils.deleteAudioFromMediaStore(oldPath);
                        break;
                }
                deleteUtils.deleteFileFromMediaStore(oldPath);
                File newFile=new File(parentPath,newName);
                addToMediaStore(newFile);
            }

            return true;
        }
    }

    public boolean renameInRoot(MyFile myFile,String parentPath,String newName,boolean automaticKeepBoth)
    {
        String oldPath=myFile.getPath();
        String newPath=slashAppender(parentPath,newName);
        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            newPath =keepBothUtils.getUniquePathLocal(newPath,myFile.isFolder());
            //newName= HelpingBot.getNameFromPath(newPath);
        }


        return RootTools.getInternals().rename(oldPath,newPath);

        /*
        if(success)
        {
            myFile.setName(newName);//to reflect in gallery and search results
        }
         */
    }

    public boolean renameInDrive(MyFile myFile,String newName)
    {
        com.google.api.services.drive.model.File newContent=new com.google.api.services.drive.model.File();
        newContent.setName(newName);
        try
        {
            com.google.api.services.drive.model.File created= GoogleDriveConnection.m_service_client.files().update(myFile.getPath(),newContent).execute();
            if(created==null || created.getId()==null)
            {
                return false;
            }
        }
        catch (IOException e)
        {
            return false;
        }
        catch (Exception e)
        {
            return false;
        }

        myFile.setName(newName);//to reflect in gallery and search results
        return true;
    }

    public boolean renameInDropBox(MyFile myFile,String parentPath,String newName,boolean automaticKeepBoth)
    {
        String oldPath=myFile.getPath();
        String newPath=slashAppender(parentPath,newName);
        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            newPath=keepBothUtils.getUniquePathDropBox(newPath,myFile.isFolder());
            if(newPath==null)
                return false;

            newName=HelpingBot.getNameFromPath(newPath);
        }

        try
        {
            Metadata metadata= DropBoxConnection.mDbxClient.files().moveV2(oldPath,newPath).getMetadata();
            if(metadata==null)
                return false;
        }
        catch (DbxException e)
        {
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
        myFile.setName(newName);//to reflect in gallery and search results
        return true;
    }

    public boolean renameInFtp(MyFile myFile,String parentPath,String newName)
    {
        String oldPath=myFile.getPath();
        String newPath=slashAppender(parentPath,newName);
        try
        {
            boolean result= FtpCache.mFTPClient.rename(oldPath,newPath);
            if(!result)
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        myFile.setName(newName);//to reflect in gallery and search results
        return true;
    }

    @NonNull
    private String slashAppender(String a, String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }

    private void addToMediaStore(File to)
    {
        Log.e("adding to media store:",to.getAbsolutePath()+"--isFile:"+to.isFile());
        Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(to));
        mainActivity.sendBroadcast(intent);
    }
}
