package com.example.sahil.f2.Utilities;

import android.content.Context;
import android.os.Build;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.OperationTheater.PagerXUtilities;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.internal.RootToolsInternalMethods;

import org.apache.ftpserver.command.impl.HELP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hit4man47 on 2/19/2018.
 */

public class CreateNewUtils
{
    /*
    ALL THESE OPERATIONS ARE BLOCKING OPERATIONS, SO CALL THEM IN BACKGROUND THREAD
     */

    private final Context context;
    private final KeepBothUtils keepBothUtils;
    private final String TAG="CREATE_NEW_UTILS";

    public CreateNewUtils(Context context)
    {
        this.context=context;
        keepBothUtils=new KeepBothUtils();
    }

    public boolean createInLocal(String fullPath,final boolean createFolder,final boolean automaticKeepBoth)
    {
        if(createInInternal(fullPath,createFolder,automaticKeepBoth))
        {
            return true;
        }
        if(SuperUser.hasUserEnabledSU && createInRoot(fullPath, createFolder, automaticKeepBoth))
        {
            return true;
        }

        if(PagerXUtilities.isExternalSdCardPath(fullPath))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                //assuming that we are having SAF permissions at this point
                return createInSaf(fullPath,createFolder,automaticKeepBoth);
            }
            else
            {
                //MEDIA STORE HACK

            }
        }
        return false;
    }

    public boolean createInInternal(String fullPath,final boolean createFolder,final boolean automaticKeepBoth)
    {
        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            fullPath=keepBothUtils.getUniquePathLocal(fullPath,createFolder);
        }

        final File newFile=new File(fullPath);
        boolean success;
        if(createFolder)
        {
            success=newFile.mkdirs();
        }
        else
        {
            try
            {
                success=newFile.createNewFile();
            }
            catch (Exception e)
            {
                success=false;
            }
        }

        return success;
    }

    public boolean createInSaf(String fullPath,final boolean createFolder,final boolean automaticKeepBoth)
    {
        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            fullPath=keepBothUtils.getUniquePathLocal(fullPath,createFolder);
        }

        DocumentFile df= StorageAccessFramework.fileToDocumentFileConverter(fullPath,createFolder,context);
        final File newFile=new File(fullPath);
        if(df==null || !newFile.exists() )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean createInRoot(String fullPath,final boolean createFolder,boolean automaticKeepBoth)
    {
        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            fullPath =keepBothUtils.getUniquePathLocal(fullPath,createFolder);
        }

        if(createFolder)
        {
            return RootTools.getInternals().createFolder(fullPath);
        }
        else
        {
            return RootTools.getInternals().createFile(fullPath);
        }
    }

    public boolean createInDrive(String parentPath,String newName,final boolean createFolder)
    {
        try
        {
            com.google.api.services.drive.model.File newContent=new com.google.api.services.drive.model.File();
            newContent.setName(newName);
            List<String> parents=new ArrayList<>();
            parents.add(parentPath);
            newContent.setParents(parents);
            if(createFolder)
                newContent.setMimeType("application/vnd.google-apps.folder");
            //else
            //newContent.setMimeType("application/vnd.google-apps.file");

            com.google.api.services.drive.model.File created = GoogleDriveConnection.m_service_client.files().create(newContent).setFields("id,name").execute();
            Log.e(TAG,"fileOrFolder created:"+created.getId()+"--"+created.getName());
            if(created.getId()==null)
                throw new Exception("-_-");
        }
        catch (IOException e)
        {
            Log.e(TAG,e.getLocalizedMessage()+e.getMessage());
            return false;
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getLocalizedMessage()+e.getMessage());
            return false;
        }
        return true;
    }

    public boolean createInDropBox(String fullPath,final boolean createFolder,final boolean automaticKeepBoth)
    {
        if(automaticKeepBoth)
        {
            /*
            will check if this newPath already exist or not
            if it does it will make a newUnique non existing path
             */
            fullPath=keepBothUtils.getUniquePathDropBox(fullPath,createFolder);
            if(fullPath==null)
                return false;
        }

        try
        {
            FolderMetadata folder = DropBoxConnection.mDbxClient.files().createFolderV2(fullPath).getMetadata();
            if(folder.getName()==null)
                throw new Exception();
        }
        catch (DbxException e)
        {
            return false;
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    public boolean createInFtp(final String fullPath)
    {
        try
        {
            boolean created = FtpCache.mFTPClient.makeDirectory(fullPath);
            if(!created)
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }



}
