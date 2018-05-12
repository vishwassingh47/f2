package com.example.sahil.f2.Utilities;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.OperationTheater.PagerXUtilities;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.stericson.RootTools.RootTools;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.ECField;

/**
 * Created by hit4man47 on 1/8/2018.
 */

public class DeleteUtils
{
    final private FTPClient ftpClient;
    final private Context context;
    final private TinyDB tinyDB;
    final private ExtensionUtil extensionUtil;

    public DeleteUtils(FTPClient ftpClient,Context context)
    {
        this.ftpClient=ftpClient;
        this.context=context;
        tinyDB=new TinyDB(context);
        extensionUtil=new ExtensionUtil();
    }

    public boolean deleteFromLocal(String path,boolean isFolder)
    {
        if(deleteFromInternal2(path))
        {
            //deleted
            return true;
        }

        if(SuperUser.hasUserEnabledSU && RootTools.deleteFileOrDirectory(path,true))
        {
            return true;
        }
        if(PagerXUtilities.isExternalSdCardPath(path))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                //assuming that we are having SAF permissions at this point
                return deleteFromSaf(path,isFolder);
            }
            else
            {
                //MEDIA STORE HACK

            }
        }
        return false;

    }

    public boolean deleteFromSaf(String path,boolean isFolder)
    {
        DocumentFile df= StorageAccessFramework.fileToDocumentFileConverter(path,isFolder,context);
        if(df==null)
        {
            return false;
        }
        if(df.delete())
        {
            if(!isFolder)
            {
                deleteFromAllMediaStore(new File(path));
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean deleteFromInternal2(String path)
    {
        File jFile=new File(path);
        if(jFile.exists())
        {
            if(jFile.isFile())
            {
                if(jFile.delete())
                {
                    deleteFromAllMediaStore(jFile);
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                try
                {
                    for (File child : jFile.listFiles())
                    {
                        deleteFromInternal(child);
                    }
                    return jFile.delete();
                }
                catch (Exception e)
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }

    }

    public boolean deleteFromInternal(File fileOrDirectory)
    {
        if(!fileOrDirectory.exists())
        {
            return true;
            //already deleted
        }
        if (fileOrDirectory.isDirectory())
        {
            try
            {
                for (File child : fileOrDirectory.listFiles())
                {
                    deleteFromInternal(child);
                }
            }
            catch (Exception e)
            {
                return false;
            }
        }

        if(fileOrDirectory.isFile())
        {
            if(fileOrDirectory.delete())
            {
                deleteFromAllMediaStore(fileOrDirectory);
                return true;
            }
            else
            {
                return false;
            }
        }

        return fileOrDirectory.delete();
    }



    public boolean deleteDocumentFile(File fileOrDirectory)
    {
        if(!fileOrDirectory.exists())
        {
            return true;
            //already deleted
        }
        DocumentFile df= StorageAccessFramework.fileToDocumentFileConverter(fileOrDirectory.getPath(),fileOrDirectory.isDirectory(),context);
        if(df==null)
        {
            return false;
        }
        if(fileOrDirectory.isFile())
        {
            if(df.delete())
            {

                return true;
            }
            else
            {
                return false;
            }
        }

        return df.delete();
    }

    public boolean moveToRecycleBin(File fileOrDirectory)
    {
        String pathFrom=fileOrDirectory.getAbsolutePath();
        final String recycleBinPath=slashAppender(MainActivity.Physical_Storage_PATHS.get(0),"f2/.RecycleBin");
        File recycleBin=new File(recycleBinPath);
        if(!recycleBin.exists())
        {
            boolean x=recycleBin.mkdirs();
            if(!x)
            {
                return  false;
            }
        }

        String toPath=slashAppender(recycleBinPath,fileOrDirectory.getName());
        File to=new File(toPath);
        if(to.exists())
        {
            to=keepBoth(to);
        }
        if(fileOrDirectory.renameTo(to))
        {
            tinyDB.putString(to.getAbsolutePath(),pathFrom);
            return true;
        }
        else
        {
            return false;
        }
    }


    public void deleteImageFromMediaStore(String path)
    {
        int x=context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + path + "'", null);
    }
    public void deleteVideoFromMediaStore(String path)
    {
        context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + path + "'", null);
    }
    public void deleteAudioFromMediaStore(String path)
    {
        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + path + "'", null);
    }

    public void deleteFileFromMediaStore(String path)
    {
        context.getContentResolver().delete(MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATA + "='" + path + "'", null);
    }


    private File keepBoth(final File file)
    {

        int fileNumber=0;
        String path=file.getAbsolutePath();
        int lastIndex=path.lastIndexOf('/');
        String rootPath=path.substring(0,lastIndex);
        if(file.isDirectory())        //is a directory
        {
            String x1=file.getName()+"("+(++fileNumber)+")";

            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
            *
            *
            * like if a.txt and a(1).txt is present how to copy???
            *
            */
            while(true)
            {
                if(new File(slashAppender(rootPath,x1)).exists())
                {
                    x1=file.getName()+"("+(++fileNumber)+")";
                }
                else
                {
                    break;
                }
            }
            return new File(slashAppender(rootPath,x1));
        }

        else //is a file
        {
            String x=file.getName(); //.....'vishwas.txt'------'mydiiiicccc'
            String x2="";
            if(x.lastIndexOf('.')>=0)
            {
                x2=x.substring(x.lastIndexOf('.'),x.length());//.....'.txt'------''
            }

            String x1="";

            if(x.lastIndexOf('.')>=0)
            {
                x1=x.substring(0,x.lastIndexOf('.'))+"("+(++fileNumber)+")";//....'vishwas(1)'
            }
            else
            {
                x1=x.substring(0,x.length())+"("+(++fileNumber)+")";//------'mydiiiicccc(1'
            }


            while (true)
            {
                if(new File(slashAppender(rootPath,x1+x2)).exists())
                {
                    if(x.lastIndexOf('.')>=0)
                    {
                        x1=x.substring(0,x.lastIndexOf('.'))+"("+(++fileNumber)+")";//....'vishwas(1)'
                    }
                    else
                    {
                        x1=x.substring(0,x.length())+"("+(++fileNumber)+")";//------'mydiiiicccc(1'
                    }
                }
                else
                {
                    break;
                }
            }
            return new File(slashAppender(rootPath,x1+x2));
        }
    }


    private void deleteFromAllMediaStore(File jFile)
    {
        int extensionId=extensionUtil.getExtensionId(jFile.getName());
        switch (extensionId)
        {
            case 1:
                deleteImageFromMediaStore(jFile.getAbsolutePath());
                break;
            case 2:
                deleteVideoFromMediaStore(jFile.getAbsolutePath());
                break;
            case 3:
                deleteAudioFromMediaStore(jFile.getAbsolutePath());
                break;
        }
        deleteFileFromMediaStore(jFile.getAbsolutePath());
    }









    /**
     *
     * @param path
     * @param fileOrFolder:0=assume,1=file,2=folder
     * @return
     */
    public boolean deleteFromFtpServer(String path,int fileOrFolder)
    {
        Log.e("ftp delete",path+"--"+fileOrFolder);
        if(ftpClient==null)
            return false;
        boolean isFile;
        if(fileOrFolder==0) //assume
        {
            int lastIndex1=path.lastIndexOf('/');
            int lastIndex2=path.lastIndexOf('.');
            if(lastIndex2>lastIndex1)
            {
                isFile=true;
            }
            else
            {
                isFile=false;
            }
        }
        else
        {
            isFile=fileOrFolder==1;
        }


        if(isFile)   //is a file
        {
            try
            {
                return ftpClient.deleteFile(path);
            }
            catch (Exception e)
            {}
            return false;
        }
        else    //is a directory
        {
            FTPFile[] filesFtp=null;
            try
            {
                filesFtp= ftpClient.listFiles(path);
            }
            catch (Exception e)
            {
                return false;
            }
            Log.e("list",filesFtp+"---"+filesFtp.length);
            if(filesFtp!=null && filesFtp.length>0)
            {
                for(FTPFile ftpFile:filesFtp)
                {
                    String name=ftpFile.getName();
                    if(name.equals(".") || name.equals(".."))
                    {
                        continue;
                    }
                    String fullPath=slashAppender(path,name);
                    if(ftpFile.isDirectory())
                    {
                        deleteFromFtpServer(fullPath,2);
                    }
                    else
                    {
                        try
                        {
                            ftpClient.deleteFile(fullPath);
                        }
                        catch (Exception w)
                        {}
                    }
                }
            }
            try
            {
                return ftpClient.removeDirectory(path);
            }
            catch (Exception e)
            {}
            return false;
        }

    }

    public boolean deleteFromDropBox(String path)
    {
        try
        {
            DropBoxConnection.mDbxClient.files().deleteV2(path).getMetadata();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean deleteFromGoogleDrive(String id,boolean temporaryDelete)
    {
        if(temporaryDelete)
        {
            com.google.api.services.drive.model.File newContent=new com.google.api.services.drive.model.File();
            newContent.setTrashed(true);
            try
            {
                GoogleDriveConnection.m_service_client.files().update(id,newContent).execute();
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        }
        else
        {
            try
            {
                GoogleDriveConnection.m_service_client.files().delete(id).execute();
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        }
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
