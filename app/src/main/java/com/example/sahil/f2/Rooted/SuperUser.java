package com.example.sahil.f2.Rooted;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.stericson.RootTools.RootTools;

import java.util.List;

/**
 * Created by hit4man47 on 11/1/2017.
 */

public class SuperUser
{
    private static String TAG="SuperUser";
    public static boolean hasUserEnabledSU=false;
    public static boolean isDeviceRooted=false;
    public static boolean hasSUPermissionGranted=false;//check this for root operations
    public static boolean isBusyBoxInstalled=false;


    public static boolean turnOnRoot(Context context)
    {
        if(RootTools.isRootAvailable())
        {
            isDeviceRooted=true;
            Log.e(TAG,"root is available");
            if(RootTools.isAccessGiven())
            {
                hasUserEnabledSU=true;
                hasSUPermissionGranted=true;

                if(RootTools.isBusyboxAvailable())
                {
                    isBusyBoxInstalled=true;
                }
                else
                {
                    isBusyBoxInstalled=false;
                }

                SharedPreferences pref=context.getSharedPreferences("SU",0);
                SharedPreferences.Editor editor=pref.edit();
                editor.putBoolean("hasUserEnabledSU",true);
                editor.apply();

                return true;
            }
            else
            {
                hasUserEnabledSU=false;
                hasSUPermissionGranted=false;
                isBusyBoxInstalled=false;
                return false;
            }

        }
        else
        {
            hasUserEnabledSU=false;
            hasSUPermissionGranted=false;
            isBusyBoxInstalled=false;
            isDeviceRooted=false;
            Log.e(TAG,"root is not available");
            return false;
        }
    }

    public static void turnOffRoot(Context context)
    {
        hasUserEnabledSU=false;
        hasSUPermissionGranted=false;
        isBusyBoxInstalled=false;
        isDeviceRooted=false;
        Log.e(TAG,"root access Revoked");
        SharedPreferences pref=context.getSharedPreferences("SU",0);
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean("hasUserEnabledSU",false);
        editor.apply();
    }




}
