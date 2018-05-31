package com.example.sahil.f2.Cache;

import com.example.sahil.f2.MainActivity;

/**
 * Created by hit4man47 on 12/28/2017.
 */

public class Constants
{
    public static String DROPBOX_DOWNLOAD_PATH,GOOGLEDRIVE_DOWNLOAD_PATH,FTP_DOWNLOAD_PATH,FTP_SERVER_PATH,APK_BACKUP_PATH;

    public static long thumbRunnerDelay=200;
    public static final int SEXY_INPUT_STREAM_READ_FAILED=-99;
    public static void clear()
    {
        DROPBOX_DOWNLOAD_PATH= MainActivity.Physical_Storage_PATHS.get(0)+"/f2/Downloads/DropBox";
        GOOGLEDRIVE_DOWNLOAD_PATH= MainActivity.Physical_Storage_PATHS.get(0)+"/f2/Downloads/Google Drive";
        FTP_DOWNLOAD_PATH=MainActivity.Physical_Storage_PATHS.get(0)+"/f2/Downloads/FTP";
        FTP_SERVER_PATH=MainActivity.Physical_Storage_PATHS.get(0);
        APK_BACKUP_PATH=MainActivity.Physical_Storage_PATHS.get(0)+"/f2/Apk Backups";
        thumbRunnerDelay=200;

    }


    public static final String ACCEPT_WIFI_DATA="YES";
    public static final String REJECT_WIFI_DATA="NO";

    public static final int PHONE_PICKER_PORT=9234;
    public static final int HANDSHAKE_PORT=9876;


}
