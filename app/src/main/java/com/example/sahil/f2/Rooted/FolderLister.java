package com.example.sahil.f2.Rooted;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Cache.variablesCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.google.api.services.drive.model.FileList;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;

import static com.example.sahil.f2.OperationTheater.PagerXUtilities.isExternalSdCardPath;

/**
 * Created by hit4man47 on 3/2/2018.
 */

public class FolderLister
{
    private final HelpingBot helpingBot=new HelpingBot();

    public ArrayList<MyFile> listRootFolder(Context context, String folderPath)
    {
        if(!SuperUser.hasUserEnabledSU)
        {
            return null;
        }

        final String TAG="listRootFolder";
        File myLsFile=context.getFileStreamPath("myls");
        if(!myLsFile.exists())
        {
            Log.e(TAG,"command to execute :" + myLsFile.getAbsolutePath() + " is missing");
            return null;
        }
        final ArrayList<MyFile> myFilesList=new ArrayList<>();

        final String commandString=myLsFile.getAbsolutePath()+" 1 '"+folderPath+"'";
        Log.e(TAG,"Running command:"+commandString);
        Command command= new Command(1502,10000,commandString)
        {
            @Override
            public void commandOutput(int id, String line)
            {
                super.commandOutput(id, line);
                Log.e(TAG,"Output"+line+"--"+id);

                String s[]=line.split("#@\\$");
                if(s.length==6)
                {
                    MyFile file=new MyFile();
                    file.setPermission(s[1]);
                    file.setName(s[2]);
                    if(file.getName().startsWith(".") && !variablesCache.showHidden)
                    {
                        return;
                    }
                    file.setPath(s[5]);
                    file.setLastModified((long)(HelpingBot.parseLong(s[4])*1000));
                    int x=HelpingBot.parseInt(s[0]);
                    long size;
                    switch (x)
                    {
                        case 1:
                            file.setSymLink(false);
                            file.setFolder(false);
                            size=HelpingBot.parseLong(s[3]);
                            file.setSizeLong(size);
                            file.setSize(helpingBot.sizeinwords(size));
                            break;
                        case 2:
                            file.setSymLink(false);
                            file.setFolder(true);
                            file.setSizeLong(0);
                            file.setSize("");
                            break;
                        case 4:
                            file.setSymLink(true);
                            file.setFolder(false);
                            size=HelpingBot.parseLong(s[3]);
                            file.setSizeLong(size);
                            file.setSize(helpingBot.sizeinwords(size));
                            break;
                        case 5:
                            file.setSymLink(true);
                            file.setFolder(true);
                            file.setSizeLong(0);
                            file.setSize("");
                            break;
                        default:
                            return;
                    }
                    file.setChecked(false);
                    file.setFavourite(favouritesCache.favouritePaths.contains(s[5]));
                    file.setFileId(null);
                    file.setThumbUrl(s[5]);
                    myFilesList.add(file);
                }
                else
                {
                    if(s.length==2)
                    {
                        Log.e(TAG,s[0]+"--"+s[1]);
                    }
                }
            }
        };

        try
        {
            Shell shell= RootTools.getShell(true);
            shell.add(command);
            RootShell.commandWait(Shell.startRootShell(), command);
            if(command.getExitCode() != 0)
            {
                Log.e(TAG,"UnKnown Error");
                return null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getLocalizedMessage()+e.getMessage()+"--");
            return null;
        }
        return myFilesList;
    }

    public ArrayList<MyFile> listLocalFolder(String folderPath)
    {
        final String TAG="listLocalFolder";
        final File rootFolder=new File(folderPath);
        File [] filesLocal=null;
        try
        {
            filesLocal=rootFolder.listFiles();
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage()+"--");
            filesLocal=null;
        }
        if(filesLocal==null)
        {
            return null;
        }

        final ArrayList<MyFile> myFilesList=new ArrayList<>();
        for(File f:filesLocal)
        {
            if(f.getName().startsWith(".") && !variablesCache.showHidden)
            {
                continue;
            }
            MyFile file=new MyFile();
            file.setChecked(false);
            file.setSymLink(false);
            file.setThumbUrl(f.getAbsolutePath());
            file.setName(f.getName());
            file.setPath(f.getAbsolutePath());
            file.setFavourite(favouritesCache.favouritePaths.contains(f.getAbsolutePath()));
            file.setFileId(null);
            file.setLastModified(f.lastModified());

            long size=f.length();
            if(f.isDirectory())
            {
                file.setFolder(true);
                file.setSize("");
                file.setSizeLong(0);
            }
            else
            {
                file.setFolder(false);
                file.setSizeLong(size);
                file.setSize(helpingBot.sizeinwords(size));
            }
            myFilesList.add(file);
        }

        return myFilesList;
    }

    /**
     *
     * @param folderId is of no use if listRecycleBin=true
     * @param listRecycleBin true or false
     * @return
     */
    public ArrayList<MyFile> listDriveFolder(String folderId,boolean listRecycleBin)
    {
        final ArrayList<MyFile> myFilesList=new ArrayList<>();
        final String TAG="listDriveFolder";
        try
        {
            com.google.api.services.drive.Drive.Files.List request;
            if(listRecycleBin)
            {
                request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id, name, parents,mimeType,quotaBytesUsed,thumbnailLink,webContentLink)").setQ("trashed=true");
            }
            else
            {
                request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id, name, parents,mimeType,quotaBytesUsed,thumbnailLink,webContentLink)").setQ("'" +folderId+ "' in parents and trashed=false");
            }

            ArrayList<com.google.api.services.drive.model.File> filesDrive = new ArrayList<com.google.api.services.drive.model.File>();
            do
            {
                FileList filelist = request.execute();
                filesDrive.addAll(filelist.getFiles());
                request.setPageToken(filelist.getNextPageToken());
            }
            while (request.getPageToken() != null && request.getPageToken().length() > 0);


            for(com.google.api.services.drive.model.File f:filesDrive)
            {
                if(f.getName().startsWith(".") && !variablesCache.showHidden)
                {
                    continue;
                }

                MyFile file=new MyFile();
                file.setChecked(false);
                file.setSymLink(false);
                file.setName(f.getName());
                file.setPath(f.getId());
                file.setFavourite(favouritesCache.favouritePaths.contains(f.getId()));
                file.setFileId(f.getId());
                file.setThumbUrl(f.getThumbnailLink()+ "@#$" + file.getFileId());
                try
                {
                    file.setLastModified(f.getModifiedTime().getValue());
                }
                catch (Exception e)
                {
                    file.setLastModified(0);
                }


                long size=f.getQuotaBytesUsed();
                if(f.getMimeType().contains("folder"))
                {
                    file.setFolder(true);
                    file.setSize("");
                    file.setSizeLong(0);
                }
                else
                {
                    file.setFolder(false);
                    file.setSizeLong(size);
                    file.setSize(helpingBot.sizeinwords(size));
                }

                myFilesList.add(file);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,"no net"+e.getLocalizedMessage());
            return null;
        }
        return myFilesList;
    }

    /**
     *
     * @param folderPath is of no use if listRecycleBin=true
     * @param listRecycleBin true or false
     * @return
     */
    public ArrayList<MyFile> listDropBoxFolder(String folderPath,boolean listRecycleBin)
    {
        final ArrayList<MyFile> myFilesList=new ArrayList<>();
        final String TAG="listDropBoxFolder";

        try
        {
            ListFolderResult result;
            if (listRecycleBin)
            {
                result =DropBoxConnection.mDbxClient.files().listFolderBuilder("").withRecursive(true).withIncludeDeleted(true).start();
            }
            else
            {
                result = DropBoxConnection.mDbxClient.files().listFolder(folderPath);
            }


            ArrayList<Metadata> filesDropBox=new ArrayList<>();
            while (true)
            {
                for (Metadata metadata : result.getEntries())
                {
                    filesDropBox.add(metadata);
                }
                if (!result.getHasMore())
                {
                    break;
                }
                result = DropBoxConnection.mDbxClient.files().listFolderContinue(result.getCursor());
            }

            for(com.dropbox.core.v2.files.Metadata f:filesDropBox)
            {
                if(f.getName().startsWith(".") && !variablesCache.showHidden)
                {
                    continue;
                }
                MyFile file=new MyFile();

                file.setSymLink(false);
                file.setChecked(false);
                file.setName(f.getName());
                file.setPath(f.getPathDisplay());
                file.setFavourite(favouritesCache.favouritePaths.contains(f.getPathDisplay()));

                if( f instanceof FileMetadata && !listRecycleBin)
                {
                    file.setFolder(false);
                    long size=((FileMetadata) f).getSize();
                    file.setSize(helpingBot.sizeinwords(size));
                    file.setSizeLong(size);
                    file.setFileId(((FileMetadata) f).getId());
                    file.setThumbUrl(f.getPathDisplay() + "@#$" + file.getFileId());
                    file.setLastModified(((FileMetadata)f).getClientModified().getTime());
                }
                else
                {
                    if(f instanceof FolderMetadata && !listRecycleBin)
                    {
                        file.setFolder(true);
                        file.setSize("");
                        file.setSizeLong(0);
                        file.setThumbUrl(null);
                        file.setFileId(((FolderMetadata) f).getId());
                        file.setLastModified(0);//DROPBOX FOLDER HAS NO MODIFIED TIME VARIABLE
                    }
                    else
                    {
                        if(f instanceof DeletedMetadata && listRecycleBin)
                        {
                            String name=f.getName();
                            if(name.contains("."))
                            {
                                file.setFolder(false);
                            }
                            else
                            {
                                //probably a folder
                                continue;
                            }
                            file.setSize("");
                            file.setSizeLong(0);
                            file.setThumbUrl(null);
                            file.setFileId(null);
                            file.setLastModified(0);
                        }
                        else
                        {
                            continue;
                        }
                    }
                }
                file.setThumbUrl(file.getPath()+ "@#$" + file.getFileId());
                myFilesList.add(file);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getLocalizedMessage());
            return null;
        }

        return myFilesList;

    }

    public ArrayList<MyFile> listFtpFolder(final String folderPath)
    {
        final ArrayList<MyFile> myFilesList=new ArrayList<>();
        final String TAG="listFtpFolder";
        FTPFile[] filesFtp=null;
        try
        {
            filesFtp= FtpCache.mFTPClient.listFiles(folderPath);
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage()+"--"+e.getLocalizedMessage());
            filesFtp=null;
        }
        if(filesFtp==null)
        {
            return null;
        }

        for(FTPFile f:filesFtp)
        {
            if(f.getName().equals(".") || f.getName().equals(".."))
            {
                continue;
            }
            if(f.getName().startsWith(".") && !variablesCache.showHidden)
            {
                continue;
            }

            MyFile file=new MyFile();
            String name=f.getName();
            String path=HelpingBot.slashAppender(folderPath,name);
            file.setChecked(false);
            file.setSymLink(false);
            file.setThumbUrl(path);
            file.setName(f.getName());
            file.setPath(path);
            int index=favouritesCache.favouritePaths.indexOf(path);
            if(index<0 || favouritesCache.favouriteStorageIdList.get(index)!=6)
            {
                file.setFavourite(false);
            }
            else
            {
                file.setFavourite(true);
            }

            file.setFileId(null);
            file.setLastModified(f.getTimestamp().getTimeInMillis());

            long size=f.getSize();
            if(f.isDirectory())
            {
                file.setFolder(true);
                file.setSize("");
                file.setSizeLong(0);
            }
            else
            {
                file.setFolder(false);
                file.setSizeLong(size);
                file.setSize(helpingBot.sizeinwords(size));
            }
            myFilesList.add(file);
        }

        return myFilesList;
    }

    public ArrayList<MyFile> listLocalFavouriteFolder()
    {
        final ArrayList<MyFile> myFilesList=new ArrayList<>();
        final String TAG="listLocalFavFolder";
        final String storagePath=MainActivity.Physical_Storage_PATHS.get(favouritesCache.storageId-1);
        final boolean listExternalSDCard=isExternalSdCardPath(storagePath);

        for(int i=0;i<favouritesCache.favouritePaths.size();i++)
        {
            int sId=favouritesCache.favouriteStorageIdList.get(i);
            String path=favouritesCache.favouritePaths.get(i);
            try
            {
                if (sId>3 || sId<1)
                    continue;

                boolean isExternal=isExternalSdCardPath(path);
                if(listExternalSDCard!=isExternal)
                {
                    continue;
                }

                File f=new File(path);
                if(f.getName().startsWith(".") && !variablesCache.showHidden)
                {
                    continue;
                }
                if(!f.exists())
                {
                    if(isExternal || path.startsWith(MainActivity.Physical_Storage_PATHS.get(0)))
                    continue;
                }
                if(isExternal && !path.startsWith(storagePath))
                {
                    continue;
                }

                MyFile file=new MyFile();
                file.setFavourite(true);
                file.setChecked(false);
                file.setSymLink(false);
                file.setThumbUrl(f.getAbsolutePath());
                file.setName(f.getName());
                file.setPath(f.getAbsolutePath());
                file.setFileId(null);
                file.setLastModified(f.lastModified());

                long size=f.length();
                if(f.isDirectory())
                {
                    file.setFolder(true);
                    file.setSize("");
                    file.setSizeLong(0);
                }
                else
                {
                    file.setFolder(false);
                    file.setSizeLong(size);
                    file.setSize(helpingBot.sizeinwords(size));
                }
                myFilesList.add(file);
            }
            catch (Exception e)
            {
              Log.e(TAG,e.getMessage()+"--"+e.getLocalizedMessage());
            }
        }

        return myFilesList;

    }

    public ArrayList<MyFile> listDriveFavouriteFolder()
    {
        final ArrayList<MyFile> myFilesList=new ArrayList<>();
        final String TAG="listDriveFavFolder";
        for(int i=0;i<favouritesCache.favouritePaths.size();i++)
        {
            int sId=favouritesCache.favouriteStorageIdList.get(i);
            String path=favouritesCache.favouritePaths.get(i);

            try
            {
                if(sId!=4)
                {
                    continue;
                }
                com.google.api.services.drive.model.File f;
                try
                {
                    f= GoogleDriveConnection.m_service_client.files().get(path).setFields("id, name, parents,mimeType,quotaBytesUsed,thumbnailLink,webContentLink").execute();
                }
                catch (Exception e)
                {
                    f=null;
                }

                if(f==null)
                {
                    continue;
                }
                if(f.getName().startsWith(".") && !variablesCache.showHidden)
                {
                    continue;
                }
                MyFile file=new MyFile();
                file.setFavourite(true);
                file.setChecked(false);
                file.setSymLink(false);
                file.setName(f.getName());
                file.setPath(f.getId());
                file.setFileId(f.getId());
                file.setThumbUrl(f.getThumbnailLink()+ "@#$" + file.getFileId());
                try
                {
                    file.setLastModified(f.getModifiedTime().getValue());
                }
                catch (Exception e)
                {
                    file.setLastModified(0);
                }
                long size=f.getQuotaBytesUsed();
                if(f.getMimeType().contains("folder"))
                {
                    file.setFolder(true);
                    file.setSize("");
                    file.setSizeLong(0);
                }
                else
                {
                    file.setFolder(false);
                    file.setSizeLong(size);
                    file.setSize(helpingBot.sizeinwords(size));
                }
                myFilesList.add(file);
            }
            catch (Exception e)
            {
                Log.e(TAG,"--"+e.getLocalizedMessage());
                return null;
            }
        }
        return myFilesList;
    }

    public ArrayList<MyFile> listDropBoxFavouriteFolder()
    {
        final ArrayList<MyFile> myFilesList=new ArrayList<>();
        final String TAG="listDropBoxFavFolder";
        for(int i=0;i<favouritesCache.favouritePaths.size();i++)
        {
            int sId=favouritesCache.favouriteStorageIdList.get(i);
            String path=favouritesCache.favouritePaths.get(i);

            try
            {
                if(sId!=5)
                {
                    continue;
                }
                Metadata f;
                try
                {
                    f=DropBoxConnection.mDbxClient.files().getMetadata(path);
                }
                catch (Exception e)
                {
                    f=null;
                }
                if(f==null)
                {
                    continue;
                }

                if(f.getName().startsWith(".") && !variablesCache.showHidden)
                {
                    continue;
                }

                MyFile file=new MyFile();

                file.setFavourite(true);
                file.setSymLink(false);
                file.setChecked(false);
                file.setName(f.getName());
                file.setPath(f.getPathDisplay());

                if( f instanceof FileMetadata)
                {
                    file.setFolder(false);
                    long size=((FileMetadata) f).getSize();
                    file.setSize(helpingBot.sizeinwords(size));
                    file.setSizeLong(size);
                    file.setFileId(((FileMetadata) f).getId());
                    file.setThumbUrl(f.getPathDisplay() + "@#$" + file.getFileId());
                    file.setLastModified(((FileMetadata)f).getClientModified().getTime());
                }
                else
                {
                    if(f instanceof FolderMetadata)
                    {
                        file.setFolder(true);
                        file.setSize("");
                        file.setSizeLong(0);
                        file.setThumbUrl(null);
                        file.setFileId(((FolderMetadata) f).getId());
                        file.setLastModified(0);//DROPBOX FOLDER HAS NO MODIFIED TIME VARIABLE
                    }
                    else
                    {
                        continue;
                    }
                }
                file.setThumbUrl(file.getPath()+ "@#$" + file.getFileId());
                myFilesList.add(file);
            }
            catch (Exception e)
            {
                Log.e(TAG,e.getMessage()+"--");
                return null;
            }
        }
        return myFilesList;
    }



}
