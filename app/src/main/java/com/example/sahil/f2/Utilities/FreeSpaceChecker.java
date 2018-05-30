package com.example.sahil.f2.Utilities;

import android.content.Context;

import com.dropbox.core.v2.users.SpaceUsage;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.google.api.services.drive.model.About;

import java.io.IOException;

public class FreeSpaceChecker
{
    public long freeLocalSpace(String path,Context context)
    {
        RootUtils rootUtils=new RootUtils();
        return rootUtils.getFreeSpace(path,context);
    }


    public long freeDriveSpace()
    {
        try
        {
            About about = GoogleDriveConnection.m_service_client.about().get().setFields("storageQuota, user").execute();
            GoogleDriveConnection.totalSize = about.getStorageQuota().getLimit();
            GoogleDriveConnection.usedSize = about.getStorageQuota().getUsageInDrive();

            return GoogleDriveConnection.totalSize-GoogleDriveConnection.usedSize;
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    public long freeDropBoxSpace()
    {
        try
        {
            SpaceUsage spaceUsage = DropBoxConnection.mDbxClient.users().getSpaceUsage();
            DropBoxConnection.totalSize=spaceUsage.getAllocation().getIndividualValue().getAllocated();
            DropBoxConnection.usedSize=spaceUsage.getUsed();

            return DropBoxConnection.totalSize-DropBoxConnection.usedSize;
        }
        catch (Exception e)
        {
            return -1;
        }
    }


}
