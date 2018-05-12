package com.example.sahil.f2.Classes.DropBox;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

/**
 * Created by hit4man47 on 10/6/2017.
 */

public class GoogleDriveConnection
{

    public static GoogleApiClient mGoogleApiClient;
    public static com.google.api.services.drive.Drive m_service_client;


    public static int whatToDo=0;
    public static String userName=null;
    public static long totalSize=0;
    public static long usedSize=0;
    public static boolean isDriveAvailable=false;
    public static boolean isDriveConnected=false;
    public static boolean isDriveConnecting=false;
    public static boolean isErrorConnecting=false;
    public static int progress=0;


    public static int index=-1;

    public static void clear()
    {

        mGoogleApiClient=null;
        m_service_client=null;
        whatToDo=0;
        userName=null;
        totalSize=0;
        usedSize=0;
        isDriveAvailable=false;
        isDriveConnected=false;
        isDriveConnecting=false;
        isErrorConnecting=false;
        progress=0;



        index=-1;
    }
}
