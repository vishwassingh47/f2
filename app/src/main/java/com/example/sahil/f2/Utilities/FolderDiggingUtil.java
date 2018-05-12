package com.example.sahil.f2.Utilities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.FolderDigger;
import com.google.api.services.drive.model.FileList;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hit4man47 on 2/20/2018.
 */

public class FolderDiggingUtil
{
    String arr[];

    private void digLocal(File fileOrDirectory, FolderDigger folderDigger)
    {
        if(FolderDigger.stopDigging)
        {
            return;
        }
        File [] files;
        try
        {
            files=fileOrDirectory.listFiles();
        }
        catch (Exception e)
        {
            return;
        }

        if(files!=null)
            for (File child :files)
            {
                if(FolderDigger.stopDigging)
                {
                    return;
                }
                if(child.exists() && child.canRead())
                {

                    if(child.isDirectory() )
                    {
                        folderDigger.subFolder++;
                        digLocal(child,folderDigger);
                    }

                    else
                    {
                        folderDigger.subFiles++;
                        folderDigger.sizeInBytes+=child.length();
                    }
                }
            }
    }

    private void digDropBox(String path,FolderDigger folderDigger)
    {

        if(FolderDigger.stopDigging)
        {
            return;
        }
        List<Metadata> list=new ArrayList<>();
        try
        {
            ListFolderResult result = DropBoxConnection.mDbxClient.files().listFolder(path);
            while (true)
            {
                for (Metadata metadata : result.getEntries())
                {
                    list.add(metadata);
                }

                if (!result.getHasMore())
                {
                    break;
                }

                result = DropBoxConnection.mDbxClient.files().listFolderContinue(result.getCursor());
            }

        }
        catch (Exception e)
        {
            return;
        }

        for (Metadata child : list)
        {
            if(FolderDigger.stopDigging)
            {
                return;
            }
            if(child instanceof FolderMetadata)
            {
                folderDigger.subFolder++;
                digDropBox(child.getPathDisplay(),folderDigger);
            }
            else
            {
                folderDigger.subFiles++;
                folderDigger.sizeInBytes+=((FileMetadata)child).getSize();
            }
        }
    }

    private void digGoogleDrive(String folderId,FolderDigger folderDigger)
    {

        if(FolderDigger.stopDigging)
        {
            return;
        }
        try
        {
            com.google.api.services.drive.Drive.Files.List request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,quotaBytesUsed)").setQ("'" +folderId+ "' in parents");
            List<com.google.api.services.drive.model.File> filesInFolder= new ArrayList<com.google.api.services.drive.model.File>();
            do
            {
                try
                {
                    FileList filelist = request.execute();
                    filesInFolder.addAll(filelist.getFiles());
                    request.setPageToken(filelist.getNextPageToken());
                }
                catch (IOException e)
                {
                    System.out.println("An error occurred: " + e);
                    request.setPageToken(null);
                    return;
                }
            }
            while (request.getPageToken() != null && request.getPageToken().length() > 0);

            for(com.google.api.services.drive.model.File f:filesInFolder)
            {
                if(FolderDigger.stopDigging)
                {
                    return;
                }

                if(f.getMimeType().contains("folder"))
                {
                    folderDigger.subFolder++;
                    digGoogleDrive(f.getId(),folderDigger);
                }
                else
                {
                    folderDigger.subFiles++;
                    folderDigger.sizeInBytes+=f.getQuotaBytesUsed();
                }
            }
        }
        catch (IOException e)
        {
            return;
        }
    }

    private void digFtp(String folderPath,FolderDigger folderDigger)    //OKOK
    {
        if(FolderDigger.stopDigging)
        {
            return;
        }
        FTPFile[] filesFtp=null;
        try
        {
            filesFtp= FtpCache.mFTPClient.listFiles(folderPath);
        }
        catch (Exception e)
        {
            return ;
        }

        if(filesFtp!=null)
            for (FTPFile child :filesFtp)
            {
                if(FolderDigger.stopDigging)
                {
                    return;
                }
                String name=child.getName();
                String path=slashAppender(folderPath,name);

                if(child.isDirectory() )
                {
                    folderDigger.subFolder++;
                    digFtp(path,folderDigger);
                }

                else
                {
                    folderDigger.subFiles++;
                    folderDigger.sizeInBytes+=child.getSize();
                }
            }

    }

    private void digRoot(String path,FolderDigger folderDigger)
    {


    }

    @NonNull
    private String slashAppender(String a, String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }
}
