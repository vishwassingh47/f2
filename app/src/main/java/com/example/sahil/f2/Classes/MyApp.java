package com.example.sahil.f2.Classes;

import android.graphics.drawable.Drawable;

/**
 * Created by hit4man47 on 2/1/2018.
 */

public class MyApp
{
    private String appName,packageName,versionName;
    private int versionCode;
    private boolean isSystemApp;
    private boolean isSystemAppUpdated;
    private long sizeOfApk;
    private long lastModified;
    private String apkPath;

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getSizeOfApk() {
        return sizeOfApk;
    }

    public void setSizeOfApk(long sizeOfApk) {
        this.sizeOfApk = sizeOfApk;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public boolean isSystemAppUpdated() {
        return isSystemAppUpdated;
    }

    public void setSystemAppUpdated(boolean systemAppUpdated) {
        isSystemAppUpdated = systemAppUpdated;
    }


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

}
