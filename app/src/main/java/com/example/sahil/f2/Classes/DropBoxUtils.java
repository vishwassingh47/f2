package com.example.sahil.f2.Classes;

import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;

/**
 * Created by hit4man47 on 10/20/2017.
 */

public class DropBoxUtils
{
    public static boolean exist(String path) throws Exception
    {
        try
        {
            Metadata m=DropBoxConnection.mDbxClient.files().getMetadata(path);
            return true;
            //this path already exists
        }
        catch (GetMetadataErrorException e)
        {
            if(e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound())
            {
                //this path doesnt exist and we can use this as ours
                return false;
            }
            else
            {
                throw new Exception("error while checking if dropbox file exist");
            }
        }
        catch (DbxException e)
        {
            throw new Exception("error while checking if dropbox file exist");
        }
    }

    public static String getSharingLink(String path)
    {
        String link=null;
        try
        {
            link=DropBoxConnection.mDbxClient.sharing().createSharedLinkWithSettings(path).getUrl();
        }
        catch (DbxException e)
        {
            link=null;
        }

        if(link==null)
        {
            try
            {
                link=DropBoxConnection.mDbxClient.sharing().listSharedLinksBuilder().withPath(path).withDirectOnly(true).start().getLinks().get(0).getUrl();
            }
            catch (DbxException e)
            {
                link=null;
            }
        }
        return link;
    }

    public static String getDownloadLink(String path)
    {
        String link=null;
        try
        {
            String shareLink=getSharingLink(path);
            int index=shareLink.lastIndexOf('?');
            link=shareLink.substring(0,index)+"?raw=1";
        }
        catch (Exception e)
        {
            link=null;
        }

        Log.e("url to download:",link+"--");
        return link;
    }


}
